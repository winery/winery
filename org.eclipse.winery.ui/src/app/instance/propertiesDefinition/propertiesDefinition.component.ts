/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter, Niko Stadelmaier- initial API and implementation
 */

import { Component, OnInit, NgZone } from '@angular/core';
import { InstanceService } from '../instance.service';
import { PropertiesDefinitionService } from './propertiesDefinition.service';
import { PropertiesDefinitionsResourceApiData } from './propertiesDefinitionsResourceApiData';
import { SelectData } from '../../interfaces/selectData';
import { isNullOrUndefined } from 'util';
import isEmpty = hbs.Utils.isEmpty;
import { XsdDefinitionsApiData } from './XsdDefinitionsApiData';

const EMPTY = 'Empty';

@Component({
    selector: 'winery-instance-propertyDefinition',
    templateUrl: 'propertiesDefinition.component.html',
    providers: [
        PropertiesDefinitionService
    ]
})
export class PropertyDefinitionComponent implements OnInit {

    xsdElement = EMPTY;
    xsdType = EMPTY;
    loading: boolean = true;
    showSelect: boolean = false;

    resourceApiData: PropertiesDefinitionsResourceApiData;
    selectedItems: SelectData[];
    activeElement: string;

    constructor(private sharedData: InstanceService,
                private service: PropertiesDefinitionService,
                private zone : NgZone
    ) {}

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
    // region ########## Event Handler ##########
    /**
     * Called by the template, if property (none) is selected. It sends a DELETE request
     * to the backend to delete all properties definitions.
     */
    onNoneSelected(): void {
        this.emptyDescriptionString();
        this.showSelect = false;
        this.loading = true;
        this.service.deletePropertiesDefinitions()
            .subscribe(
                data => this.handleDelete(data),
                error => this.handleError(error)
            );
    }

    /**
     * Called by the template, if property XML Element is selected. It sends a GET request
     * to the backend to get the data for the select dropdown.
     */
    onXmlElementSelected(): void {
        this.service.getXsdElementDefinitions()
            .subscribe(
                data => this.handleXmlTypeDefinitions(data),
                error => this.handleError(error)
            );
        // Force angular to re-render the dom in order to clean the select.
        this.showSelect = false;
        this.zone.run(() => console.log('rendering'));
    }

    /**
     * Called by the template, if property XML Type is selected. It sends a GET request
     * to the backend to get the data for the select dropdown.
     */
    onXmlTypeSelected(): void {
        this.service.getXsdTypeDefinitions()
            .subscribe(
                data => this.handleXmlElementDefinitions(data),
                error => this.handleError(error)
            );
        // Force angular to re-render the dom in order to clean the select.
        this.showSelect = false;
        this.zone.run(() => console.log('rendering'));
    }

    /**
     * Called by the template, if the custom key/value pair property is selected. It will display
     * a table to enter those pairs.
     */
    onCustomKeyValuePairSelected(): void {
        this.emptyDescriptionString();
        this.showSelect = false;
        // show table...
    }

    /**
     * Called by the template, if in the select box is a property selected.
     */
    xmlValueSelected(event: any): void {
        if (!this.xsdElement.includes(EMPTY)) {
            this.xsdElement = event.text;
            this.resourceApiData.propertiesDefinition.element = event.text;
        }
        if (!this.xsdType.includes(EMPTY)) {
            this.xsdType = event.text;
            this.resourceApiData.propertiesDefinition.type = event.text;
        }
    }
    // endregion

    // region ########## Get selected value methods ##########
    /**
     * Called by the template to evaluate, if (none) was initially selected.
     */
    isNoneSelected(): boolean {
        return !this.isCustomSelected() && !this.isXmlElementSelected() && !this.isXmlTypeSelected();
    }

    /**
     * Called by the template to evaluate, if XML Element was initially selected.
     */
    isXmlElementSelected(): boolean {
        return !isNullOrUndefined(this.resourceApiData.propertiesDefinition)
            && !isNullOrUndefined(this.resourceApiData.propertiesDefinition.element);
    }

    /**
     * Called by the template to evaluate, if XML Type was initially selected.
     */
    isXmlTypeSelected(): boolean {
        return !isNullOrUndefined(this.resourceApiData.propertiesDefinition)
            && !isNullOrUndefined(this.resourceApiData.propertiesDefinition.type);
    }

    /**
     * Called by the template to evaluate, if a custom key/value pair was initially selected.
     */
    isCustomSelected(): boolean {
        return isNullOrUndefined(this.resourceApiData.propertiesDefinition)
            && !isNullOrUndefined(this.resourceApiData.winerysPropertiesDefinition);
    }
    // endregion
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

        if (!isNullOrUndefined(this.resourceApiData.propertiesDefinition)) {
            this.xsdElement = isNullOrUndefined(this.resourceApiData.propertiesDefinition.element) ? EMPTY : this.resourceApiData.propertiesDefinition.element;
            this.xsdType = isNullOrUndefined(this.resourceApiData.propertiesDefinition.type) ? EMPTY : this.resourceApiData.propertiesDefinition.type;
        }

        this.handleSuccess(data);
    };

    private handleXmlElementDefinitions(xsdDefinitionsApiData: XsdDefinitionsApiData): void {
        this.selectedItems = xsdDefinitionsApiData.xsdDefinitions;

        if (this.xsdElement.includes(EMPTY) || this.xsdElement.length === 0) {
            this.emptyDescriptionString('element');
        }

        this.showSelect = true;
    }

    private handleXmlTypeDefinitions(xsdDefinitionsApiData: XsdDefinitionsApiData): void {
        this.selectedItems = xsdDefinitionsApiData.xsdDefinitions;

        if (this.xsdElement.includes(EMPTY) || this.xsdType.length === 0) {
            this.emptyDescriptionString('type');
        }

        this.showSelect = true;
    }

    /**
     * Sets loading to false and sets error notification.
     *
     * @param error
     */
    private handleError(error: any): void {
        console.log(error);
    }

    /**
     * Adds behaviour for showing "Empty" on property change.
     *
     * @param type
     */
    private emptyDescriptionString(type = '') {
        if (type.toLowerCase().includes('type')) {
            this.xsdType = '';
            this.xsdElement = EMPTY;
        } else if (type.toLowerCase().includes('element')) {
            this.xsdType = EMPTY;
            this.xsdElement = '';
        } else {
            this.xsdElement = EMPTY;
            this.xsdType = EMPTY;
        }
    }

    // endregion
}
