/**
 * Copyright (c) 2017 ZTE Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     ZTE - initial API and implementation and/or initial documentation
 */

import { Injectable } from '@angular/core';
import { jsPlumb } from 'jsplumb/dist/js/jsplumb.js';

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
                .forEach(target => this.jsplumbInstance.connect({ source: node.id, target })));
    }

    public initJsPlumbInstance() {
        console.log('init jsplumb instance start');

        jsPlumb.ready(() => {
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
            this.jsplumbInstance.bind('connection', info => {
                this.modelService.addConnection(info.connection.sourceId, info.connection.targetId);

                info.connection.bind('click', connection => {
                    this.modelService.deleteConnection(connection.sourceId, connection.targetId);
                    jsPlumb.detach(connection);
                });
            });
        });
    }

    public initNode(node: Node) {

        this.jsplumbInstance.draggable(node.id, {
            stop(event) {
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
            drop: event => {
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
