/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
import { PatternRefinementModel, RefinementElement, RefinementWebSocketService } from './refinementWebSocket.service';
import { BackendService } from '../services/backend.service';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { WineryActions } from '../redux/actions/winery.actions';
import { Utils } from '../models/utils';

@Component({
    selector: 'winery-refinement',
    templateUrl: 'refinementSidebar.component.html',
    providers: [
        RefinementWebSocketService
    ],
    styleUrls: [
        'refinementSidebar.component.css'
    ]
})
export class RefinementSidebarComponent implements OnDestroy {

    refinementIsRunning: boolean;
    refinementIsLoading: boolean;
    refinementIsDone: boolean;
    prmCandidates: PatternRefinementModel[];

    constructor(private ngRedux: NgRedux<IWineryState>,
                private rendererActions: TopologyRendererActions,
                private wineryActions: WineryActions,
                private webSocketService: RefinementWebSocketService,
                private backendService: BackendService) {
    }

    startRefinement(event: MouseEvent) {
        event.stopPropagation();
        this.refinementIsDone = false;
        this.refinementIsRunning = true;
        this.refinementIsLoading = true;
        this.webSocketService.startRefinement()
            .subscribe(
                value => this.handleWebSocketData(value),
                error => this.handleError(error),
                () => this.handleWebSocketComplete()
            );
    }

    prmChosen(event: MouseEvent, candidate: PatternRefinementModel) {
        event.stopPropagation();
        this.webSocketService.refineWith(candidate);
        this.refinementIsLoading = true;
    }

    ngOnDestroy(): void {
        this.webSocketService.cancel();
    }

    onHoverOver(candidate: PatternRefinementModel) {
        this.ngRedux.dispatch(this.rendererActions.highlightNodes(candidate.nodeIdsToBeReplaced));
    }

    openModeler(event: MouseEvent, name: string, targetNamespace: string) {
        event.stopPropagation();
        this.openModelerFor(name, targetNamespace);
    }

    private handleWebSocketData(value: RefinementElement) {
        if (value) {
            this.refinementIsLoading = false;
            this.prmCandidates = value.patternRefinementCandidates;

            if (!this.prmCandidates) {
                this.refinementIsDone = true;
                this.refinementIsRunning = false;
            }

            if (value.currentTopology) {
                const wineryState = this.ngRedux.getState().wineryState;

                wineryState.currentJsonTopology.nodeTemplates
                    .forEach(
                        node => this.ngRedux.dispatch(this.wineryActions.deleteNodeTemplate(node.id))
                    );
                wineryState.currentJsonTopology.relationshipTemplates
                    .forEach(
                        relationship => this.ngRedux.dispatch(this.wineryActions.deleteRelationshipTemplate(relationship.id))
                    );

                Utils.initNodeTemplates(value.currentTopology.nodeTemplates, wineryState.nodeVisuals)
                    .forEach(
                        node => this.ngRedux.dispatch(this.wineryActions.saveNodeTemplate(node))
                    );
                Utils.initRelationTemplates(value.currentTopology.relationshipTemplates)
                    .forEach(
                        relationship => this.ngRedux.dispatch(this.wineryActions.saveRelationship(relationship))
                    );
            } else {
                this.openModelerFor(value.serviceTemplateContainingRefinements.xmlId.decoded,
                    value.serviceTemplateContainingRefinements.namespace.decoded,
                    this.backendService.configuration.parentPath,
                    this.backendService.configuration.elementPath,
                    false
                );
            }
        }
    }

    private handleError(error: any) {
        this.refinementIsLoading = false;
    }

    private handleWebSocketComplete() {
        this.refinementIsDone = true;
        this.refinementIsRunning = false;
        this.refinementIsLoading = false;
    }

    private openModelerFor(id: string, ns: string, type = 'patternrefinementmodels', elment = 'refinementstructure', readonly = true) {
        let editorConfig = '?repositoryURL=' + encodeURIComponent(this.backendService.configuration.repositoryURL)
            + '&uiURL=' + encodeURIComponent(this.backendService.configuration.uiURL)
            + '&ns=' + encodeURIComponent(ns)
            + '&id=' + id
            + '&parentPath=' + type
            + '&elementPath=' + elment;
        if (readonly) {
            editorConfig += '&isReadonly=true';
        }
        window.open(editorConfig, '_blank');
    }
}
