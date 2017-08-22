/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter, Niko Stadelmaier - initial API and implementation
 */
import { Component, OnInit, ViewChild } from '@angular/core';
import { InstanceService } from '../../instance.service';
import { PropertiesDefinitionService } from './propertiesDefinition.service';
import {
    PropertiesDefinition,
    PropertiesDefinitionEnum,
    PropertiesDefinitionKVList,
    PropertiesDefinitionsResourceApiData,
    WinerysPropertiesDefinition
} from './propertiesDefinitionsResourceApiData';
import { SelectData } from '../../../wineryInterfaces/selectData';
import { isNullOrUndefined } from 'util';
import { Response } from '@angular/http';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { WineryValidatorObject } from '../../../wineryValidators/wineryDuplicateValidator.directive';
import { WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';
import { ModalDirective } from 'ngx-bootstrap';

@Component({
    templateUrl: 'propertiesDefinition.component.html',
    styleUrls: [
        'propertiesDefinition.component.css'
    ],
    providers: [
        PropertiesDefinitionService
    ]
})
export class PropertiesDefinitionComponent implements OnInit {

    propertiesEnum = PropertiesDefinitionEnum;
    loading = true;

    resourceApiData: PropertiesDefinitionsResourceApiData;
    selectItems: SelectData[];
    activeElement: SelectData;
    selectedCell: any;
    elementToRemove: any = null;
    columns: Array<WineryTableColumn> = [
        {title: 'Name', name: 'key', sort: true},
        {title: 'Type', name: 'type', sort: true},
    ];
    newProperty: PropertiesDefinitionKVList = new PropertiesDefinitionKVList();

    validatorObject: WineryValidatorObject;
    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;
    @ViewChild('addModal') addModal: ModalDirective;

    constructor(private sharedData: InstanceService, private service: PropertiesDefinitionService,
                private notify: WineryNotificationService) {
    }

    // region ########## Angular Callbacks ##########
    /**
     * @override
     */
    ngOnInit() {
        this.getPropertiesDefinitionsResourceApiData();
    }

    // endregion

    // region ########## Template Callbacks ##########
    // region ########## Radio Buttons ##########
    onNoneSelected(): void {
        this.resourceApiData.selectedValue = PropertiesDefinitionEnum.None;
    }

    /**
     * Called by the template, if property XML Element is selected. It sends a GET request
     * to the backend to get the data for the select dropdown.
     */
    onXmlElementSelected(): void {
        this.resourceApiData.selectedValue = PropertiesDefinitionEnum.Element;
        this.service.getXsdElementDefinitions()
            .subscribe(
                data => this.selectItems = data,
                error => this.handleError(error)
            );

        if (isNullOrUndefined(this.resourceApiData.propertiesDefinition)) {
            this.resourceApiData.propertiesDefinition = new PropertiesDefinition();
        }

        this.resourceApiData.propertiesDefinition.type = null;
        this.resourceApiData.winerysPropertiesDefinition = null;

        this.activeElement = new SelectData();
        this.activeElement.text = this.resourceApiData.propertiesDefinition.element;
    }

    /**
     * Called by the template, if property XML Type is selected. It sends a GET request
     * to the backend to get the data for the select dropdown.
     */
    onXmlTypeSelected(): void {
        this.resourceApiData.selectedValue = PropertiesDefinitionEnum.Type;
        this.service.getXsdTypeDefinitions()
            .subscribe(
                data => this.selectItems = data,
                error => this.handleError(error)
            );

        if (isNullOrUndefined(this.resourceApiData.propertiesDefinition)) {
            this.resourceApiData.propertiesDefinition = new PropertiesDefinition();
        }

        this.resourceApiData.propertiesDefinition.element = null;
        this.resourceApiData.winerysPropertiesDefinition = null;

        this.activeElement = new SelectData();
        this.activeElement.text = this.resourceApiData.propertiesDefinition.type;
    }

    /**
     * Called by the template, if the custom key/value pair property is selected. It will display
     * a table to enter those pairs.
     */
    onCustomKeyValuePairSelected(): void {
        this.resourceApiData.selectedValue = PropertiesDefinitionEnum.Custom;

        if (isNullOrUndefined(this.resourceApiData.propertiesDefinition)) {
            this.resourceApiData.propertiesDefinition = new PropertiesDefinition();
        }
        this.resourceApiData.propertiesDefinition.element = null;
        this.resourceApiData.propertiesDefinition.type = null;

        if (isNullOrUndefined(this.resourceApiData.propertiesDefinition)) {
            this.resourceApiData.propertiesDefinition = new PropertiesDefinition();
        }
        this.resourceApiData.propertiesDefinition.element = null;
        this.resourceApiData.propertiesDefinition.type = null;

        if (isNullOrUndefined(this.resourceApiData.winerysPropertiesDefinition)) {
            this.resourceApiData.winerysPropertiesDefinition = new WinerysPropertiesDefinition();
        }
        // The key/value pair list may be null
        if (isNullOrUndefined(this.resourceApiData.winerysPropertiesDefinition.propertyDefinitionKVList)) {
            this.resourceApiData.winerysPropertiesDefinition.propertyDefinitionKVList = [];
        }

        if (isNullOrUndefined(this.resourceApiData.winerysPropertiesDefinition.namespace)) {
            this.resourceApiData.winerysPropertiesDefinition.namespace = this.sharedData.selectedNamespace + '/properties';
        }
        if (isNullOrUndefined(this.resourceApiData.winerysPropertiesDefinition.elementName)) {
            this.resourceApiData.winerysPropertiesDefinition.elementName = 'properties';
        }

        this.activeElement = new SelectData();
        this.activeElement.text = this.resourceApiData.winerysPropertiesDefinition.namespace;
    }

    // endregion

    // region ########## Button Callbacks ##########
    save(): void {
        this.loading = true;
        if (this.resourceApiData.selectedValue === PropertiesDefinitionEnum.None) {
            this.service.deletePropertiesDefinitions()
                .subscribe(
                    data => this.handleDelete(data),
                    error => this.handleError(error)
                );
        } else {
            this.service.postPropertiesDefinitions(this.resourceApiData)
                .subscribe(
                    data => this.handleSave(data),
                    error => this.handleError(error)
                );
        }
    }

    /**
     * handler for clicks on remove button
     * @param data
     */
    onRemoveClick(data: any) {
        if (isNullOrUndefined(data)) {
            return;
        } else {
            this.elementToRemove = data;
            this.confirmDeleteModal.show();
        }
    }

    /**
     * handler for clicks on the add button
     */
    onAddClick() {
        this.newProperty = new PropertiesDefinitionKVList();
        this.validatorObject = new WineryValidatorObject(this.resourceApiData.winerysPropertiesDefinition.propertyDefinitionKVList, 'key');
        this.addModal.show();
    }

    // endregion

    /**
     * Called by the template, if a property is selected in the select box. Cannot be replaced
     * by ngModel in the template because the same select is used for element and type definitions.
     */
    xmlValueSelected(event: SelectData): void {
        if (this.resourceApiData.selectedValue === PropertiesDefinitionEnum.Element) {
            this.resourceApiData.propertiesDefinition.element = event.text;
        } else if (this.resourceApiData.selectedValue === PropertiesDefinitionEnum.Type) {
            this.resourceApiData.propertiesDefinition.type = event.text;
        }
    }

    onCellSelected(data: any) {
        if (isNullOrUndefined(data)) {
            this.selectedCell = data;
        }
    }

    // endregion

    // region ########## Modal Callbacks ##########
    /**
     * Adds a property to the table and model
     * @param propType
     * @param propName
     */
    addProperty(propType: string, propName: string) {
        this.resourceApiData.winerysPropertiesDefinition.propertyDefinitionKVList.push({
            key: propName,
            type: propType
        });
        this.addModal.hide();
    }

    removeConfirmed() {
        this.confirmDeleteModal.hide();
        this.deleteItemFromPropertyDefinitionKvList(this.elementToRemove);
        this.elementToRemove = null;
    }

    // endregion

    // region ########## Private Methods ##########
    private getPropertiesDefinitionsResourceApiData(): void {
        this.loading = true;
        this.service.getPropertiesDefinitionsData()
            .subscribe(
                data => this.handlePropertiesDefinitionData(data),
                error => this.handleError(error)
            );
    }

    /**
     * Set loading to false and show success notification.
     *
     * @param data
     * @param actionType
     */
    private handleSuccess(data: any, actionType?: string): void {
        this.loading = false;
        switch (actionType) {
            case 'delete':
                this.notify.success('Deleted PropertiesDefinition', 'Success');
                break;
            case 'change':
                this.notify.success('Saved changes on server', 'Success');
                break;
            default:
                break;
        }
    }

    /**
     * Reloads the new data from the backend (only called on success).
     *
     * @param data
     */
    private handleDelete(data: any): void {
        this.handleSuccess(data, 'delete');
        this.getPropertiesDefinitionsResourceApiData();
    }

    private handlePropertiesDefinitionData(data: PropertiesDefinitionsResourceApiData): void {
        this.resourceApiData = data;

        // because the selectedValue doesn't get set correctly do it here
        switch (isNullOrUndefined(this.resourceApiData.selectedValue) ? '' : this.resourceApiData.selectedValue.toString()) {
            case 'Element':
                this.onXmlElementSelected();
                break;
            case 'Type':
                this.onXmlTypeSelected();
                break;
            case 'Custom':
                this.onCustomKeyValuePairSelected();
                break;
            default:
                this.resourceApiData.selectedValue = PropertiesDefinitionEnum.None;
        }

        this.handleSuccess(data);
    };

    private handleSave(data: Response) {
        this.handleSuccess(data, 'change');
        this.getPropertiesDefinitionsResourceApiData();
    }

    /**
     * Deletes a property from the table and model.
     * @param itemToDelete
     */
    private deleteItemFromPropertyDefinitionKvList(itemToDelete: any): void {
        const list = this.resourceApiData.winerysPropertiesDefinition.propertyDefinitionKVList;
        for (let i = 0; i < list.length; i++) {
            if (list[i].key === itemToDelete.key) {
                list.splice(i, 1);
            }
        }
    }

    /**
     * Sets loading to false and shows error notification.
     *
     * @param error
     */
    private handleError(error: any): void {
        this.notify.error(error.toString(), 'Error');
    }

    // endregion
}
