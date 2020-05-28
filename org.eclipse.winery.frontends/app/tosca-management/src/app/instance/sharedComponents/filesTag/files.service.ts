/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Router } from '@angular/router';

@Injectable()
export class FilesService {

    private path: string;

    constructor(private http: HttpClient,
                private sharedData: InstanceService,
                private route: Router) {
        this.path = this.sharedData.path + '/files';
    }

    getFiles(path?: string): Observable<{ files: FilesApiData[], paths: string[] }> {
        return this.http.get<{ files: FilesApiData[], paths: string[] }>(path ? path : this.path);
    }

    getLocalFiles(): Observable<{ files: FilesApiData[], paths: string[] }> {
        const tokens = this.route.url.split('/');
        tokens.pop();
        return this.http.get<{ files: FilesApiData[], paths: string[] }>(backendBaseURL + tokens.join('/') + '/files');
    }

    get uploadUrl() {
        return this.path;
    }

    delete(fileToRemove: FilesApiData): Observable<HttpResponse<string>> {
        let url = fileToRemove.deleteUrl;
        if (!url.startsWith('http')) {
            url = hostURL + fileToRemove.deleteUrl;
        }
        return this.http
            .delete(
                url + '?path=' + fileToRemove.subDirectory,
                { observe: 'response', responseType: 'text' }
            );
    }
}

export interface FilesApiData {
    deleteType: string;
    deleteUrl: string;
    name: string;
    size: number;
    thumbnailUrl: string;
    url: string;
    subDirectory: string;
}
