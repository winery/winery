/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 whichääboth accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Nicole Keppler - initial API and Implementation
 *
 */
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Headers, RequestOptions, Http, Response } from '@angular/http';
import { backendBaseUri } from '../../configuration';
import { ImplementationAPIData } from './implementationAPIData';
import { ImplementationWithTypeAPIData } from './implementationWithTypeAPIData';

@Injectable()
export class ImplementationService {
    private path: string;

    constructor(private http: Http) {
    }

    getImplementationData(): Observable<ImplementationAPIData[]> {
        console.log('get implementation request');
        let headers = new Headers({ 'Accept': 'application/json' });
        let options = new RequestOptions({ headers: headers });
        console.log(backendBaseUri + this.path + '/implementations/');
        return this.http.get(backendBaseUri + this.path + '/implementations/', options)
            .map(res => res.json());
    }
    setPath(path: string): void {
        this.path = path;
    }
    getAllNamespaces(): Observable<string[]> {
        let headers = new Headers({ 'Accept': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http.get(backendBaseUri + '/admin/namespaces', options)
            .map(res => res.json());
    }
    postImplementation(resourceApiData: ImplementationWithTypeAPIData): Observable<Response> {
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http.post(backendBaseUri + this.path + '/propertiesdefinition/', JSON.stringify(resourceApiData), options);
    }
}
