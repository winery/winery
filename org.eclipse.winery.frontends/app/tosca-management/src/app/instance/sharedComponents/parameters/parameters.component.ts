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
import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { Parameter } from '../../../model/parameters';
import { InstanceService } from '../../instance.service';
import { ModalDirective } from 'ngx-bootstrap';
import { WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';
import { WineryValidatorObject } from '../../../wineryValidators/wineryDuplicateValidator.directive';

@Component({
    selector: 'winery-parameters',
    templateUrl: 'parameters.component.html'
})
export class ParametersComponent {

    /* tslint:disable no-bitwise */
    uuid: string = (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);

    @Input() tableTitle = 'Parameters';
    @Input() modalTitle = 'Parameter';
    @Input() parameters: Parameter[] = [];
    @Input() columns: Array<WineryTableColumn> = [
        { title: 'Name', name: 'key', sort: true },
        { title: 'Type', name: 'type', sort: false },
        { title: 'Required', name: 'required', sort: false },
        { title: 'Default Value', name: 'defaultValue', sort: false },
        { title: 'Value', name: 'value', sort: false },
        { title: 'Description', name: 'description', sort: false },
    ];
    @Input() enableFiltering = false;

    @Output() onParameterAdded = new EventEmitter<Parameter>();
    @Output() onParameterRemoved = new EventEmitter<Parameter>();

    @ViewChild('modal') modal: ModalDirective;
    @ViewChild('confirmRemoveModal') confirmRemoveModal: ModalDirective;

    validatorObject: WineryValidatorObject;

    param: Parameter = new Parameter();
    selectedParam: Parameter;

    constructor(public instanceService: InstanceService) {
    }

    openModal() {
        this.param = new Parameter();
        this.validatorObject = new WineryValidatorObject(this.parameters, 'key');
        this.modal.show();
    }

    openConfirmRemoveModal(param: Parameter) {
        if (param === null || param === undefined) {
            return;
        }
        this.selectedParam = param;
        this.confirmRemoveModal.show();
    }

    addParameter(param: Parameter) {
        const p = Object.assign(new Parameter(), param);
        this.parameters.push(p);
        this.onParameterAdded.emit(p);
    }

    removeParameter() {
        for (let i = 0; i < this.parameters.length; i++) {
            if (this.parameters[i].key === this.selectedParam.key) {
                this.parameters.splice(i, 1);
            }
        }
        this.onParameterRemoved.emit(Object.assign(this.selectedParam));
        this.confirmRemoveModal.hide();
        this.selectedParam = null;
    }

    containsColumn(name: String): boolean {
        return this.columns.filter(c => c.name === name).length > 0;
    }
}
