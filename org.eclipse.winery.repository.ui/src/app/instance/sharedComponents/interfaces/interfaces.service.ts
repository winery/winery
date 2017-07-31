/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */
import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions, Response } from '@angular/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { GenerateArtifactApiData } from './generateArtifactApiData';
import { InterfacesApiData } from './interfacesApiData';
import { InstanceService } from '../../instance.service';
import { backendBaseURL } from '../../../configuration';
import { isNullOrUndefined } from 'util';

@Injectable()
export class InterfacesService {

    private path: string;
    private implementationsUrl: string;

    constructor(private http: Http,
                private route: Router, private sharedData: InstanceService) {
        this.path = decodeURIComponent(this.route.url);
        this.implementationsUrl = this.sharedData.selectedResource.replace(' ', '').toLowerCase() + 'implementations/';
    }

    getInterfaces(url?: string, relationshipInterfaces = false): Observable<InterfacesApiData[]> {
        if (isNullOrUndefined(url)) {
            return this.get(this.path + '/?noId=true')
                .map(res => res.json());
        } else if (relationshipInterfaces) {
            return this.getRelationshipInterfaces(url);
        } else {
            return this.get(url + '/interfaces/')
                .map(res => res.json());
        }
    }

    save(interfacesData: InterfacesApiData[]): Observable<Response> {
        const headers = new Headers({ 'Content-Type': 'application/json' });
        const options = new RequestOptions({ headers: headers });

        return this.http.post(backendBaseURL + this.path + '/', JSON.stringify(interfacesData), options);
    }

    createImplementation(implementationName: string, implementationNamespace: string): Observable<any> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.post(backendBaseURL + '/' + this.implementationsUrl,
            JSON.stringify({
                localname: implementationName,
                namespace: implementationNamespace,
                type: '{' + this.sharedData.selectedNamespace + '}' + this.sharedData.selectedComponentId
            }),
            options);
    }

    createArtifactTemplate(implementationName: string, implementationNamespace: string,
                                 generateArtifactApiData: GenerateArtifactApiData) {
        const headers = new Headers({ 'Content-Type': 'application/json' });
        const options = new RequestOptions({ headers: headers });

        return this.http.post(backendBaseURL + '/' + this.implementationsUrl
            + encodeURIComponent(encodeURIComponent(implementationNamespace)) + '/'
            + implementationName + '/implementationartifacts/',
            JSON.stringify(generateArtifactApiData), options);
    }

    getRelationshipInterfaces(url: string): Observable<InterfacesApiData[]> {
        return Observable
            .forkJoin(
                this.get(url + '/targetinterfaces/').map(res => res.json()),
                this.get(url + '/sourceinterfaces/').map(res => res.json())
            ).map(res => {
                for (const i of res[1]) {
                    res[0].push(i);
                }
                return res[0];
            });
    }

    private get(url: string): Observable<any> {
        const headers = new Headers({ 'Accept': 'application/json' });
        const options = new RequestOptions({ headers: headers });

        return this.http.get(backendBaseURL + url, options);
    }
}
