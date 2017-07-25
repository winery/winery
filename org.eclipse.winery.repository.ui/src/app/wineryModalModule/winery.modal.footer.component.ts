/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Tino Stadelmaier - initial API and implementation
 */
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
    selector: 'winery-modal-footer',
    templateUrl: 'winery.modal.footer.component.html'
})
export class WineryModalFooterComponent {

    @Input() showDefaultButtons = true;
    @Input() closeButtonLabel = 'Cancel';
    @Input() okButtonLabel = 'Add';
    @Input() modalRef: any;
    @Input() disableOkButton = false;
    @Output() onOk = new EventEmitter<any>();
    @Output() onCancel = new EventEmitter<any>();

    ok() {
        this.onOk.emit();
        this.modalRef.hide();
    }

    cancel() {
        this.onCancel.emit();
        this.modalRef.hide();
    }
}
