/********************************************************************************
 * Copyright(c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/catch';
import { ActivatedRoute } from '@angular/router';
import { backendBaseURL, hostURL } from '../models/configuration';
import { Subject } from 'rxjs/Subject';
import { isNullOrUndefined } from 'util';
import { EntityType, TTopologyTemplate, Visuals } from '../models/ttopology-template';
import { QNameWithTypeApiData } from '../models/generateArtifactApiData';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { WineryAlertService } from '../winery-alert/winery-alert.service';
import { urlElement } from '../models/enums';
import { ToscaDiff } from '../models/ToscaDiff';

/**
 * Responsible for interchanging data between the app and the server.
 */
@Injectable()
export class BackendService {
    readonly headers = new HttpHeaders().set('Accept', 'application/json');
    // readonly options = new RequestOptions({headers: this.headers});

    entityLoaded = {
        topologyTemplatesDiffAndVisuals: false,
        artifactTypes: false,
        artifactTemplates: false,
        policyTypes: false,
        policyTemplates: false,
        capabilityTypes: false,
        requirementTypes: false,
        groupedNodeTypes: false,
        ungroupedNodeTypes: false,
        relationshipTypes: false,
    };
    allEntitiesLoaded = false;

    configuration: TopologyModelerConfiguration;
    topologyTemplateURL;

    private serviceTemplate = new Subject<any>();
    serviceTemplate$ = this.serviceTemplate.asObservable();

    private visuals = new Subject<any>();
    visuals$ = this.visuals.asObservable();

    private policyTypes = new Subject<any>();
    policyTypes$ = this.policyTypes.asObservable();

    private policyTemplates = new Subject<any>();
    policyTemplates$ = this.policyTemplates.asObservable();

    private capabilityTypes = new Subject<any>();
    capabilityTypes$ = this.capabilityTypes.asObservable();

    private requirementTypes = new Subject<any>();
    requirementTypes$ = this.requirementTypes.asObservable();

    private artifactTypes = new Subject<any>();
    artifactTypes$ = this.artifactTypes.asObservable();

    private artifactTemplates = new Subject<any>();
    artifactTemplates$ = this.artifactTemplates.asObservable();

    private groupedNodeTypes = new Subject<any>();
    groupedNodeTypes$ = this.groupedNodeTypes.asObservable();

    private nodeTypes = new Subject<any>();
    nodeTypes$ = this.nodeTypes.asObservable();

    private relationshipTypes = new Subject<any>();
    relationshipTypes$ = this.relationshipTypes.asObservable();

    private topologyTemplatesDiffAndVisuals = new Subject<[TTopologyTemplate, Visuals, ToscaDiff, TTopologyTemplate]>();
    topologyTemplatesDiffAndVisuals$ = this.topologyTemplatesDiffAndVisuals.asObservable();

