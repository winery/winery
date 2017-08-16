/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Thommy Zelenik - initial API and implementation
 */
import {combineReducers, Reducer} from 'redux';
import {
  AppReducer,
  AppState,
  INITIAL_APP_STATE
} from '../reducers/app.reducer';
import {
  INITIAL_TOPOLOGY_RENDERER_STATE, TopologyRendererReducer,
  TopologyRendererState
} from '../reducers/topologyRenderer.reducer';

export interface IAppState {
  /*
  paletteItem: PaletteItemState;
  paletteOpened: PaletteOpenedState;
  enhanceGrid: EnhanceGridState;
  */
  topologyRendererState: TopologyRendererState;
  appState: AppState;
}

export const INITIAL_IAPP_STATE: IAppState = {
  /*
  paletteItem: INITIAL_PALETTE_ITEM_STATE,
  paletteOpened: INITIAL_PALETTE_OPENED_STATE,
  enhanceGrid: INITIAL_ENHANCE_GRID_STATE,
  */
  topologyRendererState: INITIAL_TOPOLOGY_RENDERER_STATE,
  appState: INITIAL_APP_STATE
};

export const rootReducer: Reducer<IAppState> = combineReducers<IAppState>({
  /*
  paletteItem: PaletteItemReducer,
  paletteOpened: PaletteOpenedReducer,
  enhanceGrid: EnhanceGridReducer,
  */
  topologyRendererState: TopologyRendererReducer,
  appState: AppReducer
});
