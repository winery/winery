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

import { InstanceService } from '../../instance.service';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';
import { ValidEndingsData } from './validEndingsApiData';
import { isNullOrUndefined } from 'util';
import { SelectData } from '../../../model/selectData';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

@Injectable()
export class ValidService {

    private path: string;

    constructor(private http: HttpClient,
                private sharedData: InstanceService) {
        this.path = this.sharedData.path + '/validsourcesandtargets/';
    }

    getValidEndingsData(): Observable<ValidEndingsData> {
        return this.http.get<ValidEndingsData>(this.path);
    }

    getSelectorData(resourceType?: string): Observable<SelectData[]> {
        if (!resourceType) {
            resourceType = this.path;
        }
        return this.http.get<SelectData[]>(backendBaseURL + resourceType + '/');
    }

    saveValidEndings(validEndingsData: ValidEndingsData): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http
            .put(
                this.path,
                validEndingsData,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }
}
