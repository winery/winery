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
import { backendBaseURL } from '../../../configuration';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { HttpClient, HttpResponse } from '@angular/common/http';

@Injectable()
export class RepositoryService {

    path: string;

    constructor(private http: HttpClient,
                private route: Router) {
        this.path = decodeURIComponent(this.route.url);
    }

    clearRepository(): Observable<HttpResponse<string>> {
        return this.http
            .delete(
                backendBaseURL + this.path + '/',
                { observe: 'response', responseType: 'text' }
            );
    }

}
