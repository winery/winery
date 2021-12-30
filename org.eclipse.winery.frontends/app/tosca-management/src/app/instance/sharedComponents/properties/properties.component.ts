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
    properties: Properties = null;
    propertyKeys: string[] = [];
    isXMLData: boolean;
    @ViewChild('propertiesEditor') propertiesEditor: WineryEditorComponent;

    private _loading = {
        getProperties: false,
        saveProperties: false,
        getDefinitions: false
    };

    constructor(
        private propertiesService: PropertiesService,
        private propertiesDefinitionService: PropertiesDefinitionService,
        private notify: WineryNotificationService,
        public sharedData: InstanceService) {
    }

    isLoading = () => isLoading(this._loading);

    ngOnInit() {
        this.getProperties();
        this.getPropertiesDefinition();
    }

    save() {
        this._loading.getProperties = true;
        if (this.isXMLData) {
            this.properties = this.propertiesEditor.getData();
        }
        this.propertiesService.saveProperties(this.properties, this.isXMLData)
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

    // TODO: handle inheritance
    private getPropertiesDefinition() {
        this._loading.getDefinitions = true;
        // TODO: this does not send a request to the correct endpoint this endpoint url is constructed from browser url kekw
        this.propertiesDefinitionService.getPropertiesDefinitionsData()
            .subscribe(
                data => this.handleDefinitions(data),
                error => this.handleError(error, 'getDefinitions')
            );
    }

    private handleDefinitions(data: PropertiesDefinitionsResourceApiData) {
        console.log('data', data);
        // this.definitions = data.winerysPropertiesDefinition.propertyDefinitionKVList;
        this._loading.getDefinitions = false;
    }

    private handleSave() {
        this.notify.success('Successfully updated properties!');
        this.getProperties();
    }

    private handleProperties(data: PropertiesData) {
        if (data.isXML) {
            this.isXMLData = true;
            this.properties = data.properties;
        } else {
            this.isXMLData = false;
            if (data.properties) {
                this.propertyKeys = Object.keys(data.properties);
            }
            if (this.propertyKeys.length > 0) {
                this.properties = data.properties;
            }
        }
        this._loading.getProperties = false;
    }

    private handleError(error: HttpErrorResponse, loadingKey: string) {
        this.notify.error(error.message);
        this._loading[loadingKey] = false;
    }
}
