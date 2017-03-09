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
import { Headers, RequestOptions, Http } from '@angular/http';
import { backendBaseUri } from '../../configuration';
import {ImplementationAPIData} from "./implementationAPIData";

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

    /*saveDocumentationData(documentationData: string): Observable<any> {
        let headers = new Headers({'Content-Type': 'text/plain', 'Accept': 'text/plain'});
        let options = new RequestOptions({headers: headers});
        return this.http.put(backendBaseUri + decodeURIComponent(this.path), documentationData, options);
    }*/
}
