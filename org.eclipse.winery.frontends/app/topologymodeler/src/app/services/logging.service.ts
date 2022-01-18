/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
import { ReplaySubject } from 'rxjs';
import { LiveModelingLog } from '../models/liveModelingLog';
import { LiveModelingLogTypes } from '../models/enums';

@Injectable()
export class LoggingService {
    private logsSubject = new ReplaySubject<LiveModelingLog[]>();
    private logs: LiveModelingLog[];

    constructor() {
        this.logs = new Array<LiveModelingLog>();
    }

    get logStream() {
        return this.logsSubject.asObservable();
    }

    public clearLogs() {
        this.logs = new Array<LiveModelingLog>();
        this.logsSubject.next(this.logs);
    }

    public logInfo(message: string) {
        this.logs = [...this.logs, new LiveModelingLog(message, LiveModelingLogTypes.INFO)];
        this.logsSubject.next(this.logs);
    }

    public logWarning(message: string) {
        this.logs = [...this.logs, new LiveModelingLog(message, LiveModelingLogTypes.WARNING)];
        this.logsSubject.next(this.logs);
    }

    public logError(message: string) {
        this.logs = [...this.logs, new LiveModelingLog(message, LiveModelingLogTypes.DANGER)];
        this.logsSubject.next(this.logs);
    }

    public logSuccess(message: string) {
        this.logs = [...this.logs, new LiveModelingLog(message, LiveModelingLogTypes.SUCCESS)];
        this.logsSubject.next(this.logs);
    }

    public logContainer(message: string) {
        this.logs = [...this.logs, new LiveModelingLog(message, LiveModelingLogTypes.CONTAINER)];
        this.logsSubject.next(this.logs);
    }
}
