/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

import { Injectable } from '@angular/core';
import { Action } from 'redux';
import { LiveModelingStates, ServiceTemplateInstanceStates } from '../../models/enums';
import { Csar } from '../../models/container/csar.model';
import { PlanInstance } from '../../models/container/plan-instance.model';
import { TTopologyTemplate } from '../../models/ttopology-template';
import { LiveModelingSettings } from '../../models/liveModelingSettings';

export interface SetStateAction extends Action {
    state: LiveModelingStates;
}

export interface SetContainerUrlAction extends Action {
    containerUrl: string;
}

export interface SetCurrentCsarIdAction extends Action {
    csarId: string;
}

export interface SetCurrentCsarAction extends Action {
    csar: Csar;
}

export interface SetCurrentServiceTemplateInstanceIdAction extends Action {
    serviceTemplateInstanceId: string;
}

export interface SetCurrentServiceTemplateInstanceStateAction extends Action {
    serviceTemplateInstanceState: ServiceTemplateInstanceStates;
}

export interface SetSettingsAction extends Action {
    settings: any;
}

export interface SetDeploymentChangesAction extends Action {
    deploymentChanges: boolean;
}

export interface SetCurrentBuildPlanInstance extends Action {
    buildPlanInstance: PlanInstance;
}

export interface SetDeployedJsonTopology extends Action {
    deployedJsonTopology: TTopologyTemplate;
}

/**
 * Actions for live modeling
 */
@Injectable()
export class LiveModelingActions {
    static SET_STATE = 'SET_STATE';
    static SET_CONTAINER_URL = 'SET_CONTAINER_URL';
    static SET_CURRENT_CSAR_ID = 'SET_CURRENT_CSAR_ID';
    static SET_CURRENT_CSAR = 'SET_CURRENT_CSAR';
    static SET_CURRENT_SERVICE_TEMPLATE_INSTANCE_ID = 'SET_CURRENT_SERVICE_TEMPLATE_INSTANCE_ID';
    static SET_CURRENT_SERVICE_TEMPLATE_INSTANCE_STATE = 'SET_CURRENT_SERVICE_TEMPLATE_INSTANCE_STATE';
    static SET_SETTINGS = 'SET_SETTING';
    static SET_DEPLOYMENT_CHANGES = 'SET_DEPLOYMENT_CHANGES';
    static SET_CURRENT_BUILD_PLAN_INSTANCE = 'SET_CURRENT_BUILD_PLAN_INSTANCE';
    static SET_DEPLOYED_JSON_TOPOLOGY = 'SET_DEPLOYED_JSON_TOPOLOGY';

    setState(state: LiveModelingStates): SetStateAction {
        return {
            type: LiveModelingActions.SET_STATE,
            state: state
        };
    }

    setContainerUrl(containerUrl: string): SetContainerUrlAction {
        return {
            type: LiveModelingActions.SET_CONTAINER_URL,
            containerUrl: containerUrl
        };
    }

    setCurrentCsar(csar: Csar): SetCurrentCsarAction {
        return {
            type: LiveModelingActions.SET_CURRENT_CSAR,
            csar: csar
        };
    }

    setCurrentCsarId(csarId: string): SetCurrentCsarIdAction {
        return {
            type: LiveModelingActions.SET_CURRENT_CSAR_ID,
            csarId: csarId
        };
    }

    setCurrentServiceTemplateInstanceId(serviceTemplateInstanceId: string): SetCurrentServiceTemplateInstanceIdAction {
        return {
            type: LiveModelingActions.SET_CURRENT_SERVICE_TEMPLATE_INSTANCE_ID,
            serviceTemplateInstanceId: serviceTemplateInstanceId
        };
    }

    setCurrentServiceTemplateInstanceState(serviceTemplateInstanceState: ServiceTemplateInstanceStates): SetCurrentServiceTemplateInstanceStateAction {
        return {
            type: LiveModelingActions.SET_CURRENT_SERVICE_TEMPLATE_INSTANCE_STATE,
            serviceTemplateInstanceState: serviceTemplateInstanceState
        };
    }

    setSettings(settings: LiveModelingSettings): SetSettingsAction {
        return {
            type: LiveModelingActions.SET_SETTINGS,
            settings: settings
        };
    }

    setDeploymentChanges(deploymentChanges: boolean): SetDeploymentChangesAction {
        return {
            type: LiveModelingActions.SET_DEPLOYMENT_CHANGES,
            deploymentChanges: deploymentChanges
        };
    }

    setCurrentBuildPlanInstance(buildPlanInstance: PlanInstance): SetCurrentBuildPlanInstance {
        return {
            type: LiveModelingActions.SET_CURRENT_BUILD_PLAN_INSTANCE,
            buildPlanInstance: buildPlanInstance
        };
    }

    setDeployedJsonTopology(topologyTemplate: TTopologyTemplate): SetDeployedJsonTopology {
        return {
            type: LiveModelingActions.SET_DEPLOYED_JSON_TOPOLOGY,
            deployedJsonTopology: topologyTemplate
        };
    }
}
