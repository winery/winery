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
import { InputParameter } from '../../../models/container/input-parameter.model';

@Component({
    selector: 'winery-live-modeling-input-parameters-modal',
    templateUrl: './input-parameters-modal.component.html',
    styleUrls: ['./input-parameters-modal.component.css']
})
export class InputParametersModalComponent implements OnInit {
    inputParameters: InputParameter[];
    cancelled = true;

    constructor(private bsModalRef: BsModalRef,
    ) {
    }

    ngOnInit(): void {
    }

    disableButton(): boolean {
        for (let i = 0; i < this.inputParameters.length; i++) {
            if (this.isUndefined(this.inputParameters[i].value)) {
                return true;
            }
        }
        return false;
    }

    isUndefined(someString: string): boolean {
        if (someString === null ||
            typeof someString === 'undefined' ||
            someString === '') {
            return true;
        } else {
            return false;
        }
    }

    confirm() {
        this.cancelled = false;
        this.dismissModal();
    }

    cancel() {
        this.cancelled = true;
        this.dismissModal();
    }

    dismissModal() {
        this.bsModalRef.hide();
    }
}
