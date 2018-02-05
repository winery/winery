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
import { Headers, Http, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { InstanceService } from '../instance/instance.service';
import { backendBaseURL } from '../configuration';

@Injectable()
export class WineryLicenseService {

    constructor(private http: Http,
                private sharedData: InstanceService) {
    }

    getData(): Observable<string> {
        const headers = new Headers({ 'Accept': 'text/plain' });
        const options = new RequestOptions({ headers: headers });
        return this.http.get(backendBaseURL + this.sharedData.path + '/LICENSE', options)
            .map(res => res.text());
    }

    save(licenseFile: String) {
        const headers = new Headers({ 'Content-Type': 'application/json' });
        const options = new RequestOptions({ headers: headers });

        return this.http.put(backendBaseURL + this.sharedData.path + '/LICENSE', licenseFile, options)
            .map(res => res.json());
    }
}
