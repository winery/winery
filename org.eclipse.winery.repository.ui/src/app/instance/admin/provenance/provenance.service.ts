/*******************************************************************************
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
 *******************************************************************************/
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { ProvenanceParticipant } from './ProvenanceParticipant';
import { backendBaseURL } from '../../../configuration';
import { SelectData } from '../../../wineryInterfaces/selectData';
import { ToscaTypes } from '../../../wineryInterfaces/enums';
import { AuthorizationNode } from '../../../wineryInterfaces/provenance';

@Injectable()
export class ProvenanceService {

    private provenanceUrl = backendBaseURL + '/API/provenance/';

    constructor(private http: HttpClient) {
    }

    authorize(provenanceProcessId: string, participant: ProvenanceParticipant): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        const url = this.provenanceUrl + encodeURIComponent(encodeURIComponent(provenanceProcessId)) + '/authorize';

        return this.http.post(url, participant, { headers: headers, observe: 'response', responseType: 'text' });
    }

    authenticate(provenanceProcessId: string, participantAddress: string): Observable<AuthorizationNode[]> {
        const url = this.provenanceUrl + encodeURIComponent(encodeURIComponent(provenanceProcessId))
            + '/authenticate?participantAddress=' + participantAddress;

        return this.http.get<AuthorizationNode[]>(url);
    }

    getServiceTemplates(): Observable<SelectData[]> {
        return this.http.get<SelectData[]>(backendBaseURL + '/' + ToscaTypes.ServiceTemplate
            + '?grouped=angularSelect'
            + '&includeVersions=componentVersionOnly');
    }
}
