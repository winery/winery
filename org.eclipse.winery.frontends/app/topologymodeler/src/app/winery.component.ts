/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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

import { AfterViewInit, Component, Input, OnInit } from '@angular/core';
import {
    Entity, EntityType, TArtifactType, TNodeTemplate, TPolicyType, TRelationshipTemplate, TTopologyTemplate, VisualEntityType
} from './models/ttopology-template';
import { ILoaded, LoadedService } from './services/loaded.service';
import { AppReadyEventService } from './services/app-ready-event.service';
import { BackendService } from './services/backend.service';
import { Subscription } from 'rxjs';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from './redux/store/winery.store';
import { ToscaDiff } from './models/ToscaDiff';
import { isNullOrUndefined } from 'util';
import { TopologyTemplateUtil } from './models/topologyTemplateUtil';
import { EntityTypesModel, TopologyModelerInputDataFormat } from './models/entityTypesModel';
import { ActivatedRoute } from '@angular/router';
import { TopologyModelerConfiguration } from './models/topologyModelerConfiguration';
import { ToastrService } from 'ngx-toastr';
import { TopologyRendererState } from './redux/reducers/topologyRenderer.reducer';
import { VersionElement } from './models/versionElement';
import { TopologyRendererActions } from './redux/actions/topologyRenderer.actions';
import { WineryRepositoryConfigurationService } from '../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { TPolicy } from './models/policiesModalData';

/**
 * This is the root component of the topology modeler.
 */
@Component({
    selector: 'winery-topologymodeler',
    templateUrl: './winery.component.html',
    styleUrls: ['./winery.component.css']
})
export class WineryComponent implements OnInit, AfterViewInit {

    // If this input variable is not null, it means that data is passed to the topologymodeler to be rendered.
    @Input() topologyModelerData: TopologyModelerInputDataFormat;
    versionElements: VersionElement[];
    sidebarDeleteButtonClickEvent: any;
    nodeTemplates: Array<TNodeTemplate> = [];
    relationshipTemplates: Array<TRelationshipTemplate> = [];
    groupedNodeTypes: Array<any> = [];
    entityTypes: EntityTypesModel;
    hideNavBarState: boolean;
    subscriptions: Array<Subscription> = [];
    someNodeMissingCoordinates = false;

    // This variable is set via the topologyModelerData input and decides if the editing functionalities are enabled
    readonly: boolean;
    refiningType: string;

    topologyDifferences: [ToscaDiff, TTopologyTemplate];

    public loaded: ILoaded;
    private loadedRelationshipVisuals = 0;
    private requiredRelationshipVisuals: number;

    constructor(private loadedService: LoadedService,
                private appReadyEvent: AppReadyEventService,
                public backendService: BackendService,
                private ngRedux: NgRedux<IWineryState>,
                private actions: TopologyRendererActions,
                private alert: ToastrService,
                private activatedRoute: ActivatedRoute,
                private configurationService: WineryRepositoryConfigurationService) {
        this.subscriptions.push(this.ngRedux.select(state => state.wineryState.hideNavBarAndPaletteState)
            .subscribe(hideNavBar => this.hideNavBarState = hideNavBar));
        this.subscriptions.push(this.ngRedux.select(state => state.topologyRendererState)
            .subscribe(currentButtonsState => this.setButtonsState(currentButtonsState)));
    }

