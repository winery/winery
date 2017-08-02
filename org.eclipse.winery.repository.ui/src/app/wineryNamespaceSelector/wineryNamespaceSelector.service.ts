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
 *     Niko Stadelmaier - add types
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

    getAllNamespaces(): Observable<NamespaceWithPrefix[]> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.get(backendBaseURL + '/admin/namespaces/', options)
            .map(res => res.json());
    }
}
