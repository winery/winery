/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions } from '@angular/http';
import { InstanceService } from '../../instance.service';
import { Observable } from 'rxjs/Observable';
import { backendBaseURL, hostURL } from '../../../configuration';
import { ArtifactResourceApiData } from './ArtifactResourceApiData';

@Injectable()
export class ArtifactSourceService {

    private path: string;
    private pathToFiles: string;

    constructor(private http: Http,
                private sharedData: InstanceService) {
        this.path = backendBaseURL + this.sharedData.path + '/source/';
        this.pathToFiles = backendBaseURL + this.sharedData.path + '/files/';
    }

    getFiles(): Observable<{ files: FilesApiData[] }> {
        const headers = new Headers({ 'Accept': 'application/json' });
        const options = new RequestOptions({ headers: headers });

        return this.http.get(this.path, options)
            .map(res => res.json());
    }

    getFile(file: FilesApiData): Observable<string> {
        const headers = new Headers({ 'Accept': 'text/plain' });
        const options = new RequestOptions({ headers: headers });

        return this.http.get(this.path + file.name, options)
            .map(res => res.text());
    }

    get uploadUrl() {
        return this.path;
    }

    deleteFile(fileToRemove: FilesApiData) {
        return this.http.delete(hostURL + fileToRemove.deleteUrl);
    }

    postToSources(data: ArtifactResourceApiData) {
        const headers = new Headers({ 'Accept': 'application/json', 'Content-Type': 'application/json' });
        const options = new RequestOptions({ headers: headers });
        return this.http.post(this.path + data.getFileName(), data, options)
            .map(res => res.json());
    }

    postToFiles(data: ArtifactResourceApiData) {
        const headers = new Headers({ 'Accept': 'application/json', 'Content-Type': 'application/json' });
        const options = new RequestOptions({ headers: headers });

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
}
