/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */

import { Injectable } from '@angular/core';
import { SectionData } from './sectionData';
import { Headers, RequestOptions, Http } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs';
import { backendBaseUri } from '../configuration';

@Injectable()
export class SectionService {

    private type: string;

    constructor(private http: Http) {
    }

    getSectionData(type: string): Observable<SectionData[]> {
        this.type = type.toLowerCase();

        let headers = new Headers({'Accept': 'application/json'});
        let options = new RequestOptions({headers: headers});

        return this.http.get(backendBaseUri + '/' + this.type + '/', options)
            .map(res => res.json());
    }

    createComponent(newComponentName: string, newComponentNamespace: string) {
        let headers = new Headers({'Accept': 'application/json'});
        let options = new RequestOptions({headers: headers});

        return this.http.post(backendBaseUri + '/' + this.type + '/', JSON.stringify({ name: newComponentName, namespace: newComponentNamespace }), options)
            .map(res => res.json());
    }
}
