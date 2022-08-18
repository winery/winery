import { Component, OnDestroy } from '@angular/core';
import { SplitMatchTopologyService } from './split-match-topology.service';
import { EntityTypesModel } from '../../models/entityTypesModel';
import { Subscription } from 'rxjs/Subscription';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';
import { TopologyRendererActions } from '../../redux/actions/topologyRenderer.actions';
import { WineryActions } from '../../redux/actions/winery.actions';
import { ToastrService } from 'ngx-toastr';
import { WineryRepositoryConfigurationService } from '../../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { BackendService } from '../../services/backend.service';
import { TopologyRendererState } from '../../redux/reducers/topologyRenderer.reducer';
import { HttpErrorResponse } from '@angular/common/http';
import { InjectionOption, InjectorReplaceOptions } from './matchingEntity';
import { InjectionOptionsResponse, InjectionSelection, InjectorReplaceData } from './injectorData';
import { QNameApiData } from '../../../../../tosca-management/src/app/model/qNameApiData';
import { QName } from '../../../../../shared/src/app/model/qName';

/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

@Component({
    selector: 'split-match-topology',
    templateUrl: 'split-match-topology.component.html',
    providers: [
        SplitMatchTopologyService
    ],
    styleUrls: ['split-match-topology.component.css']
})
export class SplitMatchTopologyComponent implements OnDestroy {

    loading = false;
    applied = false;
    injectionOptionsApiData: InjectorReplaceOptions;
    selectedFragments: InjectorReplaceData;
    entityTypes: EntityTypesModel;
    hostInjectionSelectionMap: Map<string, string>;
    hostOptions: InjectionOption[];
    completionSelection: InjectionOptionsResponse = {
        'hostInjections': {},
        'connectionInjections': {}
    };

    private subscriptions: Subscription[] = [];


    constructor(private ngRedux: NgRedux<IWineryState>,
                private actions: TopologyRendererActions,
                private wineryActions: WineryActions,
                private splitMatchTopologyService: SplitMatchTopologyService,
                private alert: ToastrService,
                private configurationService: WineryRepositoryConfigurationService,
                private backendService: BackendService) {
        this.subscriptions.push(this.ngRedux.select(state => state.topologyRendererState)
            .subscribe(currentButtonsState => this.checkMatchButtonsState(currentButtonsState)));
        this.subscriptions.push(this.ngRedux.select(state => state.wineryState.entityTypes)
            .subscribe(data => {
                if (data) {
                    this.entityTypes = data;
                }
            }));
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(s => s.unsubscribe());
        this.subscriptions = null;
    }


    selectReplacementFragment(nodeTemplateId: string, replacementFragment: string) {
        if (!this.hostInjectionSelectionMap) {
            this.hostInjectionSelectionMap = new Map<string, string>();

        }
        if (this.hostInjectionSelectionMap.get(nodeTemplateId) &&
            this.hostInjectionSelectionMap.get(nodeTemplateId) == replacementFragment) {
            this.hostInjectionSelectionMap.delete(nodeTemplateId);
        } else {
            this.hostInjectionSelectionMap.set(nodeTemplateId, replacementFragment);
        }

    }

    onHoverOver(topology: string, nodeTemplateId: string) {
        const nodeTemplateIds: string[] = [];
        nodeTemplateIds.push(nodeTemplateId);
        this.ngRedux.dispatch(this.actions.highlightNodes(nodeTemplateIds));
    }

    hoverOut() {
        this.ngRedux.dispatch(this.actions.highlightNodes([]));
    }

    cancel() {
        delete this.selectedFragments;
        this.ngRedux.dispatch(this.actions.matchTopology());
    }

    applyMatching() {
        debugger;
        this.loading = true;
        if (this.selectedFragments == null) {
            this.selectedFragments = new InjectorReplaceData(new Array(), new Array());
        }

        this.hostInjectionSelectionMap.forEach((value: string, key: string) => {
            let selection = new InjectionSelection();
            selection.nodeID = key;
            selection.injection = value;
            this.selectedFragments.hostInjections.push(selection);
        });

        //for (let entry of Array.from(this.selectedFragments.hostInjections.entries())) {
        //  this.completionSelection['hostInjections'][entry[0]] = entry[1];
        //}
        this.splitMatchTopologyService.inject(this.selectedFragments).subscribe(
            res => this.solutionApplied(res),
            error => this.handleError(error)
        );
    }

    private checkMatchButtonsState(currentButtonsState: TopologyRendererState) {
        if (currentButtonsState.buttonsState.matchTopologyButton && !this.injectionOptionsApiData) {
            this.splitMatchTopologyService.getInjectionOptions()
                .subscribe(
                    data => this.showInjectionOptions(data),
                    error => this.handleError(error)
                );
            this.loading = true;
        }
    }

    private showInjectionOptions(injectionOptions: InjectorReplaceOptions) {
        debugger;
        this.injectionOptionsApiData = injectionOptions;
        //this.hostInjectionOptionsMap = new Map<string, TTopologyTemplate[]>();
        this.hostOptions = new Array();
        for (let entry of this.injectionOptionsApiData.hostInjections) {
            this.hostOptions.push(entry);
        }
        //for (let entry of Object.keys(this.injectionOptionsApiData.hostInjections)) {
        //  this.hostInjectionOptionsMap.set(entry, injectionOptions.hostInjections[entry]);
        //let option: InjectionOption;
        //option = new InjectionOption();
        //option.nodeTemplateId = entry;
        //option.options = injectionOptions.hostInjections[entry];
        //this.hostOptions.push(option);
        //}
        this.loading = false;
    }

    private handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.alert.error(error.message);
    }


    private solutionApplied(data: QNameApiData) {
        this.applied = true;
        this.loading = false;
        delete this.selectedFragments;
        delete this.hostOptions;
        delete this.hostInjectionSelectionMap;
        this.ngRedux.dispatch(this.actions.matchTopology());
        this.openModelerFor(data.localname, data.namespace, false);
    }

    private openModelerFor(id: string, ns: string, readonly: boolean) {
        let editorConfig = '?repositoryURL=' + encodeURIComponent(this.backendService.configuration.repositoryURL)
            + '&uiURL=' + encodeURIComponent(this.backendService.configuration.uiURL)
            + '&ns=' + encodeURIComponent(ns)
            + '&id=' + id
            + '&parentPath=' + this.backendService.configuration.parentPath
            + '&elementPath=' + this.backendService.configuration.elementPath;
        if (readonly) {
            editorConfig += '&isReadonly=true';
        }
        window.open(editorConfig, '_blank');
    }

}
