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
import { SectionData } from './sectionData';
import { Headers, RequestOptions, Http } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs';
import { backendBaseUri } from '../configuration';
import { FileUploader } from 'ng2-file-upload';
import { Router } from '@angular/router';

@Injectable()
export class SectionService {

    private path: string;
    private fileUploader: FileUploader;

    constructor(private http: Http) {
        this.fileUploader = new FileUploader({ url: backendBaseUri + '/' });
    }

    get uploader(): FileUploader {
        return this.fileUploader;
    }

    getSectionData(): Observable<SectionData[]> {
        let headers = new Headers({ 'Accept': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http.get(backendBaseUri + this.path + '/', options)
            .map(res => res.json());
    }

    createComponent(newComponentName: string, newComponentNamespace: string) {
        let headers = new Headers({ 'Accept': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http.post(backendBaseUri + this.path + '/', JSON.stringify({
            name: newComponentName,
            namespace: newComponentNamespace
        }), options)
            .map(res => res.json());
    }

    setPath(path: string) {
        this.path = '/' + path;
    }
}
