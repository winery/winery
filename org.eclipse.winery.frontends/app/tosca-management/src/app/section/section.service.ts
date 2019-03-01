/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
import { Injectable } from '@angular/core';
import { FileUploader } from 'ng2-file-upload';
import { Observable } from 'rxjs';
import { debug, isNullOrUndefined } from 'util';
import { backendBaseURL } from '../configuration';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { FileProvenanceElement } from '../model/provenance';

@Injectable()
export class SectionService {

    private path: string;
    private readonly fileUploader: FileUploader;

    constructor(private http: HttpClient) {
        this.fileUploader = new FileUploader({ url: backendBaseURL + '/' });
    }

    get uploader(): FileUploader {
        return this.fileUploader;
    }

    getSectionData(resourceType?: string): Observable<any> {
        const headers = new HttpHeaders({ 'Accept': 'application/json' });

        if (isNullOrUndefined(resourceType)) {
            resourceType = this.path;
        }

        return this.http.get(backendBaseURL + resourceType + '/?includeVersions=true', { headers: headers });
    }

    createComponent(newComponentName: string, newComponentNamespace: string, newComponentSelectedType: string): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });

        const saveObject: any = { localname: newComponentName, namespace: newComponentNamespace };

        if (!isNullOrUndefined(newComponentSelectedType) && newComponentSelectedType.length > 0) {
            saveObject.type = newComponentSelectedType;
        }

        return this.http.post(backendBaseURL + this.path + '/',
            JSON.stringify(saveObject),
            { headers: headers, observe: 'response', responseType: 'text' });
    }

    setPath(path: string) {
        this.path = '/' + path;
    }

}
