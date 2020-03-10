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

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { InstanceService } from '../../../instance.service';
import { backendBaseURL } from '../../../../configuration';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { YamlProperty } from './yamlProperty';


@Injectable()
export class YamlPropertiesService {

    path: string;

    constructor(private http: HttpClient,
                private sharedData: InstanceService) {
        this.path = backendBaseURL + this.sharedData.path + '/properties/';
    }
    
    public getProperties(): Observable<YamlProperty[]> {
        return this.http.get(this.path, { observe: 'response', responseType: 'text' })
            .pipe(map(res => {
                if (res.headers.get('Content-Type') === 'application/json') {
                    // fake some null-coalescing
                    if (res.body === null) { return []; }
                    return JSON.parse(res.body);
                } else {
                    // log an error
                    return [];
                }
            }));
    }

    public saveProperties(properties: any): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders();
        headers.set('Content-Type', 'application/json');
        return this.http
            .put(
                this.path,
                properties,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }
}
