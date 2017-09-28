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
import { Observable } from 'rxjs/Observable';
import { hostURL } from '../../../configuration';
import { FilesApiData } from '../../artifactTemplates/filesTag/files.service.';

@Injectable()
export class WineryArtifactFilesService {

    private path: string;

    constructor(private http: Http) {
    }

    getFiles(templateUrl: string): Observable<{files: FilesApiData[]}> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.get(templateUrl, options)
            .map(res => res.json());
    }

    get uploadUrl() {
        return this.path;
    }

    delete(fileToRemove: FilesApiData) {
        return this.http.delete(hostURL + fileToRemove.deleteUrl);
    }
}
