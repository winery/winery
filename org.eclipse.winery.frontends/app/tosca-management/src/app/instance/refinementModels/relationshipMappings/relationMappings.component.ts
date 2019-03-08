/********************************************************************************
 * Copyright (c) 2018-2019 Contributors to the Eclipse Foundation
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
import { Component, OnInit, ViewChild } from '@angular/core';
import { RefinementMappingsService } from '../refinementMappings.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { RelationDirection, RelationMapping } from './relationMapping';
import { HttpErrorResponse } from '@angular/common/http';
import { forkJoin } from 'rxjs';
import { WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';
import { NodeTemplate } from '../../../model/wineryComponent';
import { SelectData } from '../../../model/selectData';
import { BsModalRef, BsModalService, ModalDirective } from 'ngx-bootstrap';
import { InstanceService } from '../../instance.service';
import { ToscaTypes } from '../../../model/enums';

@Component({
    templateUrl: 'relationMappings.component.html',
    providers: [
        RefinementMappingsService
    ]
})
export class RelationMappingsComponent implements OnInit {

    readonly relDirections = RelationDirection;
    readonly toscaType = ToscaTypes;

    loading = true;
    columns: Array<WineryTableColumn> = [
        { title: 'Id', name: 'id', sort: true },
        { title: 'Direction', name: 'direction', sort: true },
        { title: 'Detector Node', name: 'detectorNode', sort: true },
        { title: 'Refinement Node', name: 'refinementNode', sort: true },
        { title: 'Relation Type', name: 'relationType', sort: true },
    ];

    relationshipMappings: RelationMapping[];
    detectorNodeTemplates: NodeTemplate[];
    refinementStructureNodeTemplates: NodeTemplate[];
    relationshipTypes: SelectData[];
    nodeTypes: SelectData[];

    mapping: RelationMapping;

    @ViewChild('addModal') addModal: ModalDirective;
    @ViewChild('removeModal') removeModal: ModalDirective;
    addModalRef: BsModalRef;
    removeModalRef: BsModalRef;

    constructor(private service: RefinementMappingsService,
                private notify: WineryNotificationService,
                private sharedData: InstanceService,
                private modalService: BsModalService) {
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

        if (this.sharedData.toscaComponent.toscaType === ToscaTypes.PatternRefinementModel) {
            this.columns.push({ title: 'Valid Endpoint', name: 'validSourceOrTarget', sort: true });
        }
    }

    private handleData(data: any) {
        this.loading = false;
        this.relationshipMappings = data[0];
        this.detectorNodeTemplates = data[1];
        this.refinementStructureNodeTemplates = data[2];
        this.relationshipTypes = data[3];
        this.nodeTypes = data[4];
    }

    private handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.notify.error(error.message);
    }

    onAddButtonClicked() {
        let id = 0;
        this.relationshipMappings.forEach(value => {
            const number = Number(value.id.split(RelationMapping.idPrefix)[1]);
            if (!isNaN(number) && number >= id) {
                id = number;
                if (number === id) {
                    id++;
                }
            }
        });

        this.mapping = new RelationMapping(id);
        this.addModalRef = this.modalService.show(this.addModal);
    }

    onRemoveButtonClicked(selected: RelationMapping) {
        this.mapping = selected;
        this.removeModalRef = this.modalService.show(this.removeModal);
    }

    onAddRelationMapping() {
        this.loading = true;
        this.service.addRelationMapping(this.mapping)
            .subscribe(
                data => this.handleSave('Added', data),
                error => this.handleError(error)
            );
    }

    onRemoveRelationMapping() {
        this.service.deleteRelationMapping(this.mapping)
            .subscribe(
                data => this.handleSave('Removed', data),
                error => this.handleError(error)
            );
    }

    private handleSave(type: string, data: RelationMapping[]) {
        this.notify.success(type + ' Relation Mapping ' + this.mapping.id);
        this.relationshipMappings = data;
        this.loading = false;
    }
}
