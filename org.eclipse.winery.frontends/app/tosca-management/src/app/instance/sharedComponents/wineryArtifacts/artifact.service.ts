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
import { Response } from '@angular/http';
import { Router } from '@angular/router';
import { backendBaseURL } from '../../../configuration';
import { Observable } from 'rxjs';
import { GenerateArtifactApiData } from '../interfaces/generateArtifactApiData';
import { InterfacesApiData } from '../interfaces/interfacesApiData';
import { NameAndQNameApiData } from '../../../wineryQNameSelector/wineryNameAndQNameApiData';
import { ArtifactApiData } from '../../../model/wineryComponent';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { SelectData } from '../../../model/selectData';

export class SelectableInterface extends SelectData {
    operations: string[];
}

@Injectable()
export class WineryArtifactService {

    constructor(private http: HttpClient,
                private route: Router) {
    }

    getAllArtifacts(): Observable<ArtifactApiData[]> {
        return this.http.get<ArtifactApiData[]>(backendBaseURL + this.route.url);
    }

    /**
     * Deletes an artifact
     *
     * @returns {Observable<Response>}
     */
    deleteArtifact(artifactName: string): Observable<HttpResponse<string>> {
        return this.http
            .delete(
                backendBaseURL + this.route.url + '/' + artifactName + '/',
                { observe: 'response', responseType: 'text' }
            );
    }

    createNewArtifact(artifact: GenerateArtifactApiData): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http
            .post(
                backendBaseURL + this.route.url + '/',
                artifact,
                { headers: headers, observe: 'response', responseType: 'text' }
            );

    }

    getInterfacesOfAssociatedType(): Observable<SelectableInterface[]> {
        return this.http.get<SelectableInterface[]>(backendBaseURL + this.route.url + '/interfaces/');
    }

    getAllArtifactTypes(): Observable<NameAndQNameApiData[]> {
        return this.http.get<NameAndQNameApiData[]>(backendBaseURL + '/artifacttypes');
    }

    getAllArtifactTemplates(): Observable<NameAndQNameApiData[]> {
        return this.http.get<NameAndQNameApiData[]>(backendBaseURL + '/artifacttemplates');
    }
}
