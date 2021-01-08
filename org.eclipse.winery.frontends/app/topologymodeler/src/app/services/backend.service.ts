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
import {
    Entity, EntityType, TArtifactType, TDataType, TPolicyType, TTopologyTemplate, VisualEntityType
} from '../models/ttopology-template';
import { QNameWithTypeApiData } from '../models/generateArtifactApiData';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { urlElement } from '../models/enums';
import { ServiceTemplateId } from '../models/serviceTemplateId';
import { ToscaDiff } from '../models/ToscaDiff';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject, concat, forkJoin, of } from 'rxjs';
import { TopologyModelerConfiguration } from '../models/topologyModelerConfiguration';
import { ErrorHandlerService } from './error-handler.service';
import { ThreatCreation } from '../models/threatCreation';
import { Threat, ThreatAssessmentApiData } from '../models/threatModelingModalData';
import { Visuals } from '../models/visuals';
import { VersionElement } from '../models/versionElement';
import { WineryRepositoryConfigurationService } from '../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { takeLast } from 'rxjs/operators';
import { TPolicy } from '../models/policiesModalData';
import { EntityTypesModel } from '../models/entityTypesModel';
import { ToscaUtils } from '../models/toscaUtils';
import { TopologyTemplateUtil } from '../models/topologyTemplateUtil';

/**
 * Responsible for interchanging data between the app and the server.
 */
@Injectable()
export class BackendService {

    readonly headers = new HttpHeaders().set('Accept', 'application/json');

    configuration: TopologyModelerConfiguration;
    serviceTemplateURL: string;
    serviceTemplateUiUrl: string;

    private loaded = new Subject<boolean>();
    loaded$ = this.loaded.asObservable();

    // use stored model to aggregate data
    private storedModel: EntityTypesModel = new EntityTypesModel();
    // BehaviourSubject allows caching the latest value for subscribers
    model$ = new BehaviorSubject<EntityTypesModel>(this.storedModel);

    // TODO avoid splitting the stored data into four different subjects including a loaded state
    private topologyTemplate: TTopologyTemplate;
    private topTemplate = new Subject<TTopologyTemplate>();
    topTemplate$ = this.topTemplate.asObservable();

    private topologyDifferences: [ToscaDiff, TTopologyTemplate];
    private topDiff = new Subject<[ToscaDiff, TTopologyTemplate]>();
    topDiff$ = this.topDiff.asObservable();

    constructor(private http: HttpClient,
                private alert: ToastrService,
                private errorHandler: ErrorHandlerService,
                private configurationService: WineryRepositoryConfigurationService) {
    }

    public configure(params: TopologyModelerConfiguration) {
        if (params.id && params.ns && params.repositoryURL && params.uiURL) {
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
            this.requestAllEntitiesAtOnce().subscribe(r => this.handleAllEntitiesResult(r));
        }
    }

    private handleAllEntitiesResult(results: [any, any, boolean]) {
        const templateAndVisuals = results[0];

        this.topologyTemplate = BackendService.patchProperties(templateAndVisuals[0]);
        const visuals = templateAndVisuals[1];
        const diff = templateAndVisuals[2];

        this.storedModel.nodeVisuals = visuals[0];
        this.storedModel.relationshipVisuals = visuals[1];
        this.storedModel.policyTemplateVisuals = visuals[2];
        this.storedModel.policyTypeVisuals = visuals[3];
        this.topologyDifferences = diff;
        // FIXME EWWWWW!
        if (this.topologyDifferences[0] !== undefined && this.topologyDifferences[1] !== undefined) {
            this.topDiff.next(this.topologyDifferences);
        }

        // entity types are encapsulated in a separate forkJoin
        const entityTypes = results[1];
        this.initEntityType(entityTypes[0], 'groupedNodeTypes');
        this.initEntityType(entityTypes[1], 'artifactTemplates');
        this.initEntityType(entityTypes[2], 'artifactTypes');
        this.initEntityType(entityTypes[3], 'policyTypes');
        this.initEntityType(entityTypes[4], 'capabilityTypes');
        this.initEntityType(entityTypes[5], 'requirementTypes');
        this.initEntityType(entityTypes[6], 'policyTemplates');
        this.initEntityType(entityTypes[7], 'relationshipTypes');
        this.initEntityType(entityTypes[8], 'unGroupedNodeTypes');
        this.initEntityType(entityTypes[9], 'versionElements');

        // handle YAML specifics
        if (this.configurationService.isYaml()) {
            this.initEntityType(entityTypes[10], 'dataTypes');
            // init YAML policies if they exist
            if (this.topologyTemplate.policies) {
                this.initEntityType(this.topologyTemplate.policies.policy, 'yamlPolicies');
            } else {
                this.initEntityType([], 'yamlPolicies');
            }
        }

        this.model$.next(this.storedModel);
        // FIXME there is currently some temporal coupling in winery component that requires us to push the model before the topologyTemplate
        this.topTemplate.next(this.topologyTemplate);
        this.loaded.next(true);
    }

