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
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { backendBaseURL } from '../../../configuration';
import { ToscaComponent } from '../../../wineryInterfaces/toscaComponent';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

@Injectable()
export class PropertyRenameService {

    private propertyName: string;
    private toscaComponent: ToscaComponent;

    constructor(private http: HttpClient,
                private route: Router) {
    }

    setPropertyName(propertyName: string) {
        this.propertyName = propertyName;
    }

    setToscaComponent(toscaComponent: ToscaComponent) {
        this.toscaComponent = toscaComponent;
    }

    setPropertyValue(value: string, renameAllComponents: boolean): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });

        let payload;
        if (this.propertyName === 'localName') {
            payload = {
                localname: value,
                renameAllComponents: renameAllComponents
            };
        } else {
            payload = {
                namespace: value,
                renameAllComponents: renameAllComponents
            };
        }

        return this.http
            .post(
                backendBaseURL + this.toscaComponent.path + '/' + this.propertyName,
                payload,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }

    reload(property: string) {
        if (this.propertyName === 'localName') {
            this.route.navigateByUrl(this.toscaComponent.toscaType + '/'
                + encodeURIComponent(encodeURIComponent(this.toscaComponent.namespace)) + '/' + property);
        } else {
            this.route.navigateByUrl(this.toscaComponent.toscaType + '/'
                + encodeURIComponent(encodeURIComponent(property)) + '/' + this.toscaComponent.localName);
        }
    }
}
