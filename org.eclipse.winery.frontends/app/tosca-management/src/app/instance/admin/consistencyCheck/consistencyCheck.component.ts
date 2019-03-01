/********************************************************************************
 * Copyright (c)  Contributors to the Eclipse Foundation
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
import {Component} from '@angular/core';
import {ConsistencyCheckConfiguration, ConsistencyCheckService, ConsistencyUpdate} from './consistencyCheck.service';
import {WineryNotificationService} from '../../../wineryNotificationModule/wineryNotification.service';
import {isNullOrUndefined} from 'util';
import { KeyValueItem } from '../../../model/keyValueItem';

@Component({
    selector: 'winery-consistency-check',
    templateUrl: 'consistencyCheck.component.html',
    providers: [
        ConsistencyCheckService
    ],
    styleUrls: [
        'consistencyCheck.component.css'
    ]
})

export class ConsistencyCheckComponent {

    configuration: ConsistencyCheckConfiguration = {
        serviceTemplatesOnly: false,
        checkDocumentation: false
    };
    data: ConsistencyUpdate;

    loading = false;
    progress: number;

    errorList: KeyValueItem[] = null;

    constructor(private service: ConsistencyCheckService,
                private notify: WineryNotificationService) {
    }

    startWebSocket() {
        this.loading = true;
        this.progress = 0;
        this.service.checkConsistencyUsingWebSocket(this.configuration).subscribe(
            data => this.onData(data),
            error => this.onError(error),
            () => this.onComplete()
        );
    }

    onData(data: ConsistencyUpdate) {
        if (!isNullOrUndefined(data)) {
            this.data = data;

            if (!isNullOrUndefined(data.progress)) {
                this.progress = Math.round(data.progress * 100);
            }
        }
    }

    onComplete() {
        this.loading = false;
        this.errorList = this.data.errorList;
    }

    onError(error: any) {
        if (error.reason) {
            this.notify.error(error.reason);
        } else {
            this.notify.error(error.message, error.name);
        }
        this.loading = false;
    }
}
