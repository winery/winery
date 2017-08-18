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

import { Node } from '../model/workflow/node';

/**
 * NodeService
 * provides all operations about workflow node.
 */
@Injectable()
export class NodeService {
    public addConnection(node: Node, targetId: string) {
        if (!node.connection.includes(targetId)) {
            node.connection.push(targetId);
        }
    }

    public deleteConnection(node: Node, targetId: string) {
        const index = node.connection.findIndex(target => target === targetId);
        if (index !== -1) {
            node.connection.splice(index, 1);
        }
    }
}
