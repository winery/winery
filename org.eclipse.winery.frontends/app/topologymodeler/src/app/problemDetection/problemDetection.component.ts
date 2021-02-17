/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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
import { SolutionInputData } from './solutionEntity';
import { TTopologyTemplate } from '../models/ttopology-template';
import { TopologyTemplateUtil } from '../models/topologyTemplateUtil';
import { WineryActions } from '../redux/actions/winery.actions';
import { WineryRepositoryConfigurationService } from '../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { EntityTypesModel } from '../models/entityTypesModel';

@Component({
    selector: 'winery-problem-detection',
    templateUrl: 'problemDetection.component.html',
    providers: [
        ProblemDetectionService
    ],
    styleUrls: ['problemDetection.component.css']
})
export class ProblemDetectionComponent {

    loading = false;
    problemFindings: ProblemFindings[];
    selectedFinding: ProblemOccurrence;
    possibleSolutions: SolutionInputData[];
    selectedSolution: SolutionInputData;
    entityTypes: EntityTypesModel;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private actions: TopologyRendererActions,
                private wineryActions: WineryActions,
                private problemDetectionService: ProblemDetectionService,
                private alert: ToastrService,
                private configurationService: WineryRepositoryConfigurationService,
                private backendService: BackendService) {
        this.ngRedux.select(state => state.topologyRendererState)
            .subscribe(currentButtonsState => this.checkButtonsState(currentButtonsState));
        this.ngRedux.select(state => state.wineryState.entityTypes)
            .subscribe(data => {
                if (data) {
                    this.entityTypes = data;
                }
            });
    }

    private checkButtonsState(currentButtonsState: TopologyRendererState) {
        if (currentButtonsState.buttonsState.problemDetectionButton && !this.problemFindings) {
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

    selectFinding(problem: ProblemEntity, finding: ComponentFinding[]) {
        this.selectedFinding = {
            problem: problem.problem,
            description: problem.description,
            pattern: problem.pattern,
            serviceTemplateNs: this.backendService.configuration.ns,
            serviceTemplateId: this.backendService.configuration.id,
            occurrence: finding
        };
    }

    solve() {
        this.problemDetectionService.findSolutions(this.selectedFinding)
            .subscribe(
                data => this.showPossibleSolutions(data),
                error => this.handleError(error)
            );
        this.loading = true;
        delete this.problemFindings;
    }

    selectSolution(solution: SolutionInputData) {
        this.selectedSolution = solution;
    }

    private showPossibleSolutions(possibleSolutions: SolutionInputData[]) {
        this.possibleSolutions = possibleSolutions;
        this.loading = false;
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
        delete this.possibleSolutions;
        delete this.selectedSolution;
        this.ngRedux.dispatch(this.actions.detectProblems());
    }

    applySolution() {
        this.problemDetectionService.applySolution(this.selectedSolution)
            .subscribe(
                data => this.solutionApplied(data),
                error => this.handleError(error)
            );
        this.loading = true;
    }

    private solutionApplied(data: TTopologyTemplate) {
        TopologyTemplateUtil.updateTopologyTemplate(this.ngRedux, this.wineryActions, data, this.entityTypes, this.configurationService.isYaml());
        this.loading = false;
    }
}
