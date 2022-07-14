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
import { BehaviorSubject } from 'rxjs';
import { BackendService } from '../../services/backend.service';
import { Observable } from 'rxjs/Rx';

export enum RefinementTasks {
    START = 'START',
    REFINE_WITH = 'REFINE_WITH',
    APPLY_PLUGIN = 'APPLY_PLUGIN',
    STOP = 'STOP'
}

export interface RefinementWebSocketData {
    task: RefinementTasks;
    refineWith?: number;
    serviceTemplate?: string;
    selectedNodeTemplateIds?: string[];
}

export abstract class AbstractRefinementWebSocketService<T> {

    protected socket: WebSocket;
    protected listener: BehaviorSubject<T>;
    private observable: Observable<T>;

    protected constructor(protected backendService: BackendService) {
    }

    cancel() {
        if (this.socket && this.socket.readyState !== this.socket.CLOSING && this.socket.readyState !== this.socket.CLOSED) {
            this.socket.send(JSON.stringify({ task: RefinementTasks.STOP }));
        }
    }

    protected startRefinementSocket(endpoint: string, subgraphDetector?: string[]): Observable<T> {
        const start: RefinementWebSocketData = {
            task: RefinementTasks.START,
            serviceTemplate: this.backendService.configuration.definitionsElement.qName
        };

        if (subgraphDetector) {
            start.selectedNodeTemplateIds = subgraphDetector;
        }
        
        if (!this.socket) {
            this.socket = new WebSocket(this.backendService.configuration.webSocketUrl + endpoint);
            this.listener = new BehaviorSubject<T>(null);


            this.socket.onmessage = event => this.onMessage(event);
            this.socket.onclose = event => this.onClose(event);
            this.socket.onopen = () => this.socket.send(JSON.stringify(start));
            this.observable = this.listener.asObservable();
        } else {
            this.socket.send(JSON.stringify(start));
        }
        
        return this.observable;
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
}
