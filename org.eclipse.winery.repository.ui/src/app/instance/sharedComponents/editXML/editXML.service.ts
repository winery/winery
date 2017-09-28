/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions, Response } from '@angular/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';

@Injectable()
export class EditXMLService {

    private path: string;

    constructor(private http: Http,
                private route: Router) {
        this.path = this.route.url;
        if (this.path.endsWith('xml')) {
            this.path = this.path.slice(0, -3);
        }
    }

    getXmlData(): Observable<string> {
        const headers = new Headers({ 'Accept': 'application/xml' });
        const options = new RequestOptions({ headers: headers });

        let getPath = this.path;
        if (!getPath.endsWith('properties') && !getPath.endsWith(('selfserviceportal/'))) {
            getPath += 'xml/';
        }

        return this.http.get(backendBaseURL + getPath, options)
            .map(res => res.text());
    }

    saveXmlData(xmlData: String): Observable<Response> {
        const headers = new Headers({ 'Content-Type': 'application/xml' });
        const options = new RequestOptions({ headers: headers });

        return this.http.put(backendBaseURL + this.path, xmlData, options);
    }
}
