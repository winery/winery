/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Balzer - initial API and implementation
 */

import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { TagsAPIData } from './tagsAPIData';
import { backendBaseURL } from '../../../configuration';

@Injectable()
export class TagService {
    private path: string;

    constructor(private http: Http,
                private route: Router) {
        this.path = decodeURIComponent(this.route.url);
    }

    getTagsData(): Observable<TagsAPIData[]> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});
        return this.http.get(backendBaseURL + this.path + '/', options)
            .map(res => res.json());
    }
    removeTagData(data: TagsAPIData): Observable<Response> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});
        const pathAddition = this.path
            + '/' + data.id + '/';
        return this.http.delete(backendBaseURL + pathAddition, options);
    }

    postTag(tagsApiData: TagsAPIData): Observable<string> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});
        return this.http.post(backendBaseURL + this.path + '/', JSON.stringify(tagsApiData), options)
            .map(res => res.text());
    }
}
