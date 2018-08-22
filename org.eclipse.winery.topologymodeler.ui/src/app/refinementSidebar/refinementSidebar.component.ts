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
import { Component } from '@angular/core';
import { RefinementElement, RefinementWebSocketService } from './refinementWebSocket.service';
import { BackendService } from '../services/backend.service';

@Component({
    selector: 'winery-refinement',
    templateUrl: 'refinementSidebar.component.html',
    providers: [
        RefinementWebSocketService
    ]
})
export class RefinementSidebarComponent {

    refinementIsRunning: boolean;
    refinementIsLoading: boolean;
    refinementIsDone: boolean;
    prmOptions: RefinementElement[];

    constructor(private webSocketService: RefinementWebSocketService,
                private backendService: BackendService) {
    }

    startRefinement() {
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

    onAbortRefinement() {
        this.webSocketService.cancel();
    }

    private handleWebSocketData(value: RefinementElement[]) {
        if (value) {
            this.refinementIsLoading = false;
            this.prmOptions = value;
        }
    }

    private handleError(error: any) {
        this.refinementIsLoading = false;
        console.log(error);
    }

    private handleWebSocketComplete() {
        this.refinementIsDone = true;
        this.refinementIsRunning = false;
        this.refinementIsLoading = false;
    }

    openModelerFor(patternRefinementModel: { name: string; targetNamespace: string }, element: string, type = 'patternrefinementmodels') {
        const editorConfig = '?repositoryURL=' + this.backendService.configuration.repositoryURL
            + '&uiURL=' + this.backendService.configuration.uiURL
            + '&ns=' + encodeURIComponent(patternRefinementModel.targetNamespace)
            + '&id=' + patternRefinementModel.name
            + '&parentPath=' + type
            + '&elementPath=' + element
            + '&isReadonly=true';
        window.open(editorConfig, '_blank');
    }

    prmChosen(option: RefinementElement) {
        this.webSocketService.refineWith(option);
        this.refinementIsLoading = true;
    }
}
