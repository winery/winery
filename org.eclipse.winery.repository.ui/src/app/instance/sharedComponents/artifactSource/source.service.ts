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
import {SourceApiData} from './sourceApiData';
import {ToscaTypes} from '../../../wineryInterfaces/enums';
import {Router} from '@angular/router';

@Injectable()
export class SourceService {

    private readonly path: string;
    private pathToFiles: string;
    private parentPath: string;

    constructor(private http: Http,
                private route: Router,
                private sharedData: InstanceService) {
        this.parentPath = backendBaseURL + this.sharedData.path + '/';

        if (this.sharedData.toscaComponent.toscaType === ToscaTypes.ServiceTemplate) {
            this.path = this.pathToFiles = backendBaseURL + this.route.url + '/';
        } else {
            this.path = this.parentPath + 'source/';
            this.pathToFiles = this.parentPath + 'files/';
        }
    }

    get getSourcePath() {
        return this.path;
    }

    getFiles(): Observable<{ files: FilesApiData[], paths: string[] }> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.get(this.path, options)
            .map(res => res.json());
    }

    getFile(file: FilesApiData): Observable<string> {
        const headers = new Headers({'Accept': 'text/plain'});
        const params = new URLSearchParams();
        params.set('path', file.subDirectory);
        const options = new RequestOptions({headers: headers, params: params});
        return this.http.get(this.path + file.name, options)
            .map(res => res.text());
    }

    deleteFile(fileToRemove: FilesApiData) {
        const params = new URLSearchParams();
        params.set('path', fileToRemove.subDirectory);
        const options = new RequestOptions({params: params});
        return this.http.delete(hostURL + fileToRemove.deleteUrl, options);
    }

    copySourcesToFiles() {
        const headers = new Headers({'Accept': 'application/json', 'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});
        const data = {};
        return this.http.post(this.parentPath, data, options)
            .map(res => res.ok);
    }

    postToSources(data: SourceApiData) {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});
        return this.http.post(this.path + data.getFileName(), data, options)
            .map(res => res.json());
    }

    postToFiles(data: SourceApiData) {
        const headers = new Headers({'Accept': 'application/json', 'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.post(this.pathToFiles + data.getFileName(), data, options)
            .map(res => res.json());
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
