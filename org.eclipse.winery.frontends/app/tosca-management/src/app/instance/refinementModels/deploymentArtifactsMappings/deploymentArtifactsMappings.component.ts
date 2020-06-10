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
import { Component, OnInit, ViewChild } from '@angular/core';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { InstanceService } from '../../instance.service';
import { BsModalRef, BsModalService, ModalDirective } from 'ngx-bootstrap';
import { WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';
import { HttpErrorResponse } from '@angular/common/http';
import { forkJoin } from 'rxjs';
import { NodeTemplate } from '../../../model/wineryComponent';
import { SelectData } from '../../../model/selectData';
import { DeploymentArtifactMapping } from './deploymentArtifactMapping';

@Component({
    templateUrl: 'deploymentArtifactsMappings.component.html',
    providers: [
        RefinementMappingsService
    ]
})
export class DeploymentArtifactsMappingsComponent implements OnInit {

    loading = true;
    columns: Array<WineryTableColumn> = [
        { title: 'Deployment Artifact Type', name: 'artifactType', sort: true },
        { title: 'Detector Element', name: 'detectorNode', sort: true },
        { title: 'Refinement Element', name: 'refinementNode', sort: true },
    ];

    deploymentArtifactMappings: DeploymentArtifactMapping[];
    detectorNodes: NodeTemplate[];
    refinementNodes: NodeTemplate[];
    artifactTypes: SelectData[];

    mapping: DeploymentArtifactMapping;

    @ViewChild('addModal') addModal: ModalDirective;
    @ViewChild('removeModal') removeModal: ModalDirective;
    addModalRef: BsModalRef;
    removeModalRef: BsModalRef;

    constructor(private service: RefinementMappingsService,
                private notify: WineryNotificationService,
                public sharedData: InstanceService,
                private modalService: BsModalService) {
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
    }

    onAddButtonClicked() {
        const id = this.service.getNewMappingsId(this.deploymentArtifactMappings, DeploymentArtifactMapping.idPrefix);
        console.log(id);

        this.mapping = new DeploymentArtifactMapping(id);
        this.addModalRef = this.modalService.show(this.addModal);
    }

    onAddRelationMapping() {
        this.loading = true;
        this.service.addDeploymentArtifactMappings(this.mapping)
            .subscribe(
                data => this.handleSave('Added', data),
                error => this.handleError(error)
            );
    }

    detectorNodeSelected(node: SelectData) {
        this.mapping.detectorNode = node.id;
    }

    onRemoveButtonClicked(selected: DeploymentArtifactMapping) {
        this.mapping = selected;
        this.removeModalRef = this.modalService.show(this.removeModal);
    }

    onRemoveRelationMapping() {
        this.service.deleteDeploymentArtifactMappings(this.mapping)
            .subscribe(
                data => this.handleSave('Removed', data),
                error => this.handleError(error)
            );
    }

    // region ********** Private Methods *********
    private handleSave(type: string, data: DeploymentArtifactMapping[]) {
        this.notify.success(type + ' Deployment Artifact Mapping ' + this.mapping.id);
        this.deploymentArtifactMappings = data;
        this.loading = false;
    }

    private handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.notify.error(error.message);
    }

    private handleData(data: [DeploymentArtifactMapping[], NodeTemplate[], NodeTemplate[], SelectData[]]) {
        this.loading = false;
        this.deploymentArtifactMappings = data[0];
        this.detectorNodes = data[1];
        this.refinementNodes = data[2];
        this.artifactTypes = data[3];
    }

    // endregion
}
