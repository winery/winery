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
import { TNodeTemplate, TRelationshipTemplate, TTopologyTemplate } from './models/ttopology-template';
import { ILoaded, LoadedService } from './services/loaded.service';
import { AppReadyEventService } from './services/app-ready-event.service';
import { BackendService } from './services/backend.service';
import { Subscription } from 'rxjs';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from './redux/store/winery.store';
import { ToscaDiff } from './models/ToscaDiff';
import { TopologyTemplateUtil } from './models/topologyTemplateUtil';
import { EntityTypesModel, TopologyModelerInputDataFormat } from './models/entityTypesModel';
import { ActivatedRoute } from '@angular/router';
import { TopologyModelerConfiguration } from './models/topologyModelerConfiguration';
import { ToastrService } from 'ngx-toastr';
import { TopologyRendererState } from './redux/reducers/topologyRenderer.reducer';
import { VersionElement } from './models/versionElement';
import { TopologyRendererActions } from './redux/actions/topologyRenderer.actions';
import { WineryRepositoryConfigurationService } from '../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { WineryActions } from './redux/actions/winery.actions';

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

    detailsSidebarVisible: boolean;

    showVersionSlider: boolean;

    public loaded: ILoaded;
    private loadedRelationshipVisuals = 0;
    private requiredRelationshipVisuals: number;

    constructor(private loadedService: LoadedService,
                private appReadyEvent: AppReadyEventService,
                public backendService: BackendService,
                private ngRedux: NgRedux<IWineryState>,
                private actions: TopologyRendererActions,
                private uiActions: WineryActions,
                private alert: ToastrService,
                private activatedRoute: ActivatedRoute,
                private configurationService: WineryRepositoryConfigurationService) {
        this.subscriptions.push(this.ngRedux.select(state => state.wineryState.hideNavBarAndPaletteState)
            .subscribe(hideNavBar => this.hideNavBarState = hideNavBar));
        this.subscriptions.push(this.ngRedux.select(state => state.topologyRendererState)
            .subscribe(currentButtonsState => this.setButtonsState(currentButtonsState)));
        this.subscriptions.push(this.ngRedux.select(state => state.wineryState.sidebarContents.visible)
            .subscribe(detailsSidebarVisible => this.detailsSidebarVisible = detailsSidebarVisible));
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
            // If data is passed to the topologymodeler directly,
            //  rendering is initiated immediately without backend calls
            if (this.topologyModelerData.topologyTemplate) {
                this.initiateLocalRendering(this.topologyModelerData);
            } else {
                if (this.topologyModelerData.configuration.repositoryURL) {
                    this.backendService.configure(this.topologyModelerData.configuration);
                } else {
                    this.activatedRoute.queryParams.subscribe((params: TopologyModelerConfiguration) => {
                        this.backendService.configure(params);
                    });
                    this.initiateData();
                }
            }
        } else {
            this.activatedRoute.queryParams.subscribe((params: TopologyModelerConfiguration) => {
                this.backendService.configure(params);
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

    /**
     * notifies the redux store of the sidebar with a given key being closed
     * @param key The identifier for the sidebar that has been closed
     */
    notifyClose(key: string): void {
        // FIXME this currently basically only supports the node-details sidebar
        //  because none of the other sidebars are based off ng-sidebar
        this.ngRedux.dispatch(this.uiActions.openSidebar({
            sidebarContents: {
                visible: false,
                nodeClicked: '',
                template: {
                    id: '',
                    name: '',
                    type: '',
                    properties: { kvproperties: { foo: 'bar' } },
                },
                minInstances: -1,
                maxInstances: -1,
                source: '',
                target: '',
            }
        }));
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
        // TODO well, this is a mess
        this.backendService.model$.subscribe(m => {
            this.entityTypes = m;
            this.ngRedux.dispatch(this.uiActions.addEntityTypes(this.entityTypes));
        });
        this.backendService.topDiff$
            .subscribe(diff => this.topologyDifferences = diff);
        this.backendService.topTemplate$
            .subscribe((template) => {
                this.initTopologyTemplateForRendering(template.nodeTemplates, template.relationshipTemplates);
                // init groups
                this.ngRedux.dispatch(this.uiActions.updateGroupDefinitions(template.groups));
                // init participants
                this.ngRedux.dispatch(this.uiActions.updateParticipants(template.participants));
            });
        this.backendService.loaded$
            .subscribe(l => {
                if (l) {
                    this.triggerLoaded('everything');
                }
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
            this.refiningType = 'topology';
        } else if (currentButtonsState.buttonsState.refinePatternsButton) {
            this.refiningType = 'patterns';
        } else if (currentButtonsState.buttonsState.refineTopologyWithTestsButton) {
            this.refiningType = 'tests';
        } else {
            delete this.refiningType;
        }
        this.showVersionSlider = currentButtonsState.buttonsState.versionSliderButton;
    }
}
