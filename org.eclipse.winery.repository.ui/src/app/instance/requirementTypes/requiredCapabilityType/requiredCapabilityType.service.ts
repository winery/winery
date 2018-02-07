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
import {Injectable} from '@angular/core';
import {Headers, Http, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs';
import {RequiredCapabilityTypeApiData} from './requiredCapabilityTypeApiData';
import {backendBaseURL} from '../../../configuration';
import {Router} from '@angular/router';

@Injectable()
export class RequiredCapabilityTypeService {

    path: string;

    constructor(private http: Http,
                private route: Router) {
        this.path = backendBaseURL + this.route.url + '/';
    }

    getRequiredCapabilityTypeData(): Observable<RequiredCapabilityTypeApiData> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.get(this.path, options)
            .map(res => res.json());
    }

    save(requiredCapabilityType: string): Observable<any> {
        const headers = new Headers({'Content-Type': 'text/plain'});
        const options = new RequestOptions({headers: headers});
        return this.http.put(this.path, requiredCapabilityType, options);
    }

    delete(): Observable<any> {
        return this.http.delete(this.path);
    }
}
