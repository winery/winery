/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Philipp Meyer & Tino Stadelmaier - initial API and implementation
 */

import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs';
import { backendBaseUri } from '../../configuration';
import { Router } from '@angular/router';
import { InstanceService } from '../instance.service';

@Injectable()
export class EditXMLService {

    private path: string;

    constructor(private http: Http,
                private sharedData: InstanceService) {
        this.path = this.sharedData.path;
    }

    getXmlData(): Observable<string> {
        let headers = new Headers({ 'Accept': 'application/xml' });
        let options = new RequestOptions({ headers: headers });

        return this.http.get(backendBaseUri + this.path + '/xml/', options)
            .map(res => res.text());
    }

    saveXmlData(xmlData: String): Observable<any> {
        let headers = new Headers({ 'Content-Type': 'text/xml' });
        let options = new RequestOptions({ headers: headers });

        return this.http.put(backendBaseUri + this.path + '/', xmlData, options);
    }
}