    /**
     * Requests all entities together.
     * We use forkJoin() to await all responses from the backend.
     * This is required
     */
    private requestAllEntitiesAtOnce(): Observable<[any, any, boolean]> {
        if (this.configuration) {
            // TODO latest rxjs allows passing a dictionary to forkJoin to get a strongly typed object instead
            //  that would allow us to change this mess to an Observable[TTopologyTemplate, EntityTypesModel, [ToscaDiff, TTopologyTemplate], boolean]
            //  or even encapsulate that complication into a single type
            return forkJoin<any, any, boolean>([
                forkJoin<TTopologyTemplate, any[], [ToscaDiff, TTopologyTemplate]>([
                    this.requestTopologyTemplate(),
                    forkJoin([
                        this.requestNodeVisuals(),
                        this.requestRelationshipVisuals(),
                        this.requestPolicyVisuals(),
                        this.requestPolicyTypesVisuals(),
                    ]),
                    this.requestTopologyDiff(),
                ]),
                forkJoin<any[]>([
                    this.requestGroupedNodeTypes(),
                    this.requestArtifactTemplates(),
                    this.requestArtifactTypes(),
                    this.requestPolicyTypes(),
                    this.requestCapabilityTypes(),
                    this.requestRequirementTypes(),
                    this.requestPolicyTemplates(),
                    this.requestRelationshipTypes(),
                    this.requestNodeTypes(),
                    this.requestVersionElements(),
                    this.requestDataTypes(),
                ]),
                this.configurationService.getConfigurationFromBackend(this.configuration.repositoryURL),
            ]);
        }
    }

