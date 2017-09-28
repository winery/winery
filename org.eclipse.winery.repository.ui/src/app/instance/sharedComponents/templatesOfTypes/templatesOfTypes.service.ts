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
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';
import { ImplementationAPIData } from '../implementations/implementationAPIData';

@Injectable()
export class TemplatesOfTypeService {

    constructor(private http: Http,
                private route: Router) {
    }

    getTemplateData(): Observable<ImplementationAPIData[]> {
        const headers = new Headers({ 'Accept': 'application/json' });
        const options = new RequestOptions({ headers: headers });
        return this.http.get(backendBaseURL + this.route.url + '/', options)
            .map(res => res.json());
    }

    getPath(): string {
        return this.route.url
    }
}
