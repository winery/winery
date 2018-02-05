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
import {Injectable} from '@angular/core';
import {Headers, Http, RequestOptions, URLSearchParams} from '@angular/http';
import {InstanceService} from '../../instance.service';
import {Observable} from 'rxjs/Observable';
import {backendBaseURL, hostURL} from '../../../configuration';

@Injectable()
export class FilesService {

    private path: string;

    constructor(private http: Http,
                private sharedData: InstanceService) {
        this.path = backendBaseURL + this.sharedData.path + '/files/';
    }

    getFiles(): Observable<{ files: FilesApiData[], paths: string[] }> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.get(this.path, options)
            .map(res => res.json());
    }

    get uploadUrl() {
        return this.path;
    }

    delete(fileToRemove: FilesApiData) {
        const params = new URLSearchParams();
        params.set('path', fileToRemove.subDirectory);
        const options = new RequestOptions({params: params});
        return this.http.delete(hostURL + fileToRemove.deleteUrl, options);
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
