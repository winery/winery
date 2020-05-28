/********************************************************************************
 * Copyright(c) 2018-2020 Contributors to the Eclipse Foundation
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
import { Subject } from 'rxjs/Subject';
import { isNullOrUndefined } from 'util';
import { EntityType, TTopologyTemplate } from '../models/ttopology-template';
import { QNameWithTypeApiData } from '../models/generateArtifactApiData';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { urlElement } from '../models/enums';
import { ServiceTemplateId } from '../models/serviceTemplateId';
import { ToscaDiff } from '../models/ToscaDiff';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';
import { concat, forkJoin } from 'rxjs';
import { TopologyModelerConfiguration } from '../models/topologyModelerConfiguration';
import { ErrorHandlerService } from './error-handler.service';
import { ThreatCreation } from '../models/threatCreation';
import { Threat, ThreatAssessmentApiData } from '../models/threatModelingModalData';
import { Visuals } from '../models/visuals';
import { VersionElement } from '../models/versionElement';
import { WineryRepositoryConfigurationService } from '../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { takeLast } from 'rxjs/operators';
import { TPolicy } from '../models/policiesModalData';

/**
 * Responsible for interchanging data between the app and the server.
 */
@Injectable()
export class BackendService {

    readonly headers = new HttpHeaders().set('Accept', 'application/json');

    configuration: TopologyModelerConfiguration;
    serviceTemplateURL: string;
    serviceTemplateUiUrl: string;

    endpointConfiguration = new Subject<any>();
    endpointConfiguration$ = this.endpointConfiguration.asObservable();

    private allEntities = new Subject<any>();
    allEntities$ = this.allEntities.asObservable();

    constructor(private http: HttpClient,
                private alert: ToastrService,
                private errorHandler: ErrorHandlerService,
                private configurationService: WineryRepositoryConfigurationService) {
        this.endpointConfiguration$.subscribe((params: TopologyModelerConfiguration) => {
            if (!(isNullOrUndefined(params.id) && isNullOrUndefined(params.ns) &&
                isNullOrUndefined(params.repositoryURL) && isNullOrUndefined(params.uiURL))) {

                this.configuration = new TopologyModelerConfiguration(
                    params.id,
                    params.ns,
                    params.repositoryURL,
                    params.uiURL,
                    params.compareTo,
                    params.compareTo ? true : params.isReadonly,
                    params.parentPath,
                    params.elementPath,
                    params.topologyProDecURL
                );

                const url = this.configuration.parentPath + '/'
                    + encodeURIComponent(encodeURIComponent(this.configuration.ns)) + '/'
                    + this.configuration.id;
                this.serviceTemplateURL = this.configuration.repositoryURL + '/' + url;
                this.serviceTemplateUiUrl = this.configuration.uiURL + url;

                // All Entity types
                this.requestAllEntitiesAtOnce().subscribe(data => {
                    // add JSON to Promise, WineryComponent will subscribe to its Observable
                    this.allEntities.next(data);
                });
            }
        });
    }

    /**
     * Requests all entities together.
     * We use forkJoin() to await all responses from the backend.
     * This is required
     */
    private requestAllEntitiesAtOnce(): Observable<any> {
        if (this.configuration) {
            return forkJoin(
                this.requestGroupedNodeTypes(),
                this.requestArtifactTemplates(),
                this.requestTopologyTemplateAndVisuals(),
                this.requestArtifactTypes(),
                this.requestPolicyTypes(),
                this.requestCapabilityTypes(),
                this.requestRequirementTypes(),
                this.requestPolicyTemplates(),
                this.requestRelationshipTypes(),
                this.requestNodeTypes(),
                this.requestVersionElements(),
                this.configurationService.getConfigurationFromBackend(this.configuration.repositoryURL)
            );
        }
    }

