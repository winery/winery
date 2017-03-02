/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 whichääboth accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Balzer - initial API and Implementation
 *     Nicole Keppler - fixes for path routing, saveData
 */
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Headers, RequestOptions, Http } from '@angular/http';
import { backendBaseUri } from '../../configuration';

@Injectable()
export class DocumentationService {
    private path: string;

    constructor(private http: Http) {
    }

    getDocumentationData(path: string): Observable<string> {
        let headers = new Headers({ 'Accept': 'text/plain' });
        let options = new RequestOptions({ headers: headers });
        if (path.indexOf('documentation') === -1) {
            path += '/documentation/';
        } else {
            path += '/';
        }
        this.path = path;
        return this.http.get(backendBaseUri + decodeURIComponent(path), options)
            .map(res => res.text());
    }

    saveDocumentationData(documentationData: string): Observable<any> {
        let headers = new Headers({'Content-Type': 'text/plain', 'Accept': 'text/plain'});
        let options = new RequestOptions({headers: headers});
        return this.http.put(backendBaseUri + decodeURIComponent(this.path), documentationData, options);
    }
}
