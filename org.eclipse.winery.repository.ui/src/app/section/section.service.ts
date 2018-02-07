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
import {Headers, Http, RequestOptions} from '@angular/http';
import {FileUploader} from 'ng2-file-upload';
import {Observable} from 'rxjs';
import 'rxjs/add/operator/map';
import {isNullOrUndefined} from 'util';
import {backendBaseURL} from '../configuration';

@Injectable()
export class SectionService {

    private path: string;
    private fileUploader: FileUploader;

    constructor(private http: Http) {
        this.fileUploader = new FileUploader({url: backendBaseURL + '/'});
    }

    get uploader(): FileUploader {
        return this.fileUploader;
    }

    getSectionData(resourceType?: string): Observable<any> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        if (isNullOrUndefined(resourceType)) {
            resourceType = this.path;
        }

        return this.http.get(backendBaseURL + resourceType + '/', options)
            .map(res => res.json());
    }

    createComponent(newComponentName: string, newComponentNamespace: string, newComponentSelectedType: string) {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});

        const saveObject: any = {localname: newComponentName, namespace: newComponentNamespace};

        if (!isNullOrUndefined(newComponentSelectedType) && newComponentSelectedType.length > 0) {
            saveObject.type = newComponentSelectedType;
        }

        return this.http.post(backendBaseURL + this.path + '/', JSON.stringify(saveObject), options);
    }

    setPath(path: string) {
        this.path = '/' + path;
    }
}
