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
import { Headers, Http, RequestOptions } from '@angular/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../configuration';
import { InstanceService } from '../instance.service';
import { GenerateArtifactApiData } from './generateArtifactApiData';
import { InterfacesApiData } from './interfacesApiData';

@Injectable()
export class InterfacesService {

    private path: string;
    private interfaceType: string;

    constructor(private http: Http,
                private route: Router, private sharedData: InstanceService) {
        this.path = decodeURIComponent(this.route.url);
    }

    getInterfaces(): Observable<InterfacesApiData[]> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.get(backendBaseURL + this.path + '/', options)
            .map(res => res.json());
    }

    save(interfacesData: InterfacesApiData[]): Observable<any> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.post(backendBaseURL + this.path + '/', JSON.stringify(interfacesData), options);
    }

    createImplementation(resourceType: string, implementationName: string, implementationNamespace: string): Observable<any> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.post(backendBaseURL + '/' + resourceType + 'implementations/',
            JSON.stringify({
                localname: implementationName,
                namespace: implementationNamespace,
                type: '{' + this.sharedData.selectedNamespace + '}' + this.sharedData.selectedComponentId
            }),
            options);
    }

    createImplementationArtifact(resourceType: string, implementationName: string, implementationNamespace: string,
                                 generateArtifactApiData: GenerateArtifactApiData) {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.post(backendBaseURL + '/' + resourceType + 'implementations/'
            + encodeURIComponent(encodeURIComponent(implementationNamespace)) + '/'
            + implementationName + '/implementationartifacts/',
            JSON.stringify(generateArtifactApiData), options);
    }
}
