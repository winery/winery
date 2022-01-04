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
import { Component, OnInit, ViewChild } from '@angular/core';
import { PropertiesService } from './properties.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { WineryEditorComponent } from '../../../wineryEditorModule/wineryEditor.component';
import { InstanceService } from '../../instance.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Properties, PropertiesData } from './properties.types';
import { PropertiesDefinitionService } from '../propertiesDefinition/propertiesDefinition.service';
import {
    PropertiesDefinitionKVElement, PropertiesDefinitionsResourceApiData
} from '../propertiesDefinition/propertiesDefinitionsResourceApiData';

interface LoadingMap {
    [key: string]: boolean;
}

function isLoading(map: LoadingMap): boolean {
    return Object.keys(map).some(k => map[k]);
}

@Component({
    selector: 'winery-properties',
    templateUrl: 'properties.component.html',
    styleUrls: [
        'properties.component.css'
    ],
    providers: [
        PropertiesService,
        PropertiesDefinitionService
    ]
})
export class PropertiesComponent implements OnInit {

    definitions: PropertiesDefinitionKVElement[];
    properties: Properties;
    isXML: boolean;
    @ViewChild('propertiesEditor') propertiesEditor: WineryEditorComponent;

    private _loading = {
        getProperties: false,
        saveProperties: false,
        getDefinitions: false
    };

    constructor(
        private propertiesService: PropertiesService,
        private notify: WineryNotificationService,
        public sharedData: InstanceService) {
    }

    isLoading = () => isLoading(this._loading);

    ngOnInit() {
        this.getProperties();
        this.getPropertiesDefinitions();
    }

    save() {
        this._loading.getProperties = true;
        if (this.isXML) {
            this.properties = this.propertiesEditor.getData();
        }
        this.propertiesService.saveProperties(this.properties, this.isXML)
            .subscribe(
                () => this.handleSave(),
                error => this.handleError(error, 'saveProperties')
            );
    }

    private getProperties() {
        this._loading.getProperties = true;
        this.propertiesService.getProperties()
            .subscribe(
                data => this.handleProperties(data),
                error => this.handleError(error, 'getProperties')
            );
    }

    private getPropertiesDefinitions() {
        this._loading.getDefinitions = true;
        this.propertiesService.getPropertiesDefinitions()
            .subscribe(
                data => this.handlePropertiesDefinitions(data),
                error => this.handleError(error, 'getDefinitions')
            );
    }

    private handlePropertiesDefinitions(data: PropertiesDefinitionsResourceApiData) {
        this.definitions = data.winerysPropertiesDefinition.propertyDefinitionKVList;
        this._loading.getDefinitions = false;
    }

    private handleSave() {
        this.notify.success('Successfully updated properties!');
        this.getProperties();
    }

    private handleProperties(data: PropertiesData) {
        this.properties = data.properties;
        this.isXML = data.isXML;
        this._loading.getProperties = false;
    }

    private handleError(error: HttpErrorResponse, loadingKey: string) {
        this.notify.error(error.message);
        this._loading[loadingKey] = false;
    }

}
