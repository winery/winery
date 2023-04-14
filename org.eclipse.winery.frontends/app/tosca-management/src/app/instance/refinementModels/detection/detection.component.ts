/*******************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
import { DetectionModel, DetectionService } from './detection.service';
import { InstanceService } from '../../instance.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';

@Component({
    templateUrl: 'detection.component.html',
    providers: [
        DetectionService
    ]
})
export class DetectionComponent implements OnInit {

    detectionModel: DetectionModel = { isPdrm: false };
    loading = true;

    constructor(private service: DetectionService,
                private sharedData: InstanceService,
                private notify: WineryNotificationService) {
    }

    ngOnInit(): void {
        this.service.getDetectionModel()
            .subscribe(data => {
                    this.detectionModel = data;
                    this.loading = false;
                },
                error => {
                    this.notify.error(error.message);
                    this.loading = false;
                });
    }

    public saveDetectionModel() {
        this.loading = true;
        this.service.setDetectionModel(this.detectionModel)
            .subscribe(
                data => {
                    this.notify.success('Saved successfully');
                    this.loading = false;
                },
                error => {
                    this.notify.error(error.message);
                    this.loading = false;
                }
            );
    }
}