    /**
     * Requests topologyTemplate and visualappearances together. If the topology should be compared, it also gets
     * the old topology as well as the diff representation.
     * We use Observable.forkJoin to await all responses from the backend.
     * This is required
     */
    private requestTopologyTemplateAndVisuals(): Observable<any> {
        if (this.configuration) {
            const nodeVisualsUrl = this.configuration.repositoryURL + '/nodetypes/allvisualappearancedata';
            const relationshipVisualsUrl = this.configuration.repositoryURL + '/relationshiptypes/allvisualappearancedata';
            const policyVisualsUrl = this.configuration.repositoryURL + '/policytemplates/allvisualappearancedata';
            const policyTypesVisualsUrl = this.configuration.repositoryURL + '/policytypes/allvisualappearancedata';
            // This is required because the information has to be returned together

            if (isNullOrUndefined(this.configuration.compareTo)) {
                return forkJoin(
                    this.http.get<TTopologyTemplate>(this.configuration.elementUrl),
                    this.http.get<Visuals>(nodeVisualsUrl),
                    this.http.get<Visuals>(relationshipVisualsUrl),
                    this.http.get<Visuals>(policyVisualsUrl),
                    this.http.get<Visuals>(policyTypesVisualsUrl),
                );
            } else {
                const url = this.configuration.repositoryURL + '/' + this.configuration.parentPath + '/'
                    + encodeURIComponent(encodeURIComponent(this.configuration.ns)) + '/';
                const compareUrl = url
                    + this.configuration.id + '/?compareTo='
                    + this.configuration.compareTo;
                const templateUrl = url
                    + this.configuration.compareTo + '/topologytemplate';

                return forkJoin(
                    this.http.get<TTopologyTemplate>(this.configuration.elementUrl),
                    this.http.get<Visuals>(nodeVisualsUrl),
                    this.http.get<Visuals>(relationshipVisualsUrl),
                    this.http.get<Visuals>(policyVisualsUrl),
                    this.http.get<Visuals>(policyTypesVisualsUrl),
                    this.http.get<ToscaDiff>(compareUrl),
                    this.http.get<TTopologyTemplate>(templateUrl)
                );
            }
        }
    }

    /**
     * Requests all policy types from the backend
     */
    private requestPolicyTypes(): Observable<any> {
        if (this.configuration) {
            return this.http.get(this.configuration.repositoryURL + '/policytypes?full', { headers: this.headers });
        }
    }

    /**
     * Requests all requirement types from the backend
     */
    private requestRequirementTypes(): Observable<any> {
        if (this.configuration) {
            return this.http.get(this.configuration.repositoryURL + '/requirementtypes?full', { headers: this.headers });
        }
    }

    /**
     * Requests all capability types from the backend
     */
    private requestCapabilityTypes(): Observable<any> {
        if (this.configuration) {
            return this.http.get(this.configuration.repositoryURL + '/capabilitytypes?full', { headers: this.headers });
        }
    }

    /**
     * Requests all grouped node types from the backend
     */
    private requestGroupedNodeTypes(): Observable<any> {
        if (this.configuration) {
            return this.http.get(this.configuration.repositoryURL + '/nodetypes?grouped&full', { headers: this.headers });
        }
    }

    /**
     * Requests all ungrouped node types from the backend
     */
    private requestNodeTypes(): Observable<any> {
        if (this.configuration) {
            return this.http.get(this.configuration.repositoryURL + '/nodetypes?full', { headers: this.headers });
        }
    }

    /**
     * Requests all artifact types from the backend
     */
    private requestArtifactTypes(): Observable<any> {
        if (this.configuration) {
            return this.http.get(this.configuration.repositoryURL + '/artifacttypes?full', { headers: this.headers });
        }
    }

    /**
     * Requests all artifact templates from the backend
     */
    requestArtifactTemplates(): Observable<any> {
        if (this.configuration) {
            return this.http.get(this.configuration.repositoryURL + '/artifacttemplates', { headers: this.headers });
        }
    }

