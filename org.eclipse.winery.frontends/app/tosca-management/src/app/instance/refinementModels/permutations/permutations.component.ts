/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
import { Component, OnInit } from '@angular/core';
import { PermutationsResponse, PermutationsService } from './permutationsService';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { HttpErrorResponse } from '@angular/common/http';
import { backendBaseURL, editorURL } from '../../../configuration';
import { InstanceService } from '../../instance.service';

@Component({
    templateUrl: 'permutations.component.html',
    styleUrls: [
        'permutations.component.css'
    ],
    providers: [
        PermutationsService
    ]
})
export class PermutationsComponent implements OnInit {

    readonly uiURL = encodeURIComponent(window.location.origin + window.location.pathname + '#/');

    loading: boolean;
    permutationsResponse: PermutationsResponse;
    keys: string[];
    links: any;

    constructor(private service: PermutationsService,
                private sharedData: InstanceService,
                private notify: WineryNotificationService) {
    }

    ngOnInit(): void {
    }

    generatePermutations() {
        this.service.generatePermutations().subscribe(
            data => this.handleData(data, 'Successfully generated permutations!'),
            error => this.handleError(error)
        );
        this.loading = true;
    }

    private handleData(data: PermutationsResponse, message: string) {
        this.permutationsResponse = data;

        if (data.permutations) {
            this.keys = Object.keys(data.permutations);
            this.links = {};
            this.keys.forEach(key =>
                this.links[key] = editorURL + '?repositoryURL=' + encodeURIComponent(backendBaseURL)
                    + '&uiURL=' + this.uiURL
                    + '&ns=' + encodeURIComponent(data.permutations[key].targetNamespace)
                    + '&id=' + key
                    + '&parentPath=' + this.sharedData.toscaComponent.toscaType
                    + '&elementPath=detector'
            );
        }

        this.loading = false;
        this.notify.success(message);
    }

    private handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.notify.error(error.message);
    }

    checkMutability() {
        this.service.checkMutability().subscribe(
            data => this.handleData(data, 'Successfully checked mutability!'),
            error => this.handleError(error)
        );
        this.loading = true;
    }
}
