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
import { Router } from '@angular/router';
import { backendBaseURL } from '../../../configuration';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

export interface ApplicationOption {
    description: string;
    iconUrl: string;
    planServiceName: string;
    planInputMessageUrl: string;
    id: string;
    name: string;
}

export interface Options {
    option: ApplicationOption[];
}

export interface SelfServiceApiData {
    csarName: string;
    displayName: string;
    version: string;
    authors: string[];
    description: string;
    iconUrl: string;
    imageUrl: string;
    options: Options;
}

@Injectable()
export class SelfServicePortalService {

    private url: string;
    private path: string;
    selfServiceData: SelfServiceApiData;

    constructor(private http: HttpClient,
                private route: Router) {
        this.url = this.route.url.substring(0, this.route.url.lastIndexOf('/'));
    }

    getIconPath(): string {
        return backendBaseURL + this.url + '/icon.jpg';
    }

    getImagePath(): string {
        return backendBaseURL + this.url + '/image.jpg';
    }

    getSelfServiceData(): Observable<SelfServiceApiData> {
        const o = this.http.get<SelfServiceApiData>(backendBaseURL + this.path);
        o.subscribe(data => this.selfServiceData = data);
        return o;
    }

    saveName(displayName: string): Observable<HttpResponse<string>> {
        return this.saveSingleProperty({ displayName: displayName }, this.path + 'displayname');
    }

    saveDescription(description: string): Observable<HttpResponse<string>> {
        return this.saveSingleProperty({ description: description }, this.path + 'description');
    }

    saveSingleProperty(property: any, path: string): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http
            .put(
                backendBaseURL + path,
                property,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }

    setPath(path: string) {
        this.path = path;
    }
}
