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
import {Component, OnInit} from '@angular/core';
import {WineryLicenseService} from './wineryLicense.service';
import {WineryNotificationService} from '../wineryNotificationModule/wineryNotification.service';
import {InstanceService} from '../instance/instance.service';
import {ToscaTypes} from '../wineryInterfaces/enums';
import {LicenseEnum, WineryLicense} from './wineryLicense.enum';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    templateUrl: 'wineryLicense.component.html',
    styleUrls: ['wineryLicense.component.css'],
    providers: [WineryLicenseService]
})

export class WineryLicenseComponent implements OnInit {

    licenseText = '';
    intialLicenseText = '';
    licenseAvailable = true;
    licenseType = '';

    loading = true;
    options: any;
    isEditable = false;

    toscaType: ToscaTypes;

    constructor(private service: WineryLicenseService, private notify: WineryNotificationService, public sharedData: InstanceService) {
        this.toscaType = this.sharedData.toscaComponent.toscaType;
        this.options = Object.keys(LicenseEnum).map(key => LicenseEnum[key]);
    }

    ngOnInit() {
        this.service.getData().subscribe(
            data => {
                this.licenseText = data;
                this.intialLicenseText = data;
            },
            error => this.handleMissingLicense()
        );
    }

    saveLicenseFile() {
        this.service.save(this.licenseText).subscribe(
            data => this.handleSave(),
            error => this.handleError(error)
        );
    }

    dropdownAction(item: string) {
        this.licenseType = item;
        this.licenseText = WineryLicense.getLicense(this.licenseType);
    }

    cancelEdit() {
        this.licenseText = this.intialLicenseText;
        this.isEditable = false;
    }

    private handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.notify.error(error.message);
    }

    private handleMissingLicense() {
        this.loading = false;
        this.licenseAvailable = false;
    }

    private handleSave() {
        this.notify.success('Successfully saved LICENSE');
    }
}
