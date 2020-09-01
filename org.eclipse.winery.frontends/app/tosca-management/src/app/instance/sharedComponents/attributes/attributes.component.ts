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
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { AttributesService } from './attributes.service';
import { InstanceService } from '../../instance.service';
import { AttributeDefinition } from '../../../model/attribute';
import { HttpErrorResponse } from '@angular/common/http';
import { WineryDynamicTableMetadata } from '../../../wineryDynamicTable/wineryDynamicTableMetadata';
import { DynamicTextData } from '../../../wineryDynamicTable/formComponents/dynamicText.component';
import { Validators } from '@angular/forms';
import { DynamicDropdownData } from '../../../wineryDynamicTable/formComponents/dynamicDropdown.component';

@Component({
    selector: 'winery-attributes',
    templateUrl: 'attributes.component.html',
})
export class AttributesComponent implements OnInit {

    attributes: AttributeDefinition[] = [];

    dynamicTableData: WineryDynamicTableMetadata[] = [
        new DynamicTextData(
            'key',
            'Name',
            0,
            Validators.required,
            undefined,
            false,
            true),
        new DynamicDropdownData<'string' | 'integer' | 'float' | 'boolean' | 'timestamp'>(
            'type',
            'Type',
            [{ label: 'string', value: 'string' },
                { label: 'integer', value: 'integer' },
                { label: 'float', value: 'float' },
                { label: 'boolean', value: 'boolean' },
                { label: 'timestamp', value: 'timestamp' }],
            1,
            'string'),
        new DynamicTextData(
            'defaultValue',
            'Default Value',
            2),
        new DynamicTextData(
            'description',
            'Description',
            3)
    ];

    loading = false;
    tableTitle = 'Attributes';
    modalTitle = 'Add/Change Attribute';

    constructor(private attributeService: AttributesService, public instanceService: InstanceService) {
    }

    ngOnInit() {
        this.loading = true;
        this.attributeService.getAttributes()
            .subscribe(
                data => {
                    this.attributes = [];
                    data.forEach(item => this.attributes.push(Object.assign(new AttributeDefinition(), item)));
                    this.loading = false;
                },
                error => this.handleError(error)
            );
    }

    private handleError(error: HttpErrorResponse) {
        console.error(error);
        this.loading = false;
    }

    save() {
        this.loading = true;
        this.attributeService.updateAttributes(this.attributes)
            .subscribe(
                () => this.loading = false,
                error => this.handleError(error)
            );
    }
}
