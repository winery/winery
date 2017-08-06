/**
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
import { Headers, Http, RequestOptions } from '@angular/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';

@Injectable()
export class DocumentationService {
    private path: string;

    constructor(private http: Http,
                private route: Router) {
        this.path = decodeURIComponent(this.route.url);
    }

    getDocumentationData(): Observable<string> {
        const headers = new Headers({ 'Accept': 'text/plain' });
        const options = new RequestOptions({ headers: headers });

        return this.http.get(backendBaseURL + this.path, options)
            .map(res => res.text());
    }

    saveDocumentationData(documentationData: string): Observable<any> {
        const headers = new Headers({'Content-Type': 'text/plain', 'Accept': 'text/plain'});
        const options = new RequestOptions({headers: headers});
        return this.http.put(backendBaseURL + this.path + '/', documentationData, options);
    }
}
