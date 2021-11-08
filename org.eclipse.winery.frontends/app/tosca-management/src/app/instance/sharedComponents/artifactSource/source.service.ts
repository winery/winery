/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
import { InstanceService } from '../../instance.service';
import { Observable } from 'rxjs';
import { backendBaseURL, hostURL } from '../../../configuration';
import { SourceApiData } from './sourceApiData';
import { ToscaTypes } from '../../../model/enums';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';

@Injectable()
export class SourceService {

    private readonly path: string;
    private readonly pathToFiles: string;
    private readonly parentPath: string;

    constructor(private http: HttpClient,
                private route: Router,
                private sharedData: InstanceService) {
        this.parentPath = this.sharedData.path;

        if (this.sharedData.toscaComponent.toscaType === ToscaTypes.ServiceTemplate) {
            this.path = this.pathToFiles = backendBaseURL + this.route.url;
        } else {
            this.path = this.parentPath + '/source';
            this.pathToFiles = this.parentPath + '/files';
        }
    }

    get getSourcePath() {
        return this.path;
    }

    getFiles(): Observable<{ files: FilesApiData[], paths: string[] }> {
        return this.http.get<{ files: FilesApiData[], paths: string[] }>(this.path);
    }

    getFile(file: FilesApiData): Observable<string> {
        const headers = new HttpHeaders({ 'Accept': 'text/plain' });

        return this.http
            .get(
                this.path + '/' + file.name + '?path=' + file.subDirectory,
                { headers: headers, responseType: 'text' }
            );
    }

    deleteFile(fileToRemove: FilesApiData): Observable<HttpResponse<string>> {
        return this.http
            .delete(
                hostURL + fileToRemove.deleteUrl + '?path=' + fileToRemove.subDirectory,
                { observe: 'response', responseType: 'text' }
            );
    }

    copySourcesToFiles(): Observable<HttpResponse<string>> {
        return this.http
            .post(
                this.getSourcePath, {},
                { observe: 'response', responseType: 'text' }
            );
    }

    postToSources(data: SourceApiData): Observable<HttpResponse<string>> {
        return this.http
            .post(
                this.path + '/' + data.getFileName(),
                data,
                { observe: 'response', responseType: 'text' }
            );
    }

    postToFiles(data: SourceApiData): Observable<HttpResponse<string>> {
        return this.http
            .post(
                this.pathToFiles + '/' + data.getFileName(),
                data,
                { observe: 'response', responseType: 'text' }
            );
    }

}

export class FilesApiData {
    deleteType: string;
    deleteUrl: string;
    name: string;
    size: number;
    thumbnailUrl: string;
    url: string;
    subDirectory: string;
}
