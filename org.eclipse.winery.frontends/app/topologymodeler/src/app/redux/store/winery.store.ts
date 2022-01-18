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

import { combineReducers, Reducer } from 'redux';
import { INITIAL_WINERY_STATE, WineryReducer, WineryState } from '../reducers/winery.reducer';
import {
    INITIAL_TOPOLOGY_RENDERER_STATE, TopologyRendererReducer, TopologyRendererState
} from '../reducers/topologyRenderer.reducer';
import { INITIAL_LIVE_MODELING_STATE, LiveModelingReducer, LiveModelingState } from '../reducers/live-modeling.reducer';

/**
 * The topology modeler has one store for all data.
 */
export interface IWineryState {
    topologyRendererState: TopologyRendererState;
    wineryState: WineryState;
    liveModelingState: LiveModelingState;
}

export const INITIAL_IWINERY_STATE: IWineryState = {
    topologyRendererState: INITIAL_TOPOLOGY_RENDERER_STATE,
    wineryState: INITIAL_WINERY_STATE,
    liveModelingState: INITIAL_LIVE_MODELING_STATE
};

export const rootReducer: Reducer<IWineryState> = combineReducers<IWineryState>({
    topologyRendererState: TopologyRendererReducer,
    wineryState: WineryReducer,
    liveModelingState: LiveModelingReducer
});
