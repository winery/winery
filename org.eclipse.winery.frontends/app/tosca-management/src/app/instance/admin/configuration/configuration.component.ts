/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
import { WineryConfiguration, WineryRepositoryConfigurationService } from '../../../wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { HttpClient } from '@angular/common/http';
import { backendBaseURL } from '../../../configuration';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';

@Component({
    selector: 'winery-instance-configuration-component',
    templateUrl: 'configuration.component.html',
    styleUrls: [
        'configuration.component.css',
    ]
})

export class FeatureConfigurationComponent implements OnInit {
    config: WineryConfiguration;

    constructor(private http: HttpClient,
                private configData: WineryRepositoryConfigurationService,
                private notify: WineryNotificationService) {
    }

    ngOnInit(): void {
        this.config = this.configData.configuration;
    }

    saveChanges() {
        this.http.put<WineryConfiguration>(backendBaseURL + '/admin' + '/config', this.config)
            .subscribe(
                () => this.notify.success('Successfully saved changes!'),
                () => this.notify.error('Error while saving changes')
            );
    }
}
