/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */
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

    private path: string;
    selfServiceData: SelfServiceApiData;

    constructor(private http: Http,
                private route: Router) {
    }

    getSelfServiceData(): Observable<SelfServiceApiData> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.get(backendBaseURL + this.path, options)
            .map(res => this.selfServiceData = res.json());
    }

    saveName(displayName: string): Observable<any> {
        return this.saveSingleProperty({'displayName': displayName}, this.path + 'displayname');
    }

    saveDescription(description: string): Observable<any> {
        return this.saveSingleProperty({'description': description}, this.path + 'description');
    }

    saveSingleProperty(property: any, path: string): Observable<any> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.put(backendBaseURL + path, JSON.stringify(property), options);
    }

    setPath(path: string) {
        this.path = path;
    }
}