    /**
     * Requests all policy templates from the backend
     */
    requestPolicyTemplates(): Observable<any> {
        if (this.configuration) {
            return this.http.get(this.configuration.repositoryURL + '/policytemplates', { headers: this.headers });
        }
    }

    /**
     * Requests all relationship types from the backend
     */
    private requestRelationshipTypes(): Observable<any> {
        if (this.configuration) {
            return this.http.get(this.configuration.repositoryURL + '/relationshiptypes?full', { headers: this.headers });
        }
    }

    /**
     * Requests all namespaces from the backend
     */
    requestNamespaces(all: boolean = false): Observable<any> {
        if (this.configuration) {
            let URL: string;
            if (all) {
                URL = this.configuration.repositoryURL + '/admin/namespaces/?all';
            } else {
                URL = this.configuration.repositoryURL + '/admin/namespaces/';
            }
            return this.http.get(URL, { headers: this.headers });
        }
    }

    /**
     * This method retrieves a single Artifact Template from the backend.
     */
    requestArtifactTemplate(artifact: QNameWithTypeApiData): Observable<any> {
        const url = this.configuration.repositoryURL + urlElement.ArtifactTemplateURL
            + encodeURIComponent(encodeURIComponent(artifact.namespace)) + '/' + artifact.localname;
        return this.http.get(url + '/', { headers: this.headers });
    }

    /**
     * This method retrieves a single Policy Template from the backend.
     */
    requestPolicyTemplate(artifact: QNameWithTypeApiData): Observable<any> {
        const url = this.configuration.repositoryURL + urlElement.PolicyTemplateURL
            + encodeURIComponent(encodeURIComponent(artifact.namespace)) + '/' + artifact.localname;
        return this.http.get(url + '/', { headers: this.headers });
    }

    /**
     * Saves the topologyTemplate back to the repository
     */
    saveTopologyTemplate(topologyTemplate: TTopologyTemplate): Observable<HttpResponse<string>> {
        if (this.configuration) {
            // Initialization
            const topologySkeleton = {
                documentation: [],
                any: [],
                otherAttributes: {},
                relationshipTemplates: [],
                nodeTemplates: [],
                policies: { policy: new Array<TPolicy>() }
            };
            // Prepare for saving by updating the existing topology with the current topology state inside the Redux store
            topologySkeleton.nodeTemplates = topologyTemplate.nodeTemplates;
            topologySkeleton.relationshipTemplates = topologyTemplate.relationshipTemplates;
            topologySkeleton.relationshipTemplates.map(relationship => {
                delete relationship.state;
            });
            // remove the 'Color' field from all nodeTemplates as the REST Api does not recognize it.
            topologySkeleton.nodeTemplates.map(nodeTemplate => {
                delete nodeTemplate.visuals;
                delete nodeTemplate._state;
            });
            topologySkeleton.policies = topologyTemplate.policies;
            console.log(topologySkeleton);

            const headers = new HttpHeaders().set('Content-Type', 'application/json');
            return this.http.put(this.configuration.elementUrl,
                topologySkeleton,
                { headers: headers, responseType: 'text', observe: 'response' }
            );
        }
    }

    saveYamlArtifact(topology: TTopologyTemplate,
                     nodeTemplateId: string,
                     artifactName: string,
                     file: File): Observable<HttpResponse<string>> {
        const url =
            `${this.serviceTemplateURL}${urlElement.TopologyTemplate}${urlElement.NodeTemplates}${nodeTemplateId}${urlElement.YamlArtifacts}/${artifactName}`;
        // handle entries managed by the backend
        const formData: FormData = new FormData();
        formData.append('file', file, file.name);

        // we save the new topology template first, and then post the artifact file.
        return concat(
            this.saveTopologyTemplate(topology),
            this.http.post(url, formData, { observe: 'response', responseType: 'text' }))
            .pipe(
                takeLast(1)
            );
    }