    constructor(private http: HttpClient,
                private activatedRoute: ActivatedRoute,
                private alert: WineryAlertService) {

        this.activatedRoute.queryParams.subscribe((params: TopologyModelerConfiguration) => {
            if (!(isNullOrUndefined(params.id) &&
                isNullOrUndefined(params.ns) &&
                isNullOrUndefined(params.repositoryURL) &&
                isNullOrUndefined(params.uiURL))) {
                this.configuration = params;
                this.topologyTemplateURL = this.configuration.repositoryURL + '/servicetemplates/'
                    + encodeURIComponent(encodeURIComponent(this.configuration.ns)) + '/'
                    + this.configuration.id;
                console.log(this.topologyTemplateURL);
                // ServiceTemplate / TopologyTemplate
                this.requestServiceTemplate().subscribe(data => {
                    // add JSON to Promise, WineryComponent will subscribe to its Observable
                    this.serviceTemplate.next(data);
                });
                // NodeType Visuals
                this.requestAllNodeTemplateVisuals().subscribe(data => {
                    // add JSON to Promise, WineryComponent will subscribe to its Observable
                    this.visuals.next(data);
                });
                // TopologyTemplate and Visuals together
                this.requestTopologyTemplateAndVisuals().subscribe(data => {
                    this.entityLoaded.topologyTemplatesDiffAndVisuals = true;
                    this.topologyTemplatesDiffAndVisuals.next(data);
                });
                // Policy Types
                this.requestPolicyTypes().subscribe(data => {
                    // add JSON to Promise, WineryComponent will subscribe to its Observable
                    this.entityLoaded.policyTypes = true;
                    this.policyTypes.next(data);
                });
                // Policy Templates
                this.requestPolicyTemplates().subscribe(data => {
                    // add JSON to Promise, WineryComponent will subscribe to its Observable
                    this.entityLoaded.policyTemplates = true;
                    this.policyTemplates.next(data);
                });
                // Capability Types
                this.requestCapabilityTypes().subscribe(data => {
                    // add JSON to Promise, WineryComponent will subscribe to its Observable
                    this.entityLoaded.capabilityTypes = true;
                    this.capabilityTypes.next(data);
                });
                // Requirement Types
                this.requestRequirementTypes().subscribe(data => {
                    // add JSON to Promise, WineryComponent will subscribe to its Observable
                    this.entityLoaded.requirementTypes = true;
                    this.requirementTypes.next(data);
                });
                // Artifact Types
                this.requestArtifactTypes().subscribe(data => {
                    // add JSON to Promise, WineryComponent will subscribe to its Observable
                    this.entityLoaded.artifactTypes = true;
                    this.artifactTypes.next(data);
                });
                // Artifact Templates
                this.requestArtifactTemplates().subscribe(data => {
                    // add JSON to Promise, WineryComponent will subscribe to its Observable
                    this.entityLoaded.artifactTemplates = true;
                    this.artifactTemplates.next(data);
                });
                // Grouped NodeTypes
                this.requestGroupedNodeTypes().subscribe(data => {
                    // add JSON to Promise, WineryComponent will subscribe to its Observable
                    this.entityLoaded.groupedNodeTypes = true;
                    this.groupedNodeTypes.next(data);
                });
                // NodeTypes
                this.requestNodeTypes().subscribe(data => {
                    // add JSON to Promise, WineryComponent will subscribe to its Observable
                    this.entityLoaded.ungroupedNodeTypes = true;
                    this.nodeTypes.next(data);
                });
                // Relationship Types
                this.requestRelationshipTypes().subscribe(data => {
                    this.entityLoaded.relationshipTypes = true;
                    // add JSON to Promise, WineryComponent will subscribe to its Observable
                    this.relationshipTypes.next(data);
                });
            } else {
                // TODO: how does it have to behave when no params are specified?
            }
        });

        this.everythingLoaded().then(() => {
            console.log('all data arrived');

            console.log(this.entityLoaded);
            // TODO: fire actual event here
        });

    }

    everythingLoaded() {
        return new Promise((resolve) => {
            if (this.entityLoaded.topologyTemplatesDiffAndVisuals &&
                this.entityLoaded.artifactTypes &&
                this.entityLoaded.artifactTemplates &&
                this.entityLoaded.policyTypes &&
                this.entityLoaded.policyTemplates &&
                this.entityLoaded.capabilityTypes &&
                this.entityLoaded.requirementTypes &&
                this.entityLoaded.groupedNodeTypes &&
                this.entityLoaded.ungroupedNodeTypes &&
                this.entityLoaded.relationshipTypes) {
                resolve(true);
            } else {
                resolve(false);
            }
        });
    }

    /**
     * Requests topologyTemplate and visualappearances together. If the topology should be compared, it also gets
     * the old topology as well as the diff representation.
     * We use Observable.forkJoin to await all responses from the backend.
     * This is required
     * @returns data  The JSON from the server
     */
    requestTopologyTemplateAndVisuals(): Observable<any> {
        if (this.configuration) {
            const url = this.configuration.repositoryURL + '/servicetemplates/'
                + encodeURIComponent(encodeURIComponent(this.configuration.ns)) + '/';
            const currentUrl = url + this.configuration.id + '/topologytemplate/';
            const visualsUrl = backendBaseURL + '/nodetypes/allvisualappearancedata';
            // This is required because the information has to be returned together

            if (isNullOrUndefined(this.configuration.compareTo)) {
                return Observable.forkJoin(
                    this.http.get<TTopologyTemplate>(currentUrl),
                    this.http.get<Visuals>(visualsUrl)
                );
            } else {
                const compareUrl = url
                    + this.configuration.id + '/?compareTo='
                    + this.configuration.compareTo;
                const templateUrl = url
                    + this.configuration.compareTo + '/topologytemplate';

                return Observable.forkJoin(
                    this.http.get<TTopologyTemplate>(currentUrl),
                    this.http.get<Visuals>(visualsUrl),
                    this.http.get<ToscaDiff>(compareUrl),
                    this.http.get<TTopologyTemplate>(templateUrl)
                );
            }
        }
    }

