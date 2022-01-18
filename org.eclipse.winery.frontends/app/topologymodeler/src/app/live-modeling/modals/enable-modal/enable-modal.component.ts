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

import { Component } from '@angular/core';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { LiveModelingService } from '../../../services/live-modeling.service';
import { BackendService } from '../../../services/backend.service';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { SettingsModalComponent } from '../settings-modal/settings-modal.component';
import { PropertyValidatorService } from '../../../services/property-validator.service';

@Component({
    selector: 'winery-live-modeling-enable-modal',
    templateUrl: './enable-modal.component.html',
    styleUrls: ['./enable-modal.component.css']
})
export class EnableModalComponent {

    containerUrl: string;
    currentCsarId: string;

    testingContainerUrl = false;
    isContainerUrlInvalid: boolean;

    startInstance = true;

    constructor(private bsModalRef: BsModalRef,
                private liveModelingService: LiveModelingService,
                private backendService: BackendService,
                private http: HttpClient,
                private modalService: BsModalService,
                private propertyValidatorService: PropertyValidatorService
    ) {
        this.currentCsarId = this.normalizeCsarId(this.backendService.configuration.id);
        this.containerUrl = 'http://' + window.location.hostname + ':1337';
    }

    normalizeCsarId(csarId: string) {
        const csarEnding = '.csar';
        return csarId.endsWith(csarEnding) ? csarId : csarId + csarEnding;
    }

    async enableLiveModeling() {
        this.resetErrorsAndAnimations();
        this.testingContainerUrl = true;
        try {
            const isContainerUrlValid = await this.checkContainerUrl();
            if (isContainerUrlValid) {
                await this.liveModelingService.init(this.startInstance, this.containerUrl);
                this.dismissModal();
            } else {
                this.isContainerUrlInvalid = true;
            }
        } catch (e) {
            this.isContainerUrlInvalid = true;
        } finally {
            this.testingContainerUrl = false;
        }
    }

    checkContainerUrl(): Promise<boolean> {
        return this.http.get(this.containerUrl, { observe: 'response' }).pipe(
            map((resp) => {
                return resp.ok;
            }),
        ).toPromise();
    }

    resetErrorsAndAnimations() {
        this.testingContainerUrl = null;
        this.isContainerUrlInvalid = null;
    }

    handleSettings() {
        this.openModal(SettingsModalComponent);
    }

    openModal(modal: any, options?: any) {
        const defaultConfig = { backdrop: 'static' };
        this.modalService.show(modal, { ...defaultConfig, ...options });
    }

    isTopologyInvalid(): boolean {
        return this.propertyValidatorService.isTopologyInvalid();
    }

    dismissModal() {
        this.bsModalRef.hide();
    }
}
