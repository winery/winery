/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter, Niko Stadelmaier- initial API and implementation
 *******************************************************************************/

import { Component, OnInit } from '@angular/core';
import { InstanceService } from '../instance.service';
import { PropertiesDefinitionService } from './propertiesDefinition.service';
import { PropertiesDefinitonsResourceApiData } from './propertiesDefinitionsResourceApiData';
import { SelectData } from '../../interfaces/selectData';
import { isNullOrUndefined } from 'util';

@Component({
    selector: 'winery-instance-propertyDefinition',
    templateUrl: 'propertiesDefinition.component.html',
    providers: [
        PropertiesDefinitionService
    ]
})
export class PropertyDefinitionComponent implements OnInit {

    xsdElement: string;
    xsdType: string;
    loading: boolean = true;
    showSelect: boolean = false;

    resourceApiData: PropertiesDefinitonsResourceApiData;
    selectedItems: SelectData[];

    constructor(private sharedData: InstanceService,
                private propertiesService: PropertiesDefinitionService) {
    }

    ngOnInit() {
        this.propertiesService.setPath(this.sharedData.path);
        this.propertiesService.getPropertiesDefinitionsData()
            .subscribe(
                data => this.handlePropertiesDefinitionData(data),
                error => this.handleError(error)
            );
    }

    onNoneSelected(): void {
        this.showSelect = false;
    }

    onXmlElementSelected(): void {
        this.showSelect = false;
        this.selectedItems = this.resourceApiData.xsdElementDefinitions;
        this.showSelect = true;
    }

    onXmlTypeSelected(): void {
        this.showSelect = false;
        this.selectedItems = this.resourceApiData.xsdTypeDefinitions;
        this.showSelect = true;
    }

    onCustomKeyValuePairSelected(): void {
        this.showSelect = false;
       // show table...
    }

    isNoneSelected(): boolean {
        return !this.isCustomSelected() && !this.isXmlElementSelected() && !this.isXmlTypeSelected();
    }

    isXmlElementSelected(): boolean {
        return !isNullOrUndefined(this.resourceApiData.propertiesDefinition)
            && !isNullOrUndefined(this.resourceApiData.propertiesDefinition.element);
    }

    isXmlTypeSelected(): boolean {
        return !isNullOrUndefined(this.resourceApiData.propertiesDefinition)
            && !isNullOrUndefined(this.resourceApiData.propertiesDefinition.type);
    }

    isCustomSelected(): boolean {
        return isNullOrUndefined(this.resourceApiData.propertiesDefinition)
            && !isNullOrUndefined(this.resourceApiData.winerysPropertiesDefinition);
    }

    private handlePropertiesDefinitionData(data: PropertiesDefinitonsResourceApiData) {
        this.resourceApiData = data;
        this.loading = false;
    };

    private handleError(error: any): void  {
        console.log(error);
    }
}
