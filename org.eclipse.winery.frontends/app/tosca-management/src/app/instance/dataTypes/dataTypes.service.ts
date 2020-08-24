/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { backendBaseURL } from '../../configuration';
import { TDataType } from '../../../../../topologymodeler/src/app/models/ttopology-template';
import { Observable } from 'rxjs';

@Injectable()
export class DataTypesService {
    readonly path: string;

    constructor(private http: HttpClient,
                private route: Router) {
        this.path = backendBaseURL + this.route.url + '/';
    }

    getDataTypes(): Observable<TDataType[]> {
        return this.getJson(backendBaseURL + '/datatypes/?full=true');
    }

    addDataType(newType: TDataType): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http
            .post(
                backendBaseURL + '/datatypes',
                newType,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }

    updateDataType(editedType: TDataType): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http
            .put(
                backendBaseURL + `/datatypes/${editedType.namespace}/${editedType.name}`,
                editedType,
                {headers: headers, observe: 'response', responseType: 'text' }
            );
    }

    updateConstraints(type: TDataType): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http
            .put(
                backendBaseURL + `/datatypes/${type.namespace}/${type.name}/constraints`,
                type.constraints,
                {headers: headers, observe: 'response', responseType: 'text' }
            );
    }

    deleteDataType(deletedType: TDataType): Observable<HttpResponse<string>> {
        return this.http.delete(
            backendBaseURL + `/datatypes/${deletedType.namespace}/${deletedType.name}`,
            { observe: 'response', responseType: 'text' }
        );
    }

    private getJson<T>(path: string): Observable<T> {
        return this.http.get<T>(path);
    }
}
