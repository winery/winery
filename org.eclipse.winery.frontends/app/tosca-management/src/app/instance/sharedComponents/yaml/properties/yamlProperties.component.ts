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

import { Component, OnInit, ViewChild } from '@angular/core';
import { WineryEditorComponent } from '../../../../wineryEditorModule/wineryEditor.component';
import { WineryNotificationService } from '../../../../wineryNotificationModule/wineryNotification.service';
import { InstanceService } from '../../../instance.service';
import { HttpErrorResponse } from '@angular/common/http';
import { YamlPropertiesService } from './yamlProperties.service';
import { YamlProperty } from './yamlProperty';

@Component({
    selector: 'winery-yaml-properties',
    templateUrl: 'yamlProperties.component.html',
    styleUrls: [
        'yamlProperties.component.css'
    ],
    providers: [
        YamlPropertiesService
    ]
})
export class YamlPropertiesComponent implements OnInit {

    properties: YamlProperty[] = [];
    loading = true;
    @ViewChild('propertiesEditor') propertiesEditor: WineryEditorComponent;

    constructor(private notify: WineryNotificationService, private service: YamlPropertiesService,
                public sharedData: InstanceService) {
    }

    ngOnInit() {
        this.getProperties();
    }

    save() {
        this.loading = true;
        this.service.saveProperties(this.properties)
            .subscribe(
                () => this.handleSave(),
                error => this.handleError(error)
            );
    }

    private getProperties() {
        this.service.getProperties()
            .subscribe(
                data => this.handleProperties(data),
                error => this.handleError(error)
            );
    }

    private handleSave() {
        this.notify.success('Successfully updated properties!');
        this.getProperties();
    }

    private handleProperties(data: YamlProperty[]) {
        this.loading = false;
        this.properties = data;
    }

    private handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.notify.error(error.message);
    }
}
