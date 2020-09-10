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

import { Component, DoCheck, EventEmitter, Input, IterableDiffer, IterableDiffers, OnInit, Output, ViewChild } from '@angular/core';
import { WineryDynamicTableMetadata } from './wineryDynamicTableMetadata';
import { ModalDirective } from 'ngx-bootstrap';
import { WineryRowData, WineryTableColumn, WineryTableComponent } from '../wineryTableModule/wineryTable.component';
import { WineryDynamicFormModalComponent } from './modal/wineryDynamicFormModal.component';
import { ValidatorFn } from '@angular/forms';
import { WineryValidatorObject } from '../wineryValidators/wineryDuplicateValidator.directive';

/**
 * This component contains functionality commonly used inside the winery when working with tables.
 * By using this component modals to Add, Edit and Remove data are generated automatically based
 * on the supplied <code>dynamicMetadata</code>. Furthermore, for each supplied {@link WineryDynamicTableMetadata} object
 * a table column is created which can be labelled as well.
 *
 * Keys supplied in <code>dynamicMetadata</code> have to match the corresponding keys inside <code>data</code>.
 * E.g.
 * data: { key: 'someKey', type: 'float', description: 'test'}
 * metadata:
 * [new DynamicTextData('key', 'Name', ...),
 *  new DynamicDropdownData<'integer'|'float'>('type','Type',
 *      [{label: 'integer', value: 'integer'},
 *      {label: 'float', value: 'float'},), ], ...),
 *  new DynamicTextData('description','Description', ..)]
 *
 * creates a table with the following rows: 'Name', 'Type', 'Description'.
 * Furthermore, when clicking Add/Edit a modal is generated which contains two text fields and
 * a dropdown form. The dropdown contains the options given in the constructor.
 * Each Metadata Object is configurable by changing values in the constructor. It correlates
 * to a component which contains the individual logic, templates etc.
 *
 * A complete and short example can be found in {@link AttributesComponent}
 *
 * <label>Inputs</label>
 * <ul>
 *     <li><code>dynamicMetadata</code> Contains necessary meta data to generate the table and forms </li>
 *     <li><code>tableTitle</code> Sets title of the table </li>
 *     <li><code>modalTitle</code> Sets title of the modal for editing/adding </li>
 *     <li><code>deleteModalTitle</code>  Sets title of the modal for deleting </li>
 *     <li><code>data</code>  Contains data with which the table will be filled </li>
 *     <li><code>avoidDuplicateProperties</code> Prevents setting the same property in different rows e.g. unique names.
 *     Requires a list of strings which contain the keys of the property</li>
 *     <li><code>disableFiltering</code>  Disables filtering of the table </li>
 * </ul>
 *
 * <label>Outputs</label>
 * <ul>
 *     <li><code>entryEdited</code> Called if an entry has been edited, contains the changed row</li>
 *     <li><code>entryEdited</code> Called if an entry has been added, contains the added row</li>
 *     <li><code>entryEdited</code> Called if an entry has been deleted, contains the deleted row</li>
 * </ul>
 */
@Component({
    selector: 'winery-dynamic-table',
    templateUrl: './wineryDynamicTable.component.html'
})
export class WineryDynamicTableComponent implements OnInit, DoCheck {

    @Input() dynamicMetadata: WineryDynamicTableMetadata[] = [];
    @Input() tableTitle = 'Table';
    @Input() modalTitle = 'Add/Edit Entry';
    @Input() deleteModalTitle = 'Delete';
    @Input() data: any[];
    @Input() avoidDuplicateProperties: string[] = [];
    @Input() disableFiltering = true;

    @Output() entryEdited = new EventEmitter<any>();
    @Output() entryRemoved = new EventEmitter<any>();
    @Output() entryAdded = new EventEmitter<any>();

    @ViewChild('table') wineryTable: WineryTableComponent;
    @ViewChild('generatedModal') generatedModal: WineryDynamicFormModalComponent;
    @ViewChild('confirmRemoveModal') confirmRemoveModal: ModalDirective;

    selectedRow: any;
    defaultData: any;
    tableColumns: WineryTableColumn[] = [];

    // checks current state according to button presses on table
    currentState: 'Add' | 'Edit' | 'Remove' = 'Add';
    humanReadableTableData: any[] = [];

