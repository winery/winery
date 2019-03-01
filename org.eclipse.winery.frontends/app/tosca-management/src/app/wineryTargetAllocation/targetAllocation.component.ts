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
 ********************************************************************************/

import { Component, OnInit, ViewChild } from '@angular/core';

import { SelectItem } from 'ng2-select';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { PolicySelectionComponent } from './policySelection/policySelection.component';
import { WineryTableColumn } from '../wineryTableModule/wineryTable.component';
import { TargetAllocationService } from './targetAllocation.service';
import { ModalDirective } from 'ngx-bootstrap';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { AllocationRequest, CriteriaRequest } from './request';

@Component({
    selector: 'winery-target-allocation-component',
    templateUrl: 'targetAllocation.component.html'
})

export class TargetAllocationComponent implements OnInit {

    @ViewChild(ModalDirective) targetAllocationModal: ModalDirective;
    @ViewChild(PolicySelectionComponent) policySelection: PolicySelectionComponent;

    // criteria selection spinner
    criteria = this.getCriteriaForSelect();

    // selected criteria table
    data: Array<any> = [];
    columns: Array<WineryTableColumn> = [
        { title: '#', name: 'number', sort: false },
        { title: 'Criteria', name: 'criteria', sort: false }
    ];

    selectedCriteria: string;
    request = new AllocationRequest();

    constructor(private service: TargetAllocationService, private notify: WineryNotificationService) {
    }

    ngOnInit() {
    }

    showModal(backendLink: string) {
        this.service.backendLink = backendLink;
        this.targetAllocationModal.show();
    }

    allocate() {
        this.request.assignOnly = false;
        this.service.allocateRequest(this.request)
            .subscribe(
                data => this.notifySuccess(data),
                error => this.notifyError(error)
            );
        this.notify.success('Allocating...');
    }

    assignTargetLabels() {
        this.addSelectedCriteria();
        this.request.assignOnly = true;
        this.service.allocateRequest(this.request)
            .subscribe(
                data => this.notifySuccess(data),
                error => this.notifyError(error)
            );
        this.notify.success('Assigning target labels...');

        this.data.splice(this.data.length - 1, 1);
        this.request.selectedCriteria.splice(this.request.selectedCriteria.length - 1, 1);
        this.targetAllocationModal.hide();
    }

    criteriaSelected(value: SelectItem) {
        this.selectedCriteria = value.id;
    }

    addSelectedCriteria() {
        // ui table
        if (this.data.length === 0) {
            this.data.push({
                number: 1,
                criteria: 'Allocate: ' + this.selectedCriteria
            });
        } else {
            this.data.push({
                number: this.data.length + 1,
                criteria: 'Filter: ' + this.selectedCriteria
            });
        }

        // request
        let criteriaParams = {};
        if (this.selectedCriteria === 'FulfillPolicies') {
            criteriaParams = {
                policySelection: this.policySelection.data
            };
        }
        this.request.selectedCriteria.push(new CriteriaRequest(
            this.selectedCriteria,
            criteriaParams
        ));
    }

    removeCriteria() {
        this.data.splice(this.data.length - 1, 1);
        this.request.selectedCriteria.splice(this.request.selectedCriteria.length - 1, 1);
    }

    isAllocationButtonEnabled(): boolean {
        return this.data === undefined ||
            this.data.length === 0;
    }

    isAddCriteriaButtonDisabled(): boolean {
        if (!this.selectedCriteria) {
            return true;
        }
        if (this.selectedCriteria === 'FulfillPolicies') {
            if (this.policySelection === undefined) {
                return true;
            } else {
                if (this.policySelection.data.length === 0) {
                    return true;
                }
            }
        }
        return false;
    }

    isAssignOnlyButtonDisabled(): boolean {
        return this.isAddCriteriaButtonDisabled() ||
            this.data.length >= 1;
    }

    private notifySuccess(data: HttpResponse<string[]>) {
        const length = data.body.length;
        if (length === 1) {
            this.notify.success('Created 1 topology');
        } else {
            this.notify.success('Created ' + length + ' topologies');
        }
    }

    private notifyError(error: HttpErrorResponse) {
        this.notify.error(error.error);
    }

    private getCriteriaForSelect(): SelectItem[] {
        const criteria: SelectItem[] = [];

        const minHosts = new SelectItem('');
        minHosts.id = 'MinHosts';
        minHosts.text = 'Min Hosts';
        criteria.push(minHosts);

        const fulfillPolicies = new SelectItem('');
        fulfillPolicies.id = 'FulfillPolicies';
        fulfillPolicies.text = 'Fulfill Policies';
        criteria.push(fulfillPolicies);

        const minExternalConnections = new SelectItem('');
        minExternalConnections.id = 'MinExternalConnections';
        minExternalConnections.text = 'Min External Connections';
        criteria.push(minExternalConnections);

        return criteria;
    }
}
