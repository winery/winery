/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Component, OnInit } from '@angular/core';
import { WineryLicenseService } from './wineryLicense.service';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { InstanceService } from '../instance/instance.service';
import { ToscaTypes } from '../wineryInterfaces/enums';
import { LicenseEnum, WineryLicense } from './wineryLicense.enum';

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

    constructor(private service: WineryLicenseService, private notify: WineryNotificationService, private sharedData: InstanceService) {
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

    private handleError(error: any) {
        this.loading = false;
        this.notify.error(error);
    }

    private handleMissingLicense() {
        this.loading = false;
        this.licenseAvailable = false;
    }

    private handleSave() {
        this.notify.success('Successfully saved LICENSE');
    }
}