    /**
     * Angular LifeCycle function OnInit().
     * All necessary data is being requested inside this function via the backendService instance.
     * The data is passed to various init...() functions that parse the received JSON data into Objects that get stored
     * inside the Redux store of this application.
     */
    ngOnInit() {
        this.entityTypes = new EntityTypesModel();

        if (this.topologyModelerData) {
            if (this.topologyModelerData.configuration.isReadonly) {
                this.readonly = true;
            }
            // If data is passed to the topologymodeler directly, rendering is initiated immediately without backend
            // calls
            if (this.topologyModelerData.topologyTemplate) {
                this.initiateLocalRendering(this.topologyModelerData);
            } else {
                if (this.topologyModelerData.configuration.repositoryURL) {
                    this.backendService.endpointConfiguration.next(this.topologyModelerData.configuration);
                } else {
                    this.activatedRoute.queryParams.subscribe((params: TopologyModelerConfiguration) => {
                        this.backendService.endpointConfiguration.next(params);
                    });
                    this.initiateData();
                }
            }
        } else {
            this.activatedRoute.queryParams.subscribe((params: TopologyModelerConfiguration) => {
                this.backendService.endpointConfiguration.next(params);
            });
            this.initiateData();
        }
    }

    ngAfterViewInit() {
        // auto layout when some nodes are missing coordinates
        if (this.someNodeMissingCoordinates) {
            this.ngRedux.dispatch(this.actions.executeLayout());
        }
    }

