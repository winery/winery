/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Headers, Http, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { backendBaseURL } from '../../configuration';

@Injectable()
export class ListDefinedTypesAndElementsService {

    private path: string;

    constructor(private http: Http, private route: Router) {
        this.path = backendBaseURL + this.route.url;
    }

    public getDeclarations(): Observable<string[]> {
        const headers = new Headers({ 'Accept': 'application/json' });
        const options = new RequestOptions({ headers: headers });

        return this.http.get(this.path, options)
            .map(res => res.json());
    }
}
