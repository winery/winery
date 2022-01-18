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
import { LiveModelingStates, ServiceTemplateInstanceStates } from '../../models/enums';
import { Action } from 'redux';
import {
    LiveModelingActions, SetCurrentBuildPlanInstance, SetContainerUrlAction, SetCurrentCsarAction,
    SetCurrentCsarIdAction,
    SetCurrentServiceTemplateInstanceIdAction, SetCurrentServiceTemplateInstanceStateAction, SetDeploymentChangesAction, SetSettingsAction, SetStateAction,
    SetDeployedJsonTopology
} from '../actions/live-modeling.actions';
import { InputParameter } from '../../models/container/input-parameter.model';
import { Csar } from '../../models/container/csar.model';
import { PlanInstance } from '../../models/container/plan-instance.model';
import { TTopologyTemplate } from '../../models/ttopology-template';
import { TopologyTemplateUtil } from '../../models/topologyTemplateUtil';
import { LiveModelingSettings } from '../../models/liveModelingSettings';

export interface LiveModelingState {
    state: LiveModelingStates;
    containerUrl: string;
    currentCsarId: string;
    currentCsar: Csar;
    currentServiceTemplateInstanceId: string;
    currentServiceTemplateInstanceState: ServiceTemplateInstanceStates;
    settings: LiveModelingSettings;
    deploymentChanges: boolean;
    currentBuildPlanInstance: PlanInstance;
    deployedJsonTopology: TTopologyTemplate;
}

export const INITIAL_LIVE_MODELING_STATE: LiveModelingState = {
    state: LiveModelingStates.DISABLED,
    containerUrl: null,
    currentCsarId: null,
    currentCsar: null,
    currentServiceTemplateInstanceId: null,
    currentServiceTemplateInstanceState: ServiceTemplateInstanceStates.INITIAL,
    settings: LiveModelingSettings.initial(),
    deploymentChanges: false,
    currentBuildPlanInstance: null,
    deployedJsonTopology: null
};

export const LiveModelingReducer =
    function (lastState: LiveModelingState = INITIAL_LIVE_MODELING_STATE, action: Action): LiveModelingState {
        switch (action.type) {
            case LiveModelingActions.SET_STATE:
                const state = (<SetStateAction>action).state;
                return <LiveModelingState>{
                    ...lastState,
                    state: state
                };
            case LiveModelingActions.SET_CONTAINER_URL: {
                const containerUrl = (<SetContainerUrlAction>action).containerUrl;

                return <LiveModelingState>{
                    ...lastState,
                    containerUrl: containerUrl
                };
            }
            case LiveModelingActions.SET_CURRENT_CSAR_ID: {
                const csarId = (<SetCurrentCsarIdAction>action).csarId;

                return <LiveModelingState>{
                    ...lastState,
                    currentCsarId: csarId
                };
            }
            case LiveModelingActions.SET_CURRENT_CSAR: {
                const csar = (<SetCurrentCsarAction>action).csar;

                return <LiveModelingState>{
                    ...lastState,
                    currentCsar: csar
                };
            }
            case LiveModelingActions.SET_CURRENT_SERVICE_TEMPLATE_INSTANCE_ID: {
                const serviceTemplateInstanceId = (<SetCurrentServiceTemplateInstanceIdAction>action).serviceTemplateInstanceId;

                return <LiveModelingState>{
                    ...lastState,
                    currentServiceTemplateInstanceId: serviceTemplateInstanceId
                };
            }
            case LiveModelingActions.SET_CURRENT_SERVICE_TEMPLATE_INSTANCE_STATE: {
                const serviceTemplateInstanceState = (<SetCurrentServiceTemplateInstanceStateAction>action).serviceTemplateInstanceState;

                return <LiveModelingState>{
                    ...lastState,
                    currentServiceTemplateInstanceState: serviceTemplateInstanceState
                };
            }
            case LiveModelingActions.SET_SETTINGS: {
                const settings = (<SetSettingsAction>action).settings;

                return <LiveModelingState>{
                    ...lastState,
                    settings: settings
                };
            }
            case LiveModelingActions.SET_DEPLOYMENT_CHANGES: {
                const deploymentChanges = (<SetDeploymentChangesAction>action).deploymentChanges;

                return <LiveModelingState>{
                    ...lastState,
                    deploymentChanges: deploymentChanges
                };
            }
            case LiveModelingActions.SET_CURRENT_BUILD_PLAN_INSTANCE: {
                const buildPlanInstance = (<SetCurrentBuildPlanInstance>action).buildPlanInstance;

                return <LiveModelingState>{
                    ...lastState,
                    currentBuildPlanInstance: buildPlanInstance
                };
            }
            case LiveModelingActions.SET_DEPLOYED_JSON_TOPOLOGY: {
                const topologyTemplate = (<SetDeployedJsonTopology>action).deployedJsonTopology;

                return <LiveModelingState>{
                    ...lastState,
                    deployedJsonTopology: TopologyTemplateUtil.cloneTopologyTemplate(topologyTemplate)
                };
            }
            default: {
                return <LiveModelingState>lastState;
            }
        }
    };
