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

export enum RefinementTasks {
    START = 'START',
    REFINE_WITH = 'REFINE_WITH',
    STOP = 'STOP'
}

export interface RefinementElement {
    refinementCandidates: PatternRefinementModel[];
    serviceTemplateContainingRefinements: {
        xmlId?: {
            decoded: string;
        };
        namespace?: {
            decoded: string
        };
    };
    currentTopology: TTopologyTemplate;
}

export interface PatternRefinementModel {
    id: number;
    nodeIdsToBeReplaced: string[];
    refinementModel: {
        name: string;
        targetNamespace: string;
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
    private listener: BehaviorSubject<RefinementElement>;

    constructor(private backendService: BackendService) {
    }

    startRefinement(refinementType: string) {
        this.socket = new WebSocket(this.backendService.configuration.webSocketUrl + '/refinetopology?type=' + refinementType);
        this.listener = new BehaviorSubject<RefinementElement>(null);

        const start: RefinementWebSocketData = {
            task: RefinementTasks.START,
            serviceTemplate: this.backendService.configuration.definitionsElement.qName
        };

        this.socket.onmessage = event => this.onMessage(event);
        this.socket.onclose = event => this.onClose(event);
        this.socket.onopen = event => this.socket.send(JSON.stringify(start));

        return this.listener.asObservable();
    }

    refineWith(option: PatternRefinementModel) {
        const update: RefinementWebSocketData = {
            task: RefinementTasks.REFINE_WITH,
            refineWith: option.id
        };
        this.socket.send(JSON.stringify(update));
    }

    private onMessage(event: MessageEvent) {
        if (event.data) {
            const data: RefinementElement = JSON.parse(event.data);
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
