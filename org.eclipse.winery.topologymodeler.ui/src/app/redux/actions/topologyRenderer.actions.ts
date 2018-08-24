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
    static EXECUTE_LAYOUT = 'EXECUTE_LAYOUT';
    static EXECUTE_ALIGN_H = 'EXECUTE_ALIGN_H';
    static EXECUTE_ALIGN_V = 'EXECUTE_ALIGN_V';
    static IMPORT_TOPOLOGY = 'IMPORT_TOPOLOGY';
    static SPLIT_TOPOLOGY = 'SPLIT_TOPOLOGY';
    static MATCH_TOPOLOGY = 'MATCH_TOPOLOGY';
    static SUBSTITUTE_TOPOLOGY = 'SUBSTITUTE_TOPOLOGY';
    static REFINE_TOPOLOGY = 'REFINE_TOPOLOGY';
    static HIGHLIGHT_NODES = 'HIGHLIGHT_NODES';

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

    splitTopology(): Action {
        return { type: TopologyRendererActions.SPLIT_TOPOLOGY };
    }

    matchTopology(): Action {
        return { type: TopologyRendererActions.MATCH_TOPOLOGY };
    }

    substituteTopology(): Action {
        return { type: TopologyRendererActions.SUBSTITUTE_TOPOLOGY };
    }

    refineTopology(): Action {
        return { type: TopologyRendererActions.REFINE_TOPOLOGY };
    }

    highlightNodes(listOfNodeIds: string[]): HighlightNodesAction {
        return {
            type: TopologyRendererActions.HIGHLIGHT_NODES,
            nodesToHighlight: listOfNodeIds
        };
    }
}
