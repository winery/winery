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
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Parameter, YamlTypes } from '../../../model/parameters';
import { WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';
import { WineryValidatorObject } from '../../../wineryValidators/wineryDuplicateValidator.directive';
import { DynamicDropdownData } from '../../../wineryDynamicTable/formComponents/dynamicDropdown.component';
import { DynamicCheckboxData } from '../../../wineryDynamicTable/formComponents/dynamicCheckbox.component';
import { DynamicTextData } from '../../../wineryDynamicTable/formComponents/dynamicText.component';
import { WineryDynamicTableMetadata } from '../../../wineryDynamicTable/wineryDynamicTableMetadata';
import { Validators } from '@angular/forms';

@Component({
    selector: 'winery-parameters',
    templateUrl: 'parameters.component.html'
})
export class ParametersComponent implements OnInit {

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

    @Output() onParameterEdited = new EventEmitter<Parameter>();
    @Output() onParameterAdded = new EventEmitter<Parameter>();
    @Output() onParameterRemoved = new EventEmitter<Parameter>();

    dynamicTableData: Array<WineryDynamicTableMetadata> = [];

    validatorObject: WineryValidatorObject;

    ngOnInit(): void {
        this.dynamicTableData = [
            new DynamicTextData(
                'key',
                'Name',
                0,
                [Validators.required],
            )];
        if (this.containsColumn('type')) {
            this.dynamicTableData.push(
                new DynamicDropdownData<YamlTypes>(
                    'type',
                    'Type',
                    [
                        { label: 'string', value: 'string' },
                        { label: 'integer', value: 'integer' },
                        { label: 'float', value: 'float' },
                        { label: 'boolean', value: 'boolean' },
                        { label: 'timestamp', value: 'timestamp' }
                    ],
                    1,
                    'string',
                    [Validators.required],
                ));
        }
        if (this.containsColumn('required')) {
            this.dynamicTableData.push(
                new DynamicCheckboxData(
                    'required',
                    'Required',
                    false,
                    2,
                ));
        }
        if (this.containsColumn('defaultValue')) {
            this.dynamicTableData.push(
                new DynamicTextData(
                    'defaultValue',
                    'Default Value',
                    3,
                ));
        }
        if (this.containsColumn('value')) {
            this.dynamicTableData.push(
                new DynamicTextData(
                    'value',
                    'Value',
                    4
                ));
        }
        if (this.containsColumn('description')) {
            this.dynamicTableData.push(
                new DynamicTextData(
                    'description',
                    'Description',
                    5
                ));
        }
    }

    editParameter(param: Parameter) {
        const p = Object.assign(new Parameter(), param);
        this.onParameterAdded.emit(p);
    }

    addParameter(param: Parameter) {
        const p = Object.assign(new Parameter(), param);
        this.onParameterAdded.emit(p);
    }

    removeParameter(param: Parameter) {
        this.onParameterRemoved.emit(Object.assign(param));
    }

    private containsColumn(name: String): boolean {
        return this.columns.filter(c => c.name === name).length > 0;
    }
}
