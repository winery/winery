/********************************************************************************
 * Copyright (c) 2017-2021 Contributors to the Eclipse Foundation
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
    HighlightNodesAction, ShowSelectedMappingAction, TopologyRendererActions
} from '../actions/topologyRenderer.actions';

export enum ResearchPlugin {
    REFINEMENT = 'REFINEMENT',
    PROBLEM_DETECTION = 'PROBLEM_DETECTION',
    ENRICHER = 'ENRICHER',
    EDMM_TRANSFORM = 'EDMM_TRANSFORM',
    GROUP_VIEW = 'GROUP_VIEW',
    MNG_PARTICIPANTS = 'MNG_PARTICIPANTS',
    MULTI_PARTICIPANTS = 'MULTI_PARTICIPANTS',
    INSTANCE_MODEL_REFINEMENT = 'INSTANCE_MODEL_REFINEMENT'
}

export interface TopologyRendererState {
    buttonsState: {
        targetLocationsButton?: boolean;
        policiesButton?: boolean;
        requirementsCapabilitiesButton?: boolean;
        deploymentArtifactsButton?: boolean;
        propertiesButton?: boolean;
        typesButton?: boolean;
        edmmTransformationCheck?: boolean;
        idsButton?: boolean;
        layoutButton?: boolean;
        alignHButton?: boolean;
        alignVButton?: boolean;
        importTopologyButton?: boolean;
        threatModelingButton?: boolean;
        splitTopologyButton?: boolean;
        matchTopologyButton?: boolean;
        problemDetectionButton?: boolean;
        enrichmentButton?: boolean;
        substituteTopologyButton?: boolean;
        refineInstanceModelButton?: boolean;
        refinePatternsButton?: boolean;
        refineTopologyButton?: boolean;
        refineTopologyWithTestsButton?: boolean;
        generateGDM?: boolean;
        extractLDM?: boolean;
        generatePlaceholderSubs?: boolean;
        determineStatefulComponents?: boolean;
        determineFreezableComponentsButton?: boolean;
        cleanFreezableComponentsButton?: boolean;
        placeComponentsButton?: boolean;
        manageYamlPoliciesButton?: boolean;
        versionSliderButton?: boolean;
        manageYamlGroupsButton?: boolean;
        yamlGroupsButton?: boolean;
        manageParticipantsButton?: boolean;
        assignParticipantsButton?: boolean;
        assignDeploymentTechnologyButton?: boolean;
        hideDependsOnRelations?: boolean;
        detectPatternsButton?: boolean;
    };
    activeResearchPlugin: ResearchPlugin;
    nodesToSelect?: string[];

    mappingType?: string;
}

export const INITIAL_TOPOLOGY_RENDERER_STATE: TopologyRendererState = {
    buttonsState: {
        targetLocationsButton: false,
        policiesButton: false,
        requirementsCapabilitiesButton: false,
        deploymentArtifactsButton: false,
        propertiesButton: false,
        typesButton: true,
        edmmTransformationCheck: false,
        idsButton: false,
        layoutButton: false,
        alignHButton: false,
        alignVButton: false,
        importTopologyButton: false,
        threatModelingButton: false,
        splitTopologyButton: false,
        matchTopologyButton: false,
        problemDetectionButton: false,
        enrichmentButton: false,
        substituteTopologyButton: false,
        refineInstanceModelButton: false,
        refinePatternsButton: false,
        refineTopologyButton: false,
        refineTopologyWithTestsButton: false,
        determineStatefulComponents: false,
        generateGDM: false,
        extractLDM: false,
        generatePlaceholderSubs: false,
        determineFreezableComponentsButton: false,
        cleanFreezableComponentsButton: false,
        placeComponentsButton: false,
        manageYamlPoliciesButton: false,
        versionSliderButton: false,
        manageYamlGroupsButton: false,
        yamlGroupsButton: false,
        manageParticipantsButton: false,
        assignDeploymentTechnologyButton: false,
        detectPatternsButton: false,
    },
    activeResearchPlugin: undefined,
};
/**
 * Reducer for the TopologyRenderer
 */