    downloadYamlArtifactFile(nodeTemplateId: string,
                             artifactName: string,
                             fileName: string) {
        const url =
            `${this.serviceTemplateURL}${urlElement.TopologyTemplate}${urlElement.NodeTemplates}${nodeTemplateId}${urlElement.YamlArtifacts}/${artifactName}/` +
            fileName;
        return this.http.get(url, { observe: 'response', responseType: 'blob' });
    }

    /**
     * Imports the template.
     */
    importTopology(importedTemplateQName: string): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders().set('Content-Type', 'text/plain');
        return this.http.post(this.configuration.elementPath + '/merge/',
            importedTemplateQName,
            { headers: headers, observe: 'response', responseType: 'text' }
        );
    }

    /**
     *
     */
    threatCatalogue(): Observable<Array<Threat>> {
        return this.http.get<Array<Threat>>(this.configuration.repositoryURL + '/threats');
    }

    /**
     *
     */
    threatCreation(data: ThreatCreation): Observable<string> {
        const url = this.configuration.repositoryURL;
        return this.http.post(url + '/threats', data, { responseType: 'text' });
    }

    /**
     *
     */
    threatAssessment(): Observable<ThreatAssessmentApiData> {
        return this.http.get<ThreatAssessmentApiData>(this.serviceTemplateURL + '/threatmodeling');
    }

    substituteTopology(): void {
        this.alert.info('', 'Substitution in progress...');
        this.http.get<ServiceTemplateId>(this.serviceTemplateURL + '/substitute')
            .subscribe(res => {
                    const url = window.location.origin + window.location.pathname + '?repositoryURL=' + this.configuration.repositoryURL
                        + '&uiURL=' + this.configuration.uiURL
                        + '&ns=' + res.namespace.encoded
                        + '&id=' + res.xmlId.encoded
                        + '&parentPath=' + this.configuration.parentPath
                        + '&elementPath=' + this.configuration.elementPath;
                    this.alert.success('automatically opening does not work currently...', 'Substitution successful!');
                },
                error => {
                    this.errorHandler.handleError(error);
                });
    }

    /**
     * Splits the template.
     */
    splitTopology(): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');
        return this.http.post(this.configuration.elementUrl + '/split/',
            {},
            { headers: headers, observe: 'response', responseType: 'text' }
        );
    }

    /**
     * Matches the template.
     */
    matchTopology(): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');
        return this.http.post(this.configuration.elementUrl + '/match/',
            {},
            { headers: headers, observe: 'response', responseType: 'text' }
        );
    }

    /**
     * Place the components of the topology.
     */
    placeComponents(): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');
        const url = this.serviceTemplateURL + urlElement.TopologyTemplate + 'applyplacement';
        return this.http.post(url, {}, { headers: headers, observe: 'response', responseType: 'text' });
    }

    /**
     * Used for creating new artifactOrPolicy templates on the backend.
     */
    createNewArtifactOrPolicy(artifactOrPolicy: QNameWithTypeApiData, type: string): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');
        let url;
        if (type === 'policy') {
            url = this.configuration.repositoryURL + urlElement.PolicyTemplateURL;
        } else {
            url = this.configuration.repositoryURL + urlElement.ArtifactTemplateURL;
        }
        return this.http.post(
            url,
            artifactOrPolicy,
            { headers: headers, responseType: 'text', observe: 'response' }
        );
    }

    /**
     * Requests all topology template ids
     */
    requestAllTopologyTemplates(): Observable<EntityType[]> {
        const url = this.configuration.repositoryURL + urlElement.ServiceTemplates;
        return this.http.get<EntityType[]>(url, { headers: this.headers });
    }

    requestVersionElements(): Observable<VersionElement[]> {
        if (this.configuration) {
            return this.http.get<VersionElement[]>(this.configuration.elementUrl + '/newversions', { headers: this.headers });
        }
    }
}
