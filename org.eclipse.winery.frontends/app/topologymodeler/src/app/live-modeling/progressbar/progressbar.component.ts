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
import { Component, OnDestroy, OnInit, } from '@angular/core';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';
import { Subscription } from 'rxjs';
import { LiveModelingStates } from '../../models/enums';
import { ProgressbarConfig } from 'ngx-bootstrap';

export function getProgressbarConfig(): ProgressbarConfig {
    return Object.assign(new ProgressbarConfig(), { animate: true, striped: true, max: 100 });
}

@Component({
    selector: 'winery-live-modeling-sidebar-progressbar',
    providers: [{ provide: ProgressbarConfig, useFactory: getProgressbarConfig }],
    templateUrl: './progressbar.component.html',
    styleUrls: ['./progressbar.component.css'],
})
export class ProgressbarComponent implements OnInit, OnDestroy {

    showProgressbar: boolean;
    subscriptions: Array<Subscription> = [];
    content = '';

    constructor(private ngRedux: NgRedux<IWineryState>) {
    }

    ngOnInit() {
        this.subscriptions.push(this.ngRedux.select((state) => {
            return state.liveModelingState.state;
        })
            .subscribe((state) => {
                this.toggleProgressbar(state);
                this.updateProgressbarContent(state);
            }));
    }

    ngOnDestroy() {
        this.subscriptions.forEach((subscription) => {
            subscription.unsubscribe();
        });
    }

    private toggleProgressbar(liveModelingState: LiveModelingStates): void {
        switch (liveModelingState) {
            case LiveModelingStates.TERMINATED:
            case LiveModelingStates.DISABLED:
            case LiveModelingStates.ENABLED:
            case LiveModelingStates.ERROR:
                this.showProgressbar = false;
                break;
            default:
                this.showProgressbar = true;
        }
    }

    private updateProgressbarContent(liveModelingState: LiveModelingStates): void {
        switch (liveModelingState) {
            case LiveModelingStates.INIT:
                this.content = 'UPLOADING CSAR';
                break;
            case LiveModelingStates.DEPLOY:
                this.content = 'DEPLOYING INSTANCE';
                break;
            case LiveModelingStates.UPDATE:
                this.content = 'UPDATING INSTANCE';
                break;
            case LiveModelingStates.RECONFIGURATE:
                this.content = 'RECONFIGURING INSTANCE';
                break;
            case LiveModelingStates.TERMINATE:
                this.content = 'TERMINATING INSTANCE';
                break;
            default:
                this.content = '';
                break;
        }
    }
}