    /**
     * Requests data from the server
     * @returns data  The JSON from the server
     */
    requestServiceTemplate(): Observable<Object> {
        if (this.configuration) {
            const url = this.configuration.repositoryURL + '/servicetemplates/'
                + encodeURIComponent(encodeURIComponent(this.configuration.ns)) + '/'
                + this.configuration.id + '/topologytemplate/';
            return this.http.get(url, { headers: this.headers });
        }
    }

    /**
     * Returns data that is later used by jsPlumb to render a relationship connector
     * @returns data The JSON from the server
     */
    requestRelationshipTypeVisualappearance(namespace: string, id: string): Observable<EntityType> {
        if (this.configuration) {
            const url = this.configuration.repositoryURL + '/relationshiptypes/'
                + encodeURIComponent(encodeURIComponent(namespace)) + '/'
                + id + '/visualappearance/';
            return this.http.get<EntityType>(url, { headers: this.headers })
                .map(relationship => {
                    if (!isNullOrUndefined(this.configuration.compareTo)) {
                        relationship.color = 'grey';
                    }
                    return relationship;
                });
        }
    }

    /**
     * Requests all visual appearances used for the NodeTemplates
     * @returns {Observable<string>}
     */
    requestAllNodeTemplateVisuals(): Observable<any> {
        if (this.configuration) {
            return this.http.get(backendBaseURL + '/nodetypes/allvisualappearancedata', { headers: this.headers });
        }
    }

    /**
     * Requests all policy types from the backend
     * @returns {Observable<string>}
     */
    requestPolicyTypes(): Observable<any> {
        if (this.configuration) {
            return this.http.get(backendBaseURL + '/policytypes?full', { headers: this.headers });
        }
    }

    /**
     * Requests all requirement types from the backend
     * @returns {Observable<string>}
     */
    requestRequirementTypes(): Observable<any> {
        if (this.configuration) {
            return this.http.get(backendBaseURL + '/requirementtypes?full', { headers: this.headers });
        }
    }

    /**
     * Requests all capability types from the backend
     * @returns {Observable<string>}
     */
    requestCapabilityTypes(): Observable<any> {
        if (this.configuration) {
            return this.http.get(backendBaseURL + '/capabilitytypes?full', { headers: this.headers });
        }
    }

    /**
     * Requests all grouped node types from the backend
     * @returns {Observable<string>}
     */
    requestGroupedNodeTypes(): Observable<any> {
        if (this.configuration) {
            return this.http.get(backendBaseURL + '/nodetypes?grouped&full', { headers: this.headers });
        }
    }

    /**
     * Requests all ungrouped node types from the backend
     * @returns {Observable<string>}
     */
    requestNodeTypes(): Observable<any> {
        if (this.configuration) {
            return this.http.get(backendBaseURL + '/nodetypes?full', { headers: this.headers });
        }
    }

    /**
     * Requests all policy templates from the backend
     * @returns {Observable<string>}
     */
    requestPolicyTemplates(): Observable<any> {
        if (this.configuration) {
            return this.http.get(backendBaseURL + '/policytemplates', { headers: this.headers });
        }
    }

    /**
     * Requests all artifact types from the backend
     * @returns {Observable<string>}
     */
    requestArtifactTypes(): Observable<any> {
        if (this.configuration) {
            return this.http.get(backendBaseURL + '/artifacttypes', { headers: this.headers });
        }
    }

    /**
     * Requests all artifact templates from the backend
     * @returns {Observable<string>}
     */
    requestArtifactTemplates(): Observable<any> {
        if (this.configuration) {
            return this.http.get(backendBaseURL + '/artifacttemplates', { headers: this.headers });
        }
    }

    /**
     * Requests all relationship types from the backend
     * @returns {Observable<string>}
     */
    requestRelationshipTypes(): Observable<any> {
        if (this.configuration) {
            return this.http.get(backendBaseURL + '/relationshiptypes', { headers: this.headers });
        }
    }

    /**
     * Requests all namespaces from the backend
     * @returns {Observable<any>} json of namespaces
     */
    requestNamespaces(all: boolean = false): Observable<any> {
        if (this.configuration) {
            let URL: string;
            if (all) {
                URL = backendBaseURL + '/admin/namespaces/?all';
            } else {
                URL = backendBaseURL + '/admin/namespaces/';
            }
            return this.http.get(URL, { headers: this.headers });
        }
    }

