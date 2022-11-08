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

import { Component, OnDestroy } from '@angular/core';
import {
    PlaceholderSubstitutionCandidate, PlaceholderSubstitutionWebSocketService, SubstitutionElement
} from './placeholderSubstitutionWebSocket.service';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';
import { TopologyRendererActions } from '../../redux/actions/topologyRenderer.actions';
import { WineryActions } from '../../redux/actions/winery.actions';
import { WineryRepositoryConfigurationService } from '../../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { BackendService } from '../../services/backend.service';
import { EntityTypesModel } from '../../models/entityTypesModel';
import { TopologyTemplateUtil } from '../../models/topologyTemplateUtil';
import { Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: 'winery-placeholder-substitution',
    templateUrl: 'placeholderSubstitution.component.html',
    providers: [
        PlaceholderSubstitutionWebSocketService
    ]
})
export class PlaceholderSubstitutionComponent implements OnDestroy {

    substitutionIsRunning: boolean;
    substitutionIsLoading: boolean;
    substitutionIsDone: boolean;
    serverErrorOccurs: boolean;
    substitutionCandidates: PlaceholderSubstitutionCandidate[];
    selectedNodeTemplateIds: string[] = [];

    private entityTypes: EntityTypesModel;

    private substitutionElement: Subscription;


    constructor(private ngRedux: NgRedux<IWineryState>,
                private rendererActions: TopologyRendererActions,
                private wineryActions: WineryActions,
                private webSocketService: PlaceholderSubstitutionWebSocketService,
                private configurationService: WineryRepositoryConfigurationService,
                private backendService: BackendService,
                private alert: ToastrService,
    ) {
        this.ngRedux.select(state => state.wineryState.entityTypes)
            .subscribe(types => this.entityTypes = types);
        this.ngRedux.select(state => state.topologyRendererState.selectedNodes)
            .subscribe(nodeIds => this.selectedNodeTemplateIds = nodeIds);
    }

    startSubstitution(event: MouseEvent) {
        event.stopPropagation();
        this.serverErrorOccurs = false;
        this.substitutionIsDone = false;
        this.substitutionIsRunning = true;
        this.substitutionIsLoading = true;
        const substitutionElementObservable = this.webSocketService.startPlaceholderSubstitution(this.selectedNodeTemplateIds);

        if (!this.substitutionElement) {
            this.substitutionElement = substitutionElementObservable.subscribe(
                value => this.handleWebSocketData(value),
                error => this.handleError(error),
                () => this.handleWebSocketComplete()
            );
        }

    }

    stopSubstitution(): void {
        this.webSocketService.cancel();
        this.substitutionElement = null;
    }

    restartSubstitution(event: MouseEvent): void {
        event.stopPropagation();
        this.substitutionIsDone = false;
        this.serverErrorOccurs = false;
        this.selectedNodeTemplateIds.splice(0, this.selectedNodeTemplateIds.length);
    }

    substitutionChosen(event: MouseEvent, candidate: PlaceholderSubstitutionCandidate) {
        event.stopPropagation();
        this.webSocketService.substituteWith(candidate);
        this.substitutionIsLoading = true;
    }

    ngOnDestroy(): void {
        this.webSocketService.cancel();
        this.substitutionElement = null;
    }

    openModeler(event: MouseEvent, name: string, targetNamespace: string) {
        event.stopPropagation();
        this.openModelerFor(name, targetNamespace, true);
    }

    private handleWebSocketData(value: SubstitutionElement) {
        if (value) {
            this.substitutionIsLoading = false;
            this.substitutionCandidates = value.substitutionCandidates;

            if (!this.substitutionCandidates) {
                this.substitutionIsDone = true;
                this.substitutionIsRunning = false;
            }

            if (value.currentTopology) {
                TopologyTemplateUtil.updateTopologyTemplate(this.ngRedux, this.wineryActions, value.currentTopology,
                    this.entityTypes, this.configurationService.isYaml());
            } else if (!value.errorMessage) {
                this.openModelerFor(value.serviceTemplateContainingSubstitution.xmlId.decoded,
                    value.serviceTemplateContainingSubstitution.namespace.decoded,
                    false
                );
            }
            if (value.errorMessage) {
                this.serverErrorOccurs = true;
                this.alert.error(value.errorMessage);
                this.substitutionIsRunning = false;

            }
        }
    }

    private handleError(error: any) {
        this.substitutionIsLoading = false;
    }

    private handleWebSocketComplete() {
        this.substitutionIsDone = true;
        this.substitutionIsLoading = false;
        this.substitutionIsRunning = false;
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
