/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
import { RequestOptions, Response } from '@angular/http';
import { InstanceService } from '../../instance.service';
import { Observable } from 'rxjs/Observable';
import { backendBaseURL } from '../../../configuration';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

@Injectable()
export class PropertiesService {

    path: string;

    constructor(private http: HttpClient,
                private sharedData: InstanceService) {
        this.path = backendBaseURL + this.sharedData.path + '/properties/';
    }

    /**
     * We use `any` as return value because the backend delivers the json object containing the property as a key
     * and the value the value. Example: { "property": "this is my property" }.
     */
    public getProperties(): Observable<any> {
        return this.http.get(this.path, { observe: 'response', responseType: 'text' })
            .map(res => {
                if (res.headers.get('Content-Type') === 'application/json') {
                    return {
                        isXML: false, properties: JSON.parse(res.body)
                    };
                } else {
                    return { isXML: true, properties: res.body };
                }
            });
    }

    public saveProperties(properties: any, isXML: boolean): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders();
        headers.set('Content-Type', isXML ? 'application/xml' : 'application/json');
        return this.http
            .put(
                this.path,
                properties,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }
}
