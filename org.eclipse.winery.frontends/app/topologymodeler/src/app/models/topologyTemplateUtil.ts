/********************************************************************************
 * Copyright (c) 2018-2020 Contributors to the Eclipse Foundation
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
import { DifferenceStates, ToscaDiff, VersionUtils } from './ToscaDiff';
import { Visuals } from './visuals';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { WineryActions } from '../redux/actions/winery.actions';
import { CapabilityDefinitionModel } from './capabilityDefinitionModel';
import { EntityTypesModel } from './entityTypesModel';
import { CapabilityModel } from './capabilityModel';
import { RequirementDefinitionModel } from './requirementDefinitonModel';
import { RequirementModel } from './requirementModel';
import { InheritanceUtils } from './InheritanceUtils';

export class TopologyTemplateUtil {

    static HORIZONTAL_OFFSET_FOR_NODES_WITHOUT_COORDINATES = 350;
    static VERTICAL_OFFSET_FOR_NODES_WITHOUT_COORDINATES = 200;

    static createTNodeTemplateFromObject(node: TNodeTemplate, nodeVisuals: Visuals[],
                                         isYaml: boolean, types: EntityTypesModel, state?: DifferenceStates): TNodeTemplate {
        const nodeVisualsObject = this.getNodeVisualsForNodeTemplate(node.type, nodeVisuals, state);
        let properties;
        if (node.properties) {
            properties = node.properties;
        }

        let nameSpace: string;
        let targetLocationKey: string;
        let providerKey: string;
        let regionKey: string;
        let otherAttributes;
        for (const key in node.otherAttributes) {
            if (node.otherAttributes.hasOwnProperty(key)) {
                nameSpace = key.substring(key.indexOf('{'), key.indexOf('}') + 1);
                if (nameSpace) {
                    if (key.substring(key.indexOf('}') + 1) === 'location') {
                        targetLocationKey = key;
                    }
                    if (key.substring(key.indexOf('}') + 1) === 'provider') {
                        providerKey = key;
                    }
                    if (key.substring(key.indexOf('}') + 1) === 'region') {
                        regionKey = key;
                    }
                    otherAttributes = {
                        [nameSpace + 'location']: node.otherAttributes[targetLocationKey],
                        [nameSpace + 'provider']: node.otherAttributes[providerKey],
                        [nameSpace + 'region']: node.otherAttributes[regionKey],
                        [nameSpace + 'x']: node.x,
                        [nameSpace + 'y']: node.y
                    };
                } else if (key === 'location') {
                    targetLocationKey = 'location';
                }
            }
        }

        // for Yaml, we add missing capabilities, find their types, and fix their ids, we also fix requirement ids (to avoid duplicates)
        if (isYaml) {
            if (!types) {
                // todo ensure entity types model is always available. See TopologyTemplateUtil.updateTopologyTemplate
                console.error('The required entity types model is not available! Unexpected behavior');
            }
            // look for missing capabilities and add them
            const capDefs: CapabilityDefinitionModel[] = InheritanceUtils.getEffectiveCapabilityDefinitionsOfNodeType(node.type, types);
            if (!node.capabilities || !node.capabilities.capability) {
                node.capabilities = { capability: [] };
            }
            capDefs.forEach(def => {
                const capAssignment = node.capabilities.capability.find(capAss => capAss.name === def.name);
                const cap: CapabilityModel = CapabilityModel.fromCapabilityDefinitionModel(def);

                if (capAssignment) {
                    const capAssignmentIndex = node.capabilities.capability.indexOf(capAssignment);
                    cap.properties = capAssignment.properties;
                    node.capabilities.capability.splice(capAssignmentIndex, 1);
                }

                cap.id = this.generateYAMLCapabilityID(node, cap.name);
                node.capabilities.capability.push(cap);
            });

            // we assume that either all requirements are in the template, or none are (and therefore must be retrieved from the type hierarchy)
            const reqDefs: RequirementDefinitionModel[] = InheritanceUtils.getEffectiveRequirementDefinitionsOfNodeType(node.type, types);
            if (!node.requirements) {
                node.requirements = { requirement: [] };
            }
            reqDefs.forEach(reqDef => {
                const req = RequirementModel.fromRequirementDefinition(reqDef);
                if (!node.requirements.requirement.find(r => r.name === req.name)) {
                    node.requirements.requirement.push(req);
                }
            });
            node.requirements.requirement.forEach(req => req.id = this.generateYAMLRequirementID(node, req.name));
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
            node.capabilities ? node.capabilities : { capability: [] },
            node.requirements ? node.requirements : { requirement: [] },
            node.deploymentArtifacts ? node.deploymentArtifacts : {},
            node.policies ? node.policies : { policy: [] },
            node.artifacts ? node.artifacts : { artifact: [] },
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
            relationship.properties,
            relationship.documentation,
            relationship.any,
            relationship.otherAttributes,
            state,
            relationship.policies
        );
    }

    static getNodeVisualsForNodeTemplate(nodeType: string, nodeVisuals: Visuals[], state?: DifferenceStates): Visuals {
        for (const visual of nodeVisuals) {
            const qName = new QName(visual.typeId);
            const localName = qName.localName;
            if (localName === new QName(nodeType).localName) {
                const color = !state ? visual.color : VersionUtils.getElementColorByDiffState(state);
                return <Visuals>{
                    color: color,
                    typeId: nodeType,
                    imageUrl: visual.imageUrl,
                    pattern: visual.pattern
                };
            }
        }
    }

    static initNodeTemplates(nodeTemplateArray: Array<TNodeTemplate>, nodeVisuals: Visuals[], isYaml: boolean, types: EntityTypesModel,
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
                    TopologyTemplateUtil.createTNodeTemplateFromObject(node, nodeVisuals, isYaml, types, state)
                );
            });
        }

        return nodeTemplates;
    }

    static generateYAMLRequirementID(nodeTemplate: TNodeTemplate, requirement: string): string {
        return `${nodeTemplate.id}_req_${requirement}`;
    }

    static generateYAMLCapabilityID(nodeTemplate: TNodeTemplate, capability: string): string {
        return `${nodeTemplate.id}_cap_${capability}`;
    }

    static handleYamlRelationship(relationship: TRelationshipTemplate, nodeTemplateArray: Array<TNodeTemplate>) {
        // First, we look for the source node template / requirement
        for (const nodeTemplate of nodeTemplateArray) {
            const foundRequirement: RequirementModel = nodeTemplate.requirements.requirement
                .find(requirement => requirement.relationship === relationship.id);
            if (foundRequirement) {
                // the id was calculated before by the init node template method
                relationship.sourceElement = { ref: foundRequirement.id };
                // now we look for the target node template / capability.
                const targetNodeTemplate = nodeTemplateArray.find(nt => nt.id === foundRequirement.node);
                if (targetNodeTemplate) {
                    const targetCapability = targetNodeTemplate.capabilities.capability
                        .find(cap => cap.name === foundRequirement.capability);
                    // the id was calculated before by the init node template method
                    relationship.targetElement = { ref: targetCapability.id };
                    break;
                }
            }
        }
    }

    static initRelationTemplates(relationshipTemplateArray: Array<TRelationshipTemplate>, nodeTemplateArray: Array<TNodeTemplate>, isYaml: boolean,
                                 topologyDifferences?: [ToscaDiff, TTopologyTemplate]): Array<TRelationshipTemplate> {
        const relationshipTemplates: TRelationshipTemplate[] = [];
        if (relationshipTemplateArray.length > 0) {
            relationshipTemplateArray.forEach(relationship => {
                if (isYaml) {
                    this.handleYamlRelationship(relationship, nodeTemplateArray);
                }
                const state = topologyDifferences ? DifferenceStates.UNCHANGED : null;
                relationshipTemplates.push(
                    TopologyTemplateUtil.createTRelationshipTemplateFromObject(relationship, state)
                );
            });
        }

        return relationshipTemplates;
    }

    static updateTopologyTemplate(ngRedux: NgRedux<IWineryState>, wineryActions: WineryActions, topology: TTopologyTemplate, isYaml: boolean) {
        const wineryState = ngRedux.getState().wineryState;

        // Required because if the palette is open, the last node inserted will be bound to the mouse movement.
        ngRedux.dispatch(wineryActions.sendPaletteOpened(false));

        wineryState.currentJsonTopology.nodeTemplates
            .forEach(
                node => ngRedux.dispatch(wineryActions.deleteNodeTemplate(node.id))
            );
        wineryState.currentJsonTopology.relationshipTemplates
            .forEach(
                relationship => ngRedux.dispatch(wineryActions.deleteRelationshipTemplate(relationship.id))
            );

        TopologyTemplateUtil.initNodeTemplates(topology.nodeTemplates, wineryState.nodeVisuals, isYaml, null)
            .forEach(
                node => ngRedux.dispatch(wineryActions.saveNodeTemplate(node))
            );
        TopologyTemplateUtil.initRelationTemplates(topology.relationshipTemplates, topology.nodeTemplates, isYaml)
            .forEach(
                relationship => ngRedux.dispatch(wineryActions.saveRelationship(relationship))
            );
    }
}
