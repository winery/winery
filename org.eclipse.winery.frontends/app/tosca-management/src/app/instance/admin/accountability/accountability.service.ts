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
import { AccountabilityParticipant } from './AccountabilityParticipant';
import { backendBaseURL } from '../../../configuration';
import { SelectData } from '../../../model/selectData';
import { ToscaTypes } from '../../../model/enums';
import { AuthorizationElement, FileProvenanceElement, ModelProvenanceElement } from '../../../model/provenance';

@Injectable()
export class AccountabilityService {
    private accountabilityUrl = backendBaseURL + '/API/accountability/';

    static getDownloadURLForFile(fileAddress: string, fileName: string, accountabilityId: string) {
        return `${backendBaseURL}/API/accountability/${encodeURIComponent(encodeURIComponent(accountabilityId))}` +
            `/downloadFile?address=${fileAddress}&filename=${fileName}`;
    }

    constructor(private http: HttpClient) {
    }

    authorize(accountabilityProcessId: string, participant: AuthorizationElement): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        const url = this.accountabilityUrl + encodeURIComponent(encodeURIComponent(accountabilityProcessId)) + '/authorize';

        return this.http.post(url, participant, { headers: headers, observe: 'response', responseType: 'text' });
    }

    authenticate(accountabilityProcessId: string, participantAddress: string): Observable<AuthorizationElement[]> {
        const url = this.accountabilityUrl + encodeURIComponent(encodeURIComponent(accountabilityProcessId))
            + '/authenticate?participantAddress=' + participantAddress;

        return this.http.get<AuthorizationElement[]>(url);
    }

    getServiceTemplates(): Observable<SelectData[]> {
        return this.http.get<SelectData[]>(backendBaseURL + '/' + ToscaTypes.ServiceTemplate
            + '?grouped=angularSelect'
            + '&includeVersions=componentVersionOnly');
    }

    getFileProvenance(accountabilityId: string, fileId: string): Observable<FileProvenanceElement[]> {
        const url = backendBaseURL + '/API/accountability/'
            + encodeURIComponent(encodeURIComponent(accountabilityId))
            + '/fileHistory?fileId=' + encodeURIComponent(encodeURIComponent(fileId));

        return this.http.get<FileProvenanceElement[]>(url);
    }

    getModelProvenance(modelId: string): Observable<ModelProvenanceElement[]> {
        const url = backendBaseURL + '/API/accountability/'
            + encodeURIComponent(encodeURIComponent(modelId))
            + '/modelHistory';

        return this.http.get<ModelProvenanceElement[]>(url);
    }

    retrieveFileContent(fileAddressInImmutableStorage: string, accountabilityId: string): Observable<string> {
        const url = `${backendBaseURL}/API/accountability/${encodeURIComponent(encodeURIComponent(accountabilityId))}` +
            `/retrieveFile?address=${fileAddressInImmutableStorage}`;

        return this.http.get(url, { responseType: 'text' });
    }


}
