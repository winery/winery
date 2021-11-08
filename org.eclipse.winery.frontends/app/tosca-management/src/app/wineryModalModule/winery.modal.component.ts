/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
import { AfterContentInit, AfterViewInit, Component, ContentChild, HostBinding, Input } from '@angular/core';
import { WineryModalFooterComponent } from './winery.modal.footer.component';
import { WineryModalHeaderComponent } from './winery.modal.header.component';
import { ModalDirective } from 'ngx-bootstrap';
import { WineryModalSize } from './wineryModalSize';

/**
 * @deprecated
 * This component should not be used anymore
 * Please use <ng-template> and the BsModalService to show a modal (e.g. plan.component.ts).
 * @example
 * <ng-template #removeElementModal>
 *     <winery-modal-header [modalRef]="removeElementModalRef" [title]="modalTitle"></winery-modal-header>
 *     <winery-modal-body>
 *         <p>Test</p>
 *     </winery-modal-body>
 *     <winery-modal-footer [modalRef]="removeElementModalRef"
 *              [closeButtonLabel]="'Cancel'" [okButtonLabel]="'Delete'"
 *              (onOk)="onRemoveElement()"></winery-modal-footer>
 *</ng-template>
 */
@Component({
    selector: 'winery-modal',
    templateUrl: 'winery.modal.component.html',
})
export class WineryModalComponent implements AfterViewInit, AfterContentInit {

    @Input() modalRef: ModalDirective;
    @Input() size: string;
    @Input() keyboard = true;
    @Input() backdrop = true;

    @HostBinding('class') hostClass = 'modal fade';
    @HostBinding('attr.role') hostRole = 'dialog';
    @HostBinding('tabindex') hostTabIndex = '-1';

    @ContentChild(WineryModalHeaderComponent) headerContent: WineryModalHeaderComponent;
    @ContentChild(WineryModalFooterComponent) footerContent: WineryModalFooterComponent;

    private overrideSize: string = null;
    private cssClass = '';

    ngAfterContentInit(): void {
        if (this.headerContent) {
            this.headerContent.modalRef = this.modalRef;
        }
        if (this.footerContent) {
            this.footerContent.modalRef = this.modalRef;
        }
    }

    ngAfterViewInit(): void {
        if (this.backdrop) {
            this.modalRef.config.backdrop = true;
        } else {
            this.modalRef.config.backdrop = 'static';
        }

        this.modalRef.config.keyboard = this.keyboard;

        if (WineryModalSize.validSize(this.size)) {
            this.overrideSize = this.size;
        }
    }

    getCssClasses(): string {
        const classes: string[] = [];

        if (this.isSmall()) {
            classes.push(WineryModalSize.SMALL);
        }

        if (this.isLarge()) {
            classes.push(WineryModalSize.LARGE);
        }

        if (this.cssClass !== '') {
            classes.push(this.cssClass);
        }

        return classes.join(' ');
    }

    private isSmall() {
        return this.overrideSize !== WineryModalSize.LARGE
            && this.size === WineryModalSize.SMALL
            || this.overrideSize === WineryModalSize.SMALL;
    }

    private isLarge() {
        return this.overrideSize !== WineryModalSize.SMALL
            && this.size === WineryModalSize.LARGE
            || this.overrideSize === WineryModalSize.LARGE;
    }
}
