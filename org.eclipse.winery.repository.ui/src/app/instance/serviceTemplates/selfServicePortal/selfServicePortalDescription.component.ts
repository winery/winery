/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
import { Component, OnInit } from '@angular/core';
import { SelfServiceApiData, SelfServicePortalService } from './selfServicePortal.service';
import { isNullOrUndefined } from 'util';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { InstanceService } from '../../instance.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'winery-self-service-portal-description',
    templateUrl: 'selfServicePortalDescription.component.html'
})
export class SelfServiceDescriptionComponent implements OnInit {

    data: SelfServiceApiData;
    loading = true;

    constructor(private service: SelfServicePortalService,
                private notify: WineryNotificationService,
                public sharedData: InstanceService) {

    }

    ngOnInit() {
        if (!isNullOrUndefined(this.service.selfServiceData)) {
            this.data = this.service.selfServiceData;
            this.loading = false;
        } else {
            this.getSelfServiceData();
        }
    }

    getSelfServiceData() {
        this.service.getSelfServiceData().subscribe(
            data => this.handleData(),
            error => {
                this.notify.error(error.toString());
                this.loading = false;
            });
    }

    save() {
        this.service.saveName(this.data.displayName).subscribe(
            dataName => {
                this.handleSuccess('Saved name');
                this.service.saveDescription(this.data.description).subscribe(
                    () => this.handleSuccess('Saved description'),
                    error => this.handleError(error)
                );
            },
            error => this.handleError(error)
        );
    }

    handleData() {
        this.data = this.service.selfServiceData;
        this.loading = false;
        // this.notify.success('');
    }

    handleSuccess(message: string) {
        this.notify.success(message);
    }

    handleError(error: HttpErrorResponse) {
        this.notify.error(error.message);
    }

}
