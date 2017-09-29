/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */
import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions } from '@angular/http';
import { FileUploader } from 'ng2-file-upload';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map';
import { isNullOrUndefined } from 'util';
import { backendBaseURL } from '../configuration';

@Injectable()
export class SectionService {

    private path: string;
    private fileUploader: FileUploader;

    constructor(private http: Http) {
        this.fileUploader = new FileUploader({ url: backendBaseURL + '/' });
    }

    get uploader(): FileUploader {
        return this.fileUploader;
    }

    getSectionData(resourceType?: string): Observable<any> {
        const headers = new Headers({ 'Accept': 'application/json' });
        const options = new RequestOptions({ headers: headers });

        if (isNullOrUndefined(resourceType)) {
            resourceType = this.path;
        }

        return this.http.get(backendBaseURL + resourceType + '/', options)
            .map(res => res.json());
    }

    createComponent(newComponentName: string, newComponentNamespace: string, newComponentSelectedType?: string) {
        const headers = new Headers({ 'Content-Type': 'application/json' });
        const options = new RequestOptions({ headers: headers });

        let saveObject: any;
        if (!isNullOrUndefined(newComponentSelectedType)) {
            saveObject = { localname: newComponentName, namespace: newComponentNamespace, type: newComponentSelectedType };
        } else {
            saveObject = { localname: newComponentName, namespace: newComponentNamespace };
        }

        return this.http.post(backendBaseURL + this.path + '/', JSON.stringify(saveObject), options);
    }

    setPath(path: string) {
        this.path = '/' + path;
    }
}
