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

import { Component, EventEmitter, Input, OnChanges, OnInit, Output, ViewChild } from '@angular/core';
import { WineryDynamicTableMetadata } from '../wineryDynamicTableMetadata';
import { FormBuilder, FormGroup, ValidatorFn } from '@angular/forms';
import { ModalDirective } from 'ngx-bootstrap';

/**
 * This component contains the modal used in {@link WineryDynamicTableComponent}.
 * Based on the <code>config</code> forms are generated and added to the form.
 * Moreover, validators and controls are added as well.
 *
 * <label>Inputs</label>
 * <ul>
 *     <li><code>config</code> Contains {@Link WineryDynamicTableMetadata} to configure the forms </li>
 *     <li><code>modalTitle</code> Specifies the title of this modal </li>
 *     <li><code>validators</code> A list of validators which require multiple properties and can not be
 *     validated from the form itself</li>
 * </ul>
 *
 * <label>Outputs</label>
 * <ul>
 *     <li><code>saveClicked</code> Called as soon as the save button is clicked</li>
 * </ul>
 */
@Component({
    selector: 'winery-dynamic-form-modal',
    templateUrl: './wineryDynamicFormModal.component.html',
})
export class WineryDynamicFormModalComponent implements OnInit, OnChanges {

    @Input() config: WineryDynamicTableMetadata[] = [];
    @Input() modalTitle: string;
    @Input() validators: ValidatorFn[] = [];

    @Output() saveClicked = new EventEmitter<any>();

    @ViewChild('modal') modal: ModalDirective;

    elementsToShow: WineryDynamicTableMetadata[] = [];

    form: FormGroup;
    okButtonLabel = 'Save';
    closeButtonLabel = 'Close';

    constructor(private fb: FormBuilder) {
    }

    show(dataToFill?: any) {
        this.modal.show();
        if (!dataToFill) {
            return;
        }
        for (const d of this.config) {
            if (dataToFill[d.key]) {
                this.setValue(d.key, dataToFill[d.key]);
            }
        }
    }

    ngOnInit(): void {
        this.config.sort((a, b) => a.order - b.order);
        // the filtered copy of config is used to display only the elements where the isVisible flag is set
        this.elementsToShow = this.config.filter(function (element) {
            return element.isVisible;
        });
        this.form = this.toFormGroup(this.config);
    }

    ngOnChanges() {
        if (this.form) {
            this.form.setValidators(this.validators);
            const controls = Object.keys(this.form.controls);
            const configControls = this.config.map((item) => item.key);

            controls
                .filter((control) => !configControls.includes(control))
                .forEach((name) => {
                    const config = this.config.find((control) => control.key === name);
                    this.form.addControl(name, this.createControl(config));
                });
        }
    }

    toFormGroup(dynamicTableData: WineryDynamicTableMetadata[]) {
        const group = this.fb.group({});
        dynamicTableData.forEach(config => {
            group.addControl(config.key, this.createControl(config));
        });
        group.setValidators(this.validators);
        return group;
    }

    createControl(config: WineryDynamicTableMetadata) {
        const value = config.defaultValue;
        const { disabled, validation } = config;
        return this.fb.control({ disabled, value }, validation);
    }

    setValue(key: string, value: any) {
        this.form.controls[key].setValue(value, { emitEvent: true });
    }

    /**
     * emits raw value of forms according to model provided
     * the keys match the keys set in the model
     */
    onOkPress() {
        this.saveClicked.emit(this.form.getRawValue());
    }
}
