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

import { Component, OnInit } from '@angular/core';
import { RefinementMappingsService } from '../refinementMappings.service';
import { PermutationMapping } from './permutationMapping';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { WineryDynamicTableMetadata } from '../../../wineryDynamicTable/wineryDynamicTableMetadata';
import { Validators } from '@angular/forms';
import { DynamicDropdownData } from '../../../wineryDynamicTable/formComponents/dynamicDropdown.component';
import { NodeTemplate, RelationshipTemplate, WineryTemplate } from '../../../model/wineryComponent';
import { forkJoin } from 'rxjs';
import { DynamicTextData } from '../../../wineryDynamicTable/formComponents/dynamicText.component';

@Component({
    templateUrl: 'permutationMappings.component.html',
    providers: [
        RefinementMappingsService
    ]
})
export class PermutationMappingsComponent implements OnInit {
    loading = true;

    dynamicTableData: Array<WineryDynamicTableMetadata> = [];
    permutationMappings: PermutationMapping[] = [];

    tableTitle = 'Permutation Mappings';
    modalTitle = 'Add Permutation Mapping';

    detectorElements: WineryTemplate[];
    refinementElements: NodeTemplate[];

    detectorElementsTableData: { label: string, value: string }[] = [];
    refinementElementsTableData: { label: string, value: string }[] = [];

    constructor(private service: RefinementMappingsService,
                private notify: WineryNotificationService) {
    }

    ngOnInit(): void {
        forkJoin(
            this.service.getPermutationMappings(),
            this.service.getDetectorNodeTemplates(),
            this.service.getDetectorRelationshipTemplates(),
            this.service.getRefinementTopologyNodeTemplates()
        ).subscribe(
            data => this.handleData(data),
            error => this.handleError(error)
        );

        this.dynamicTableData = [
            new DynamicDropdownData<string>(
                'detectorElement',
                'Detector Element',
                this.detectorElementsTableData,
                1,
                '',
                [Validators.required],
            ),
            new DynamicDropdownData<string>(
                'refinementElement',
                'Refinement Element',
                this.refinementElementsTableData,
                1,
                '',
                [Validators.required],
            ),
            new DynamicTextData(
                'id',
                'ID',
                3,
                [],
                '',
                false,
                false,
                false
            ),
        ]
        ;
    }

    save(mapping: PermutationMapping): void {
        this.loading = true;
        const id = this.service.getNewMappingsId(this.permutationMappings, PermutationMapping.idPrefix);
        const newMapping = new PermutationMapping(id);
        newMapping.detectorElement = mapping.detectorElement;
        newMapping.refinementElement = mapping.refinementElement;

        this.service.addPermutationMappings(newMapping)
            .subscribe(
                data => this.handleSave('Added', data),
                error => this.handleError(error)
            );
    }

    remove(mapping: PermutationMapping) {
        this.loading = true;
        this.service.deletePermutationMappings(mapping)
            .subscribe(
                data => this.handleSave('Removed', data),
                error => this.handleError(error)
            );
    }

    private handleData(data: [PermutationMapping[], NodeTemplate[], RelationshipTemplate[], NodeTemplate[]]) {
        this.permutationMappings = data[0];

        this.detectorElements = data[1];
        this.detectorElements.concat(data[2]);

        this.refinementElements = data[3];

        this.detectorElements.forEach((element) => {
                this.detectorElementsTableData.push({ label: element.name, value: element.id }
                );
            }
        );
        this.refinementElements.forEach((element) => {
                this.refinementElementsTableData.push({ label: element.name, value: element.id }
                );
            }
        );
        this.loading = false;
    }

    private handleSave(added: string, data: PermutationMapping[]) {
        this.notify.success(added + ' Permutation Mapping ');
        this.permutationMappings = data;
        this.loading = false;
    }

    private handleError(error: any) {
        this.loading = false;
        this.notify.error(error.message);
    }
}
