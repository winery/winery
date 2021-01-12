/********************************************************************************
 * Copyright (c) 2018-2019 Contributors to the Eclipse Foundation
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
import { BehaviorSubject } from 'rxjs';
import { BackendService } from '../../services/backend.service';
import { TTopologyTemplate } from '../../models/ttopology-template';
import { Observable } from 'rxjs/Rx';

export enum RefinementTasks {
    START = 'START',
    REFINE_WITH = 'REFINE_WITH',
    STOP = 'STOP'
}

export interface RefinementWebSocketData {
    task: RefinementTasks;
    refineWith?: number;
    serviceTemplate?: string;
}

export abstract class AbstractRefinementWebSocketService<T> {

    protected socket: WebSocket;
    protected listener: BehaviorSubject<T>;

    protected constructor(protected backendService: BackendService) {
    }

    protected startRefinementSocket(endpoint: string): Observable<T> {
        this.socket = new WebSocket(this.backendService.configuration.webSocketUrl + endpoint);
        this.listener = new BehaviorSubject<T>(null);

        const start: RefinementWebSocketData = {
            task: RefinementTasks.START,
            serviceTemplate: this.backendService.configuration.definitionsElement.qName
        };

        this.socket.onmessage = event => this.onMessage(event);
        this.socket.onclose = event => this.onClose(event);
        this.socket.onopen = event => this.socket.send(JSON.stringify(start));

        return this.listener.asObservable();
    }

    private onMessage(event: MessageEvent) {
        if (event.data) {
            const data: T = JSON.parse(event.data);
            this.listener.next(data);
        }
    }

    private onClose(event: CloseEvent) {
        this.listener.complete();
    }

    cancel() {
        if (this.socket && this.socket.readyState !== this.socket.CLOSING && this.socket.readyState !== this.socket.CLOSED) {
            this.socket.send(JSON.stringify({ task: RefinementTasks.STOP }));
        }
    }
}
