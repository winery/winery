/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
import {
    WineryRepositoryConfigurationService
} from '../../wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { Observable } from 'rxjs';
import { WineryNotificationService } from '../../wineryNotificationModule/wineryNotification.service';
import { ToscaComponent } from '../../model/toscaComponent';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable()
export class DeploymentNormalizationAnalyzerService {

    constructor(private http: HttpClient,
                private notify: WineryNotificationService,
                private configurationService: WineryRepositoryConfigurationService) {
    }

    startNormalization(toscaComponent: ToscaComponent): Observable<string> {
        if (!this.configurationService.configuration.endpoints.deploymentNormalizationAssistant) {
            this.notify.error('No DNA URL set!');
            return Observable.of('Error');
        }

        const url = this.configurationService.configuration.endpoints.deploymentNormalizationAssistant;

        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.post<HttpResponse<any>>(url,
            { namespace: toscaComponent.namespace, id: toscaComponent.localName },
            { headers: headers }
        )
            .map(response => url + response.headers.get('Location'));
    }
}
