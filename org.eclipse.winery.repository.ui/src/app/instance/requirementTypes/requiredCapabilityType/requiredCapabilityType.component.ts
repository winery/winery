/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */
import { Component, OnInit } from '@angular/core';
import { RequiredCapabilityTypeService } from './requiredCapabilityType.service';
import { RequiredCapabilityTypeApiData } from './requiredCapabilityTypeApiData';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';

@Component({
    templateUrl: 'requiredCapabilityType.component.html',
    providers: [
        RequiredCapabilityTypeService
    ]
})
export class RequiredCapabilityTypeComponent implements OnInit {

    loading = true;
    selectedCapType: string;
    requiredCapTypeData: RequiredCapabilityTypeApiData;

    constructor(private notify: WineryNotificationService, private service: RequiredCapabilityTypeService) {
    }

    ngOnInit() {
        this.service.getRequiredCapabilityTypeData()
            .subscribe(
                data => this.handleData(data),
                error => this.notify.error(error)
            );
    }

    changedCapType(event: any) {
        this.selectedCapType = event.value;
    }

    save() {
        if (this.selectedCapType === '(none)') {
            this.service.delete()
                .subscribe(
                    () => this.notify.success('Successfully removed required Capability-Type!'),
                    error => this.notify.error(error)
                );
        } else {
            this.service.save(this.selectedCapType)
                .subscribe(
                    () => this.notify.success('Successfully saved required Capability-Type!'),
                    error => this.notify.error(error)
                );
        }
    }

    private handleData(data: RequiredCapabilityTypeApiData) {
        this.loading = false;
        this.requiredCapTypeData = data;
    }
}
