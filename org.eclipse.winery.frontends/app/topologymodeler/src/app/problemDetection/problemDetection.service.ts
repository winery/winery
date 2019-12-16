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
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BackendService } from '../services/backend.service';
import { Observable } from 'rxjs/Observable';
import { ProblemFindings, ProblemOccurrence } from './problemEntity';
import { SolutionInputData } from './solutionEntity';
import { TopologyModelerConfiguration } from '../models/topologyModelerConfiguration';
import { TTopologyTemplate } from '../models/ttopology-template';

@Injectable()
export class ProblemDetectionService {

    private readonly configuration: TopologyModelerConfiguration;
    private readonly postHeaders: HttpHeaders;

    constructor(private http: HttpClient,
                backendService: BackendService) {
        this.configuration = backendService.configuration;
        this.postHeaders = new HttpHeaders().set('Accept', 'application/json');
        this.postHeaders.set('Content-Type', 'application/json');
    }

    detectProblems(): Observable<ProblemFindings[]> {
        const header = new HttpHeaders().set('Accept', 'application/json');
        const url = this.configuration.topologyProDecURL + '/checkProblems?'
            + 'wineryURL=' + encodeURIComponent(this.configuration.repositoryURL)
            + '&serviceTemplateNS=' + encodeURIComponent(this.configuration.ns)
            + '&serviceTemplateID=' + encodeURIComponent(this.configuration.id);

        return this.http.get<ProblemFindings[]>(url, { headers: header });
    }

    findSolutions(selectedProblem: ProblemOccurrence): Observable<SolutionInputData[]> {
        const url = this.configuration.topologyProDecURL + '/findSolutions';
        return this.http.post<SolutionInputData[]>(url, selectedProblem, { headers: this.postHeaders });
    }

    applySolution(selectedSolution: SolutionInputData) {
        let url;
        if (selectedSolution.csi.serviceEndpoint.endsWith('eclipse/winery')) {
            url = this.configuration.repositoryURL + '/' + this.configuration.parentPath + '/'
                + encodeURIComponent(encodeURIComponent(this.configuration.ns)) + '/'
                + encodeURIComponent(encodeURIComponent(this.configuration.id)) + '/'
                + this.configuration.elementPath
                + '/applysolution';
        } else {
            url = selectedSolution.csi.serviceEndpoint;
        }
        return this.http.post<TTopologyTemplate>(url, selectedSolution, { headers: this.postHeaders });
    }
}
