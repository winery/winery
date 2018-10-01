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
import { AccountabilityService } from './accountability.service';
import { SelectData } from '../../../model/selectData';
import { isNullOrUndefined } from 'util';
import { HttpErrorResponse } from '@angular/common/http';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { Component, OnInit } from '@angular/core';
import { AuthorizationElement } from '../../../model/provenance';

export class AccountabilityParentComponent implements OnInit {
    selectedProvenanceId: SelectData;
    serviceTemplateList: SelectData[];
    loading = true;
    error: string;
    participant: AuthorizationElement = {identity: '', address: '', transactionHash: '', unixTimestamp: 0};
    constructor (protected service: AccountabilityService, protected notify: WineryNotificationService ) {
    }

    ngOnInit(): void {
        this.error = null;
        this.service.getServiceTemplates()
            .subscribe(
                data => this.handleData(data),
                error => this.handleError(error)
            );
    }

    provenanceIdSelected(event: SelectData) {
        this.selectedProvenanceId = event;
    }

    handleData(data: SelectData[]) {
        if (!isNullOrUndefined(data) && data.length > 0) {
            this.serviceTemplateList = data;
            this.selectedProvenanceId = data[0].children[0];
        } else {
            this.error = 'No service templates available!';
        }
        this.loading = false;
    }

    handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.notify.error(error.error);
    }
}
