/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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

import { Component, Input, OnInit } from '@angular/core';
import { TPolicy } from '../../models/policiesModalData';
import { TGroupDefinition } from '../../models/ttopology-template';
import { EntityTypesModel } from '../../models/entityTypesModel';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';

@Component({
    selector: 'winery-group-view-policies',
    templateUrl: './policies.component.html',
    styleUrls: ['./policies.component.css']
})
export class GroupViewPoliciesComponent implements OnInit {

    @Input() definition: TGroupDefinition;
    @Input() entityTypes: EntityTypesModel;

    policies: TPolicy[];

    constructor(private ngRedux: NgRedux<IWineryState>) {
    }

    ngOnInit() {
        this.ngRedux.select((state) => state.wineryState.currentJsonTopology.policies)
            .subscribe((policies) => this.policies = policies.policy);
    }

    isEmpty(): boolean {
        return !this.policies || this.policies.length === 0;
    }

    isEllipsisActive(cell): boolean {
        return (cell.offsetWidth < cell.scrollWidth);
    }

    isPolicyActive(policy: TPolicy): boolean {
        return policy.targets && policy.targets.some((target) => target === this.definition.name);
    }

    togglePolicy(policy: TPolicy) {
        if (policy.targets) {
            const index = policy.targets.indexOf(this.definition.name);
            if (index >= 0) {
                policy.targets.splice(index, 1);
            } else {
                policy.targets.push(this.definition.name);
            }
        } else {
            policy.targets = [this.definition.name];
        }
    }
}
