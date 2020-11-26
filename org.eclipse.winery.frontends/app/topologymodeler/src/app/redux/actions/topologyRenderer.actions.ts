/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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

import { Injectable } from '@angular/core';
import { Action } from 'redux';

export interface HighlightNodesAction extends Action {
    nodesToHighlight: string[];
}

/**
 * Actions of the topologyRenderer
 */
@Injectable()
export class TopologyRendererActions {

    static TOGGLE_POLICIES = 'TOGGLE_POLICIES';
    static TOGGLE_TARGET_LOCATIONS = 'TOGGLE_TARGET_LOCATIONS';
    static TOGGLE_PROPERTIES = 'TOGGLE_PROPERTIES';
    static TOGGLE_REQUIREMENTS_CAPABILITIES = 'TOGGLE_REQUIREMENTS_CAPABILITIES';
    static TOGGLE_DEPLOYMENT_ARTIFACTS = 'TOGGLE_DEPLOYMENT_ARTIFACTS';
    static TOGGLE_IDS = 'TOGGLE_IDS';
    static TOGGLE_TYPES = 'TOGGLE_TYPES';
    static TOGGLE_EDMM_TRANSFORMATION_CHECK = 'TOGGLE_EDMM_TRANSFORMATION_CHECK';
    static EXECUTE_LAYOUT = 'EXECUTE_LAYOUT';
    static EXECUTE_ALIGN_H = 'EXECUTE_ALIGN_H';
    static EXECUTE_ALIGN_V = 'EXECUTE_ALIGN_V';
    static IMPORT_TOPOLOGY = 'IMPORT_TOPOLOGY';
    static THREATMODEL_TOPOLOGY = 'THREATMODEL_TOPOLOGY';
    static SPLIT_TOPOLOGY = 'SPLIT_TOPOLOGY';
    static MATCH_TOPOLOGY = 'MATCH_TOPOLOGY';
    static SUBSTITUTE_TOPOLOGY = 'SUBSTITUTE_TOPOLOGY';
    static REFINE_TOPOLOGY = 'REFINE_TOPOLOGY';
    static REFINE_PATTERNS = 'REFINE_PATTERNS';
    static REFINE_TOPOLOGY_WITH_TESTS = 'REFINE_TOPOLOGY_WITH_TESTS';
    static GENERATE_GDM = 'GENERATE_GDM';
    static GENERATE_PLACEHOLDER_SUBS = 'GENERATE_PLACEHOLDER_SUBS';
    static EXTRACT_LDM = 'EXTRACT_LDM';
    static HIGHLIGHT_NODES = 'HIGHLIGHT_NODES';
    static DETECT_PROBLEMS = 'DETECT_PROBLEMS';
    static ENRICH_NODE_TEMPLATES = 'ENRICH_NODE_TEMPLATES';
    static DETERMINE_STATEFUL_COMPONENTS = 'DETERMINE_STATEFUL_COMPONENTS';
    static DETERMINE_FREEZABLE_COMPONENTS = 'DETERMINE_FREEZABLE_COMPONENTS';
    static CLEAN_FREEZABLE_COMPONENTS = 'CLEAN_FREEZABLE_COMPONENTS';
    static PLACE_COMPONENTS = 'PLACE_COMPONENTS';
    static MANAGE_YAML_POLICIES = 'MANAGE_YAML_POLICIES';
    static TOGGLE_VERSION_SLIDER = 'TOGGLE_VERSION_SLIDER';
    static SHOW_MANAGE_YAML_GROUPS = 'SHOW_MANAGE_YAML_GROUPS';
    static TOGGLE_MANAGE_YAML_GROUPS = 'TOGGLE_MANAGE_YAML_GROUPS';
    static TOGGLE_YAML_GROUPS = 'TOGGLE_YAML_GROUPS';
    static TOGGLE_MANAGE_PARTICIPANTS = 'TOGGLE_MANAGE_PARTICIPANTS';
    static TOGGLE_ASSIGN_PARTICIPANTS = 'TOGGLE_ASSIGN_PARTICIPANTS';

    togglePolicies(): Action {
        return { type: TopologyRendererActions.TOGGLE_POLICIES };
    }

    toggleTargetLocations(): Action {
        return { type: TopologyRendererActions.TOGGLE_TARGET_LOCATIONS };
    }

    toggleProperties(): Action {
        return { type: TopologyRendererActions.TOGGLE_PROPERTIES };
    }

