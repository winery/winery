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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../../redux/store/winery.store';
import { LiveModelingActions } from '../../../redux/actions/live-modeling.actions';
import { Subscription } from 'rxjs';
import { LiveModelingSettings } from '../../../models/liveModelingSettings';

@Component({
    selector: 'winery-live-modeling-settings-modal',
    templateUrl: './settings-modal.component.html',
    styleUrls: ['./settings-modal.component.css']
})
export class SettingsModalComponent implements OnInit, OnDestroy {

    objectKeys = Object.keys;
    settings: LiveModelingSettings;
    subscriptions: Array<Subscription> = [];

    constructor(private bsModalRef: BsModalRef,
                private ngRedux: NgRedux<IWineryState>,
                private liveModelingActions: LiveModelingActions,
    ) {
    }

    ngOnInit(): void {
        this.subscriptions.push(this.ngRedux.select((state) => {
            return state.liveModelingState.settings;
        })
            .subscribe((settings) => {
                this.settings = settings;
            }));
    }

    setSettings() {
        this.ngRedux.dispatch(this.liveModelingActions.setSettings(this.settings));
        this.dismissModal();
    }

    getLabel(settingsKey: string) {
        switch (settingsKey) {
            case 'timeout':
                return 'Timeout (ms)';
            case 'interval':
                return 'Interval (ms)';
            case 'wineryEndpoint':
                return 'Winery Endpoint';
            default:
                return '';
        }
    }

    dismissModal() {
        this.bsModalRef.hide();
    }

    ngOnDestroy() {
        this.subscriptions.forEach((subscription) => {
            subscription.unsubscribe();
        });
    }
}
