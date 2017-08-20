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
import { Action, ActionCreator } from 'redux';
import { Injectable } from '@angular/core';
import {TNodeTemplate, TRelationshipTemplate} from '../../ttopology-template';

export interface SendPaletteOpenedAction extends Action {
  paletteOpened: boolean;
}

export interface SaveNodeTemplateAction extends Action {
  nodeTemplate: TNodeTemplate;
}

export interface SaveRelationshipAction extends Action {
  relationshipTemplate: TRelationshipTemplate;
}

@Injectable()
export class AppActions {
    static SEND_PALETTE_OPENED = 'SEND_PALETTE_OPENED';
    static SAVE_NODE_TEMPLATE = 'SAVE_NODE_TEMPLATE';
    static SAVE_RELATIONSHIP = 'SAVE_RELATIONSHIP';

    sendPaletteOpened: ActionCreator<SendPaletteOpenedAction> =
      ((paletteOpened) => ({
        type: AppActions.SEND_PALETTE_OPENED,
        paletteOpened: paletteOpened
      }));
    saveNodeTemplate: ActionCreator<SaveNodeTemplateAction> =
      ((newNode) => ({
        type: AppActions.SAVE_NODE_TEMPLATE,
        nodeTemplate: newNode
      }));
    saveRelationship: ActionCreator<SaveRelationshipAction> =
      ((newRelationship) => ({
        type: AppActions.SAVE_RELATIONSHIP,
        relationshipTemplate: newRelationship
      }));
}
