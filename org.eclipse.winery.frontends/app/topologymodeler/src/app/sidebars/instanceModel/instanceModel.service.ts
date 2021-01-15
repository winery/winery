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
import { BackendService } from '../../services/backend.service';
import { AbstractRefinementWebSocketService } from '../refinement/abstractRefinementWebSocket.service';
import { Observable } from 'rxjs/Rx';
import { RefinementTasks } from '../refinement/refinementWebSocket.service';

export interface SubGraphData {
    id: string;
    nodeIdsToBeReplaced: string[];
}

export interface InstanceModelPlugin {
    id: string;
    subGraphs: SubGraphData[];
}

export interface InstanceModelReceiveData {
    plugins: InstanceModelPlugin[];
}

export interface SendData {
    task?: RefinementTasks;
    pluginId: string;
    matchId: string;
    userInputs: any;
}

@Injectable()
export class InstanceModelService extends AbstractRefinementWebSocketService<InstanceModelReceiveData> {

    constructor(bs: BackendService) {
        super(bs);
    }

    start(): Observable<InstanceModelReceiveData> {
        return this.startRefinementSocket('/refineInstanceModel');
    }

    send(data: SendData) {
        data.task = RefinementTasks.APPLY_PLUGIN;
        this.socket.send(JSON.stringify(data));
    }
}