export const TopologyRendererReducer =
    function (lastState: TopologyRendererState = INITIAL_TOPOLOGY_RENDERER_STATE, action: Action): TopologyRendererState {
        switch (action.type) {
            // disables all research plugins globally
            case TopologyRendererActions.DISABLE_RESEARCH_PLUGIN:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        manageYamlGroupsButton: false,
                        manageParticipantsButton: false,
                        problemDetectionButton: false,
                        enrichmentButton: false,
                        edmmTransformationCheck: false,
                        refineInstanceModelButton: false,
                        refinePatternsButton: false,
                        refineTopologyButton: false,
                        refineTopologyWithTestsButton: false,
                        detectPatternsButton: false,
                    },
                    activeResearchPlugin: undefined
                };
            case TopologyRendererActions.TOGGLE_YAML_GROUPS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        yamlGroupsButton: !lastState.buttonsState.yamlGroupsButton,
                    },
                };
            case TopologyRendererActions.SHOW_MANAGE_YAML_GROUPS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        manageYamlGroupsButton: true
                    },
                    activeResearchPlugin: ResearchPlugin.GROUP_VIEW
                };
            case TopologyRendererActions.TOGGLE_MANAGE_YAML_GROUPS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        manageYamlGroupsButton: !lastState.buttonsState.manageYamlGroupsButton
                    },
                    activeResearchPlugin: !lastState.buttonsState.manageYamlGroupsButton ? ResearchPlugin.GROUP_VIEW : undefined,
                };
            case TopologyRendererActions.TOGGLE_MANAGE_PARTICIPANTS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        manageParticipantsButton: !lastState.buttonsState.manageParticipantsButton
                    },
                    activeResearchPlugin: !lastState.buttonsState.manageParticipantsButton ? ResearchPlugin.MNG_PARTICIPANTS : undefined,
                };
            case TopologyRendererActions.TOGGLE_ASSIGN_PARTICIPANTS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        assignParticipantsButton: !lastState.buttonsState.assignParticipantsButton
                    }
                };
            case TopologyRendererActions.TOGGLE_ASSIGN_DEPLOYMENT_TECHNOLOGY:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        assignDeploymentTechnologyButton: !lastState.buttonsState.assignDeploymentTechnologyButton
                    }
                };
            case TopologyRendererActions.TOGGLE_HIDE_DEPENDSON_RELATIONS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        hideDependsOnRelations: !lastState.buttonsState.hideDependsOnRelations
                    }
                };
            case TopologyRendererActions.TOGGLE_POLICIES:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        policiesButton: !lastState.buttonsState.policiesButton
                    }
                };
            case TopologyRendererActions.TOGGLE_TARGET_LOCATIONS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        targetLocationsButton: !lastState.buttonsState.targetLocationsButton
                    }
                };
            case TopologyRendererActions.TOGGLE_PROPERTIES:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        propertiesButton: !lastState.buttonsState.propertiesButton
                    }
                };
            case TopologyRendererActions.EXTRACT_LDM:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        extractLDM: !lastState.buttonsState.extractLDM
                    }
                };
            case TopologyRendererActions.TOGGLE_REQUIREMENTS_CAPABILITIES:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        requirementsCapabilitiesButton: !lastState.buttonsState.requirementsCapabilitiesButton
                    }
                };
            case TopologyRendererActions.TOGGLE_DEPLOYMENT_ARTIFACTS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        deploymentArtifactsButton: !lastState.buttonsState.deploymentArtifactsButton
                    }
                };
            case TopologyRendererActions.TOGGLE_IDS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        idsButton: !lastState.buttonsState.idsButton
                    }
                };
            case TopologyRendererActions.TOGGLE_TYPES:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        typesButton: !lastState.buttonsState.typesButton
                    }
                };
            case TopologyRendererActions.TOGGLE_EDMM_TRANSFORMATION_CHECK:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        edmmTransformationCheck: !lastState.buttonsState.edmmTransformationCheck
                    },
                    activeResearchPlugin: !lastState.buttonsState.edmmTransformationCheck ? ResearchPlugin.EDMM_TRANSFORM : undefined,
                };
            case TopologyRendererActions.EXECUTE_LAYOUT:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        layoutButton: !lastState.buttonsState.layoutButton
                    }
                };
            case TopologyRendererActions.EXECUTE_ALIGN_H:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        alignHButton: !lastState.buttonsState.alignHButton
                    }
                };
            case TopologyRendererActions.EXECUTE_ALIGN_V:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        alignVButton: !lastState.buttonsState.alignVButton
                    }
                };
            case TopologyRendererActions.IMPORT_TOPOLOGY:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        importTopologyButton: !lastState.buttonsState.importTopologyButton
                    }
                };
            case TopologyRendererActions.THREATMODEL_TOPOLOGY:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        threatModelingButton: !lastState.buttonsState.threatModelingButton
                    }
                };
            case TopologyRendererActions.SPLIT_TOPOLOGY:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        splitTopologyButton: !lastState.buttonsState.splitTopologyButton
                    }
                };
            case TopologyRendererActions.MATCH_TOPOLOGY:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        matchTopologyButton: !lastState.buttonsState.matchTopologyButton
                    }
                };
            case TopologyRendererActions.DETECT_PROBLEMS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        problemDetectionButton: !lastState.buttonsState.problemDetectionButton
                    },
                    activeResearchPlugin: !lastState.buttonsState.problemDetectionButton ? ResearchPlugin.PROBLEM_DETECTION : undefined,
                };
            case TopologyRendererActions.ENRICH_NODE_TEMPLATES:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        enrichmentButton: !lastState.buttonsState.enrichmentButton
                    },
                    activeResearchPlugin: !lastState.buttonsState.enrichmentButton ? ResearchPlugin.ENRICHER : undefined,
                };
            case TopologyRendererActions.SUBSTITUTE_TOPOLOGY:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        substituteTopologyButton: !lastState.buttonsState.substituteTopologyButton
                    }
                };
            case TopologyRendererActions.REFINE_INSTANCE_MODEL:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        refineInstanceModelButton: !lastState.buttonsState.refineInstanceModelButton
                    },
                    activeResearchPlugin: ResearchPlugin.INSTANCE_MODEL_REFINEMENT,
                };
            case TopologyRendererActions.REFINE_PATTERNS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        refinePatternsButton: !lastState.buttonsState.refinePatternsButton
                    },
                    activeResearchPlugin: ResearchPlugin.REFINEMENT,
                };
            case TopologyRendererActions.REFINE_TOPOLOGY:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        refineTopologyButton: !lastState.buttonsState.refineTopologyButton
                    },
                    activeResearchPlugin: ResearchPlugin.REFINEMENT,
                };
            case TopologyRendererActions.REFINE_TOPOLOGY_WITH_TESTS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        refineTopologyWithTestsButton: !lastState.buttonsState.refineTopologyWithTestsButton
                    },
                    activeResearchPlugin: ResearchPlugin.REFINEMENT,
                };
            case TopologyRendererActions.GENERATE_GDM:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        generateGDM: !lastState.buttonsState.generateGDM
                    }
                };
            case TopologyRendererActions.GENERATE_PLACEHOLDER_SUBS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        generatePlaceholderSubs: !lastState.buttonsState.generatePlaceholderSubs
                    }
                };
            case TopologyRendererActions.HIGHLIGHT_NODES:
                const data = <HighlightNodesAction>action;
                if (data.nodesToHighlight) {
                    return {
                        ...lastState,
                        nodesToSelect: data.nodesToHighlight
                    };
                } else {
                    delete lastState.nodesToSelect;
                }
                break;
            case TopologyRendererActions.DETERMINE_STATEFUL_COMPONENTS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        determineStatefulComponents: !lastState.buttonsState.determineStatefulComponents
                    }
                };
            case TopologyRendererActions.DETERMINE_FREEZABLE_COMPONENTS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        determineFreezableComponentsButton: !lastState.buttonsState.determineFreezableComponentsButton
                    }
                };
            case TopologyRendererActions.CLEAN_FREEZABLE_COMPONENTS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        cleanFreezableComponentsButton: !lastState.buttonsState.cleanFreezableComponentsButton
                    }
                };
            case TopologyRendererActions.PLACE_COMPONENTS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        placeComponentsButton: !lastState.buttonsState.placeComponentsButton
                    }
                };
            case TopologyRendererActions.MANAGE_YAML_POLICIES:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        manageYamlPoliciesButton: !lastState.buttonsState.manageYamlPoliciesButton
                    }
                };
            case TopologyRendererActions.TOGGLE_VERSION_SLIDER:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        versionSliderButton: !lastState.buttonsState.versionSliderButton
                    }
                };
            case TopologyRendererActions.DETECT_PATTERNS:
                return {
                    ...lastState,
                    buttonsState: {
                        ...lastState.buttonsState,
                        detectPatternsButton: !lastState.buttonsState.detectPatternsButton
                    },
                    activeResearchPlugin: ResearchPlugin.REFINEMENT,
                };
            case TopologyRendererActions.SHOW_ONLY_MAPPINGS_OF_SELECTED_TYPE:
                const actionData = <ShowSelectedMappingAction>action;
                if (actionData.mappingType) {
                    return {
                        ...lastState,
                        mappingType: actionData.mappingType
                    };
                } else {
                    delete lastState.mappingType;
                }
        }
        return lastState;
    };
