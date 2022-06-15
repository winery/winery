/*******************************************************************************
 * Copyright (c) 2019-2021 Contributors to the Eclipse Foundation
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
import {
    WineryConfiguration, WineryRepositoryConfigurationService
} from '../../../wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { HttpClient } from '@angular/common/http';
import { backendBaseURL, modelerURL } from '../../../configuration';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { FormControl } from '@angular/forms';

@Component({
    selector: 'winery-instance-configuration-component',
    templateUrl: 'configuration.component.html',
    styleUrls: [
        'configuration.component.css',
    ]
})

export class FeatureConfigurationComponent implements OnInit {

    config: WineryConfiguration;
    public containerUrlAvailable: boolean;
    public containerUrlControl: FormControl = new FormControl();
    public containerUrl;

    constructor(private http: HttpClient,
                private configData: WineryRepositoryConfigurationService,
                private notify: WineryNotificationService) {
    }

    ngOnInit(): void {
        this.config = this.configData.configuration;
        this.containerUrlControl.setValue(modelerURL);
        this.containerUrl = modelerURL;
        this.checkModelerAvailability();
    }

    checkModelerAvailability() {
        const containerURL = (<HTMLInputElement>document.getElementById('containerUrl2')).value;
        if (containerURL) {
            this.containerUrl = containerURL;
            this.http.get(this.containerUrl, { responseType: 'text' }).subscribe(
                () => this.containerUrlAvailable = true,
                () => this.containerUrlAvailable = false
            );
        } else if (this.containerUrl) {
            this.http.get(this.containerUrl, { responseType: 'text' }).subscribe(
                () => this.containerUrlAvailable = true,
                () => this.containerUrlAvailable = false
            );
        }
    }

    saveModelerURL() {
        this.checkModelerAvailability();
        if (this.containerUrlAvailable) {
            const containerURL = (<HTMLInputElement>document.getElementById('containerUrl2')).value;
            this.config.endpoints.bpmnModeler = containerURL;
            this.http.put<WineryConfiguration>(backendBaseURL + '/admin' + '/config', this.config)
                .subscribe(
                    () => this.notify.success('Successfully saved Modeler URL!'),
                    () => this.notify.error('Error while saving Modeler URL')
                );
        } else {
            this.notify.error('Modeler URL is not available');
        }
    }

    saveChanges() {
        this.http.put<WineryConfiguration>(backendBaseURL + '/admin' + '/config', this.config)
            .subscribe(
                () => this.notify.success('Successfully saved changes!'),
                () => this.notify.error('Error while saving changes')
            );
    }
}
