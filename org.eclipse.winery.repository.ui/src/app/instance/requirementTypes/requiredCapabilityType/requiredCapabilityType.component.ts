/*******************************************************************************
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
 *******************************************************************************/
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
