/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

import 'rxjs/add/operator/do';
import { Component, OnInit } from '@angular/core';
import { EntityType, TNodeTemplate, TRelationshipTemplate, TTopologyTemplate, Visuals } from './models/ttopology-template';
import { ILoaded, LoadedService } from './services/loaded.service';
import { AppReadyEventService } from './services/app-ready-event.service';
import { BackendService } from './services/backend.service';
import { Subscription } from 'rxjs/Subscription';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from './redux/store/winery.store';
import { NodeRelationshipTemplatesGeneratorService } from './services/node-relationship-templates-generator.service';
import { DifferenceStates, ToscaDiff } from './models/ToscaDiff';
import { isNullOrUndefined } from 'util';
import { Utils } from './models/utils';

/**
 * This is the root component of the topology modeler.
 */
@Component({
    selector: 'winery-topologymodeler',
    templateUrl: './winery.component.html',
    styleUrls: ['./winery.component.css']
})
export class WineryComponent implements OnInit {

    nodeTemplates: Array<TNodeTemplate> = [];
    relationshipTemplates: Array<TRelationshipTemplate> = [];
    artifactTypes: Array<any> = [];
    policyTypes: Array<any> = [];
    policyTemplates: Array<any> = [];
    capabilityTypes: Array<any> = [];
    requirementTypes: Array<any> = [];
    groupedNodeTypes: Array<any> = [];
    relationshipTypes: Array<EntityType> = [];
    entityTypes: any = {};
    hideNavBarState: boolean;
    subscriptions: Array<Subscription> = [];

    topologyDifferences: [ToscaDiff, TTopologyTemplate];

    public loaded: ILoaded;

    constructor(private loadedService: LoadedService,
                private appReadyEvent: AppReadyEventService,
                private backendService: BackendService,
                private nodeRelationshipGeneratorService: NodeRelationshipTemplatesGeneratorService,
                private ngRedux: NgRedux<IWineryState>) {
        this.subscriptions.push(this.ngRedux.select(state => state.wineryState.hideNavBarAndPaletteState)
            .subscribe(hideNavBar => this.hideNavBarState = hideNavBar));
    }

    /**
     * Angular LifeCycle function OnInit().
     * All necessary data is being requested inside this function via the backendService instance.
     * The data is passed to various init...() functions that parse the received JSON data into Objects that get stored
     * inside the Redux store of this application.
     */
    ngOnInit() {
        // Grouped NodeTypes
        this.backendService.groupedNodeTypes$.subscribe(JSON => {
            this.initEntityType(JSON, 'groupedNodeTypes');
        });
        // Artifact Templates
        this.backendService.artifactTemplates$.subscribe(JSON => {
            this.initEntityType(JSON, 'artifactTemplates');
        });

        /**
         * This subscriptionProperties receives an Observable of [string, string], the former value being
         * the JSON representation of the topologyTemplate and the latter value being the JSON
         * representation of the node types' visual appearances
         * the backendService makes sure that both get requests finish before pushing data onto this Observable
         * by using Observable.forkJoin(1$, 2$);
         */
        this.backendService.topologyTemplatesDiffAndVisuals$.subscribe((JSON: [TTopologyTemplate, Visuals, ToscaDiff, TTopologyTemplate]) => {
            const topologyTemplate = JSON[0];
            this.entityTypes.nodeVisuals = JSON[1];

            if (JSON.length === 4 && !isNullOrUndefined(JSON[2]) && !isNullOrUndefined(JSON[3])) {
                this.topologyDifferences = [JSON[2], JSON[3]];
            }

            // init the NodeTemplates and RelationshipTemplates to start their rendering
            this.initTopologyTemplate(topologyTemplate.nodeTemplates, topologyTemplate.relationshipTemplates);

            this.loaded = {loadedData: true, generatedReduxState: false};
            this.appReadyEvent.trigger();
        });

        // Get other entity types
        // Artifact Types
        this.backendService.artifactTypes$.subscribe(JSON => {
            this.initEntityType(JSON, 'artifactTypes');
        });
        // Policy Types
        this.backendService.policyTypes$.subscribe(JSON => {
            this.initEntityType(JSON, 'policyTypes');
        });
        // Capability Types
        this.backendService.capabilityTypes$.subscribe(JSON => {
            this.initEntityType(JSON, 'capabilityTypes');
        });
        // Requirement Types
        this.backendService.requirementTypes$.subscribe(JSON => {
            this.initEntityType(JSON, 'requirementTypes');
        });
        // PolicyTemplates
        this.backendService.policyTemplates$.subscribe(JSON => {
            this.initEntityType(JSON, 'policyTemplates');
        });
        // Relationship Types
        this.backendService.relationshipTypes$.subscribe(JSON => {
            this.initEntityType(JSON, 'relationshipTypes');
        });
        // NodeTypes
        this.backendService.nodeTypes$.subscribe(JSON => {
            this.initEntityType(JSON, 'unGroupedNodeTypes');
        });
    }