    /**
     * This method retrieves a single Artifact Template from the backend.
     * @param {QNameWithTypeApiData} artifact
     * @returns {Observable<any>}
     */
    requestArtifactTemplate(artifact: QNameWithTypeApiData): Observable<any> {
        const url = this.configuration.repositoryURL + '/artifacttemplates/'
            + encodeURIComponent(encodeURIComponent(artifact.namespace)) + '/' + artifact.localname;
        return this.http.get(url + '/', { headers: this.headers });
    }

    /**
     * This method retrieves a single Policy Template from the backend.
     * @param {QNameWithTypeApiData} artifact
     * @returns {Observable<any>}
     */
    requestPolicyTemplate(artifact: QNameWithTypeApiData): Observable<any> {
        const url = this.configuration.repositoryURL + '/policytemplates/'
            + encodeURIComponent(encodeURIComponent(artifact.namespace)) + '/' + artifact.localname;
        return this.http.get(url + '/', { headers: this.headers });
    }

    /**
     * Saves the topologyTemplate back to the repository
     * @returns {Observable<Response>}
     */
    saveTopologyTemplate(topologyTemplate: any): Observable<HttpResponse<string>> {
        if (this.configuration) {
            const headers = new HttpHeaders().set('Content-Type', 'application/json');
            const url = this.configuration.repositoryURL + '/servicetemplates/'
                + encodeURIComponent(encodeURIComponent(this.configuration.ns)) + '/'
                + this.configuration.id + '/topologytemplate/';

            return this.http.put(url, topologyTemplate, {
                headers: headers, responseType: 'text', observe: 'response'
            });
        }
    }

    /**
     * Imports the template.
     * @returns {Observable<any>}
     */
    importTopology(importedTemplateQName: string): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders().set('Content-Type', 'text/plain');
        const url = this.topologyTemplateURL + urlElement.TopologyTemplate + 'merge';
        return this.http.post(url + '/', importedTemplateQName, { headers: headers,  observe: 'response', responseType: 'text' });
    }

    /**
     * Splits the template.
     * @returns {Observable<any>}
     */
    splitTopology(): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');
        const url = this.topologyTemplateURL + urlElement.TopologyTemplate + 'split';
        return this.http.post(url + '/', {}, { headers: headers,  observe: 'response', responseType: 'text' });
    }

    /**
     * Matches the template.
     * @returns {Observable<any>}
     */
    matchTopology(): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');
        const url = this.topologyTemplateURL + urlElement.TopologyTemplate + 'match';
        return this.http.post(url + '/', {}, { headers: headers,  observe: 'response', responseType: 'text' });
    }

    /**
     * Used for creating new artifact templates on the backend.
     * @param {QNameWithTypeApiData} artifact
     * @returns {Observable<any>}
     */
    createNewArtifact(artifact: QNameWithTypeApiData): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');
        const url = this.configuration.repositoryURL + '/artifacttemplates/';
        return this.http.post(url + '/', artifact, { headers: headers, responseType: 'text', observe: 'response' });
    }

    /**
     * Used for getting the newly created artifact templates for further processing on the client.
     * @param {QNameWithTypeApiData} artifact
     * @returns {Observable<any>}
     */
    getNewlyCreatedArtifact(artifact: QNameWithTypeApiData): Observable<any> {
        const url = this.configuration.repositoryURL + '/artifacttemplates/'
            + encodeURIComponent(encodeURIComponent(artifact.namespace)) + '/' + artifact.localname;
        return this.http.get(url + '/', { headers: this.headers });
    }

    /**
     * Requests all topology template ids
     * @returns {Observable<string>}
     */
    requestAllTopologyTemplates(): Observable<EntityType[]> {
        const url = hostURL + urlElement.Winery + urlElement.ServiceTemplates;
        return this.http.get<EntityType[]>(url + '/', { headers: this.headers });
    }

    /*  saveVisuals(data: any): Observable<Response> {
     const headers = new Headers({ 'Content-Type': 'application/json' });
     const options = new RequestOptions({ headers: headers });
     return this.http.put(backendBaseURL + this.activatedRoute.url + '/', JSON.stringify(data), options);
     }*/
}

/**
 * Defines config of TopologyModeler.
 */
export class TopologyModelerConfiguration {
    readonly id: string;
    readonly ns: string;
    readonly repositoryURL: string;
    readonly uiURL: string;
    readonly compareTo: string;
}
