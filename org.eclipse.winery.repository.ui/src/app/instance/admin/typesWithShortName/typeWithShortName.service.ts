/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions, Response } from '@angular/http';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';
import { Router } from '@angular/router';

export class TypeWithShortName {
    type: string;
    shortName: string;

    constructor(pType = '', pShortName = '') {
        this.type = pType;
        this.shortName = pShortName;
    }
}

@Injectable()
export class TypeWithShortNameService {

    private path: string;

    constructor(private http: Http,
                private route: Router) {
        this.path = decodeURIComponent(this.route.url);
    }

    getAllTypes(): Observable<TypeWithShortName[]> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.get(backendBaseURL + this.path + '/', options)
            .map(res => res.json());
    }

    postTypes(types: TypeWithShortName[]): Observable<Response> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.post(backendBaseURL + this.path + '/', JSON.stringify(types), options);
    }

    postType(type: TypeWithShortName) {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.post(backendBaseURL + this.path + '/', JSON.stringify(type), options);
    }

}
