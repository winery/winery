/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Balzer - initial API and implementation
 */

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Headers, RequestOptions, Http, Response } from '@angular/http';
import { backendBaseUri } from '../../configuration';
import { Router } from '@angular/router';
import { FileUploader } from 'ng2-file-upload';

@Injectable()
export class VisualAppearanceService {

    isNodeType = true;
    private path: string;

    constructor(private http: Http,
                private route: Router) {
        this.path = decodeURIComponent(this.route.url);
        if (this.path.includes('relationshiptypes')) {
            this.isNodeType = false;
        }
    }

    getImg16x16Path(): string {
        return backendBaseUri + this.path + '/16x16';
    }

    getImg50x50Path(): string {
        return backendBaseUri + this.path + '/50x50';
    }

    getUploader(path: string): FileUploader {
        let fileUploader: FileUploader = new FileUploader({url: path});
        fileUploader.onAfterAddingFile = (item) => {
            item.method = 'PUT';
        };
        return fileUploader;
    }

    getColor(type: string): Observable<any> {
        let headers = new Headers({'Accept': 'text/plain'});
        let options = new RequestOptions({headers: headers});
        return this.http.get(backendBaseUri + this.path + type, options)
            .map(res => res.text());
    }

    saveColor(color: string, type: string): Observable<Response> {
        let headers = new Headers({'Content-Type': 'text/plain'});
        let options = new RequestOptions({headers: headers});
        let sendString: string = 'color=' + encodeURIComponent(color);
        return this.http.put(backendBaseUri + this.path + type, sendString, options);
    }
}
