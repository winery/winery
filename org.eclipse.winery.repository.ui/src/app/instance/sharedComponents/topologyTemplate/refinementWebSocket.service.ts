/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
import { webSocketURL } from '../../../configuration';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { InstanceService } from '../../instance.service';

export enum RefinementTasks {
    START = 'START',
    REFINE_WITH = 'REFINE_WITH',
    STOP = 'STOP'
}

export interface RefinementElement {
    patternRefinementModel: {
        name: string;
        targetNamespace: string;
    };
    id: number;
    xmlId?: {
        decoded: string;
    };
    namespace?: {
        decoded: string
    };
}

export interface RefinementWebSocketData {
    task: RefinementTasks;
    refineWith?: number;
    serviceTemplate?: string;
}

@Injectable()
export class RefinementWebSocketService {

    private socket: WebSocket;
    private listener: BehaviorSubject<RefinementElement[]>;

    constructor(private sharedData: InstanceService) {
    }

    startRefinement() {
        this.socket = new WebSocket(webSocketURL + '/checkconsistency');
        this.listener = new BehaviorSubject<RefinementElement[]>(null);

        const start: RefinementWebSocketData = {
            task: RefinementTasks.START,
            serviceTemplate: this.sharedData.toscaComponent.getQName()
        };

        this.socket.onmessage = event => this.onMessage(event);
        this.socket.onclose = event => this.onClose(event);
        this.socket.onopen = event => this.socket.send(JSON.stringify(start));

        return this.listener.asObservable();
    }

    refineWith(option: RefinementElement) {
        const update: RefinementWebSocketData = {
            task: RefinementTasks.REFINE_WITH,
            refineWith: option.id
        };
        this.socket.send(JSON.stringify(update));
    }

    private onMessage(event: MessageEvent) {
        if (event.data) {
            const data: RefinementElement[] = JSON.parse(event.data);
            this.listener.next(data);
        }
    }

    private onClose(event: CloseEvent) {
        this.socket.close();
        this.listener.complete();
    }

    cancel() {
        this.socket.send(JSON.stringify({ task: RefinementTasks.STOP }));
    }
}
