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
import { Headers, RequestOptions, Http } from '@angular/http';
import { backendBaseUri } from '../../configuration';
import List = _.List;
import { InstanceStateApiData } from './InstanceStateApiData';

@Injectable()
export class InstanceStateService {

    private path: string;

    constructor(private http: Http) {
    }

    getInstanceStates(): Observable<InstanceStateApiData[]> {
        let headers = new Headers({ 'Accept': 'application/json' });
        let options = new RequestOptions({ headers: headers });
        this.path += '/instancestates/';
        console.log('path');
        console.log(this.path);
        console.log('instanceStatesService');
        return this.http.get(backendBaseUri + this.path , options)
            .map(res => res.json());
        // [{"state":"ooo"},{"state":"asdfklj"},{"state":"NewState"},{"state":"newTry"},{"state":"newTry2"},{"state":"testJson"}]
    }

    setPath(path: string): void {
        this.path = path;
    }
}
