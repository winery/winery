/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../configuration';
import { Router } from '@angular/router';
import { NodeTypeData } from './xaasPackager.component';

@Injectable()
export class PackagerService {
    private path: string;

    constructor(private http: Http,
                private router: Router) {
        this.path = decodeURIComponent(this.router.url);
    }

    getNodetypes(): Observable<NodeTypeData[]> {
        return this.sendJsonRequest('/nodetypes');
    }

    getArtifactTpesAndInfrastructureNodetypes(): Observable<ArtifactTypesAndInfrastructureNodetypes> {
        return this.sendJsonRequest(this.path + '/createfromartifact');
    }

    createTempalteFromArtifact(formData: FormData): Observable<string> {
        const headers = new Headers();
        headers.append('Accept', 'application/json');
        const options = new RequestOptions({headers: headers});
        return this.http.post(backendBaseURL + this.path, formData, options)
            .map(res => res.text());
    }

    /**
     * Private method for DRY principle. It is used to get all kinds of data
     * for the specified sub path.
     *
     * @param requestPath string The path which is specific for each request.
     * @returns {Observable<any>}
     */
    private sendJsonRequest(requestPath: string = ''): Observable<any> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});
        return this.http.get(backendBaseURL + requestPath, options)
            .map(res => res.json());
    }
}

export class ArtifactTypesAndInfrastructureNodetypes {
    artifactTypes: string[];
    infrastructureNodeTypes: string[];
}
