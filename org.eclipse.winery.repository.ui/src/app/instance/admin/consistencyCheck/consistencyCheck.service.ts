/********************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { webSocketURL } from '../../../configuration';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { isNullOrUndefined } from 'util';

@Injectable()
export class ConsistencyCheckService {

    private path: string;
    private socket: WebSocket;
    private listener: BehaviorSubject<ConsistencyUpdate>;
    private checkCompleted = false;

    constructor(private route: Router) {
        this.path = this.route.url;
    }

    checkConsistencyUsingWebSocket(config: ConsistencyCheckConfiguration): Observable<ConsistencyUpdate> {
        this.socket = new WebSocket(webSocketURL + '/checkconsistency');
        this.listener = new BehaviorSubject<ConsistencyUpdate>(null);
        this.checkCompleted = false;

        this.socket.onmessage = event => this.onMessage(event);
        this.socket.onclose = event => this.onClose(event);
        this.socket.onopen = event => this.socket.send(JSON.stringify(config));

        return this.listener.asObservable();
    }

    onMessage(event: MessageEvent) {
        if (!isNullOrUndefined(event.data)) {
            const data: ConsistencyUpdate = JSON.parse(event.data);

            if (!isNullOrUndefined(data.errorList)) {
                this.checkCompleted = true;
                const keys = Object.keys(data.errorList);
                const errorList = [];

                for (const key of keys) {
                    errorList.push({ key: key, value: data.errorList[key] });
                }

                data.errorList = errorList;

                this.listener.next(data);
                this.listener.complete();
            } else {
                this.listener.next(data);
            }
        }
    }

    onClose(event: CloseEvent) {
        this.socket.close();

        if (!this.checkCompleted) {
            this.listener.error(event);
        }
        this.listener.complete();
    }

}

export interface ConsistencyCheckConfiguration {
    serviceTemplatesOnly: boolean;
    checkDocumentation: boolean;
}

export interface ConsistencyUpdate {
    progress: number;
    currentlyChecking: string;
    errorList: any;
}
