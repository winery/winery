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
import { Observable } from 'rxjs';
import { TagsAPIData } from './tagsAPIData';
import { backendBaseURL } from '../../../configuration';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

@Injectable()
export class TagService {
    private path: string;

    constructor(private http: HttpClient,
                private route: Router) {
        this.path = backendBaseURL + this.route.url + '/';
    }

    getTagsData(): Observable<TagsAPIData[]> {
        return this.http.get<TagsAPIData[]>(this.path);
    }

    removeTagData(data: TagsAPIData): Observable<HttpResponse<string>> {
        return this.http
            .delete(
                this.path + '/' + data.id + '/',
                { observe: 'response', responseType: 'text' }
            );
    }

    postTag(tagsApiData: TagsAPIData): Observable<string> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http
            .post(
                this.path,
                tagsApiData,
                { responseType: 'text' }
            );
    }
}
