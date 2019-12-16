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
import { WineryTableColumn } from '../../wineryTableModule/wineryTable.component';
import { SelectComponent, SelectItem } from 'ng2-select';
import { WineryNotificationService } from '../../wineryNotificationModule/wineryNotification.service';
import { Policy, TargetAllocationService } from '../targetAllocation.service';

@Component({
    selector: 'winery-target-allocation-policy-selection-component',
    templateUrl: 'policySelection.component.html'
})

export class PolicySelectionComponent implements OnInit {

    @ViewChild('property') propertySelect: SelectComponent;

    // policy, property, operator selection
    policiesForSelect: Array<SelectItem> = [];
    propertiesForSelect: Array<SelectItem> = [];
    operators = [
        { id: 'min', text: 'min' },
        { id: 'max', text: 'max' },
        { id: 'approx', text: 'approx' },
        { id: '=', text: '=' },
        { id: '!=', text: '!=' },
        { id: '<', text: '<' },
        { id: '>', text: '>' },
        { id: '<=', text: '<=' },
        { id: '>=', text: '>=' }
    ];

    // policies table
    data: Array<any> = [];
    columns: Array<WineryTableColumn> = [
        { title: 'Policy Template', name: 'policy', sort: false },
        { title: 'Property', name: 'property', sort: false },
        { title: 'Operator', name: 'operator', sort: false }
    ];

    private policy: string;
    private policyProperty: string;
    private operator: string;

    private policies: Policy[];

    constructor(private service: TargetAllocationService, private notify: WineryNotificationService) {
        this.getPolicies();
    }

    ngOnInit(): void {
    }

    onAdd() {
        if (this.policy !== undefined && this.policyProperty !== undefined && this.operator !== undefined) {
            this.data.push({
                policy: this.policy,
                property: this.policyProperty,
                operator: this.operator
            });
        } else {
            this.notify.error('Please enter all parameters');
        }
    }

    /**
     * @param row only used for removal from table -> no explicit data type
     */
    onDelete(row: any) {
        const index = this.data.indexOf(row, 0);
        this.data.splice(index, 1);
    }

    policySelected(value: SelectItem) {
        this.policy = value.id;
        this.propertiesForSelect = [];
        this.propertySelect.writeValue('');

        // get properties of policy and update select ui component
        for (const policy of this.policies) {
            if (policy.name === this.policy) {
                this.service.getProperties(policy).subscribe(props => {
                    for (const key of Object.keys(props)) {
                        const selectItem = new SelectItem('');
                        selectItem.id = key;
                        selectItem.text = key;
                        this.propertiesForSelect.push(selectItem);
                    }
                    this.propertiesForSelect = [...this.propertiesForSelect];
                });
            }
        }
    }

    propertySelected(value: SelectItem) {
        this.policyProperty = value.id;
    }

    operatorSelected(value: SelectItem) {
        this.operator = value.id;
    }

    private getPolicies() {
        this.service.getNodeTemplates().subscribe(nts => {
                this.policies = [];
                for (const nt of nts) {
                    if (nt.policies === undefined) {
                        continue;
                    }

                    for (const policy of nt.policies.policy) {
                        const selectItem = new SelectItem('');
                        selectItem.id = policy.name;
                        selectItem.text = policy.name;

                        this.policiesForSelect.push(selectItem);
                        this.policies.push(policy);
                    }
                }
                if (this.policies.length === 0) {
                    this.notify.error('No Policy Templates present');
                }
            },
            error => this.notify.error('Couldn\'t load policies'));
    }
}
