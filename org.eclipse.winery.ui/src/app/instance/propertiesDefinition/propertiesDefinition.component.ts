/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter, Niko Stadelmaier- initial API and implementation
 */
import { Component, OnInit, NgZone } from '@angular/core';
import { InstanceService } from '../instance.service';
import { PropertiesDefinitionService } from './propertiesDefinition.service';
import {
    PropertiesDefinition,
    PropertiesDefinitionEnum,
    PropertiesDefinitionsResourceApiData,
    WinerysPropertiesDefinition,
} from './propertiesDefinitionsResourceApiData';
import { SelectData } from '../../interfaces/selectData';
import { isNullOrUndefined } from 'util';
import { Response } from '@angular/http';

@Component({
    selector: 'winery-instance-propertyDefinition',
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
    loading: boolean = true;
    showSelect: boolean = false;
    showCustomKeyValue: boolean = false;

    resourceApiData: PropertiesDefinitionsResourceApiData;
    selectItems: SelectData[];
    activeElement: SelectData;
    allNamespaces: string[];

    columns: Array<any> = [
        { title: 'Name', name: 'key', sort: true },
        { title: 'Type', name: 'type', sort: true },
    ];

    constructor(private sharedData: InstanceService,
                private service: PropertiesDefinitionService,
                private zone: NgZone) {

    }

    onCellSelected(data: any) {
        console.log(data);
    }

    // region ########## Angular Callbacks ##########
    /**
     * @override
     */
    ngOnInit() {
        this.service.setPath(this.sharedData.path);
        this.getPropertiesDefinitionsResourceApiData();
    }

    // endregion

    // region ########## Template Callbacks ##########
    /**
     * Called by the template, if property (none) is selected. It sends a DELETE request
     * to the backend to delete all properties definitions.
     */
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
                data => this.selectItems = data.xsdDefinitions,
                error => this.handleError(error)
            );

        if (isNullOrUndefined(this.resourceApiData.propertiesDefinition)) {
            this.resourceApiData.propertiesDefinition = new PropertiesDefinition();
        }

        this.resourceApiData.propertiesDefinition.type = null;
        this.resourceApiData.winerysPropertiesDefinition = null;

        this.activeElement = new SelectData();
        this.activeElement.text = this.resourceApiData.propertiesDefinition.element;

//        this.forceSelectClear();
    }

    /**
     * Called by the template, if property XML Type is selected. It sends a GET request
     * to the backend to get the data for the select dropdown.
     */
    onXmlTypeSelected(): void {
        this.resourceApiData.selectedValue = PropertiesDefinitionEnum.Type;
        this.service.getXsdTypeDefinitions()
            .subscribe(
                data => this.selectItems = data.xsdDefinitions,
                error => this.handleError(error)
            );

        if (isNullOrUndefined(this.resourceApiData.propertiesDefinition)) {
            this.resourceApiData.propertiesDefinition = new PropertiesDefinition();
        }

        this.resourceApiData.propertiesDefinition.element = null;
        this.resourceApiData.winerysPropertiesDefinition = null;

        this.activeElement = new SelectData();
        this.activeElement.text = this.resourceApiData.propertiesDefinition.type;

//        this.forceSelectClear();
    }

    /**
     * Called by the template, if the custom key/value pair property is selected. It will display
     * a table to enter those pairs.
     */
    onCustomKeyValuePairSelected(): void {
        this.resourceApiData.selectedValue = PropertiesDefinitionEnum.Custom;
        this.service.getAllNamespaces()
            .subscribe(
                data => this.allNamespaces = data,
                error => this.handleError(error)
            );

        if (isNullOrUndefined(this.resourceApiData.winerysPropertiesDefinition)) {
            this.resourceApiData.winerysPropertiesDefinition = new WinerysPropertiesDefinition();
        }

        this.activeElement = new SelectData();
        this.activeElement.text = this.resourceApiData.winerysPropertiesDefinition.namespace;
    }

    /**
     * Called by the template, if a property is selected in the select box .
     */
    xmlValueSelected(event: SelectData): void {
        if (this.resourceApiData.selectedValue === PropertiesDefinitionEnum.Element) {
            this.resourceApiData.propertiesDefinition.element = event.text;
        } else if (this.resourceApiData.selectedValue === PropertiesDefinitionEnum.Type) {
            this.resourceApiData.propertiesDefinition.type = event.text;
        }
    }

    setWrapperName(event: any): void {
        this.resourceApiData.winerysPropertiesDefinition.elementName = event.target.value;
    }

    wrapperNamespaceSelected(event: any): void {
        this.resourceApiData.winerysPropertiesDefinition.namespace = event.text;
    }

    save(): void {
        this.loading = true;
        if (this.resourceApiData.selectedValue === PropertiesDefinitionEnum.None) {
            this.service.deletePropertiesDefinitions()
                .subscribe(
                    data => this.handleDelete(data),
                    error => this.handleError(error)
                );
        } else {
            this.service.postProperteisDefinitions(this.resourceApiData)
                .subscribe(
                    data => this.handleSave(data),
                    error => this.handleError(error)
                );
        }
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
     */
    private handleSuccess(data: any): void {
        this.loading = false;
    }

    /**
     * Reloads the new data from the backend (only called on success).
     *
     * @param data
     */
    private handleDelete(data: any): void {
        this.handleSuccess(data);
        this.getPropertiesDefinitionsResourceApiData();
    }

    private handlePropertiesDefinitionData(data: PropertiesDefinitionsResourceApiData): void {
        this.resourceApiData = data;
        console.log('resourceApiDAta', this.resourceApiData);
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
        this.handleSuccess(data);
        this.getPropertiesDefinitionsResourceApiData();
    }

    /**
     * Sets loading to false and shows error notification.
     *
     * @param error
     */
    private handleError(error: any): void {
        console.log(error);
    }
    // endregion
}
