"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
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
var redux_1 = require("redux");
var app_reducer_1 = require("../reducers/app.reducer");
var topologyRenderer_reducer_1 = require("../reducers/topologyRenderer.reducer");
exports.INITIAL_IAPP_STATE = {
    /*
    paletteItem: INITIAL_PALETTE_ITEM_STATE,
    paletteOpened: INITIAL_PALETTE_OPENED_STATE,
    enhanceGrid: INITIAL_ENHANCE_GRID_STATE,
    */
    topologyRendererState: topologyRenderer_reducer_1.INITIAL_TOPOLOGY_RENDERER_STATE,
    appState: app_reducer_1.INITIAL_APP_STATE
};
exports.rootReducer = redux_1.combineReducers({
    /*
    paletteItem: PaletteItemReducer,
    paletteOpened: PaletteOpenedReducer,
    enhanceGrid: EnhanceGridReducer,
    */
    topologyRendererState: topologyRenderer_reducer_1.TopologyRendererReducer,
    appState: app_reducer_1.AppReducer
});
