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


import { Component, OnInit } from '@angular/core';
import { InstanceComponent } from '../../../instance.component';
import { ActivatedRoute, Router } from '@angular/router';
import { InstanceService } from '../../../instance.service';
import { WineryNotificationService } from '../../../../wineryNotificationModule/wineryNotification.service';
import { ExistService } from '../../../../wineryUtils/existService';
import { WineryInstance, WineryTemplateOrImplementationComponent } from '../../../../model/wineryComponent';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'winery-datatype',
    templateUrl: 'dataType.component.html',
    styleUrls: [
        'dataType.component.css'
    ],
    providers: []
})
export class DataTypeComponent implements OnInit {

    component: object;
    loadingData = true;

    constructor(private notify: WineryNotificationService,
                public sharedData: InstanceService) {
    }

    ngOnInit(): void {
        this.loadDataType();
    }

    private loadDataType() {
        this.sharedData.getComponentData()
            .subscribe(
                data => this.handleDataInput(data),
                error => this.handleError(error)
            );
    }

    private handleDataInput(componentData: WineryInstance) {
        this.component = componentData.serviceTemplateOrNodeTypeOrNodeTypeImplementation[1];
        this.loadingData = false;
    }

    private handleError(error: HttpErrorResponse) {
        this.loadingData = false;
        this.notify.error(error.message);
    }
}