    /**
     * Save the received Array of Entity Types inside the respective variables in the entityTypes array of arrays
     * which is getting passed to the palette and the topology renderer
     * @param {Array<any>} entityTypeJSON
     * @param {string} entityType
     */
    initEntityType(entityTypeJSON: Array<any>, entityType: string): void {
        switch (entityType) {
            case 'artifactTypes': {
                entityTypeJSON.forEach(artifactType => {
                    this.artifactTypes
                        .push(new EntityType(
                            artifactType.id,
                            artifactType.qName,
                            artifactType.name,
                            artifactType.namespace
                        ));
                });
                this.entityTypes.artifactTypes = this.artifactTypes;
                break;
            }
            case 'artifactTemplates': {
                this.entityTypes.artifactTemplates = entityTypeJSON;
                break;
            }
            case 'policyTypes': {
                entityTypeJSON.forEach(policyType => {
                    this.policyTypes
                        .push(new EntityType(
                            policyType.id,
                            policyType.qName,
                            policyType.name,
                            policyType.namespace
                        ));
                });
                this.entityTypes.policyTypes = this.policyTypes;
                break;
            }
            case 'capabilityTypes': {
                entityTypeJSON.forEach(capabilityType => {
                    this.capabilityTypes
                        .push(new EntityType(
                            capabilityType.id,
                            capabilityType.qName,
                            capabilityType.name,
                            capabilityType.namespace,
                            '',
                            capabilityType.full
                        ));
                });
                this.entityTypes.capabilityTypes = this.capabilityTypes;
                break;
            }
            case 'requirementTypes': {
                entityTypeJSON.forEach(requirementType => {
                    this.requirementTypes
                        .push(new EntityType(
                            requirementType.id,
                            requirementType.qName,
                            requirementType.name,
                            requirementType.namespace,
                            '',
                            requirementType.full
                        ));
                });
                this.entityTypes.requirementTypes = this.requirementTypes;
                break;
            }
            case 'policyTemplates': {
                entityTypeJSON.forEach(policyTemplate => {
                    this.policyTemplates
                        .push(new EntityType(
                            policyTemplate.id,
                            policyTemplate.qName,
                            policyTemplate.name,
                            policyTemplate.namespace
                        ));
                });
                this.entityTypes.policyTemplates = this.policyTemplates;
                break;
            }
            case 'groupedNodeTypes': {
                this.entityTypes.groupedNodeTypes = entityTypeJSON;
                break;
            }
            case 'unGroupedNodeTypes': {
                this.entityTypes.unGroupedNodeTypes = entityTypeJSON;
                this.setNodeVisuals(this.entityTypes.nodeVisuals);
                break;
            }
            case 'relationshipTypes': {
                entityTypeJSON.forEach(relationshipType => {
                    // get relationship type visualappearances
                    let visualAppearance;
                    this.backendService
                    // returns Observable
                        .requestRelationshipTypeVisualappearance(
                            relationshipType.namespace,
                            relationshipType.id)
                        .subscribe((JSON) => {
                            visualAppearance = JSON;
                            this.relationshipTypes
                                .push(new EntityType(
                                    relationshipType.id,
                                    relationshipType.qName,
                                    relationshipType.name,
                                    relationshipType.namespace,
                                    visualAppearance.color
                                ));
                        });
                });
                this.entityTypes.relationshipTypes = this.relationshipTypes;
                break;
            }
        }
    }

    initTopologyTemplate(nodeTemplateArray: Array<TNodeTemplate>, relationshipTemplateArray: Array<TRelationshipTemplate>) {
        // init node templates
        if (nodeTemplateArray.length > 0) {
            nodeTemplateArray.forEach(node => {
                const state = isNullOrUndefined(this.topologyDifferences) ? null : DifferenceStates.UNCHANGED;
                if (!this.nodeTemplates.find(nodeTemplate => nodeTemplate.id === node.id)) {
                    this.nodeTemplates.push(Utils.createTNodeTemplateFromObject(node, this.entityTypes.nodeVisuals, state));
                }
            });
        }
        // init relationship templates
        if (relationshipTemplateArray.length > 0) {
            let relIdCount = 1;
            relationshipTemplateArray.forEach(relationship => {
                this.relationshipTemplates.push(
                    Utils.createTRelationshipTemplateFromObject(relationship, relIdCount)
                );
                relIdCount += 1;
            });
        }
    }

    private setNodeVisuals(nodeVisuals: Array<Visuals>): void {
        nodeVisuals.forEach(nodeVisual => {
            const nodeId = nodeVisual.nodeTypeId.substring(nodeVisual.nodeTypeId.indexOf('}') + 1);
            this.entityTypes.unGroupedNodeTypes.forEach(node => {
                if (node.id === nodeId) {
                    node.color = nodeVisual.color;
                }
            });
        });
    }

    onReduxReady() {
        this.loaded.generatedReduxState = true;
    }
}


