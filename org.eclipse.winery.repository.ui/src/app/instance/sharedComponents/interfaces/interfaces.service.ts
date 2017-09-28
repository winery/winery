/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
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
import { Utils } from '../../../wineryUtils/utils';

@Injectable()
export class InterfacesService {

    private path: string;
    private implementationsUrl: string;

    constructor(private http: Http,
                private route: Router, private sharedData: InstanceService) {
        this.path = backendBaseURL + this.route.url + '/';
        this.setImplementationsUrl();
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

        return this.http.post(this.path, JSON.stringify(interfacesData), options);
    }

    createImplementation(implementationName: string, implementationNamespace: string): Observable<any> {
        const headers = new Headers({ 'Content-Type': 'application/json' });
        const options = new RequestOptions({ headers: headers });

        this.setImplementationsUrl();

        return this.http.post(backendBaseURL + '/' + this.implementationsUrl,
            JSON.stringify({
                localname: implementationName,
                namespace: implementationNamespace,
                type: '{' + this.sharedData.toscaComponent.namespace + '}' + this.sharedData.toscaComponent.localName
            }),
            options);
    }

    createArtifactTemplate(implementationName: string, implementationNamespace: string,
                           generateArtifactApiData: GenerateArtifactApiData): Observable<Response> {
        const headers = new Headers({ 'Content-Type': 'application/json' });
        const options = new RequestOptions({ headers: headers });

        this.setImplementationsUrl();

        return this.http.post(backendBaseURL + '/' + this.implementationsUrl
            + encodeURIComponent(encodeURIComponent(implementationNamespace)) + '/'
            + implementationName + '/implementationartifacts/',
            JSON.stringify(generateArtifactApiData), options);
    }

    getRelationshipInterfaces(url: string): Observable<InterfacesApiData[]> {
        return Observable
            .forkJoin(
                this.get(backendBaseURL + url + '/targetinterfaces/').map(res => res.json()),
                this.get(backendBaseURL + url + '/sourceinterfaces/').map(res => res.json())
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

        return this.http.get(url, options);
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
