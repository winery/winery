/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { BsModalRef, ModalDirective } from 'ngx-bootstrap';

@Component({
    selector: 'winery-modal-footer',
    templateUrl: 'winery.modal.footer.component.html'
})
export class WineryModalFooterComponent {

    @Input() showDefaultButtons = true;
    @Input() closeButtonLabel = 'Cancel';
    @Input() okButtonLabel = 'Add';
    @Input() modalRef: ModalDirective | BsModalRef;
    @Input() hideOkButton = false;
    @Input() disableOkButton = false;
    @Input() hideOnOk = true;
    @Output() onOk = new EventEmitter<any>();
    @Output() onCancel = new EventEmitter<any>();

    ok() {
        this.onOk.emit();
        if (this.hideOnOk) {
            this.modalRef.hide();
        }
    }

    cancel() {
        this.onCancel.emit();
        this.modalRef.hide();
    }
}
