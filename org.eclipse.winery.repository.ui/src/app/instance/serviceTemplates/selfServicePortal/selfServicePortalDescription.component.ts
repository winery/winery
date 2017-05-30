/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */
import { Component, OnInit } from '@angular/core';
import { SelfServiceApiData, SelfServicePortalService } from './selfServicePortal.service';
import { isNullOrUndefined } from 'util';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';

@Component({
    selector: 'winery-self-service-portal-description',
    templateUrl: 'selfServicePortalDescription.component.html'
})
export class SelfServiceDescriptionComponent implements OnInit {

    data: SelfServiceApiData;
    loading = true;

    constructor(private service: SelfServicePortalService,
                private notify: WineryNotificationService) {

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
            error => this.notify.error(error.toString())
        );
    }

    save() {
        this.service.saveName(this.data.displayName).subscribe(
            dataName => {
                this.handleSuccess('Saved name');
                this.service.saveDescription(this.data.description).subscribe(
                    () => this.handleSuccess('Saved description'),
                    error => this.handleError(error.toString())
                );
            },
            error => this.handleError(error.toString())
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

    handleError(error: Error) {
        this.notify.error(error.toString());
    }

}
