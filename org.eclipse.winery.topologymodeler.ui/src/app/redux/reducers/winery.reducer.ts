/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

import { Action } from 'redux';
import {
    DecMaxInstances, DecMinInstances, DeleteDeploymentArtifactAction, DeleteNodeAction, DeletePolicyAction, DeleteRelationshipAction,
    HideNavBarAndPaletteAction, IncMaxInstances, IncMinInstances, SaveNodeTemplateAction, SaveRelationshipAction, SendCurrentNodeIdAction,
    SendPaletteOpenedAction, SetCababilityAction, SetDeploymentArtifactAction, SetPolicyAction, SetPropertyAction, SetRequirementAction,
    SetTargetLocation, SidebarMaxInstanceChanges, SidebarMinInstanceChanges, SidebarNodeNamechange, SidebarStateAction,
    UpdateNodeCoordinatesAction, UpdateRelationshipNameAction, WineryActions
} from '../actions/winery.actions';
import { TNodeTemplate, TRelationshipTemplate, TTopologyTemplate } from '../../models/ttopology-template';
import { TDeploymentArtifact } from '../../models/artifactsModalData';

export interface WineryState {
    currentPaletteOpenedState: boolean;
    hideNavBarAndPaletteState: boolean;
    sidebarContents: any;
    currentJsonTopology: TTopologyTemplate;
    currentNodeData: any;
}

export const INITIAL_WINERY_STATE: WineryState = {
    currentPaletteOpenedState: true,
    hideNavBarAndPaletteState: false,
    sidebarContents: {
        sidebarVisible: false,
        nodeClicked: false,
        id: '',
        nameTextFieldValue: '',
        type: '',
        minInstances: 1,
        maxInstances: 1
    },
    currentJsonTopology: new TTopologyTemplate,
    currentNodeData: {
        id: '',
        focus: false
    }
};

/**
 * Reducer for the rest of the topology modeler
 */
