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
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { AttributesService } from './attributes.service';
import { InstanceService } from '../../instance.service';
import { ModalDirective } from 'ngx-bootstrap';
import { WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';
import { WineryValidatorObject } from '../../../wineryValidators/wineryDuplicateValidator.directive';
import { AttributeDefinition } from '../../../model/attribute';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'winery-attributes',
    templateUrl: 'attributes.component.html',
})
export class AttributesComponent implements OnInit {

    attributes: AttributeDefinition[] = [];

    columns: Array<WineryTableColumn> = [
        { title: 'Name', name: 'key', sort: true },
        { title: 'Type', name: 'type', sort: false },
        { title: 'Default Value', name: 'defaultValue', sort: false },
        { title: 'Description', name: 'description', sort: false },
    ];

    @ViewChild('modal')
    modal: ModalDirective;
    @ViewChild('confirmModal')
    confirmModal: ModalDirective;
    validatorObject: WineryValidatorObject;
    @ViewChild('nameInput') nameInput: ElementRef;

    attr: AttributeDefinition = new AttributeDefinition();
    selectedAttr: AttributeDefinition;

    loading = false;

    constructor(private attributeService: AttributesService, public instanceService: InstanceService) {
    }

    ngOnInit() {
        this.loading = true;
        this.attributeService.getAttributes()
            .subscribe(
                data => {
                    this.attributes = [];
                    data.forEach(item => this.attributes.push(Object.assign(new AttributeDefinition(), item)));
                    this.loading = false;
                },
                error => this.handleError(error)
            );
    }

    private handleError(error: HttpErrorResponse) {
        console.error(error);
        this.loading = false;
    }

    save() {
        this.loading = true;
        this.attributeService.updateAttributes(this.attributes)
            .subscribe(
                () => this.loading = false,
                error => this.handleError(error)
            );
    }

    openModal() {
        this.attr = new AttributeDefinition();
        this.validatorObject = new WineryValidatorObject(this.attributes, 'key');
        this.modal.show();
    }

    openConfirmModal(attr: AttributeDefinition) {
        if (attr === null || attr === undefined) {
            return;
        }
        this.selectedAttr = attr;
        this.confirmModal.show();
    }

    onModalShown() {
        this.nameInput.nativeElement.focus();
    }

    addAttribute(attr: AttributeDefinition) {
        const o = Object.assign(new AttributeDefinition(), attr);
        this.attributes.push(o);
        this.save();
    }

    removeAttribute() {
        for (let i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i].key === this.selectedAttr.key) {
                this.attributes.splice(i, 1);
            }
        }
        this.confirmModal.hide();
        this.selectedAttr = null;
        this.save();
    }
}
