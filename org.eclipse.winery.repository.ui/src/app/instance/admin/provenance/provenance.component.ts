/*******************************************************************************
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
import { Component, OnInit, ViewChild } from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap';
import { SelectData } from '../../../wineryInterfaces/selectData';
import { ProvenanceParticipant } from './ProvenanceParticipant';
import { ProvenanceService } from './provenance.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { isNullOrUndefined } from 'util';
import { AuthorizationNode } from '../../../wineryInterfaces/provenance';

@Component({
    templateUrl: 'provenance.component.html',
    providers: [
        ProvenanceService
    ]
})
export class ProvenanceComponent implements OnInit {

    @ViewChild('provenanceModal') provenanceModal: ModalDirective;
    @ViewChild('authenticationLineageModal') authenticationLineageModal: ModalDirective;
    selectedProvenanceId: SelectData;
    serviceTemplateList: SelectData[];
    participant = new ProvenanceParticipant();
    title: string;
    buttonLabel: string;
    loading = true;
    error: string;
    isAuthorize: boolean;
    authenticationData: AuthorizationNode[];

    constructor(private service: ProvenanceService, private notify: WineryNotificationService) {
    }

    ngOnInit(): void {
        this.error = null;
        this.service.getServiceTemplates()
            .subscribe(
                data => this.handleData(data),
                error => this.handleError(error)
            );
    }

    provenanceIdSelected(event: SelectData) {
        this.selectedProvenanceId = event;
    }

    showModal(type: string) {
        this.isAuthorize = type === 'authorize';
        this.title = this.isAuthorize ? 'Authorize new participant' : 'Authenticate participant';
        this.buttonLabel = this.isAuthorize ? 'Authorize' : 'Authenticate';

        this.participant = new ProvenanceParticipant();
        this.provenanceModal.show();
    }

    handleData(data: SelectData[]) {
        if (!isNullOrUndefined(data) && data.length > 0) {
            this.serviceTemplateList = data;
            this.selectedProvenanceId = data[0].children[0];
        } else {
            this.error = 'No service templates available!';
        }
        this.loading = false;
    }

    handleAddition(data: HttpResponse<string>) {
        this.notify.success('Successfully added participant ' + this.participant.authorizedIdentity
            + ' to ' + this.selectedProvenanceId.id);
        this.loading = false;
    }

    handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.notify.error(error.message);
    }

    onOk() {
        if (this.isAuthorize) {
            this.addNewParticipant();
        } else {
            this.authenticate();
        }
    }

    private handleAuthenticationData(data: AuthorizationNode[]) {
        this.loading = false;
        this.authenticationData = data;
        this.authenticationLineageModal.show();
    }

    private authenticate() {
        this.loading = true;
        this.service.authenticate(this.selectedProvenanceId.id, this.participant.authorizedEthereumAddress)
            .subscribe(
                data => this.handleAuthenticationData(data),
                error => this.handleError(error)
            );
    }

    private addNewParticipant() {
        this.loading = true;
        this.service.authorize(this.selectedProvenanceId.id, this.participant)
            .subscribe(
                data => this.handleAddition(data),
                error => this.handleError(error)
            );
    }
}
