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
import { InstanceService } from '../../instance.service';
import { Observable } from 'rxjs';
import { backendBaseURL, hostURL } from '../../../configuration';
import { HttpClient, HttpResponse } from '@angular/common/http';

@Injectable()
export class FilesService {

    private path: string;

    constructor(private http: HttpClient,
                private sharedData: InstanceService) {
        this.path = backendBaseURL + this.sharedData.path + '/files/';
    }

    getFiles(): Observable<{ files: FilesApiData[], paths: string[] }> {
        return this.http.get<{ files: FilesApiData[], paths: string[] }>(this.path);
    }

    get uploadUrl() {
        return this.path;
    }

    delete(fileToRemove: FilesApiData): Observable<HttpResponse<string>> {
        return this.http
            .delete(
                hostURL + fileToRemove.deleteUrl + '?path=' + fileToRemove.subDirectory,
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
