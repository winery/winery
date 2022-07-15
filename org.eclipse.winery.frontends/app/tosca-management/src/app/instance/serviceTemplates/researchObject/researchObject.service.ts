/*******************************************************************************
 * Copyright (c) 2021-2022 Contributors to the Eclipse Foundation
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
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ROMetadataApiData, ROPublicationApiData } from '../../../model/researchObjectApiData';
import { FileOrFolderElement } from '../../../model/fileOrFolderElement';
import { FileApiData } from '../../../model/fileApiData';


@Injectable()
export class ResearchObjectService {

    baseUrl: string;

    constructor(private http: HttpClient) {
    }

    getResearchObjectMetadata(): Observable<ROMetadataApiData> {
        return this.http.get<ROMetadataApiData>(this.baseUrl + 'metadata');
    }

    saveResearchObjectMetadata(data: ROMetadataApiData): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http.put(this.baseUrl + 'metadata', data, {
            headers: headers, observe: 'response', responseType: 'text'
        });
    }

    getResearchObjectPublication(): Observable<ROPublicationApiData> {
        return this.http.get<ROPublicationApiData>(this.baseUrl + 'publication');
    }

    saveResearchObjectPublication(data: ROPublicationApiData): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http.put(this.baseUrl + 'publication', data, {
            headers: headers, observe: 'response', responseType: 'text'
        });
    }

    getDirsAndFiles(): Observable<Map<string, FileOrFolderElement[]>> {
        return this.http.get<any>(this.baseUrl + 'files');
    }

    createDirectory(path: string): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http.post(this.baseUrl + 'files', new FileApiData(path, null), {
            headers: headers, observe: 'response', responseType: 'text'
        });
    }

    move(sourcePath: string, targetPath: string): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http.post(this.baseUrl + 'files', new FileApiData(sourcePath, targetPath), {
            headers: headers, observe: 'response', responseType: 'text'
        });
    }

    delete(path: string): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http.request('delete', this.baseUrl + 'files', {
            body: new FileApiData(path, null),
            headers: headers, observe: 'response', responseType: 'text'
        });
    }

    setBaseUrl(url: string) {
        this.baseUrl = url + '/researchobject/';
    }

}
