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
import { Observable, forkJoin } from 'rxjs';
import { GenerateArtifactApiData } from './generateArtifactApiData';
import { InterfacesApiData } from './interfacesApiData';
import { InstanceService } from '../../instance.service';
import { backendBaseURL } from '../../../configuration';
import { isNullOrUndefined } from 'util';
import { Utils } from '../../../wineryUtils/utils';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs/operators';

@Injectable()
export class InterfacesService {

    private readonly path: string;
    private implementationsUrl: string;
    private readonly header = new HttpHeaders({ 'Content-Type': 'application/json' });

    constructor(private http: HttpClient,
                private route: Router, private sharedData: InstanceService) {
        this.path = backendBaseURL + this.route.url + '/';
        this.setImplementationsUrl();
    }

    getInterfaces(url?: string, relationshipInterfaces = false): Observable<InterfacesApiData[]> {
        if (isNullOrUndefined(url)) {
            return this.get<InterfacesApiData[]>(this.path + '/?noId=true');
        } else if (relationshipInterfaces) {
            return this.getRelationshipInterfaces(url);
        } else {
            return this.get<InterfacesApiData[]>(backendBaseURL + url + '/interfaces/');
        }
    }

    save(interfacesData: InterfacesApiData[]): Observable<HttpResponse<string>> {
        return this.http
            .post(
                this.path,
                interfacesData,
                { headers: this.header, observe: 'response', responseType: 'text' }
            );
    }

    createImplementation(implementationName: string, implementationNamespace: string): Observable<HttpResponse<string>> {
        this.setImplementationsUrl();
        return this.http
            .post(
                backendBaseURL + '/' + this.implementationsUrl,
                JSON.stringify({
                    localname: implementationName,
                    namespace: implementationNamespace,
                    type: '{' + this.sharedData.toscaComponent.namespace + '}' + this.sharedData.toscaComponent.localName
                }),
                { headers: this.header, observe: 'response', responseType: 'text' }
            );
    }

    createArtifactTemplate(implementationName: string, implementationNamespace: string,
                           generateArtifactApiData: GenerateArtifactApiData): Observable<HttpResponse<string>> {
        this.setImplementationsUrl();
        const url = backendBaseURL + '/' + this.implementationsUrl + encodeURIComponent(encodeURIComponent(implementationNamespace)) + '/'
            + implementationName + '/implementationartifacts/';
        return this.http
            .post(
                url,
                generateArtifactApiData,
                { headers: this.header, observe: 'response', responseType: 'text' }
            );
    }

    getRelationshipInterfaces(url: string): Observable<InterfacesApiData[]> {
        return forkJoin(
                this.get<InterfacesApiData[]>(backendBaseURL + url + '/interfaces/'),
                this.get<InterfacesApiData[]>(backendBaseURL + url + '/targetinterfaces/'),
                this.get<InterfacesApiData[]>(backendBaseURL + url + '/sourceinterfaces/')
            ).pipe(map(res => {
                for (const i of res[1]) {
                    res[0].push(i);
                }
                return res[0];
            }));
    }

    private get<T>(url: string): Observable<T> {
        return this.http.get<T>(url);
    }

    /**
     * Because <code>this.sharedData.toscaComponent</code> can be null on initialisation, we need to get the URL
     * shortly before we use it again.
     */
    private setImplementationsUrl() {
        if (isNullOrUndefined(this.implementationsUrl) && !isNullOrUndefined(this.sharedData.toscaComponent)) {
            this.implementationsUrl = Utils.getImplementationOrTemplateOfType(this.sharedData.toscaComponent.toscaType) + '/';
        }
    }
}
