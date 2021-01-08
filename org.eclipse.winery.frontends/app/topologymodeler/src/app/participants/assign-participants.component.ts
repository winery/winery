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
import { NgRedux } from '@angular-redux/store';
import { OTParticipant, TNodeTemplate } from '../models/ttopology-template';
import { IWineryState } from '../redux/store/winery.store';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { WineryActions } from '../redux/actions/winery.actions';

@Component({
    selector: 'winery-assign-participants',
    templateUrl: './assign-participants.component.html',
    styleUrls: ['./assign-participants.component.css']
})
export class AssignParticipantsComponent implements OnInit {

    static NAMESPACE = '{http://www.opentosca.org/winery/extensions/tosca/2013/02/12}';

    @Input() readonly: boolean;
    @Input() participants: OTParticipant[];
    @Input() node: TNodeTemplate;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private ngActions: WineryActions,
                private rendererActions: TopologyRendererActions) {
    }

    ngOnInit() {
    }

    isEllipsisActive(cell): boolean {
        return (cell.offsetWidth < cell.scrollWidth);
    }

    isMember(participant: OTParticipant) {
        const value = this.node.otherAttributes[AssignParticipantsComponent.NAMESPACE + 'participant'];
        if (value) {
            if (value.indexOf(',') > -1) {
                return value.split(',').indexOf(participant.name) > -1;
            } else {
                return participant.name === value;
            }
        }
        return false;
    }

    toggleMembership(participant: OTParticipant) {
        const value = this.node.otherAttributes[AssignParticipantsComponent.NAMESPACE + 'participant'];
        if (this.isMember(participant)) {
            if (value.indexOf(',') > -1) {
                const arr = value.split(',');
                const index = arr.findIndex((p) => p === participant.name);
                arr.splice(index, 1);
                this.ngRedux.dispatch(this.ngActions.assignParticipant(this.node, arr.join(',')));
            } else {
                this.ngRedux.dispatch(this.ngActions.assignParticipant(this.node, ''));
            }
        } else {
            if (value) {
                if (value.indexOf(',') > -1) {
                    const arr = value.split(',');
                    arr.push(participant.name);
                    this.ngRedux.dispatch(this.ngActions.assignParticipant(
                        this.node, arr.join(',')
                    ));
                } else {
                    this.ngRedux.dispatch(this.ngActions.assignParticipant(
                        this.node, value + ',' + participant.name
                    ));
                }
            } else {
                this.ngRedux.dispatch(this.ngActions.assignParticipant(this.node, participant.name));
            }
        }
    }

    isEmpty(): boolean {
        return !this.participants || this.participants.length === 0;
    }
}
