/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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
import { PaletteComponent } from '../palette/palette.component';
import { EntityType, TNodeTemplate, TRelationshipTemplate } from '../models/ttopology-template';
import { CanvasComponent } from '../canvas/canvas.component';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { NodeTypeModel } from '../models/groupedNodeTypeModel';

@Injectable()
export class ManageTopologyService {

    private paletteComponent: PaletteComponent;
    private canvasComponent: CanvasComponent;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private actions: TopologyRendererActions) {
    }

    subscribePalette(palette: PaletteComponent) {
        this.paletteComponent = palette;
    }

    subscribeCanvas(canvas: CanvasComponent) {
        this.canvasComponent = canvas;
    }

    newNode(x: number, y: number, text: string): TNodeTemplate {
        let node: NodeTypeModel = null;
        // searching the NodeTypeModel by text in the paletteComponent.entityTypes.groupedNodeTypes
        for ( const ns of this.paletteComponent.entityTypes.groupedNodeTypes) {
            for ( const child of ns.children) {
                if (child.text === text) {
                    node = child;
                }
            }
        }
        return this.paletteComponent.generateNewNode(x, y, node);
    }

    newRelationship(fromNodeId: string, toNodeId: string, currentType: string): TRelationshipTemplate {
        let relationType: EntityType = null;
        this.canvasComponent.entityTypes.relationshipTypes.some(relType => {
            if (relType.qName.includes(currentType)) {
                relationType = relType;
                return true;
            }
        });
        if (relationType != null) {
            return this.canvasComponent.generateNewRelationship(fromNodeId, toNodeId, relationType);
        } else {
            return null;
        }
    }

    deleteNode(nodeId: string) {
        this.ngRedux.dispatch(this.actions.highlightNodes([]));
        this.ngRedux.dispatch((this.actions.highlightNodes([nodeId])));

        this.canvasComponent.handleDeleteKeyEvent();
    }
}
