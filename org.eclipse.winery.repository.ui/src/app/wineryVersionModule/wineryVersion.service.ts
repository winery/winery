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
import {Injectable} from '@angular/core';
import {Headers, Http, RequestOptions, Response} from '@angular/http';
import {Observable} from 'rxjs/Rx';
import {QNameWithTypeApiData} from '../wineryInterfaces/qNameWithTypeApiData';
import {backendBaseURL} from '../configuration';
import {InstanceService} from '../instance/instance.service';
import {WineryVersion} from '../wineryInterfaces/wineryVersion';

@Injectable()
export class WineryAddVersionService {

    constructor(private sharedData: InstanceService, private http: Http) {
    }

    public getReferencedDefinitions(): Observable<QNameWithTypeApiData[]> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});
        return this.http.get(backendBaseURL + this.sharedData.path + '?subComponents', options)
            .map(res => res.json()
                .map((element: any) => new QNameWithTypeApiData(element.localname, element.namespace, element.type))
            );
    }

    addNewVersion(newVersion: WineryVersion, updateReferencedDefinitions: QNameWithTypeApiData[]): Observable<Response> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});
        return this.http.post(backendBaseURL + this.sharedData.path, JSON.stringify({
                version: newVersion,
                componentsToUpdate: updateReferencedDefinitions
            }),
            options);
    }

    freezeOrRelease(type: string): Observable<Response> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});
        return this.http.post(backendBaseURL + this.sharedData.path + '?' + type + '=true', '{}', options);
    }
}
