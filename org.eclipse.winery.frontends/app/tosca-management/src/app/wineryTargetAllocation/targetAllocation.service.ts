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
import { Observable } from 'rxjs/Observable';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { backendBaseURL } from '../configuration';
import { AllocationRequest } from './request';

@Injectable()
export class TargetAllocationService {

    backendLink: string;

    constructor(private http: HttpClient) {
    }

    allocateRequest(request: AllocationRequest): Observable<HttpResponse<string[]>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        const url = this.backendLink + '/topologytemplate/allocate';

        return this.http.post<string[]>(
            url,
            JSON.stringify(request),
            { headers: headers, observe: 'response' }
        );
    }

    getNodeTemplates(): Observable<NodeTemplate[]> {
        const url = this.backendLink + '/topologytemplate/nodetemplates/';
        return this.http.get<NodeTemplate[]>(url);
    }

    getProperties(policy: Policy): Observable<any> {
        const qName = policy.policyRef.slice(1).split('}');
        return this.http.get<any>(backendBaseURL + '/policytemplates/' +
            encodeURIComponent(encodeURIComponent(qName[0])) + '/' + qName[1] + '/properties');
    }
}

export class NodeTemplate {
    policies: NodeTemplatePolicy;
}

export class NodeTemplatePolicy {
    policy: Policy[];
}

export class Policy {
    name: string;
    policyType: string;
    policyRef: string;
}
