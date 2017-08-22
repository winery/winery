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
import { isNullOrUndefined } from 'util';

import { Node } from '../model/workflow/node';
import { BroadcastService } from './broadcast.service';
import { NodeService } from './node.service';

/**
 * ModelService
 * provides all operations about plan model.
 */
@Injectable()
export class ModelService {
    private nodes: Node[] = [];

    constructor(private broadcastService: BroadcastService, private nodeService: NodeService) {
        this.broadcastService.planModel$.subscribe(planNodes => this.nodes = planNodes);
    }

    public getNodes(): Node[] {
        return this.nodes;
    }

    public addNode(name: string, type: string, left: number, top: number) {
        const node = new Node();
        node.id = this.createId();
        node.name = name;
        node.type = type;
        node.position.left = left;
        node.position.top = top;

        this.nodes.push(node);
    }

    public deleteNode(nodeId: string) {
        // delete related connections
        this.nodes.forEach(node => this.nodeService.deleteConnection(node, nodeId));

        // delete current node
        const index = this.nodes.findIndex(node => node.id === nodeId);
        if (index !== -1) {
            this.nodes.splice(index, 1);
        }
    }

    public addConnection(sourceId: string, targetId: string) {
        const node = this.nodes.find(tmpNode => tmpNode.id === sourceId);
        if (!isNullOrUndefined(node)) {
            this.nodeService.addConnection(node, targetId);
        }
    }

    public deleteConnection(sourceId: string, targetId: string) {
        const node = this.nodes.find(tmpNode => tmpNode.id === sourceId);
        if (!isNullOrUndefined(node)) {
            this.nodeService.deleteConnection(node, targetId);
        }
    }

    public save() {
        this.broadcastService.broadcast(this.broadcastService.saveEvent, JSON.stringify(this.nodes));
    }

    private createId() {
        const idSet = new Set();
        this.nodes.forEach(node => idSet.add(node.id));

        for (let i = 0; i < idSet.size; i++) {
            if (!idSet.has('node' + i)) {
                return 'node' + i;
            }
        }

        return 'node' + idSet.size;
    }
}
