/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { QNameWithTypeApiData } from '../wineryInterfaces/qNameWithTypeApiData';
import { backendBaseURL } from '../configuration';
import { InstanceService } from '../instance/instance.service';
import { WineryVersion } from '../wineryInterfaces/wineryVersion';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

@Injectable()
export class WineryAddVersionService {

    constructor(private sharedData: InstanceService, private http: HttpClient) {
    }

    public getReferencedDefinitions(): Observable<QNameWithTypeApiData[]> {
        return this.http.get<QNameWithTypeApiData[]>(backendBaseURL + this.sharedData.path + '?subComponents');
    }

    addNewVersion(newVersion: WineryVersion, updateReferencedDefinitions: QNameWithTypeApiData[]): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http.post(
            backendBaseURL + this.sharedData.path,
            JSON.stringify({
                version: newVersion,
                componentsToUpdate: updateReferencedDefinitions
            }),
            { headers: headers, observe: 'response', responseType: 'text' });
    }

    freezeOrRelease(type: string): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });

        return this.http.post(
            backendBaseURL + this.sharedData.path + '?' + type + '=true',
            '{}',
            { headers: headers, observe: 'response', responseType: 'text' }
        );
    }
}
