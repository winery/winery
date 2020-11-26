/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/

import { EntityTypesModel } from './entityTypesModel';
import { CapabilityDefinitionModel } from './capabilityDefinitionModel';
import { RequirementDefinitionModel } from './requirementDefinitonModel';
import { EntityType, TPolicyType } from './ttopology-template';
import { PropertyDefinitionType } from './enums';

export class InheritanceUtils {

    static getParent(element: EntityType, entities: EntityType[]): EntityType {
        if (this.hasParentType(element)) {
            const parentQName = element.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].derivedFrom.type;
            return entities.find(entity => entity.qName === parentQName);
        }
        return null;
    }

    static getInheritanceAncestry<T extends EntityType>(entityType: string, entityTypes: T[]): T[] {
        const entity = entityTypes.find(type => type.qName === entityType || type.id === entityType);
        const result = [];

        if (entity) {
            result.push(entity);
            let parent = this.getParent(entity, entityTypes);

            while (parent) {
                result.push(parent);
                parent = this.getParent(parent, entityTypes);
            }
        }

        return result;
    }

    static hasParentType(element: EntityType): boolean {
        return (element && element.full
            && element.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0]
            && element.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].derivedFrom
        );
    }

    /**
     * Retrieves the effective value of a given field of an entity type by traversing the type ancestry upwards and getting the first occurrence found of
     * this field. For example, you can get the effictive mime_type of an artifact type using this method.
     * @param typeName the type of the entity as a qname string e.g., '{tosca.nodes}.Compute'.
     * @param fieldName the name of the field we are interested in, e.g., 'documentation'.
     * @param allTypes the set of all types of this specific entity type, e.g., the linear set of all node types.
     */
    static getEffectiveSingleFieldValueOfAnEntityType(typeName: string, fieldName: string, allTypes: EntityType[]) {
        const ancestry = this.getInheritanceAncestry(typeName, allTypes);

        for (const entityType of ancestry) {
            if (entityType[fieldName]) {
                return entityType[fieldName];
            }
        }

        return undefined;
    }

    static getEffectiveCapabilityDefinitionsOfNodeType(nodeType: string, entityTypes: EntityTypesModel): CapabilityDefinitionModel[] {
        const listOfEffectiveCapabilityDefinitions: CapabilityDefinitionModel[] = [];
        const listOfBequeathingNodeTypes = this.getInheritanceAncestry(nodeType, entityTypes.unGroupedNodeTypes);
        for (const currentNodeType of listOfBequeathingNodeTypes) {
            if (currentNodeType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].capabilityDefinitions &&
                currentNodeType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].capabilityDefinitions.capabilityDefinition) {
                for (const capabilityDefinition of currentNodeType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0]
                    .capabilityDefinitions.capabilityDefinition) {
                    if (!listOfEffectiveCapabilityDefinitions
                        .some(value => value.name === capabilityDefinition.name)) {
                        listOfEffectiveCapabilityDefinitions.push(capabilityDefinition);
                    }
                }
            }
        }
        return listOfEffectiveCapabilityDefinitions;
    }

    static getEffectiveRequirementDefinitionsOfNodeType(nodeType: string, entityTypes: EntityTypesModel): RequirementDefinitionModel[] {
        const listOfEffectiveRequirementDefinitions: RequirementDefinitionModel[] = [];
        const listOfBequeathingNodeTypes = this.getInheritanceAncestry(nodeType, entityTypes.unGroupedNodeTypes);
        for (const currentNodeType of listOfBequeathingNodeTypes) {
            if (currentNodeType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].requirementDefinitions &&
                currentNodeType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].requirementDefinitions.requirementDefinition) {
                for (const requirementDefinition of currentNodeType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0]
                    .requirementDefinitions.requirementDefinition) {
                    if (!listOfEffectiveRequirementDefinitions
                        .some(value => value.name === requirementDefinition.name)) {
                        listOfEffectiveRequirementDefinitions.push(requirementDefinition);
                    }
                }
            }
        }
        return listOfEffectiveRequirementDefinitions;
    }

    /**
     * Returns a list of decendants of an entity type.
     *
     * Returns a list of all decendants of the entity type in no particular order.
     * The list contains the entity type itself at position 0.
     * @param entityType
     * @param entityTypes
     */
    static getDescendantsOfEntityType<T extends EntityType>(entityType: string, entityTypes: T[]): T[] {
        const listOfDescendants: T[] = [];
        listOfDescendants.push(entityTypes.find(et => et.qName === entityType));
        // Look for any entityTypes, that have the given entityType somewhere along their inheritance hierarchy.
        for (const entType of entityTypes) {
            if (entType.qName !== entityType) {
                if (this.getInheritanceAncestry(entType.qName, entityTypes).find(candidate => candidate.qName === entityType)) {
                    // Those elements are the descendants
                    listOfDescendants.push(entType);
                }
            }
        }
        return listOfDescendants;
    }

    /**
     * Gets the active set of allowed target node types for this YAML policy type
     * i.e., returns the targets array which is the lowest possible.
     * @param policyTypeQName
     * @param policyTypes
     */
    static getEffectiveTargetsOfYamlPolicyType(policyTypeQName: string, policyTypes: TPolicyType[]): string[] {
        const hierarchy = this.getInheritanceAncestry(policyTypeQName, policyTypes);
        let result = [];

        for (const type of hierarchy) {
            if ((<TPolicyType>type).targets) {
                result = (<TPolicyType>type).targets;
                break;
            }
        }

        return result;
    }

    static getEffectivePropertiesOfTemplateElement(templateElementProperties: any, typeQName: string, entityTypes: EntityType[]): any {
        const defaultTypeProperties = this.getDefaultPropertiesFromEntityTypes(typeQName, entityTypes);
        const result = {};
        if (!defaultTypeProperties) {
            console.log('Could not find default type properties for type ' + typeQName);
            return { propertyType: PropertyDefinitionType.NONE };
        }
        if (defaultTypeProperties.propertyType === PropertyDefinitionType.KV) {
            Object.assign(result, defaultTypeProperties.kvproperties);
        }
        if (defaultTypeProperties.propertyType === PropertyDefinitionType.YAML) {
            Object.assign(result, defaultTypeProperties.properties);
        }
        // overwrite defaults from the entity type with the properties of the element
        if (templateElementProperties && templateElementProperties.properties) {
            Object.assign(result, templateElementProperties.properties);
        }
        // FIXME: because this method is only used for Yaml Policies this forced mapping to YAML-properties is doable
        //  This is highly likely to break for anything beyond that specific usecase!
        return { propertyType: PropertyDefinitionType.YAML, properties: result };
    }

    /**
     * This function gets KV properties of a type and sets their default values
     * @param any type: the element type, e.g. capabilityType, requirementType etc.
     * @returns newKVProperties: KV Properties as Object
     */
    static getKVProperties(type: any): any {
        const newKVProperies = {};
        const kvProperties = type.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.propertyDefinitionKVList;
        for (const obj of kvProperties) {
            const key = obj.key;
            let value;
            if (!obj.value && obj.defaultValue) {
                value = obj.defaultValue;
            } else if (!obj.value) {
                value = '';
            } else {
                value = obj.value;
            }
            newKVProperies[key] = value;
        }
        return newKVProperies;
    }

    static hasKVPropDefinition(element: EntityType): boolean {
        return (element && element.full &&
            element.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition &&
            element.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.propertyDefinitionKVList &&
            element.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.propertyDefinitionKVList.length > 0
        );
    }

    static hasYamlPropDefinition(element: EntityType): boolean {
        return (element && element.full &&
            element.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition &&
            element.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.properties
        );
    }

    static getYamlProperties(type: EntityType): any {
        const newProperties = {};
        const definedProperties = type.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.properties;
        for (const obj of definedProperties) {
            // set default value, regardless of whether the property is required
            try {
                newProperties[obj.name] = JSON.parse(obj.defaultValue);
            } catch (e) {
                newProperties[obj.name] = obj.defaultValue;
            }
        }
        return newProperties;
    }

    /**
     * Generates default properties from node types or relationshipTypes
     * The assumption appears to be that types only add new properties and never change existing ones (e.g., change type or default value)
     * todo why name not qname?
     * todo use the 'getInheritanceAncestry' method
     * @param qName
     * @param entities
     * @return properties
     */
    static getDefaultPropertiesFromEntityTypes(qName: string, entities: EntityType[]): any {
        for (const element of entities) {
            if (element.qName === qName) {
                // if propertiesDefinition is defined it's a XML property
                // FIXME this needs to correctly handle the type option for defining XML properties
                if (element.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition
                    && element.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.element) {
                    return {
                        propertyType: PropertyDefinitionType.XML,
                        any: element.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.element
                    };
                // properties definition contains yaml properties
                } else if (element.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition
                    && element.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.properties) {
                    let inheritedProperties = {};
                    if (InheritanceUtils.hasParentType(element)) {
                        let parent = element.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].derivedFrom.typeRef;
                        let continueFlag;

                        while (parent) {
                            continueFlag = false;
                            for (const parentElement of entities) {
                                if (parentElement.qName === parent) {
                                    if (InheritanceUtils.hasYamlPropDefinition(parentElement)) {
                                        inheritedProperties = {
                                            ...inheritedProperties, ...InheritanceUtils.getYamlProperties(parentElement)
                                        };
                                    }
                                    if (InheritanceUtils.hasParentType(parentElement)) {
                                        parent = parentElement.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].derivedFrom.typeRef;
                                        continueFlag = true;
                                    }
                                    break;
                                }
                            }
                            if (!continueFlag) {
                                break;
                            }
                        }
                    }

                    let typeProperties = {};
                    if (InheritanceUtils.hasYamlPropDefinition(element)) {
                        typeProperties = InheritanceUtils.getYamlProperties(element);
                    }

                    const mergedProperties = { ...inheritedProperties, ...typeProperties };

                    return {
                        propertyType: PropertyDefinitionType.YAML,
                        properties: { ...mergedProperties }
                    };
                } else { // otherwise KV properties or no properties at all
                    let inheritedProperties = {};
                    if (InheritanceUtils.hasParentType(element)) {
                        let parent = element.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].derivedFrom.typeRef;
                        let continueFlag;

                        while (parent) {
                            continueFlag = false;
                            for (const parentElement of entities) {
                                if (parentElement.qName === parent) {
                                    if (InheritanceUtils.hasKVPropDefinition(parentElement)) {
                                        inheritedProperties = {
                                            ...inheritedProperties, ...InheritanceUtils.getKVProperties(parentElement)
                                        };
                                    }
                                    if (InheritanceUtils.hasParentType(parentElement)) {
                                        parent = parentElement.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].derivedFrom.typeRef;
                                        continueFlag = true;
                                    }
                                    break;
                                }
                            }
                            if (continueFlag) {
                                continue;
                            }
                            parent = null;
                        }
                    }

                    let typeProperties = {};
                    if (InheritanceUtils.hasKVPropDefinition(element)) {
                        typeProperties = InheritanceUtils.getKVProperties(element);
                    }

                    const mergedProperties = { ...inheritedProperties, ...typeProperties };

                    return {
                        propertyType: PropertyDefinitionType.KV,
                        kvproperties: { ...mergedProperties }
                    };
                }
            }
        }
    }

    /**
     * Returns a list of valid source types for the given capability definition.
     *
     * If the capabilities definition itself has a list of valid source types it is used.
     * Otherwise the valid source types of the closest capability type regarding ancestry is used.
     *
     * @param capabilityDefinition The capability definition for which the valid source types list is queried.
     * @param capabilityTypes The list of all Capability Types
     */
    static getValidSourceTypes(capabilityDefinition: CapabilityDefinitionModel, capabilityTypes: EntityType[]): string[] {
        if (capabilityDefinition.validSourceTypes && capabilityDefinition.validSourceTypes.length) {
            return capabilityDefinition.validSourceTypes;
        } else {
            const ancestry: EntityType[] = InheritanceUtils.getInheritanceAncestry(capabilityDefinition.capabilityType, capabilityTypes);
            for (const ancestor of ancestry) {
                const listOfValidSourceTypes = ancestor.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].validNodeTypes;
                if (listOfValidSourceTypes && listOfValidSourceTypes.length) {
                    return listOfValidSourceTypes;
                }
            }
        }
        return [];
    }
}
