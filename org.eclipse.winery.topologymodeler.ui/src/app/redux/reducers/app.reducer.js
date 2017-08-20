"use strict";
var __assign = (this && this.__assign) || Object.assign || function(t) {
    for (var s, i = 1, n = arguments.length; i < n; i++) {
        s = arguments[i];
        for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
            t[p] = s[p];
    }
    return t;
};
Object.defineProperty(exports, "__esModule", { value: true });
var app_actions_1 = require("../actions/app.actions");
var ttopology_template_1 = require("app/ttopology-template");
exports.INITIAL_APP_STATE = {
    currentPaletteOpenedState: false,
    currentEnhancedGridState: true,
    currentSavedJsonTopology: new ttopology_template_1.TTopologyTemplate
};
exports.AppReducer = function (lastState, action) {
    if (lastState === void 0) { lastState = exports.INITIAL_APP_STATE; }
    switch (action.type) {
        case app_actions_1.AppActions.SEND_PALETTEOPENED:
            var paletteOpened = action.paletteOpened;
            return __assign({}, lastState, { currentPaletteOpenedState: paletteOpened });
        case app_actions_1.AppActions.ENHANCE_GRID:
            var enhancedGrid = action.enhancedGrid;
            return __assign({}, lastState, { currentEnhancedGridState: enhancedGrid });
        case app_actions_1.AppActions.SAVE_NODE_TEMPLATE:
            var newNode = action.nodeTemplate;
            return __assign({}, lastState, { currentSavedJsonTopology: {
                    nodeTemplates: lastState.currentSavedJsonTopology.nodeTemplates.concat([newNode]),
                    relationshipTemplates: lastState.currentSavedJsonTopology.relationshipTemplates
                } });
        case app_actions_1.AppActions.SAVE_RELATIONSHIP:
            var newRelationship = action.relationshipTemplate;
            return __assign({}, lastState, { currentSavedJsonTopology: {
                    nodeTemplates: lastState.currentSavedJsonTopology.nodeTemplates,
                    relationshipTemplates: lastState.currentSavedJsonTopology.relationshipTemplates.concat([newRelationship])
                } });
        default:
            return lastState;
    }
};
/*
export const PaletteItemReducer =
  function (lastState: PaletteItemState = INITIAL_PALETTE_ITEM_STATE, action: Action): PaletteItemState {
    switch (action.type) {
      case PaletteActions.CREATE_PALETTEITEM:
        const paletteItem: PaletteItemModel = (<CreatePaletteItemAction>action).paletteItem;
        return {
          currentPaletteItemState: paletteItem
        };
      default:
        return lastState;
    }
  };

export const PaletteOpenedReducer =
  function (lastState: PaletteOpenedState = INITIAL_PALETTE_OPENED_STATE, action: Action): PaletteOpenedState {
    switch (action.type) {
      case PaletteActions.SEND_PALETTEOPENED:
        const paletteOpened: boolean = (<SendPaletteOpenedAction>action).paletteOpened;
        return {
          currentPaletteOpenedState: paletteOpened
    };
      default:
        return lastState;
    }
  };

export const EnhanceGridReducer =
  function (lastState: EnhanceGridState = INITIAL_ENHANCE_GRID_STATE, action: Action): EnhanceGridState {
    switch (action.type) {
      case PaletteActions.ENHANCE_GRID:
        const enhanceGrid: boolean = (<EnhanceGridAction>action).enhanceGrid;
        return {
          currentEnhanceGridState: enhanceGrid
        };
      default:
        return lastState;
    }
  };
  */
