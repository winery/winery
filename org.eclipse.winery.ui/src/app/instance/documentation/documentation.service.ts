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
 *     Nicole Keppler - fixes for path routing
 *******************************************************************************/


import { Injectable } from '@angular/core';
import { DocumentationApiData } from './documentationApiData';
import { Observable } from 'rxjs';
import { Headers, RequestOptions, Http } from '@angular/http';
import { backendBaseUri } from '../../configuration';

@Injectable()
export class DocumentationService {
    private path: string;

    constructor(private http: Http) {
    }

    getDocumentationData(path: string): Observable<DocumentationApiData> {
        let headers = new Headers({ 'Accept': 'application/json' });
        let options = new RequestOptions({ headers: headers });
        if (path.indexOf('documentation') === -1) {
            path += '/documentation/';
        } else {
            path += '/';
        }
        this.path = path;
        return this.http.get(backendBaseUri + decodeURIComponent(path), options)
            .map(res => res.json());
    }

    saveDocumentationData(documentationData: DocumentationApiData, isEmpty: boolean ): Observable<any> {
        let headers = new Headers({'Content-Type': 'application/json', 'Accept': 'application/json'});
        let options = new RequestOptions({headers: headers});
        let docCopy = new DocumentationApiData(
            documentationData.documentation
        );
        console.log( 'service path for save' + this.path );
        console.log(JSON.stringify(docCopy));
        isEmpty = false;
        if (isEmpty) {
           // return this.http.post(backendBaseUri + decodeURIComponent(this.path), JSON.stringify(docCopy), options);
        } else {
            return this.http.put(backendBaseUri + decodeURIComponent(this.path), JSON.stringify(docCopy), options);
        }
    }
}
