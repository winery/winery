/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
import { Component } from '@angular/core';
import { ProblemDetectionService } from './problemDetection.service';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { TopologyRendererState } from '../redux/reducers/topologyRenderer.reducer';
import { ComponentFinding, ProblemEntity, ProblemFindings, ProblemOccurrence } from './problemEntity';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';
import { BackendService } from '../services/backend.service';
import { PatternRefinementModel } from '../refinementSidebar/refinementWebSocket.service';

@Component({
    selector: 'winery-problemDetection',
    templateUrl: 'problemDetection.component.html',
    providers: [
        ProblemDetectionService
    ],
    styleUrls: ['problemDetection.component.css']
})
export class ProblemDetectionComponent {

    loading = false;
    problemFindings : ProblemFindings[];
    selectedFinding: ProblemOccurrence;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private actions: TopologyRendererActions,
                private problemDetectionService: ProblemDetectionService,
                private alert: ToastrService,
                private backendService: BackendService) {
        this.ngRedux.select(state => state.topologyRendererState)
            .subscribe(currentButtonsState => this.checkButtonsState(currentButtonsState));
    }

    private checkButtonsState(currentButtonsState: TopologyRendererState) {
        if(currentButtonsState.buttonsState.problemDetectionButton && !this.problemFindings) {
            this.problemDetectionService.detectProblems()
                .subscribe(
                    data => this.showDetectedProblems(data),
                    error => this.handleError(error)
                );
            this.loading = true;
        }
    }

    private showDetectedProblems(problemFindings: ProblemFindings[]) {
        this.problemFindings = problemFindings;
        this.loading = false;
    }

    private handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.alert.error(error.message);
    }

    selectFinding(problem: ProblemEntity, finding: ComponentFinding[]){
        this.selectedFinding = {
            problem: problem.problem,
            description: problem.description,
            pattern: problem.pattern,
            serviceTemplateNs: this.backendService.configuration.ns,
            serviceTemplateId: this.backendService.configuration.id,
            occurrence: finding
        }
        console.log(this.selectedFinding);
    }

    onHoverOver(findings: ComponentFinding[]) {
        const nodeTemplateIds: string[] = [];
        findings.forEach(entry => nodeTemplateIds.push(entry.componentId));
        this.ngRedux.dispatch(this.actions.highlightNodes(nodeTemplateIds));
    }

    hoverOut() {
        this.ngRedux.dispatch(this.actions.highlightNodes([]));
    }

    cancel() {
        delete this.selectedFinding;
        delete this.problemFindings;
    }
}
