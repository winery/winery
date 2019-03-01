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
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { SelectItem } from 'ng2-select';
import { WineryComponent } from '../../../../model/wineryComponent';
import { backendBaseURL } from '../../../../configuration';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

@Injectable()
export class PoliciesService {

    private path: string;

    constructor(private http: HttpClient,
                private route: Router) {
        this.path = backendBaseURL + this.route.url;
    }

    getPolicies(): Observable<WineryPolicy[]> {
        return this.http.get<WineryPolicy[]>(this.path);
    }

    getPolicyTypes(): Observable<SelectItem[]> {
        return this.http.get<SelectItem[]>(backendBaseURL + '/policytypes?grouped=angularSelect');
    }

    getPolicyTemplatesForType(pT: SelectItem): Observable<SelectItem[]> {
        const qName = pT.id.slice(1).split('}');
        return this.http
            .get<SelectItem[]>(
                backendBaseURL + '/policytypes/' + encodeURIComponent(encodeURIComponent(qName[0])) + '/' + qName[1] + '/instances'
            );
    }

    postPolicy(xml: string): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/xml' });
        return this.http
            .post(
                this.path,
                xml,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }

    deletePolicy(id: string): Observable<HttpResponse<string>> {
        return this.http
            .delete(
                this.path + '/' + id,
                { observe: 'response', responseType: 'text' }
            );
    }
}

export class WineryPolicy extends WineryComponent {
    policyType: string;
    policyRef: string;
    id: string;
}
