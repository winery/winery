/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Headers, Http, RequestOptions, Response } from '@angular/http';
import { backendBaseURL } from '../../../configuration';
import { Router } from '@angular/router';

@Injectable()
export class VisualAppearanceService {

    constructor(private http: Http,
                private route: Router) {
    }

    getImg16x16Path(): string {
        return backendBaseURL + this.route.url + '/16x16';
    }

    getImg50x50Path(): string {
        return backendBaseURL + this.route.url + '/50x50';
    }

    getData() {
        const headers = new Headers({ 'Accept': 'application/json' });
        const options = new RequestOptions({ headers: headers });
        return this.http.get(backendBaseURL + this.route.url + '/', options)
            .map(res => res.json());
    }

    saveVisuals(data: any): Observable<Response> {
        const headers = new Headers({ 'Content-Type': 'application/json' });
        const options = new RequestOptions({ headers: headers });
        return this.http.put(backendBaseURL + this.route.url + '/', JSON.stringify(data), options);
    }

    get path(): string {
        return this.route.url;
    }
}
