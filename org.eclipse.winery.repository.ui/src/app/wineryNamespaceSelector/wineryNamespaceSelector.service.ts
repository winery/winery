/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../configuration';
import { NamespaceWithPrefix } from '../wineryInterfaces/namespaceWithPrefix';

@Injectable()
export class WineryNamespaceSelectorService {

    constructor(private http: Http) {
    }

    getNamespaces(all: boolean = false): Observable<NamespaceWithPrefix[]> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});
        let URL: string;
        if (all) {
            URL = backendBaseURL + '/admin/namespaces/?all';
        } else {
            URL = backendBaseURL + '/admin/namespaces/';
        }
        return this.http.get(URL, options)
            .map(res => res.json());
    }
}