export const WineryReducer =
    function (lastState: WineryState = INITIAL_WINERY_STATE, action: Action): WineryState {
        switch (action.type) {
            case WineryActions.SEND_PALETTE_OPENED:
                const paletteOpened: boolean = (<SendPaletteOpenedAction>action).paletteOpened;

                return <WineryState>{
                    ...lastState,
                    currentPaletteOpenedState: paletteOpened
                };
            case WineryActions.HIDE_NAVBAR_AND_PALETTE:
                const hideNavBarAndPalette: boolean = (<HideNavBarAndPaletteAction>action).hideNavBarAndPalette;

                return <WineryState>{
                    ...lastState,
                    hideNavBarAndPaletteState: hideNavBarAndPalette
                };
            case WineryActions.OPEN_SIDEBAR:
                const newSidebarData: any = (<SidebarStateAction>action).sidebarContents;

                return <WineryState>{
                    ...lastState,
                    sidebarContents: newSidebarData
                };
            case WineryActions.CHANGE_MIN_INSTANCES:
                const sideBarNodeId: any = (<SidebarMinInstanceChanges>action).minInstances.id;
                const minInstances: any = (<SidebarMinInstanceChanges>action).minInstances.count;
                const indexChangeMinInstances = lastState.currentJsonTopology.nodeTemplates.map(el => el.id).indexOf(sideBarNodeId);
                const fool = true;

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        nodeTemplates: lastState.currentJsonTopology.nodeTemplates.map(nodeTemplate => nodeTemplate.id === sideBarNodeId ?
                            nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute('minInstances', minInstances.toString()) : nodeTemplate
                        )
                    }
                };
            case WineryActions.CHANGE_MAX_INSTANCES:
                const sideBarNodeId2: any = (<SidebarMaxInstanceChanges>action).maxInstances.id;
                const maxInstances: any = (<SidebarMaxInstanceChanges>action).maxInstances.count;
                const indexChangeMaxInstances = lastState.currentJsonTopology.nodeTemplates
                    .map(el => el.id).indexOf(sideBarNodeId2);

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        nodeTemplates: lastState.currentJsonTopology.nodeTemplates
                            .map(nodeTemplate => nodeTemplate.id === sideBarNodeId2 ?
                                nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute('maxInstances', maxInstances.toString()) : nodeTemplate
                            )
                    }
                };
            case WineryActions.INC_MIN_INSTANCES:
                const id_incMinInstances: any = (<IncMinInstances>action).minInstances.id;
                const indexIncMinInstances = lastState.currentJsonTopology.nodeTemplates
                    .map(el => el.id).indexOf(id_incMinInstances);

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        nodeTemplates: lastState.currentJsonTopology.nodeTemplates
                            .map(nodeTemplate => nodeTemplate.id === id_incMinInstances ?
                                nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute('minInstances',
                                    (Number(lastState.currentJsonTopology.nodeTemplates[indexIncMinInstances].minInstances) + 1).toString())
                                : nodeTemplate
                            )
                    }
                };
            case WineryActions.DEC_MIN_INSTANCES:
                const id_decMinInstances: any = (<DecMinInstances>action).minInstances.id;
                const indexDecMinInstances = lastState.currentJsonTopology.nodeTemplates
                    .map(el => el.id).indexOf(id_decMinInstances);

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        nodeTemplates: lastState.currentJsonTopology.nodeTemplates
                            .map(nodeTemplate => nodeTemplate.id === id_decMinInstances ?
                                nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute('minInstances',
                                    (Number(lastState.currentJsonTopology.nodeTemplates[indexDecMinInstances].minInstances) - 1).toString())
                                : nodeTemplate
                            )
                    }
                };
            case WineryActions.INC_MAX_INSTANCES:
                const id_incMaxInstances: any = (<IncMaxInstances>action).maxInstances.id;
                const indexIncMaxInstances = lastState.currentJsonTopology.nodeTemplates
                    .map(el => el.id).indexOf(id_incMaxInstances);

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        nodeTemplates: lastState.currentJsonTopology.nodeTemplates
                            .map(nodeTemplate => nodeTemplate.id === id_incMaxInstances ?
                                nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute('maxInstances',
                                    (Number(lastState.currentJsonTopology.nodeTemplates[indexIncMaxInstances].maxInstances) + 1).toString())
                                : nodeTemplate
                            )
                    }
                };
            case WineryActions.DEC_MAX_INSTANCES:
                const id_decMaxInstances: any = (<DecMaxInstances>action).maxInstances.id;
                const indexDecMaxInstances = lastState.currentJsonTopology.nodeTemplates
                    .map(el => el.id).indexOf(id_decMaxInstances);

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        nodeTemplates: lastState.currentJsonTopology.nodeTemplates
                            .map(nodeTemplate => nodeTemplate.id === id_decMaxInstances ?
                                nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute('maxInstances',
                                    (Number(lastState.currentJsonTopology.nodeTemplates[indexDecMaxInstances].maxInstances) - 1).toString())
                                : nodeTemplate
                            )
                    }
                };
            case WineryActions.SET_REQUIREMENT:
                const newRequirement: any = (<SetRequirementAction>action).nodeRequirement;

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        nodeTemplates: lastState.currentJsonTopology.nodeTemplates
                            .map(nodeTemplate => nodeTemplate.id === newRequirement.nodeId ?
                                nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute('requirements',
                                    {
                                        requirement: newRequirement.requirement
                                    }) : nodeTemplate
                            )
                    }
                };
            case WineryActions.SET_PROPERTY:
                const newProperty: any = (<SetPropertyAction>action).nodeProperty;

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        nodeTemplates: lastState.currentJsonTopology.nodeTemplates
                            .map(nodeTemplate => nodeTemplate.id === newProperty.nodeId ?
                                nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute('properties',
                                    newProperty.propertyType === 'KV' ?
                                        { kvproperties: newProperty.newProperty } : { any: newProperty.newProperty }) : nodeTemplate
                            )
                    }
                };
            case WineryActions.SET_CAPABILITY:
                const newCapability: any = (<SetCababilityAction>action).nodeCapability;

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        nodeTemplates: lastState.currentJsonTopology.nodeTemplates
                            .map(nodeTemplate => nodeTemplate.id === newCapability.nodeId ?
                                nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute('capabilities',
                                    {
                                        capability: newCapability.capability
                                    }) : nodeTemplate
                            )
                    }
                };
            case WineryActions.SET_DEPLOYMENT_ARTIFACT:
                const newDepArt: any = (<SetDeploymentArtifactAction>action).nodeDeploymentArtifact;
                const newDeploymentArtifact: TDeploymentArtifact = newDepArt.newDeploymentArtifact;
                const indexOfNodeDepArt = lastState.currentJsonTopology.nodeTemplates
                    .map(node => node.id).indexOf(newDepArt.nodeId);
                const nodeDepArtTemplate = lastState.currentJsonTopology.nodeTemplates
                    .find(nodeTemplate => nodeTemplate.id === newDepArt.nodeId);
                const depArtExist = nodeDepArtTemplate.deploymentArtifacts && nodeDepArtTemplate.deploymentArtifacts.deploymentArtifact;

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        nodeTemplates: lastState.currentJsonTopology.nodeTemplates
                            .map(nodeTemplate => nodeTemplate.id === newDepArt.nodeId ?
                                nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute('deploymentArtifacts',
                                    depArtExist ? {
                                        deploymentArtifact: [
                                            ...lastState.currentJsonTopology.nodeTemplates[indexOfNodeDepArt].deploymentArtifacts.deploymentArtifact,
                                            newDeploymentArtifact
                                        ]
                                    } : {
                                        deploymentArtifact: [
                                            newDeploymentArtifact
                                        ]
                                    }) : nodeTemplate
                            )
                    }
                };
            case WineryActions.DELETE_DEPLOYMENT_ARTIFACT:
                const deletedDeploymentArtifact: any = (<DeleteDeploymentArtifactAction>action).nodeDeploymentArtifact.deletedDeploymentArtifact;
                const indexOfNodeWithDeletedDeploymentArtifact = lastState.currentJsonTopology.nodeTemplates
                    .map((node) => {
                        return node.id;
                    })
                    .indexOf((<DeleteDeploymentArtifactAction>action).nodeDeploymentArtifact.nodeId);

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        nodeTemplates: lastState.currentJsonTopology.nodeTemplates
                            .map((nodeTemplate) => nodeTemplate.id === (<DeleteDeploymentArtifactAction>action).nodeDeploymentArtifact.nodeId ?
                                nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute('deploymentArtifacts',
                                    {
                                        deploymentArtifact: [
                                            ...lastState.currentJsonTopology.nodeTemplates[indexOfNodeWithDeletedDeploymentArtifact]
                                                .deploymentArtifacts.deploymentArtifact
                                                .filter(da => da.name !== deletedDeploymentArtifact)
                                        ]
                                    }) : nodeTemplate
                            )
                    }
                };

            case WineryActions.SET_POLICY:
                const newPolicy: any = (<SetPolicyAction>action).nodePolicy;
                const policy = newPolicy.newPolicy;
                const indexOfNodePolicy = lastState.currentJsonTopology.nodeTemplates
                    .map(node => node.id).indexOf(newPolicy.nodeId);
                const nodePolicyTemplate = lastState.currentJsonTopology.nodeTemplates
                    .find(nodeTemplate => nodeTemplate.id === newPolicy.nodeId);
                const policyExist = nodePolicyTemplate.policies && nodePolicyTemplate.policies.policy;

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        nodeTemplates: lastState.currentJsonTopology.nodeTemplates
                            .map(nodeTemplate => nodeTemplate.id === newPolicy.nodeId ?
                                nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute('policies',
                                    policyExist ? {
                                        policy: [
                                            ...lastState.currentJsonTopology.nodeTemplates[indexOfNodePolicy].policies.policy,
                                            policy
                                        ]
                                    } : {
                                        policy: [
                                            policy
                                        ]
                                    }) : nodeTemplate
                            )
                    }
                };
            case WineryActions.SET_TARGET_LOCATION:
                const newTargetLocation: any = (<SetTargetLocation>action).nodeTargetLocation;

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        nodeTemplates: lastState.currentJsonTopology.nodeTemplates
                            .map(nodeTemplate => nodeTemplate.id === newTargetLocation.nodeId ?
                                nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute('location',
                                    newTargetLocation.newTargetLocation) : nodeTemplate
                            )
                    }
                };
            case WineryActions.DELETE_POLICY:
                const deletedPolicy: any = (<DeletePolicyAction>action).nodePolicy.deletedPolicy;
                const indexOfNodeWithDeletedPolicy = lastState.currentJsonTopology.nodeTemplates
                    .map((node) => {
                        return node.id;
                    })
                    .indexOf((<DeletePolicyAction>action).nodePolicy.nodeId);

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        nodeTemplates: lastState.currentJsonTopology.nodeTemplates
                            .map((nodeTemplate) => nodeTemplate.id === (<DeletePolicyAction>action).nodePolicy.nodeId ?
                                nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute('policies',
                                    {
                                        policy: [
                                            ...lastState.currentJsonTopology.nodeTemplates[indexOfNodeWithDeletedPolicy]
                                                .policies.policy
                                                .filter(da => da.name !== deletedPolicy)
                                        ]
                                    }) : nodeTemplate
                            )
                    }
                };
            case WineryActions.CHANGE_NODE_NAME:
                const newNodeName: any = (<SidebarNodeNamechange>action).nodeNames;
                const indexChangeNodeName = lastState.currentJsonTopology.nodeTemplates
                    .map(el => el.id).indexOf(newNodeName.id);

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        nodeTemplates: lastState.currentJsonTopology.nodeTemplates
                            .map(nodeTemplate => nodeTemplate.id === newNodeName.id ?
                                nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute('name', newNodeName.newNodeName)
                                : nodeTemplate
                            )
                    }
                };
            case WineryActions.UPDATE_NODE_COORDINATES:
                const currentNodeCoordinates: any = (<UpdateNodeCoordinatesAction>action).otherAttributes;
                const nodeId = currentNodeCoordinates.id;
                const otherAttributes = {
                    x: currentNodeCoordinates.x,
                    y: currentNodeCoordinates.y
                };
                const indexUpdateNodeCoordinates = lastState.currentJsonTopology.nodeTemplates
                    .map(nodeTemplate => nodeTemplate.id).indexOf(nodeId);

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        nodeTemplates: lastState.currentJsonTopology.nodeTemplates
                            .map(nodeTemplate => nodeTemplate.id === nodeId ?
                                nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute('coordinates', otherAttributes)
                                : nodeTemplate
                            )
                    }
                };
            case WineryActions.SAVE_NODE_TEMPLATE :
                const newNode: TNodeTemplate = (<SaveNodeTemplateAction>action).nodeTemplate;

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        nodeTemplates: [...lastState.currentJsonTopology.nodeTemplates, newNode]
                    }
                };
            case WineryActions.SAVE_RELATIONSHIP :
                const newRelationship: TRelationshipTemplate = (<SaveRelationshipAction>action).relationshipTemplate;

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        relationshipTemplates: [...lastState.currentJsonTopology.relationshipTemplates, newRelationship]
                    }
                };
            case WineryActions.DELETE_NODE_TEMPLATE:
                const deletedNodeId: string = (<DeleteNodeAction>action).nodeTemplateId;

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        nodeTemplates: lastState.currentJsonTopology.nodeTemplates
                            .filter(nodeTemplate => nodeTemplate.id !== deletedNodeId),
                        relationshipTemplates: lastState.currentJsonTopology.relationshipTemplates.filter(
                            relationshipTemplate => relationshipTemplate.sourceElement.ref !== deletedNodeId &&
                                relationshipTemplate.targetElement.ref !== deletedNodeId)
                    }
                };
            case WineryActions.DELETE_RELATIONSHIP_TEMPLATE:
                const deletedRelNodeId: string = (<DeleteRelationshipAction>action).nodeTemplateId;

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        relationshipTemplates: lastState.currentJsonTopology.relationshipTemplates.filter(
                            relationshipTemplate => relationshipTemplate.id !== deletedRelNodeId)
                    }
                };
            case WineryActions.UPDATE_REL_DATA:
                const relData: any = (<UpdateRelationshipNameAction>action).relData;
                const indexRel = lastState.currentJsonTopology.relationshipTemplates
                    .map(rel => rel.id).indexOf(relData.id);

                return <WineryState>{
                    ...lastState,
                    currentJsonTopology: {
                        ...lastState.currentJsonTopology,
                        relationshipTemplates: lastState.currentJsonTopology.relationshipTemplates
                            .map(relTemplate => relTemplate.id === relData.id ?
                                relTemplate.generateNewRelTemplateWithUpdatedAttribute('name', relData.newRelName)
                                : relTemplate
                            )
                    }
                };
            case WineryActions.SEND_CURRENT_NODE_ID :
                const currentNodeData: string = (<SendCurrentNodeIdAction>action).currentNodeData;

                return <WineryState>{
                    ...lastState,
                    currentNodeData: currentNodeData
                };

            default:
                return <WineryState> lastState;
        }
    };
