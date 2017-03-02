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

    getImplementationData(path: string): Observable<JSON> {
        console.log('get implementation request');
        let headers = new Headers({ 'Accept': 'text/json' });
        let options = new RequestOptions({ headers: headers });
        if (path.indexOf('implementation') === -1) {
            path += '/implementation/';
        } else {
            path += '/';
        }
        this.path = path;
        return this.http.get(backendBaseUri + decodeURIComponent(path), options));
    }

    /*saveDocumentationData(documentationData: string): Observable<any> {
        let headers = new Headers({'Content-Type': 'text/plain', 'Accept': 'text/plain'});
        let options = new RequestOptions({headers: headers});
        return this.http.put(backendBaseUri + decodeURIComponent(this.path), documentationData, options);
    }*/
}
