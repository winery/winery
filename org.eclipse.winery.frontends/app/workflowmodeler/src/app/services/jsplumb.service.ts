/*******************************************************************************
 * Copyright (c) 2017-2019 Contributors to the Eclipse Foundation
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
import { jsPlumb } from 'jsplumb';

import { Node } from '../model/workflow/node';
import { BroadcastService } from './broadcast.service';
import { ModelService } from './model.service';

/**
 * JsPlumbService
 * provides all of the operations about jsplumb plugin.
 */
@Injectable()
export class JsPlumbService {

    public jsplumbInstance: any;

    constructor(private broadcastService: BroadcastService,
                private modelService: ModelService) {
    }

    public connectNode() {
        this.modelService.getNodes()
            .forEach(node => node.connection
                .forEach((target: any) => this.jsplumbInstance.connect({ source: node.id, target })));
    }

    public initJsPlumbInstance() {
        jsPlumb.getInstance().ready(() => {
            this.jsplumbInstance = jsPlumb.getInstance();

            this.jsplumbInstance.importDefaults({
                Anchor: ['Top', 'RightMiddle', 'LeftMiddle', 'Bottom'],
                Connector: [
                    'Flowchart',
                    { cornerRadius: 0, stub: 0, gap: 3 },
                ],
                ConnectionOverlays: [
                    [
                        'Arrow',
                        { direction: 1, foldback: 1, location: 1, width: 10, length: 10 },
                    ],
                    ['Label', { label: '', id: 'label', cssClass: 'aLabel' }],
                ],
                connectorPaintStyle: {
                    lineWidth: 2,
                },
                Endpoint: 'Blank',
                PaintStyle: { lineWidth: 1 },
            });

            this.broadcastService.broadcast(this.broadcastService.jsPlumbInstance,
                this.jsplumbInstance);

            // add connection to model data while a new connection is build
            this.jsplumbInstance.bind('connection', (info: any) => {
                this.modelService.addConnection(info.connection.sourceId, info.connection.targetId);

                info.connection.bind('click', (connection: any) => {
                    this.modelService.deleteConnection(connection.sourceId, connection.targetId);
                    this.jsplumbInstance.deleteConnection(connection);
                });
            });
        });
    }

    public initNode(node: Node) {

        this.jsplumbInstance.draggable(node.id, {
            stop(event: any) {
                node.position.left = event.pos[0];
                node.position.top = event.pos[1];
            },
        });

        this.jsplumbInstance.makeTarget(node.id, {
            detachable: false,
            isTarget: true,
            maxConnections: -1,
        });

        this.jsplumbInstance.makeSource(node.id, {
            filter: '.anchor, .anchor *',
            detachable: false,
            isSource: true,
            maxConnections: -1,
        });

    }

    public buttonDraggable() {
        const selector = this.jsplumbInstance.getSelector('.toolbar .item');
        this.jsplumbInstance.draggable(selector,
            {
                scope: 'btn',
                clone: true,
            });
    }

    public buttonDroppable() {
        const selector = this.jsplumbInstance.getSelector('.canvas');
        this.jsplumbInstance.droppable(selector, {
            scope: 'btn',
            drop: (event: any) => {
                const el = this.jsplumbInstance.getSelector(event.drag.el);
                const type = el.attributes.nodeType.value;
                const left = event.e.clientX - event.drop.position[0];
                const top = event.e.clientY - event.drop.position[1];

                this.modelService.addNode(type, type, left, top);
            },
        });
    }

    public remove(nodeId: string) {
        this.jsplumbInstance.remove(nodeId);
    }

}
