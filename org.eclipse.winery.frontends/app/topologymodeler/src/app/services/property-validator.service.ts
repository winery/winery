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
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { BsModalService } from 'ngx-bootstrap';
import { Injectable } from '@angular/core';

@Injectable()
export class PropertyValidatorService {
    private validationEnabled: boolean;
    private nodeTemplates: any[];

    constructor(private ngRedux: NgRedux<IWineryState>,
                private modalService: BsModalService) {
        this.ngRedux.select((state) => {
            return state.topologyRendererState.buttonsState.checkNodePropertiesButton;
        })
            .subscribe((checked) => {
                this.validationEnabled = checked;
            });
        this.ngRedux.select((state) => {
            return state.wineryState.currentJsonTopology.nodeTemplates;
        })
            .subscribe((nodeTemplates) => {
                this.nodeTemplates = nodeTemplates;
            });
    }

    public isTopologyInvalid(): boolean {
        if (this.validationEnabled) {
            return this.nodeTemplates.some((node) => {
                return !node.valid;
            });
        } else {
            return false;
        }
    }
}
