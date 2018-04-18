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
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../configuration';
import { Router } from '@angular/router';
import { NodeTypeData } from './xaasPackager.component';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable()
export class PackagerService {

    private readonly headers = new HttpHeaders({ 'Accept': 'application/json' });
    private readonly path: string;

    constructor(private http: HttpClient,
                private router: Router) {
        this.path = backendBaseURL + decodeURIComponent(this.router.url);
    }

    getNodeTypes(): Observable<NodeTypeData[]> {
        return this.http.get<NodeTypeData[]>(backendBaseURL + '/nodetypes', { headers: this.headers });
    }

    getArtifactTypesAndInfrastructureNodeTypes(): Observable<ArtifactTypesAndInfrastructureNodetypes> {
        return this.http.get<ArtifactTypesAndInfrastructureNodetypes>(this.path + '/createfromartifact', { headers: this.headers });
    }

    createTemplateFromArtifact(formData: FormData): Observable<string> {
        return this.http.post(this.path, formData, { headers: this.headers, responseType: 'text' });
    }
}

export class ArtifactTypesAndInfrastructureNodetypes {
    artifactTypes: string[];
    infrastructureNodeTypes: string[];
}