    toggleRequirementsCapabilities(): Action {
        return { type: TopologyRendererActions.TOGGLE_REQUIREMENTS_CAPABILITIES };
    }

    toggleDeploymentArtifacts(): Action {
        return { type: TopologyRendererActions.TOGGLE_DEPLOYMENT_ARTIFACTS };
    }

    toggleIds(): Action {
        return { type: TopologyRendererActions.TOGGLE_IDS };
    }

    toggleTypes(): Action {
        return { type: TopologyRendererActions.TOGGLE_TYPES };
    }

    toggleEdmmTransformationCheck(): Action {
        return { type: TopologyRendererActions.TOGGLE_EDMM_TRANSFORMATION_CHECK };
    }

    executeLayout(): Action {
        return { type: TopologyRendererActions.EXECUTE_LAYOUT };
    }

    executeAlignH(): Action {
        return { type: TopologyRendererActions.EXECUTE_ALIGN_H };
    }

    executeAlignV(): Action {
        return { type: TopologyRendererActions.EXECUTE_ALIGN_V };
    }

    importTopology(): Action {
        return { type: TopologyRendererActions.IMPORT_TOPOLOGY };
    }

    threatModeling(): Action {
        return { type: TopologyRendererActions.THREATMODEL_TOPOLOGY };
    }

    splitTopology(): Action {
        return { type: TopologyRendererActions.SPLIT_TOPOLOGY };
    }

    matchTopology(): Action {
        return { type: TopologyRendererActions.MATCH_TOPOLOGY };
    }

    detectProblems(): Action {
        return { type: TopologyRendererActions.DETECT_PROBLEMS };
    }

    enrichNodeTemplates(): Action {
        return { type: TopologyRendererActions.ENRICH_NODE_TEMPLATES };
    }

    substituteTopology(): Action {
        return { type: TopologyRendererActions.SUBSTITUTE_TOPOLOGY };
    }

    refineTopology(): Action {
        return { type: TopologyRendererActions.REFINE_TOPOLOGY };
    }

    extractLDM(): Action {
        return { type: TopologyRendererActions.EXTRACT_LDM };
    }

    generatePlaceholder(): Action {
        return { type: TopologyRendererActions.GENERATE_GDM };
    }

    generatePlaceholderSubs(): Action {
        return { type: TopologyRendererActions.GENERATE_PLACEHOLDER_SUBS };
    }

    refinePatterns(): Action {
        return { type: TopologyRendererActions.REFINE_PATTERNS };
    }

    addTestRefinements(): Action {
        return {
            type: TopologyRendererActions.REFINE_TOPOLOGY_WITH_TESTS
        };
    }

    highlightNodes(listOfNodeIds: string[]): HighlightNodesAction {
        return {
            type: TopologyRendererActions.HIGHLIGHT_NODES,
            nodesToHighlight: listOfNodeIds
        };
    }

    determineStatefulComponents(): Action {
        return { type: TopologyRendererActions.DETERMINE_STATEFUL_COMPONENTS };
    }

    determineFreezableComponents(): Action {
        return { type: TopologyRendererActions.DETERMINE_FREEZABLE_COMPONENTS };
    }

    cleanFreezableComponents(): Action {
        return { type: TopologyRendererActions.CLEAN_FREEZABLE_COMPONENTS };
    }

    placeComponents(): Action {
        return { type: TopologyRendererActions.PLACE_COMPONENTS };
    }

    manageYamlPolicies(): Action {
        return { type: TopologyRendererActions.MANAGE_YAML_POLICIES };
    }

    toggleVersionSlider(): Action {
        return { type: TopologyRendererActions.TOGGLE_VERSION_SLIDER };
    }

    showManageYamlGroups(): Action {
        return { type: TopologyRendererActions.SHOW_MANAGE_YAML_GROUPS };
    }

    toggleManageYamlGroups(): Action {
        return { type: TopologyRendererActions.TOGGLE_MANAGE_YAML_GROUPS };
    }

    toggleYamlGroups(): Action {
        return { type: TopologyRendererActions.TOGGLE_YAML_GROUPS };
    }

    toggleManageParticipants(): Action {
        return { type: TopologyRendererActions.TOGGLE_MANAGE_PARTICIPANTS };
    }

    toggleAssignParticipants(): Action {
        return { type: TopologyRendererActions.TOGGLE_ASSIGN_PARTICIPANTS };
    }
}
