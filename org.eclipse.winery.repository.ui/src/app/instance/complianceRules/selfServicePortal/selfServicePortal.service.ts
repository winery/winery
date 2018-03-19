/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Headers, Http, RequestOptions } from '@angular/http';
import { backendBaseURL } from '../../../configuration';
import { Observable } from 'rxjs';

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

    constructor(private http: Http,
                private route: Router) {
        let path = this.route.url;
        path = path.substring(0, path.lastIndexOf('/'));
        this.url = decodeURIComponent(path);
    }

    getIconPath(): string {
        return backendBaseURL + this.url + '/icon.jpg';
    }

    getImagePath(): string {
        return backendBaseURL + this.url + '/image.jpg';
    }

    getSelfServiceData(): Observable<SelfServiceApiData> {
        const headers = new Headers({ 'Accept': 'application/json' });
        const options = new RequestOptions({ headers: headers });

        return this.http.get(backendBaseURL + this.path, options)
            .map(res => this.selfServiceData = res.json());
    }

    saveName(displayName: string): Observable<any> {
        return this.saveSingleProperty({ 'displayName': displayName }, this.path + 'displayname');
    }

    saveDescription(description: string): Observable<any> {
        return this.saveSingleProperty({ 'description': description }, this.path + 'description');
    }

    saveSingleProperty(property: any, path: string): Observable<any> {
        const headers = new Headers({ 'Content-Type': 'application/json' });
        const options = new RequestOptions({ headers: headers });

        return this.http.put(backendBaseURL + path, JSON.stringify(property), options);
    }

    setPath(path: string) {
        this.path = path;
    }
}
