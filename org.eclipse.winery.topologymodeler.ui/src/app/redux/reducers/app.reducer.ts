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
import { Action } from 'redux';
import {
  AppActions, EnhanceGridAction, SaveNodeTemplateAction, SaveRelationshipAction,
  SendPaletteOpenedAction,
} from '../actions/app.actions';
import {TNodeTemplate, TRelationshipTemplate, TTopologyTemplate} from 'app/ttopology-template';

export interface AppState {
  currentPaletteOpenedState: boolean;
  currentEnhancedGridState: boolean;
  currentSavedJsonTopology: TTopologyTemplate;
}

export const INITIAL_APP_STATE: AppState = {
  currentPaletteOpenedState: false,
  currentEnhancedGridState: true,
  currentSavedJsonTopology: new TTopologyTemplate
};

export const AppReducer =
  function (lastState: AppState = INITIAL_APP_STATE, action: Action): AppState {
    switch (action.type) {
      case AppActions.SEND_PALETTEOPENED:
        const paletteOpened: boolean = (<SendPaletteOpenedAction>action).paletteOpened;
        return {
          ...lastState,
          currentPaletteOpenedState: paletteOpened
        };
      case AppActions.ENHANCE_GRID:
        const enhancedGrid: boolean = (<EnhanceGridAction>action).enhancedGrid;
        return {
          ...lastState,
          currentEnhancedGridState: enhancedGrid
        };
      case AppActions.SAVE_NODE_TEMPLATE:
        const newNode: TNodeTemplate = (<SaveNodeTemplateAction>action).nodeTemplate;
        return {
          ...lastState,
          currentSavedJsonTopology: {
            nodeTemplates: [...lastState.currentSavedJsonTopology.nodeTemplates, newNode],
            relationshipTemplates: lastState.currentSavedJsonTopology.relationshipTemplates
          }
        };
      case AppActions.SAVE_RELATIONSHIP:
        const newRelationship: TRelationshipTemplate = (<SaveRelationshipAction>action).relationshipTemplate;
        return {
          ...lastState,
          currentSavedJsonTopology: {
            nodeTemplates: lastState.currentSavedJsonTopology.nodeTemplates,
            relationshipTemplates: [...lastState.currentSavedJsonTopology.relationshipTemplates, newRelationship]
          }
        };
      default:
        return lastState;
    }
    };
