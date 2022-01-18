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
import { Subscription } from 'rxjs';
import { LiveModelingService } from '../../../services/live-modeling.service';

@Component({
    selector: 'winery-live-modeling-disable-modal',
    templateUrl: './disable-modal.component.html',
    styleUrls: ['./disable-modal.component.css']
})
export class DisableModalComponent implements OnInit, OnDestroy {
    subscriptions: Array<Subscription> = [];
    currentServiceTemplateInstanceId: string;

    constructor(private bsModalRef: BsModalRef,
                private ngRedux: NgRedux<IWineryState>,
                private liveModelingService: LiveModelingService,
    ) {
    }

    ngOnInit(): void {
        this.subscriptions.push(this.ngRedux.select((state) => {
            return state.liveModelingState.currentServiceTemplateInstanceId;
        })
            .subscribe((instanceId) => {
                this.currentServiceTemplateInstanceId = instanceId;
            }));
    }

    disable() {
        this.liveModelingService.disable();
        this.dismissModal();
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
