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
import {Component, OnInit, ViewChild} from '@angular/core';
import {PropertiesService} from './properties.service';
import {WineryNotificationService} from '../../../wineryNotificationModule/wineryNotification.service';
import {isNullOrUndefined} from 'util';
import {WineryEditorComponent} from '../../../wineryEditorModule/wineryEditor.component';
import {InstanceService} from '../../instance.service';

@Component({
    selector: 'winery-properties',
    templateUrl: 'properties.component.html',
    styleUrls: [
        'properties.component.css'
    ],
    providers: [
        PropertiesService
    ]
})
export class PropertiesComponent implements OnInit {

    /**
     * Why `any`? => see {@link PropertiesService.getProperties()}
     */
    properties: any = null;
    propertyKeys: string[] = [];
    isXMLData: boolean;
    loading = true;
    @ViewChild('propertiesEditor') propertiesEditor: WineryEditorComponent;

    constructor(private service: PropertiesService, private notify: WineryNotificationService,
                public sharedData: InstanceService) {
    }

    ngOnInit() {
        this.getProperties();
    }

    save() {
        this.loading = true;
        if (this.isXMLData) {
            this.properties = this.propertiesEditor.getData();
        }
        this.service.saveProperties(this.properties, this.isXMLData)
            .subscribe(
                data => this.handleSave(),
                error => this.handleError(error)
            );
    }

    private getProperties() {
        this.service.getProperties()
            .subscribe(
                data => this.handleProperties(data),
                error => this.loading = false
            );
    }

    private handleSave() {
        this.notify.success('Successfully updated properties!');
        this.getProperties();
    }

    private handleProperties(data: any) {
        this.loading = false;
        if (data.isXML) {
            this.isXMLData = true;
            this.properties = data.properties;
        } else {
            this.isXMLData = false;
            if (!isNullOrUndefined(data.properties)) {
                this.propertyKeys = Object.keys(data.properties);
                // this.propertyKeys.sort();
            }
            if (this.propertyKeys.length > 0) {
                this.properties = data.properties;
                console.log('this.properties: ', this.properties);
                console.log('this.propertykeys: ', this.propertyKeys);
            }
        }
    }

    private handleError(error: any) {
        this.loading = false;
        this.notify.error(error);
    }
}
