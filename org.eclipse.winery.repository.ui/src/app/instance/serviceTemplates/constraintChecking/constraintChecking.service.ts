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
import { Headers, Http, RequestOptions, Response } from '@angular/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';

@Injectable()
export class ConstraintCheckingService {

    private path: string;

    constructor(private http: Http,
                private route: Router) {
        this.path = this.route.url;
        if (this.path.endsWith('xml')) {
            this.path = this.path.slice(0, -3);
        }
    }

    getCheckingResult(): Observable<string> {
        const headers = new Headers({ 'Accept': 'application/xml' });

        const options = new RequestOptions({ headers: headers });

        const getPath = this.path;
        const requestUrl = backendBaseURL + getPath + '/';

        return this.http.post(requestUrl, options)
            .map(res => res.text());
    }
}
