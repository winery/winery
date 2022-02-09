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
import {
    AfterViewInit, Component, ElementRef, OnDestroy, OnInit, QueryList, ViewChild, ViewChildren
} from '@angular/core';
import { LiveModelingLog } from '../../models/liveModelingLog';
import { Subscription } from 'rxjs';
import { LoggingService } from '../../services/logging.service';
import { LiveModelingLogTypes } from '../../models/enums';
import { InputParameter } from '../../models/container/input-parameter.model';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';

@Component({
    selector: 'winery-live-modeling-sidebar-buildplan-parameters',
    templateUrl: './buildplan-parameters.component.html',
    styleUrls: ['./buildplan-parameters.component.css'],
})
export class BuildplanParametersComponent implements OnInit, OnDestroy {

    buildPlanInputParameters: Array<InputParameter> = [];
    subscriptions: Array<Subscription> = [];

    constructor(private ngRedux: NgRedux<IWineryState>) {
    }

    ngOnInit() {
        this.subscriptions.push(this.ngRedux.select((state) => {
            return state.liveModelingState.currentBuildPlanInstance;
        })
            .subscribe((buildPlanInstance) => {
                if (buildPlanInstance && buildPlanInstance.hasOwnProperty('inputs')) {
                    this.buildPlanInputParameters = buildPlanInstance.inputs;
                }
            }));
    }

    ngOnDestroy() {
        this.subscriptions.forEach((subscription) => {
            subscription.unsubscribe();
        });
    }
}
