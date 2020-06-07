/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
import { InstanceService } from '../../instance.service';
import { PropertiesDefinitionService } from './propertiesDefinition.service';
import {
    PropertiesDefinition, PropertiesDefinitionEnum, PropertiesDefinitionKVElement, PropertiesDefinitionsResourceApiData, WinerysPropertiesDefinition
} from './propertiesDefinitionsResourceApiData';
import { SelectData } from '../../../model/selectData';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { WineryRowData, WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';
import { BsModalRef, BsModalService, ModalDirective } from 'ngx-bootstrap';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { WineryRepositoryConfigurationService } from '../../../wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { FeatureEnum } from '../../../wineryFeatureToggleModule/wineryRepository.feature.direct';
import { YamlPropertyDefinition } from './yaml/yamlPropertyDefinition';

const winery_properties_columns: Array<WineryTableColumn> = [
    { title: 'Name', name: 'key', sort: true },
    { title: 'Type', name: 'type', sort: true },
    { title: 'Required', name: 'required' },
    { title: 'Default Value', name: 'defaultValue' },
    { title: 'Description', name: 'description' },
    { title: 'Constraints', name: 'constraints', display: joinList},
];
const yaml_columns: Array<WineryTableColumn> = [
    { title: 'Name', name: 'name', sort: true },
    { title: 'Type', name: 'type', sort: true },
    { title: 'Required', name: 'required' },
    { title: 'Default Value', name: 'defaultValue' },
    { title: 'Description', name: 'description' },
    { title: 'Constraints', name: 'constraints', display: joinList },
];

function joinList(list: any[]): string {
    let constraintsString = '';
    for (const value of list) {
        if (value.value == null) {
            constraintsString += value.key + ':' + value.list.toString();
        } else if (value.list == null) {
            constraintsString += value.key + ':' + value.value;
        } else {
            constraintsString += value.key;
        }
        if (list.indexOf(value) !== list.length - 1) {
            constraintsString += ', ';
        }
    }
    return constraintsString;
}

@Component({
    templateUrl: 'propertiesDefinition.component.html',
    styleUrls: [
        'propertiesDefinition.component.css'
    ],
    providers: [
        PropertiesDefinitionService
    ],
})
export class PropertiesDefinitionComponent implements OnInit {

    propertiesEnum = PropertiesDefinitionEnum;
    loading = true;

    resourceApiData: PropertiesDefinitionsResourceApiData;
    selectItems: SelectData[];
    activeElement = new SelectData();
    selectedCell: WineryRowData;
    elementToRemove: any = null;
    columns: Array<WineryTableColumn> = [];
    tableData: Array<PropertiesDefinitionKVElement | YamlPropertyDefinition> = [];
    editedProperty: PropertiesDefinitionKVElement | YamlPropertyDefinition;
    propertyOperation: 'Add' | 'Edit';
    configEnum = FeatureEnum;

    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;
    confirmDeleteModalRef: BsModalRef;

    @ViewChild('editorModal') editorModal: ModalDirective;
    editorModalRef: BsModalRef;

    @ViewChild('nameInputForm') nameInputForm: ElementRef;

    constructor(public sharedData: InstanceService, private service: PropertiesDefinitionService,
                private modalService: BsModalService,
                private notify: WineryNotificationService, private configurationService: WineryRepositoryConfigurationService) {
    }

    // region ########## Angular Callbacks ##########
    /**
     * @override
     */
    ngOnInit() {
        this.getPropertiesDefinitionsResourceApiData();
    }

    copyToTable() {
        this.tableData = [];
        if (this.resourceApiData.propertiesDefinition !== null) {
            // assume we have yaml properties
            this.columns = yaml_columns;
            this.tableData = this.resourceApiData.propertiesDefinition.properties
                .map(prop => {
                    this.fillDefaults(prop);
                    return prop;
                });
        } else if (this.resourceApiData.winerysPropertiesDefinition !== null) {
            this.columns = winery_properties_columns;
            this.tableData = this.resourceApiData.winerysPropertiesDefinition.propertyDefinitionKVList
                .map(prop => {
                    this.fillDefaults(prop);
                    return prop;
                });
        }
    }

    private fillDefaults(propDef: YamlPropertyDefinition | PropertiesDefinitionKVElement) {
        if (!propDef.defaultValue) {
            propDef.defaultValue = '';
        }
        if (!propDef.description) {
            propDef.description = '';
        }
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

        if (!this.resourceApiData.propertiesDefinition) {
            this.resourceApiData.propertiesDefinition = new PropertiesDefinition();
        }

        this.resourceApiData.propertiesDefinition.type = null;
        this.resourceApiData.winerysPropertiesDefinition = null;

        this.service.getXsdElementDefinitions()
            .subscribe(
                data => this.handleSelectData(data, false),
                error => this.handleError(error)
            );
    }

    /**
     * Called by the template, if property XML Type is selected. It sends a GET request
     * to the backend to get the data for the select dropdown.
     */
    onXmlTypeSelected(): void {
        this.resourceApiData.selectedValue = PropertiesDefinitionEnum.Type;

        if (!this.resourceApiData.propertiesDefinition) {
            this.resourceApiData.propertiesDefinition = new PropertiesDefinition();
        }

        this.resourceApiData.propertiesDefinition.element = null;
        this.resourceApiData.winerysPropertiesDefinition = null;

        this.service.getXsdTypeDefinitions()
            .subscribe(
                data => this.handleSelectData(data, true),
                error => this.handleError(error)
            );
    }

    /**
     * Called by the template, if the custom key/value pair property is selected. It will display
     * a table to enter those pairs.
     */
    onCustomKeyValuePairSelected(): void {
        this.resourceApiData.selectedValue = PropertiesDefinitionEnum.Custom;

        if (!this.resourceApiData.propertiesDefinition) {
            this.resourceApiData.propertiesDefinition = new PropertiesDefinition();
        }
        this.resourceApiData.propertiesDefinition.element = null;
        this.resourceApiData.propertiesDefinition.type = null;

        if (!this.resourceApiData.winerysPropertiesDefinition) {
            this.resourceApiData.winerysPropertiesDefinition = new WinerysPropertiesDefinition();
        }
        // The key/value pair list may be null
        if (!this.resourceApiData.winerysPropertiesDefinition.propertyDefinitionKVList) {
            this.resourceApiData.winerysPropertiesDefinition.propertyDefinitionKVList = [];
        }

        if (!this.resourceApiData.winerysPropertiesDefinition.namespace) {
            this.resourceApiData.winerysPropertiesDefinition.namespace = this.sharedData.toscaComponent.namespace + '/propertiesdefinition/winery';
        }
        if (!this.resourceApiData.winerysPropertiesDefinition.elementName) {
            this.resourceApiData.winerysPropertiesDefinition.elementName = 'properties';
        }

        this.activeElement = new SelectData();
        this.activeElement.text = this.resourceApiData.winerysPropertiesDefinition.namespace;
    }

    onYamlProperties(): void {
        this.resourceApiData.selectedValue = PropertiesDefinitionEnum.Yaml;

        // null away all the data access points that are not yaml
        this.resourceApiData.winerysPropertiesDefinition = new WinerysPropertiesDefinition();
        this.resourceApiData.winerysPropertiesDefinition.propertyDefinitionKVList = null;
        this.resourceApiData.propertiesDefinition.element = null;
        this.resourceApiData.propertiesDefinition.type = null;
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
    onRemoveClick(data: PropertiesDefinitionKVElement) {
        if (data) {
            this.elementToRemove = data;
            this.confirmDeleteModalRef = this.modalService.show(this.confirmDeleteModal);
        }
    }

    /**
     * handler for clicks on the add button
     */
    onAddClick() {
        this.editedProperty = this.configurationService.isYaml() ? new YamlPropertyDefinition() : new PropertiesDefinitionKVElement();
        this.propertyOperation = 'Add';
        this.editorModalRef = this.modalService.show(this.editorModal);
    }

    onEditClick(data: YamlPropertyDefinition | PropertiesDefinitionKVElement) {
        this.propertyOperation = 'Edit';
        this.editedProperty = data;
        this.editorModalRef = this.modalService.show(this.editorModal);
    }

    handleEditorSubmit() {

    }

    // endregion

    /**
     * Called by the template, if a property is selected in the select box. Cannot be replaced
     * by ngModel in the template because the same select is used for element and type definitions.
     */
    xmlValueSelected(event: SelectData): void {
        if (this.resourceApiData.selectedValue === PropertiesDefinitionEnum.Element) {
            this.resourceApiData.propertiesDefinition.element = event.id;
        } else if (this.resourceApiData.selectedValue === PropertiesDefinitionEnum.Type) {
            this.resourceApiData.propertiesDefinition.type = event.id;
        }
    }

    onCellSelected(data: WineryRowData) {
        this.selectedCell = data;
    }

    // endregion

    // region ########## Modal Callbacks ##########
    /**
     * Adds a property to the table and model
     * @param propType
     * @param propName
     * @param required
     * @param defaultValue
     * @param description
     */
    addProperty(propType: string, propName: string, required: boolean, defaultValue: string, description: string) {
        this.resourceApiData.winerysPropertiesDefinition.propertyDefinitionKVList.push({
            key: propName,
            type: propType,
            defaultValue: defaultValue,
            required: required,
            description: description,
            constraints: this.editedProperty.constraints.slice(),
        });
        this.editorModalRef.hide();
        this.copyToTable();
        this.save();
    }

    removeConfirmed() {
        this.confirmDeleteModalRef.hide();
        this.deleteItemFromPropertyDefinitionKvList(this.elementToRemove);
        this.elementToRemove = null;
        this.copyToTable();
        this.save();
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

    private handleSelectData(data: SelectData[], isType: boolean) {
        this.selectItems = data;

        this.selectItems.some(nsList => {
            this.activeElement = nsList.children.find(item => {
                if (isType) {
                    return item.id === this.resourceApiData.propertiesDefinition.type;
                }
                return item.id === this.resourceApiData.propertiesDefinition.element;
            });
            return !!this.activeElement;
        });

        if (!this.activeElement) {
            this.activeElement = new SelectData();
        }
    }

    /**
     * Set loading to false and show success notification.
     *
     * @param data
     * @param actionType
     */
    private handleSuccess(data: any, actionType?: string): void {
        this.loading = false;
        this.copyToTable();
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
        switch (!this.resourceApiData.selectedValue ? '' : this.resourceApiData.selectedValue.toString()) {
            case PropertiesDefinitionEnum.Element:
                this.onXmlElementSelected();
                break;
            case PropertiesDefinitionEnum.Type:
                this.onXmlTypeSelected();
                break;
            case PropertiesDefinitionEnum.Custom:
                this.onCustomKeyValuePairSelected();
                break;
            case PropertiesDefinitionEnum.Yaml:
                this.onYamlProperties();
                break;
            default:
                this.resourceApiData.selectedValue = PropertiesDefinitionEnum.None;
        }

        this.handleSuccess(data);
    }

    private handleSave(data: HttpResponse<string>) {
        this.handleSuccess(data, 'change');
        this.getPropertiesDefinitionsResourceApiData();
    }

    /**
     * Deletes a property from the table and model.
     * @param itemToDelete
     */
    private deleteItemFromPropertyDefinitionKvList(itemToDelete: any): void {
        const list = this.resourceApiData.winerysPropertiesDefinition.propertyDefinitionKVList || [];
        for (let i = 0; i < list.length; i++) {
            if (list[i].key === itemToDelete.key) {
                list.splice(i, 1);
            }
        }

        const yamlList = this.resourceApiData.propertiesDefinition.properties || [];
        for (let i = 0; i < yamlList.length; i++) {
            if (yamlList[i].name === itemToDelete.key) {
                yamlList.splice(i, 1);
            }
        }
    }

    /**
     * Sets loading to false and shows error notification.
     *
     * @param error
     */
    private handleError(error: HttpErrorResponse): void {
        this.loading = false;
        this.notify.error(error.message, 'Error');
    }
    // endregion
}