    /**
     * Save the received Array of Entity Types inside the respective variables in the entityTypes array of arrays
     * which is getting passed to the palette and the topology renderer
     */
    // FIXME push the mappings here into the request methods instead and strongly type the observable they yield
    private initEntityType(entityTypeJSON: Array<any>, entityType: string): void {
        if (!entityTypeJSON || entityTypeJSON.length === 0) {
            this.alert.info('No ' + entityType + ' available!');
        }

        switch (entityType) {
            case 'yamlPolicies': {
                this.storedModel.yamlPolicies = [];
                entityTypeJSON.forEach(policy => {
                    this.storedModel.yamlPolicies.push(
                        new TPolicy(
                            policy.name,
                            policy.policyRef,
                            policy.policyType,
                            policy.any,
                            policy.documentation,
                            policy.otherAttributes,
                            policy.properties,
                            policy.targets)
                    );
                });
                break;
            }
            case 'artifactTypes': {
                this.storedModel.artifactTypes = [];
                entityTypeJSON.forEach(artifactType => {

                    this.storedModel.artifactTypes
                        .push(new TArtifactType(
                            artifactType.id,
                            artifactType.qName,
                            artifactType.name,
                            artifactType.namespace,
                            artifactType.full,
                            artifactType.properties,
                            artifactType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].mimeType,
                            artifactType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].fileExtensions
                        ));
                });
                break;
            }
            case 'artifactTemplates': {
                this.storedModel.artifactTemplates = entityTypeJSON;
                break;
            }
            case 'policyTypes': {
                this.storedModel.policyTypes = [];
                entityTypeJSON.forEach(element => {
                    const policyType = new TPolicyType(element.id,
                        element.qName,
                        element.name,
                        element.namespace,
                        element.properties,
                        element.full);
                    if (element.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].appliesTo) {
                        policyType.targets = element.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].appliesTo
                            .nodeTypeReference.map(ntr => ntr.typeRef);
                    }
                    this.storedModel.policyTypes.push(policyType);
                });
                break;
            }
            case 'capabilityTypes': {
                this.storedModel.capabilityTypes = [];
                entityTypeJSON.forEach(capabilityType => {
                    this.storedModel.capabilityTypes
                        .push(new EntityType(
                            capabilityType.id,
                            capabilityType.qName,
                            capabilityType.name,
                            capabilityType.namespace,
                            capabilityType.properties,
                            capabilityType.full
                        ));
                });
                break;
            }
            case 'requirementTypes': {
                this.storedModel.requirementTypes = [];
                entityTypeJSON.forEach(requirementType => {
                    this.storedModel.requirementTypes
                        .push(new EntityType(
                            requirementType.id,
                            requirementType.qName,
                            requirementType.name,
                            requirementType.namespace,
                            requirementType.properties,
                            requirementType.full
                        ));
                });
                break;
            }
            case 'policyTemplates': {
                this.storedModel.policyTemplates = [];
                entityTypeJSON.forEach(policyTemplate => {
                    this.storedModel.policyTemplates
                        .push(new Entity(
                            policyTemplate.id,
                            policyTemplate.qName,
                            policyTemplate.name,
                            policyTemplate.namespace
                        ));
                });
                break;
            }
            case 'groupedNodeTypes': {
                this.storedModel.groupedNodeTypes = entityTypeJSON;
                break;
            }
            case 'versionElements': {
                this.storedModel.versionElements = [];
                entityTypeJSON.forEach((versionElements => {
                    this.storedModel.versionElements.push(new VersionElement(versionElements.qName, versionElements.versions));
                }));
                break;
            }
            case 'unGroupedNodeTypes': {
                this.storedModel.unGroupedNodeTypes = entityTypeJSON;
                break;
            }
            case 'relationshipTypes': {
                this.storedModel.relationshipTypes = [];
                entityTypeJSON.forEach((relationshipType: EntityType) => {
                    const visuals = this.storedModel.relationshipVisuals
                        .find(value => value.typeId === relationshipType.qName);
                    this.storedModel.relationshipTypes
                        .push(new VisualEntityType(
                            relationshipType.id,
                            relationshipType.qName,
                            relationshipType.name,
                            relationshipType.namespace,
                            relationshipType.properties,
                            visuals.color,
                            relationshipType.full)
                        );
                });
                break;
            }
            case 'dataTypes': {
                this.storedModel.dataTypes = [];
                entityTypeJSON.forEach((dType: EntityType) => {
                    const definition = ToscaUtils.getDefinition(dType);
                    this.storedModel.dataTypes
                        .push(new TDataType(
                            dType.id,
                            dType.qName,
                            dType.name,
                            dType.namespace,
                            dType.properties,
                            dType.full,
                            definition.constraints,
                            definition.keySchema,
                            definition.entrySchema)
                        );
                });
                break;
            }
            default: {
                console.log(`attempting to add unhandled entityTypes of type ${entityType}`);
            }
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
            const headers = new HttpHeaders().set('Content-Type', 'application/json');
            return this.http.put(this.configuration.elementUrl,
                TopologyTemplateUtil.prepareSave(topologyTemplate),
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
        return this.http.post(`${this.serviceTemplateURL}${urlElement.TopologyTemplate}merge/`,
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
        return this.http.post(`${(this.configuration.repositoryURL)}/threats`, data, { responseType: 'text' });
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
                    this.alert.success('Automatically opening does not work currently: ' + url, 'Substitution successful!');
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

    /**
     * Patches the property values of all node- and relationship templates in the given topology.
     * @param topology
     */
    private static patchProperties(topology: TTopologyTemplate): TTopologyTemplate {
        function patchMembers(p: object): void {
            function jsonParse(v: string): string | object {
                let result;
                try {
                    result = JSON.parse(v);
                } catch (e) {
                    result = v;
                }
                return result;
            }

            for (const member in p) {
                if (!{}.hasOwnProperty.call(p, member)) {
                    // skipping object prototype inherited members to make tslint happy
                    continue;
                }
                const memberType = typeof (p[member]);
                if (memberType === 'string') {
                    const patched = jsonParse(p[member]);
                    if (typeof (patched) !== 'string') {
                        p[member] = patched;
                    }
                } else if (memberType === 'object') {
                    // recurse, just to be safe
                    patchMembers(p[member]);
                }
            }
        }

        for (const node of topology.nodeTemplates) {
            if (node.properties && node.properties.properties) {
                patchMembers(node.properties.properties);
            }
        }
        for (const rel of topology.relationshipTemplates) {
            if (rel.properties && rel.properties.properties) {
                patchMembers(rel.properties.properties);
            }
        }
        return topology;
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
    private requestGroupedNodeTypes(): Observable<any[]> {
        if (this.configuration) {
            return this.http.get<any[]>(this.configuration.repositoryURL + '/nodetypes?grouped&full', { headers: this.headers });
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
            return this.http.get<any>(this.configuration.repositoryURL + '/artifacttemplates', { headers: this.headers });
        }
    }

    /**
     * Requests all policy templates from the backend
     */
    requestPolicyTemplates(): Observable<Entity[]> {
        if (this.configuration) {
            return this.http.get<Entity[]>(this.configuration.repositoryURL + '/policytemplates', { headers: this.headers });
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

    private requestVersionElements(): Observable<VersionElement[]> {
        if (this.configuration) {
            return this.http.get<VersionElement[]>(this.configuration.elementUrl + '/newversions', { headers: this.headers });
        }
    }

    private requestDataTypes(): Observable<EntityType[]> {
        if (this.configuration) {
            return this.http.get<EntityType[]>(this.configuration.repositoryURL + '/datatypes?full', { headers: this.headers });
        }
    }

    private requestTopologyTemplate(): Observable<TTopologyTemplate> {
        if (this.configuration) {
            return this.http.get<TTopologyTemplate>(this.configuration.elementUrl);
        }
    }

    private requestNodeVisuals(): Observable<Visuals> {
        if (this.configuration) {
            return this.http.get<Visuals>(this.configuration.repositoryURL + '/nodetypes/allvisualappearancedata');
        }
    }

    private requestRelationshipVisuals(): Observable<Visuals> {
        if (this.configuration) {
            return this.http.get<Visuals>(this.configuration.repositoryURL + '/relationshiptypes/allvisualappearancedata');
        }
    }

    private requestPolicyVisuals(): Observable<Visuals> {
        if (this.configuration) {
            return this.http.get<Visuals>(this.configuration.repositoryURL + '/policytemplates/allvisualappearancedata');
        }
    }

    private requestPolicyTypesVisuals(): Observable<Visuals> {
        if (this.configuration) {
            return this.http.get<Visuals>(this.configuration.repositoryURL + '/policytypes/allvisualappearancedata');
        }
    }

    private requestTopologyDiff(): Observable<[ToscaDiff, TTopologyTemplate]> {
        if (this.configuration) {
            if (this.configuration.compareTo) {
                const url = this.configuration.repositoryURL + '/' + this.configuration.parentPath + '/'
                    + encodeURIComponent(encodeURIComponent(this.configuration.ns)) + '/';
                const compareUrl = url
                    + this.configuration.id + '/?compareTo='
                    + this.configuration.compareTo;
                const templateUrl = url
                    + this.configuration.compareTo + '/topologytemplate';

                return forkJoin(
                    this.http.get<ToscaDiff>(compareUrl),
                    this.http.get<TTopologyTemplate>(templateUrl)
                );
            } else {
                return of<[ToscaDiff, TTopologyTemplate]>([undefined, undefined]);
            }
        }
    }
}
