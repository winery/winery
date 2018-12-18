/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
import { TNodeTemplate, TRelationshipTemplate, TTopologyTemplate } from './ttopology-template';
import { QName } from './qname';
import { isNullOrUndefined } from 'util';
import { DifferenceStates, ToscaDiff, VersionUtils } from './ToscaDiff';
import { Visuals } from './visuals';

export class Utils {

    static HORIZONTAL_OFFSET_FOR_NODES_WITHOUT_COORDINATES = 350;
    static VERTICAL_OFFSET_FOR_NODES_WITHOUT_COORDINATES = 200;

    static createTNodeTemplateFromObject(node: TNodeTemplate, nodeVisuals: Visuals[], state?: DifferenceStates): TNodeTemplate {
        const nodeVisualsObject = this.getNodeVisualsForNodeTemplate(node.type, nodeVisuals, state);
        let properties;
        if (node.properties) {
            properties = node.properties;
        }

        let nameSpace: string;
        let targetLocationKey: string;
        let otherAttributes;
        for (const key in node.otherAttributes) {
            if (node.otherAttributes.hasOwnProperty(key)) {
                nameSpace = key.substring(key.indexOf('{'), key.indexOf('}') + 1);
                if (nameSpace) {
                    if (key.substring(key.indexOf('}') + 1) === 'location') {
                        targetLocationKey = key;
                    }
                    otherAttributes = {
                        [nameSpace + 'location']: node.otherAttributes[targetLocationKey],
                        [nameSpace + 'x']: node.x,
                        [nameSpace + 'y']: node.y
                    };
                    break;
                } else if (key === 'location') {
                    targetLocationKey = 'location';
                }
            }
        }

        return new TNodeTemplate(
            properties ? properties : {},
            node.id,
            node.type,
            node.name,
            node.minInstances,
            node.maxInstances,
            nodeVisualsObject,
            node.documentation ? node.documentation : [],
            node.any ? node.any : [],
            otherAttributes,
            node.x,
            node.y,
            node.capabilities ? node.capabilities : {},
            node.requirements ? node.requirements : {},
            node.deploymentArtifacts ? node.deploymentArtifacts : {},
            node.policies ? node.policies : {},
            state
        );
    }

    static createTRelationshipTemplateFromObject(relationship: TRelationshipTemplate, state?: DifferenceStates) {
        return new TRelationshipTemplate(
            relationship.sourceElement,
            relationship.targetElement,
            relationship.name,
            relationship.id,
            relationship.type,
            relationship.documentation,
            relationship.any,
            relationship.otherAttributes,
            state
        );
    }

    static getNodeVisualsForNodeTemplate(nodeType: string, nodeVisuals: Visuals[], state?: DifferenceStates): Visuals {
        for (const visual of nodeVisuals) {
            const qName = new QName(visual.typeId);
            const localName = qName.localName;
            if (localName === new QName(nodeType).localName) {
                const color = !state ? visual.color : VersionUtils.getElementColorByDiffState(state);
                return <Visuals> {
                    color: color,
                    typeId: nodeType,
                    imageUrl: visual.imageUrl,
                    pattern: visual.pattern
                };
            }
        }
    }

    static initNodeTemplates(nodeTemplateArray: Array<TNodeTemplate>, nodeVisuals: Visuals[],
                             topologyDifferences?: [ToscaDiff, TTopologyTemplate]): Array<TNodeTemplate> {
        const nodeTemplates: TNodeTemplate[] = [];
        if (nodeTemplateArray.length > 0) {
            nodeTemplateArray.forEach((node, index) => {
                const offset = 10 * index;
                if (!node.x || !node.y) {
                    node.x = this.HORIZONTAL_OFFSET_FOR_NODES_WITHOUT_COORDINATES + offset;
                    node.y = this.VERTICAL_OFFSET_FOR_NODES_WITHOUT_COORDINATES + offset;
                }
                const state = topologyDifferences ? DifferenceStates.UNCHANGED : null;
                nodeTemplates.push(
                    Utils.createTNodeTemplateFromObject(node, nodeVisuals, state)
                );
            });
        }

        return nodeTemplates;
    }

    /**
     * This function sets KV properties
     * @param any type: the element type, e.g. capabilityType, requirementType etc.
     * @returns newKVProperties: KV Properties as Object
     */
    static setKVProperties(type: any): any {
        let newKVProperies;
        const kvProperties = type.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].any[0].propertyDefinitionKVList;
        for (const obj of kvProperties) {
            const key = obj.key;
            let value;
            if (isNullOrUndefined(obj.value)) {
                value = '';
            } else {
                value = obj.value;
            }
            const keyValuePair = {
                [key]: value
            };
            newKVProperies = { ...newKVProperies, ...keyValuePair };
        }
        return newKVProperies;
    }

    static initRelationTemplates(relationshipTemplateArray: Array<TRelationshipTemplate>,
                                 topologyDifferences?: [ToscaDiff, TTopologyTemplate]): Array<TRelationshipTemplate> {
        const relationshipTemplates: TRelationshipTemplate[] = [];
        if (relationshipTemplateArray.length > 0) {
            relationshipTemplateArray.forEach(relationship => {
                const state = topologyDifferences ? DifferenceStates.UNCHANGED : null;
                relationshipTemplates.push(
                    Utils.createTRelationshipTemplateFromObject(relationship, state)
                );
            });
        }

        return relationshipTemplates;
    }
}
