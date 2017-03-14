/**
 * Copyright (c) -2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Huixin Liu, Nicole Keppler - initial API and implementation
 */
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Headers, RequestOptions, Http, Response } from '@angular/http';
import { backendBaseUri } from '../../configuration';
import { InstanceStateApiData } from './InstanceStateApiData';
import { Router } from '@angular/router';
import List = _.List;

@Injectable()
export class InstanceStateService {

    private path: string;

    constructor(private http: Http,
                private route: Router) {
        this.path = decodeURIComponent(this.route.url);
    }

    getInstanceStates(): Observable<InstanceStateApiData[]> {
        let headers = new Headers({ 'Accept': 'application/json' });
        let options = new RequestOptions({ headers: headers });
        return this.http.get(backendBaseUri + this.path + '/', options)
            .map(res => res.json());
    }

    addPropertyData(newStateData: InstanceStateApiData): Observable<Response> {
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });
        return this.http.post(backendBaseUri + this.path + '/', JSON.stringify(newStateData), options);
    }

    deleteState(stateToRemove: InstanceStateApiData): Observable<Response> {
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http.delete(backendBaseUri + this.path + '/' + stateToRemove.state, options);
    }
}
