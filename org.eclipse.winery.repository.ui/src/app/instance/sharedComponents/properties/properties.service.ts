/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Injectable } from '@angular/core';
import { Headers, Http, Response, RequestOptions } from '@angular/http';
import { InstanceService } from '../../instance.service';
import { Observable } from 'rxjs/Observable';
import { backendBaseURL } from '../../../configuration';

@Injectable()
export class PropertiesService {

    path: string;

    constructor(private http: Http,
                private sharedData: InstanceService) {
        this.path = backendBaseURL + this.sharedData.path + '/properties/';
    }

    /**
     * We use `any` as return value because the backend delivers the json object containing the property as a key
     * and the value the value. Example: { "property": "this is my property" }.
     */
    public getProperties(): Observable<any> {
        return this.http.get(this.path)
            .map(res => res.json());
    }

    public saveProperties(properties: any): Observable<Response> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});
        return this.http.put(this.path, properties, options);
    }
}
