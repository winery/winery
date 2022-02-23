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
import { Subscription } from 'rxjs';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';
import { Csar } from '../../models/container/csar.model';

@Component({
    selector: 'winery-live-modeling-sidebar-csar-info',
    templateUrl: './csar-info.component.html',
    styleUrls: ['./csar-info.component.css'],
})
export class CsarInfoComponent implements OnInit, OnDestroy {

    currentCsar: Csar;
    subscriptions: Array<Subscription> = [];

    constructor(private ngRedux: NgRedux<IWineryState>) {
    }

    ngOnInit() {
        this.subscriptions.push(this.ngRedux.select((state) => {
            return state.liveModelingState.currentCsar;
        })
            .subscribe((csar) => {
                this.currentCsar = csar;
            }));
    }

    ngOnDestroy() {
        this.subscriptions.forEach((subscription) => {
            subscription.unsubscribe();
        });
    }
}
