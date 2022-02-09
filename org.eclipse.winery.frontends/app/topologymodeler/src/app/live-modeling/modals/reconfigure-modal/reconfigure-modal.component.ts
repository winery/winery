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

import { Component, OnInit } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap';
import { ReconfigureOptions } from '../../../models/enums';
import { PropertyValidatorService } from '../../../services/property-validator.service';

@Component({
    selector: 'winery-live-modeling-reconfigure-modal',
    templateUrl: './reconfigure-modal.component.html',
    styleUrls: ['./reconfigure-modal.component.css']
})
export class ReconfigureModalComponent implements OnInit {

    selectedOption = ReconfigureOptions.NONE;
    startInstance = true;
    ReconfigureOptions = ReconfigureOptions;

    constructor(private bsModalRef: BsModalRef,
                private propertyValidatorService: PropertyValidatorService
    ) {
    }

    ngOnInit(): void {
    }

    isOptionSelected(redeployOption: ReconfigureOptions) {
        return this.selectedOption === redeployOption;
    }

    toggleOption(redeployOption: ReconfigureOptions) {
        this.selectedOption === redeployOption ? this.selectedOption = ReconfigureOptions.NONE : this.selectedOption = redeployOption;
    }

    cancel() {
        this.selectedOption = ReconfigureOptions.NONE;
        this.dismissModal();
    }

    confirm() {
        this.dismissModal();
    }

    isTopologyInvalid(): boolean {
        return this.propertyValidatorService.isTopologyInvalid();
    }

    dismissModal() {
        this.bsModalRef.hide();
    }
}
