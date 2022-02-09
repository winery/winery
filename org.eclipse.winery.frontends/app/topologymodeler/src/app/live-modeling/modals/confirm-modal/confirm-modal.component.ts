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
import { PropertyValidatorService } from '../../../services/property-validator.service';

@Component({
    selector: 'winery-live-modeling-confirm-modal',
    templateUrl: './confirm-modal.component.html',
    styleUrls: ['./confirm-modal.component.css']
})
export class ConfirmModalComponent implements OnInit {
    title: string;
    content: string;
    showWarning = false;
    showStartOption = false;

    startInstance = true;
    confirmed = false;

    constructor(private bsModalRef: BsModalRef,
                private propertyValidatorService: PropertyValidatorService
    ) {
    }

    ngOnInit(): void {
    }

    cancel() {
        this.confirmed = false;
        this.dismissModal();
    }

    confirm() {
        this.confirmed = true;
        this.dismissModal();
    }

    isTopologyInvalid(): boolean {
        return this.propertyValidatorService.isTopologyInvalid();
    }

    dismissModal() {
        this.bsModalRef.hide();
    }
}
