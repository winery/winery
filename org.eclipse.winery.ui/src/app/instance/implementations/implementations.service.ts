/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 whichääboth accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Nicole Keppler, Lukas Balzer - initial API and Implementation
 *
 */

import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions, Response } from '@angular/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { backendBaseUri } from '../../configuration';
import { ImplementationAPIData } from './implementationAPIData';
import { ImplementationWithTypeAPIData } from './implementationWithTypeAPIData';

@Injectable()
export class ImplementationService {
    private path: string;

    constructor(private http: Http,
                private route: Router) {
        this.path = decodeURIComponent(this.route.url);
    }

    getImplementationData(): Observable<ImplementationAPIData[]> {
        let headers = new Headers({'Accept': 'application/json'});
        let options = new RequestOptions({headers: headers});
        return this.http.get(backendBaseUri + this.path + '/', options)
            .map(res => res.json());
    }

    getAllNamespaces(): Observable<string[]> {
        let headers = new Headers({'Accept': 'application/json'});
        let options = new RequestOptions({headers: headers});
        return this.http.get(backendBaseUri + '/admin/namespaces', options)
            .map(res => res.json());
    }

    postImplementation(implApiData: ImplementationWithTypeAPIData): Observable<Response> {
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});
        return this.http.post(backendBaseUri + '/nodetypeimplementations/', JSON.stringify(implApiData), options);
    }

    deleteImplementations(implToDelete: ImplementationAPIData) {
        let headers = new Headers({'Accept': 'application/json'});
        let options = new RequestOptions({headers: headers});
        let pathAddition = '/nodetypeimplementations/'
            + encodeURIComponent(encodeURIComponent(implToDelete.namespace)) + '/'
            + implToDelete.localname + '/';
        return this.http.delete(backendBaseUri + pathAddition, options);
    }
}
