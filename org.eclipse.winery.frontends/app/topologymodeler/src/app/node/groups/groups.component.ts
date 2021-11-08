/********************************************************************************
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
 ********************************************************************************/

import { Component, Input, OnInit } from '@angular/core';
import { TGroupDefinition, TNodeTemplate } from '../../models/ttopology-template';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';
import { TopologyRendererActions } from '../../redux/actions/topologyRenderer.actions';

@Component({
    selector: 'winery-groups',
    templateUrl: './groups.component.html',
    styleUrls: ['./groups.component.css']
})
export class GroupsComponent implements OnInit {

    @Input() readonly: boolean;
    @Input() groups: TGroupDefinition[];
    @Input() node: TNodeTemplate;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private rendererActions: TopologyRendererActions) {
    }

    ngOnInit() {
    }

    manageYamlGroups() {
        this.ngRedux.dispatch(this.rendererActions.showManageYamlGroups());
    }

    isEllipsisActive(cell): boolean {
        return (cell.offsetWidth < cell.scrollWidth);
    }

    isNodeMemberOfGroup(group: TGroupDefinition) {
        return group.members && group.members.some((m) => m === this.node.id);
    }

    toggleGroupMembership(group: TGroupDefinition) {
        if (group.members) {
            const index = group.members.indexOf(this.node.id);
            if (index >= 0) {
                group.members.splice(index, 1);
            } else {
                group.members.push(this.node.id);
            }
        } else {
            group.members = [this.node.id];
        }
    }

    isEmpty(): boolean {
        return !this.groups || this.groups.length === 0;
    }
}
