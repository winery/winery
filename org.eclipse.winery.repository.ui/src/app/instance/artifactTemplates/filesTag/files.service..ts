/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */
import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions } from '@angular/http';
import { InstanceService } from '../../instance.service';
import { Observable } from 'rxjs/Observable';
import { backendBaseURL, hostURL } from '../../../configuration';

@Injectable()
export class FilesService {

    private path: string;

    constructor(private http: Http,
                private sharedData: InstanceService) {
        this.path = backendBaseURL + this.sharedData.path + '/files/';
    }

    getFiles(): Observable<FilesApiData[]> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.get(this.path, options)
            .map(res => res.json());
    }

    get uploadUrl() {
        return this.path;
    }

    delete(fileToRemove: FilesApiData) {
        return this.http.delete(hostURL + fileToRemove.deleteUrl);
    }
}

export interface FilesApiData {
    deleteType: string;
    deleteUrl: string;
    name: string;
    size: number;
    thumbnailUrl: string;
    url: string;
}
