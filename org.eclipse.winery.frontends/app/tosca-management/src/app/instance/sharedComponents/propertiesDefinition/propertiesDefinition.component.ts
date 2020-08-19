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
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { WineryRepositoryConfigurationService } from '../../../wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { FeatureEnum } from '../../../wineryFeatureToggleModule/wineryRepository.feature.direct';
import { WineryDynamicTableMetadata } from '../../../wineryDynamicTable/wineryDynamicTableMetadata';
import { DynamicTextData } from '../../../wineryDynamicTable/formComponents/dynamicText.component';
import { Validators } from '@angular/forms';
import { DynamicDropdownData } from '../../../wineryDynamicTable/formComponents/dynamicDropdown.component';
import { DynamicConstraintsData } from '../../../wineryDynamicTable/formComponents/dynamicConstraints/dynamicConstraints.component';
import { XmlTypes, YamlTypes } from '../../../model/parameters';

// possible constraints according to TOSCA v1.2
const valid_constraint_keys = ['equal', 'greater_than', 'greater_or_equal', 'less_than', 'less_or_equal', 'in_range',
    'valid_values', 'length', 'min_length', 'max_length', 'pattern', 'schema'];
// we differentiate between constraint keys to validate input
const list_constraint_keys = ['valid_values', 'in_range'];
const range_constraint_keys = ['in_range'];

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

    dynamicTableData: Array<WineryDynamicTableMetadata> = [];
    tableTitle = 'Properties';
    modalTitle = 'Add a Property Definition';
    tableData: PropertiesDefinitionKVElement[] = [];

    resourceApiData: PropertiesDefinitionsResourceApiData;
    selectItems: SelectData[];
    activeElement = new SelectData();
    configEnum = FeatureEnum;

    @ViewChild('nameInputForm') nameInputForm: ElementRef;

    constructor(public sharedData: InstanceService, private service: PropertiesDefinitionService,
                private notify: WineryNotificationService, private configurationService: WineryRepositoryConfigurationService) {
    }

    // region ########## Angular Callbacks ##########
    ngOnInit() {
        this.getPropertiesDefinitionsResourceApiData();

        this.dynamicTableData = [
            new DynamicTextData(
                'key',
                'Name',
                0,
                Validators.required
            ),
            new DynamicTextData(
                'defaultValue',
                'Default Value',
                2
            ),
            new DynamicTextData(
                'description',
                'Description',
                3
            ),
            new DynamicConstraintsData(
                'constraints',
                'Constraints',
                valid_constraint_keys,
                list_constraint_keys,
                range_constraint_keys,
                4,
            )
        ];
        if (this.configurationService.configuration.features.yaml) {
            const options = [
                { label: 'string', value: 'string' },
                { label: 'integer', value: 'integer' },
                { label: 'float', value: 'float' },
                { label: 'boolean', value: 'boolean' },
                { label: 'timestamp', value: 'timestamp' }
            ];
            this.dynamicTableData.push(new DynamicDropdownData<YamlTypes>('type', 'Type', options, 1));
        } else {
            const options = [
                { label: 'xsd:string', value: 'xsd:string' },
                { label: 'xsd:float', value: 'xsd:float' },
                { label: 'xsd:decimal', value: 'xsd:decimal' },
                { label: 'xsd:anyURI', value: 'xsd:anyURI' },
                { label: 'xsd:QName', value: 'xsd:QName' }
            ];
            this.dynamicTableData.push(new DynamicDropdownData<XmlTypes>('type', 'Type', options, 1));
        }
    }

    copyToTable() {
        this.tableData = [];
        if (this.resourceApiData.winerysPropertiesDefinition && this.resourceApiData.winerysPropertiesDefinition.propertyDefinitionKVList) {
            this.tableData = this.resourceApiData.winerysPropertiesDefinition.propertyDefinitionKVList;
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

        if (this.resourceApiData.propertiesDefinition) {
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

    // endregion

    // region ########## Save Callbacks ##########
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

    // endregion

    // region ########## Table Callbacks ##########
    onChangeProperty() {
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
            default:
                if (this.configurationService.configuration.features.yaml) {
                    this.onCustomKeyValuePairSelected();
                } else {
                    this.resourceApiData.selectedValue = PropertiesDefinitionEnum.None;
                }
        }

        this.handleSuccess(data);
    }

    private handleSave(data: HttpResponse<string>) {
        this.handleSuccess(data, 'change');
        this.getPropertiesDefinitionsResourceApiData();
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