    /**
     * Save the received Array of Entity Types inside the respective variables in the entityTypes array of arrays
     * which is getting passed to the palette and the topology renderer
     */
    initEntityType(entityTypeJSON: Array<any>, entityType: string): void {
        if (!entityTypeJSON || entityTypeJSON.length === 0) {
            this.alert.info('No ' + entityType + ' available!');
        }

        switch (entityType) {
            case 'yamlPolicies': {
                this.entityTypes.yamlPolicies = [];
                entityTypeJSON.forEach(policy => {
                    this.entityTypes.yamlPolicies.push(
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
                this.entityTypes.artifactTypes = [];
                entityTypeJSON.forEach(artifactType => {

                    this.entityTypes.artifactTypes
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
                this.entityTypes.artifactTemplates = entityTypeJSON;
                break;
            }
            case 'policyTypes': {
                this.entityTypes.policyTypes = [];
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
                    this.entityTypes.policyTypes.push(policyType);
                });
                break;
            }
            case 'capabilityTypes': {
                this.entityTypes.capabilityTypes = [];
                entityTypeJSON.forEach(capabilityType => {
                    this.entityTypes.capabilityTypes
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
                this.entityTypes.requirementTypes = [];
                entityTypeJSON.forEach(requirementType => {
                    this.entityTypes.requirementTypes
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
                this.entityTypes.policyTemplates = [];
                entityTypeJSON.forEach(policyTemplate => {
                    this.entityTypes.policyTemplates
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
                this.entityTypes.groupedNodeTypes = entityTypeJSON;
                break;
            }
            case 'versionElements': {
                this.entityTypes.versionElements = [];
                entityTypeJSON.forEach((versionElements => {
                    this.entityTypes.versionElements.push(new VersionElement(versionElements.qName, versionElements.versions));
                }));
                break;
            }
            case 'unGroupedNodeTypes': {
                this.entityTypes.unGroupedNodeTypes = entityTypeJSON;
                break;
            }
            case 'relationshipTypes': {
                this.entityTypes.relationshipTypes = [];
                entityTypeJSON.forEach((relationshipType: EntityType) => {
                    const visuals = this.entityTypes.relationshipVisuals
                        .find(value => value.typeId === relationshipType.qName);
                    this.entityTypes.relationshipTypes
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
        }
    }

    initiateLocalRendering(tmData: TopologyModelerInputDataFormat): void {
        const nodeTemplateArray: Array<TNodeTemplate>
            = tmData.topologyTemplate.nodeTemplates;
        const relationshipTemplateArray: Array<TRelationshipTemplate>
            = tmData.topologyTemplate.relationshipTemplates;
        // init rendering
        this.entityTypes.nodeVisuals = tmData.visuals;
        this.initTopologyTemplateForRendering(nodeTemplateArray, relationshipTemplateArray);
        this.loaded = { loadedData: true, generatedReduxState: false };
        this.appReadyEvent.trigger();
    }

    initTopologyTemplateForRendering(nodeTemplateArray: Array<TNodeTemplate>, relationshipTemplateArray: Array<TRelationshipTemplate>) {
        // init node templates
        this.nodeTemplates = TopologyTemplateUtil.initNodeTemplates(nodeTemplateArray, this.entityTypes.nodeVisuals,
            this.configurationService.isYaml(), this.entityTypes, this.topologyDifferences);
        // init relationship templates
        this.relationshipTemplates = TopologyTemplateUtil.initRelationTemplates(relationshipTemplateArray, this.nodeTemplates,
            this.configurationService.isYaml(), this.topologyDifferences);
    }

    initiateData(): void {
        this.backendService.allEntities$.subscribe(JSON => {
            // Grouped NodeTypes
            this.initEntityType(JSON[0], 'groupedNodeTypes');

            // Artifact Templates
            this.initEntityType(JSON[1], 'artifactTemplates');

            /**
             * This subscriptionProperties receives an Observable of [string, string], the former value being
             * the JSON representation of the topologyTemplate and the latter value being the JSON
             * representation of the node types' visual appearances
             * the backendService makes sure that both get requests finish before pushing data onto this Observable
             * by using Observable.forkJoin(1$, 2$);
             * */
            const topologyData = JSON[2];
            const topologyTemplate = topologyData[0];
            this.entityTypes.nodeVisuals = topologyData[1];
            this.entityTypes.relationshipVisuals = topologyData[2];
            this.entityTypes.policyTemplateVisuals = topologyData[3];
            this.entityTypes.policyTypeVisuals = topologyData[4];
            if (topologyData.length === 7 && !isNullOrUndefined(topologyData[5]) && !isNullOrUndefined(topologyData[6])) {
                this.topologyDifferences = [topologyData[5], topologyData[6]];
            }

            // init YAML policies if they exist
            if (topologyTemplate.policies) {
                this.initEntityType(topologyTemplate.policies.policy, 'yamlPolicies');
            } else {
                this.initEntityType([], 'yamlPolicies');
            }

            // Artifact types
            this.initEntityType(JSON[3], 'artifactTypes');

            // Policy types
            this.initEntityType(JSON[4], 'policyTypes');

            // Capability Types
            this.initEntityType(JSON[5], 'capabilityTypes');

            // Requirement Types
            this.initEntityType(JSON[6], 'requirementTypes');

            // PolicyTemplates
            this.initEntityType(JSON[7], 'policyTemplates');

            // Relationship Types
            this.initEntityType(JSON[8], 'relationshipTypes');

            // NodeTypes
            this.initEntityType(JSON[9], 'unGroupedNodeTypes');

            // Version Elements
            this.initEntityType(JSON[10], 'versionElements');

            // init the NodeTemplates and RelationshipTemplates to start their rendering
            this.initTopologyTemplateForRendering(topologyTemplate.nodeTemplates, topologyTemplate.relationshipTemplates);

            this.triggerLoaded('everything');
        });
    }

    onReduxReady() {
        this.loaded.generatedReduxState = true;
    }

    sidebarDeleteButtonClicked($event) {
        this.sidebarDeleteButtonClickEvent = $event;
    }

    private triggerLoaded(what?: string) {
        if (what === 'relationshipVisuals') {
            this.loadedRelationshipVisuals++;
        }
        this.loaded = {
            loadedData: true,
            generatedReduxState: false
        };
        this.appReadyEvent.trigger();
    }

    private setButtonsState(currentButtonsState: TopologyRendererState) {
        if (currentButtonsState.buttonsState.refineTopologyButton) {
            this.refiningType = 'patterns';
        } else if (currentButtonsState.buttonsState.refineTopologyWithTestsButton) {
            this.refiningType = 'tests';
        } else if (!currentButtonsState.buttonsState.refineTopologyWithTestsButton && !currentButtonsState.buttonsState.refineTopologyButton) {
            delete this.refiningType;
        }
    }
}
