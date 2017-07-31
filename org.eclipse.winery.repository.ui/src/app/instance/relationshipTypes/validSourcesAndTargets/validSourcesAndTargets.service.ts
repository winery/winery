/**
 * Copyright (c) -2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Nicole Keppler - initial API and implementation
 */
import { Headers, Http, RequestOptions, Response } from '@angular/http';
import { InstanceService } from '../../instance.service';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';
import { ValidEndingsData } from './validEndingsApiData';
import { isNullOrUndefined } from 'util';
import { SelectData } from '../../../wineryInterfaces/selectData';

@Injectable()
export class ValidService {

    private path: string;

    constructor(private http: Http,
                private sharedData: InstanceService) {
        this.path = backendBaseURL + this.sharedData.path + '/validsourcesandtargets/';
    }

    getValidEndingsData(): Observable<ValidEndingsData> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});
        return this.http.get(this.path, options)
            .map(res => res.json());
    }

    getSelectorData(resourceType?: string): Observable<SelectData[]> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        if (isNullOrUndefined(resourceType)) {
            resourceType = this.path;
        }
        return this.http.get(backendBaseURL + resourceType + '/', options)
            .map(res => res.json());
    }

    saveValidEndings(validEndingsData: ValidEndingsData): Observable<Response> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});
        return this.http.put(this.path, JSON.stringify(validEndingsData), options);
    }
}
