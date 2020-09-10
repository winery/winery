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
import { RefinementMappingsService } from '../refinementMappings.service';
import { Component, OnInit } from '@angular/core';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { HttpErrorResponse } from '@angular/common/http';
import { forkJoin } from 'rxjs';
import { NodeTemplate } from '../../../model/wineryComponent';
import { SelectData } from '../../../model/selectData';
import { DeploymentArtifactMapping } from './deploymentArtifactMapping';
import { WineryDynamicTableMetadata } from '../../../wineryDynamicTable/wineryDynamicTableMetadata';
import { DynamicDropdownData } from '../../../wineryDynamicTable/formComponents/dynamicDropdown.component';
import { Validators } from '@angular/forms';
import { DynamicTextData } from '../../../wineryDynamicTable/formComponents/dynamicText.component';

@Component({
    templateUrl: 'deploymentArtifactsMappings.component.html',
    providers: [
        RefinementMappingsService
    ]
})
export class DeploymentArtifactsMappingsComponent implements OnInit {

    loading = true;

    detectorNodes: NodeTemplate[];
    refinementNodes: NodeTemplate[];
    artifactTypes: SelectData[];

    dynamicTableData: Array<WineryDynamicTableMetadata> = [];
    deploymentArtifactMappings: DeploymentArtifactMapping[] = [];

    tableTitle = 'Deployment Artifact Mappings';
    modalTitle = 'Add Deployment Artifact Mapping';

    detectorElementsTableData: { label: string, value: string }[] = [];
    refinementElementsTableData: { label: string, value: string }[] = [];
    artifactTypesTableData: { label: string, value: string }[] = [];

    mapping: DeploymentArtifactMapping;

    constructor(private service: RefinementMappingsService,
                private notify: WineryNotificationService) {
    }

    ngOnInit(): void {
        forkJoin(
            this.service.getDeploymentArtifactMappings(),
            this.service.getDetectorNodeTemplates(),
            this.service.getRefinementTopologyNodeTemplates(),
            this.service.getArtifactTypes(),
        ).subscribe(
            data => this.handleData(data),
            error => this.handleError(error)
        );

        this.dynamicTableData = [
            new DynamicDropdownData<string>(
                'artifactType',
                'Required DA Type',
                this.artifactTypesTableData,
                1,
                '',
                [Validators.required],
            ),
            new DynamicDropdownData<string>(
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
    }

    private handleData(data: [DeploymentArtifactMapping[], NodeTemplate[], NodeTemplate[], SelectData[]]) {
        this.loading = false;
        this.deploymentArtifactMappings = data[0];
        this.detectorNodes = data[1];
        this.refinementNodes = data[2];
        this.artifactTypes = data[3];

        this.detectorNodes.forEach((element) => {
                this.detectorElementsTableData.push({ label: element.name, value: element.id }
                );
            }
        );
        this.refinementNodes.forEach((element) => {
                this.refinementElementsTableData.push({ label: element.name, value: element.id }
                );
            }
        );
        this.artifactTypes.forEach((element) => {
            element.children.forEach((child) => {
                this.artifactTypesTableData.push({ label: child.text, value: child.id }
                );
            });
        });
    }

    save(mapping: DeploymentArtifactMapping) {
        this.loading = true;
        const id = this.service.getNewMappingsId(this.deploymentArtifactMappings, DeploymentArtifactMapping.idPrefix);
        const newMapping = new DeploymentArtifactMapping(id);
        newMapping.detectorElement = mapping.detectorElement;
        newMapping.refinementElement = mapping.refinementElement;
        newMapping.artifactType = mapping.artifactType;

        this.service.addDeploymentArtifactMappings(newMapping)
            .subscribe(
                data => this.handleSave('Added', data),
                error => this.handleError(error)
            );
    }

    remove(mapping: DeploymentArtifactMapping) {
        this.loading = true;
        this.service.deleteDeploymentArtifactMappings(mapping)
            .subscribe(
                data => this.handleSave('Removed', data),
                error => this.handleError(error)
            );
    }

    private handleSave(type: string, data: DeploymentArtifactMapping[]) {
        this.notify.success(type + ' Deployment Artifact Mapping ');
        this.deploymentArtifactMappings = data;
        this.loading = false;
    }

    private handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.notify.error(error.message);
    }
}
