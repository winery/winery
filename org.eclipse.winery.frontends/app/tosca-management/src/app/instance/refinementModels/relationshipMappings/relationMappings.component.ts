/********************************************************************************
 * Copyright (c) 2018-2020 Contributors to the Eclipse Foundation
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
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { RelationMapping } from './relationMapping';
import { HttpErrorResponse } from '@angular/common/http';
import { forkJoin } from 'rxjs';
import { NodeTemplate } from '../../../model/wineryComponent';
import { SelectData } from '../../../model/selectData';
import { InstanceService } from '../../instance.service';
import { ToscaTypes } from '../../../model/enums';
import { WineryDynamicTableMetadata } from '../../../wineryDynamicTable/wineryDynamicTableMetadata';
import { DynamicDropdownData } from '../../../wineryDynamicTable/formComponents/dynamicDropdown.component';
import { Validators } from '@angular/forms';
import { DynamicTextData } from '../../../wineryDynamicTable/formComponents/dynamicText.component';

@Component({
    templateUrl: 'relationMappings.component.html',
    providers: [
        RefinementMappingsService
    ]
})
export class RelationMappingsComponent implements OnInit {
    loading = true;

    dynamicTableData: Array<WineryDynamicTableMetadata> = [];
    relationshipMappings: RelationMapping[] = [];

    detectorNodeTemplates: NodeTemplate[];
    refinementStructureNodeTemplates: NodeTemplate[];
    relationshipTypes: SelectData[];
    nodeTypes: SelectData[];

    mapping: RelationMapping;

    tableTitle = 'Relationship Mappings';
    modalTitle = 'Add Relationship Mapping';
    private detectorElementsTableData: { label: string; value: string }[] = [];
    private refinementElementsTableData: { label: string, value: string }[] = [];
    private nodeTypesTableData: { label: string, value: string }[] = [];
    private relationshipTypesTableData: { label: string, value: string }[] = [];
    private directionTableData: { label: string, value: string }[] = [{ label: 'ingoing', value: 'INGOING' }, { label: 'outgoing', value: 'OUTGOING' }];

    constructor(private service: RefinementMappingsService,
                private notify: WineryNotificationService,
                public sharedData: InstanceService) {
    }

    ngOnInit(): void {
        forkJoin(
            this.service.getRelationshipMappings(),
            this.service.getDetectorNodeTemplates(),
            this.service.getRefinementTopologyNodeTemplates(),
            this.service.getRelationshipTypes(),
            this.service.getNodeTypes()
        ).subscribe(
            data => this.handleData(data),
            error => this.handleError(error)
        );

        this.dynamicTableData = [
            new DynamicDropdownData(
                'direction',
                'Relation Direction',
                this.directionTableData,
                1,
                '',
                [Validators.required],
            ),
            new DynamicDropdownData(
                'detectorElement',
                'Detector Node',
                this.detectorElementsTableData,
                1,
                '',
                [Validators.required],
            ),
            new DynamicDropdownData<string>(
                'refinementElement',
                'Refinement Structure Node',
                this.refinementElementsTableData,
                1,
                '',
                [Validators.required],
            ),
            new DynamicDropdownData<string>(
                'relationType',
                'Applicable Relationship Type',
                this.relationshipTypesTableData,
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
        ];

        if (this.sharedData.toscaComponent.toscaType === ToscaTypes.PatternRefinementModel) {
            this.dynamicTableData.push(
                new DynamicDropdownData<string>(
                    'validSourceOrTarget',
                    'Valid Endpoint Type',
                    this.nodeTypesTableData,
                    1,
                    '',
                    [Validators.required],
                )
            );
        }

    }

    private handleData(data: any) {
        this.loading = false;
        this.relationshipMappings = data[0];
        this.detectorNodeTemplates = data[1];
        this.refinementStructureNodeTemplates = data[2];
        this.relationshipTypes = data[3];
        this.nodeTypes = data[4];

        this.detectorNodeTemplates.forEach((element) => {
                this.detectorElementsTableData.push({ label: element.name, value: element.id }
                );
            }, this
        );
        this.refinementStructureNodeTemplates.forEach((element) => {
                this.refinementElementsTableData.push({ label: element.name, value: element.id }
                );
            }, this
        );
        this.relationshipTypes.forEach((element) => {
                element.children.forEach((child) => {
                    this.relationshipTypesTableData.push({ label: child.text, value: child.id }
                    );
                });
            }
        );
        this.nodeTypes.forEach((element) => {
                element.children.forEach((child) => {
                    this.nodeTypesTableData.push({ label: child.text, value: child.id }
                    );
                });
            }
        );
    }

    save(mapping: RelationMapping) {
        this.loading = true;
        const id = this.service.getNewMappingsId(this.relationshipMappings, RelationMapping.idPrefix);
        const newMapping = new RelationMapping(id);
        newMapping.detectorElement = mapping.detectorElement;
        newMapping.refinementElement = mapping.refinementElement;
        newMapping.direction = mapping.direction;
        newMapping.relationType = mapping.relationType;
        newMapping.validSourceOrTarget = mapping.validSourceOrTarget;

        this.service.addRelationMapping(newMapping).subscribe(
            data => this.handleSave('Added', data),
            error => this.handleError(error)
        );

    }

    remove(mapping: RelationMapping) {
        this.loading = true;
        this.service.deleteRelationMapping(mapping)
            .subscribe(
                data => this.handleSave('Removed', data),
                error => this.handleError(error)
            );
    }

    private handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.notify.error(error.message);
    }

    private handleSave(type: string, data: RelationMapping[]) {
        this.notify.success(type + ' Relation Mapping ');
        this.relationshipMappings = data;
        this.loading = false;
    }
}