    // hashmap for quick access of dynamicMetadata depending on the key
    dynamicDataMap = new Map<string, WineryDynamicTableMetadata>();

    modalValidators: ValidatorFn[];

    private iterableDiffer: IterableDiffer<any>;

    constructor(private iterableDiffers: IterableDiffers) {
        this.iterableDiffer = iterableDiffers.find([]).create(null);
    }

    ngOnInit() {
        this.defaultData = {};
        for (const field of this.dynamicMetadata) {
            if (field.defaultValue) {
                this.defaultData[field.key] = field.defaultValue;
            }
        }
    }

    /**
     * checks for changes in this.data and refreshes table if they occur
     * as the OnChanges Lifecycle Hook will only trigger when the input properties instance changes
     */
    ngDoCheck() {
        const changes = this.iterableDiffer.diff(this.data);
        if (changes) {
            this.refreshData();
        }
    }

    private refreshData() {
        this.dynamicMetadata.sort((a, b) => a.order - b.order);
        // generate table columns from model
        this.tableColumns = this.dynamicMetadata.filter(function (element) {
            return element.isVisible;
        }).map(item => ({
            title: item.label,
            name: item.key,
            sort: item.sortTableCol
        }));
        if (!this.dynamicDataMap) {
            this.dynamicDataMap = new Map;
            for (const dynamicData of this.dynamicMetadata) {
                this.dynamicDataMap[dynamicData.key] = dynamicData;
            }
        }

        /*
         * insert uuids to match human readable table data to the real data
         */
        this.data.forEach((value: any, index: number, array: any[]) => {
            // tslint:disable-next-line:no-bitwise
            array[index]['uuid'] = (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
        });

        this.convertToTableData();
    }

    /**
     * try to map data arrays to human readable values according to
     * toHumanReadable function in dynamicTableData class
     *
     * this is really hacky as data is still of type any[]
     * we need a type for this.data in the future to do this properly.
     * However ng2-table does not support this, migrating to a newer
     * version of ng-table would be necessary
     */
    private convertToTableData() {
        this.humanReadableTableData = [];
        this.data.forEach((value: any, index: number, array: any[]) => {
            const data = array[index];
            const tmp = { 'uuid': data['uuid'] };
            for (const key of Object.keys(data)) {
                if (this.dynamicDataMap && this.dynamicDataMap[key]) {
                    tmp[key] = this.dynamicDataMap[key].toHumanReadable(data[key]);
                } else {
                    tmp[key] = data[key];
                }
            }
            this.humanReadableTableData.push(tmp);
        });
    }

    addOrEdit(param: any) {
        if (this.currentState === 'Edit' && this.selectedRow) {
            for (let i = 0; i < this.data.length; i++) {
                if (this.data[i].key === this.selectedRow.key) {
                    this.data[i] = param;
                    this.entryEdited.emit(param);
                }
            }
        } else if (this.currentState === 'Add') {
            this.data.push(param);
            this.entryAdded.emit(param);
        }
        this.convertToTableData();
    }

    removeParameter() {
        for (let i = 0; i < this.data.length; i++) {
            if (this.data[i].key === this.selectedRow.key) {
                this.data.splice(i, 1);
            }
        }
        this.convertToTableData();
        this.confirmRemoveModal.hide();
        this.entryRemoved.emit(this.selectedRow);
        this.selectedRow = null;
    }

    addValidatorsToModal(uuid?: string) {
        this.modalValidators = [];
        for (const avoidDuplicateKey of this.avoidDuplicateProperties) {
            this.modalValidators.push(WineryValidatorObject.duplicateValidator(this.data, avoidDuplicateKey, uuid));
        }
    }

    addClicked() {
        this.currentState = 'Add';
        this.addValidatorsToModal();
        this.generatedModal.show(this.defaultData);
    }

    editClicked() {
        this.currentState = 'Edit';
        this.addValidatorsToModal(this.selectedRow['uuid']);
        this.generatedModal.show(this.selectedRow);
    }

    removeClicked() {
        this.currentState = 'Remove';
        this.confirmRemoveModal.show();
    }

    selectedCell(cell: WineryRowData) {
        // we only need the selected row
        this.selectedRow = this.data.find((item) => item['uuid'] === cell.row['uuid']);
    }
}
