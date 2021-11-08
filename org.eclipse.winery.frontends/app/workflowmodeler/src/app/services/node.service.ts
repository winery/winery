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
        const index = node.connection.findIndex((target: any) => target === targetId);
        if (index !== -1) {
            node.connection.splice(index, 1);
        }
    }
}
