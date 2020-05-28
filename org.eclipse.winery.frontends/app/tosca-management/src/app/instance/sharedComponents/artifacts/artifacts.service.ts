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
import { NameAndQNameApiData } from '../../../wineryQNameSelector/wineryNameAndQNameApiData';
import { concat, Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';
import { Artifact } from '../../../model/artifact';
import { takeLast } from 'rxjs/operators';

@Injectable()
export class ArtifactsService {

    constructor(private http: HttpClient, private route: Router) {
    }

    getArtifactTypes() {
        return this.getJson<NameAndQNameApiData[]>(backendBaseURL + '/artifacttypes');
    }

    getArtifacts() {
        const tokens = this.route.url.split('/');
        tokens.pop();
        return this.getJson<Artifact[]>(backendBaseURL + tokens.join('/') + '/artifacts');
    }

    createArtifact(artifact: Artifact, file: File) {
        const url = `${backendBaseURL}${this.route.url}/${artifact.name}`;
        const formData: FormData = new FormData();
        formData.append('file', file, file.name);
        return concat(
            this.postJson(backendBaseURL + this.route.url, artifact),
            this.http.post(url, formData, { observe: 'response', responseType: 'text' })
        ).pipe(takeLast(1));
    }

    deleteArtifact(artifact: Artifact): Observable<HttpResponse<string>> {
        const url = `${backendBaseURL}${this.route.url}/f/${artifact.name}`;
        return this.http.delete(url, { observe: 'response', responseType: 'text' });
    }

    private getJson<T>(path: string): Observable<T> {
        return this.http.get<T>(path);
    }

    private postJson<T>(path: string, data: T): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http.post(path, data, { headers: headers, observe: 'response', responseType: 'text' });
    }
}
