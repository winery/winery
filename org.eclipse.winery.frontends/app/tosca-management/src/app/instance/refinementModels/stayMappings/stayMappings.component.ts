/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { BsModalRef, BsModalService, ModalDirective } from 'ngx-bootstrap';
import { WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';
import { WineryTemplate } from '../../../model/wineryComponent';
import { Component, OnInit, ViewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { SelectData } from '../../../model/selectData';
import { InstanceService } from '../../instance.service';
import { PrmModelElementType, StayMapping } from './stayMapping';
import { forkJoin } from 'rxjs';

@Component({
    templateUrl: 'stayMappings.component.html',
    providers: [
        RefinementMappingsService
    ]
})
export class StayMappingsComponent implements OnInit {

    readonly modelElementTypes = PrmModelElementType;

    loading = true;
    loadingElements = false;
    columns: Array<WineryTableColumn> = [
        { title: 'Id', name: 'id', sort: true },
        { title: 'Element Type', name: 'modelElementType', sort: true },
        { title: 'Detector Element', name: 'detectorNode', sort: true },
        { title: 'Refinement Element', name: 'refinementNode', sort: true },
    ];

    stayMappings: StayMapping[];
    detectorTemplates: WineryTemplate[];
    refinementStructureTemplates: WineryTemplate[];

    @ViewChild('addModal') addModal: ModalDirective;
    @ViewChild('removeModal') removeModal: ModalDirective;
    addModalRef: BsModalRef;
    removeModalRef: BsModalRef;

    mapping: StayMapping;

    constructor(private service: RefinementMappingsService,
                private notify: WineryNotificationService,
                private sharedData: InstanceService,
                private modalService: BsModalService) {
    }

    ngOnInit(): void {
        this.service.getStayMappings()
            .subscribe(
                data => {
                    this.loading = false;
                    this.stayMappings = data;
                },
                error => this.handleError(error)
            );
    }

    // region ********** Table Callbacks **********
    onAddButtonClicked() {
        let id = 0;
        this.stayMappings.forEach(value => {
            const number = Number(value.id.split(StayMapping.idPrefix)[1]);
            if (!isNaN(number) && number >= id) {
                id = number;
                if (number === id) {
                    id++;
                }
            }
        });

        this.mapping = new StayMapping(id);
        this.addModalRef = this.modalService.show(this.addModal);
    }

    onRemoveButtonClicked(selected: StayMapping) {
        this.mapping = selected;
        this.removeModalRef = this.modalService.show(this.removeModal);
    }

    // endregion

    // region ********** Add Modal Callbacks **********
    onAddPrmPropertyMapping() {
        this.loading = true;
        this.service.addStayMapping(this.mapping)
            .subscribe(
                data => this.handleSave('Added', data),
                error => this.handleError(error)
            );
    }

    modelElementTypeSelected(type: PrmModelElementType) {
        this.mapping.modelElementType = type;
        if (type === PrmModelElementType.NODE) {
            forkJoin([
                this.service.getDetectorNodeTemplates(),
                this.service.getRefinementTopologyNodeTemplates()
            ]).subscribe(
                data => this.handleData(data),
                error => this.handleError(error)
            );
        } else { // Relation
            forkJoin([
                this.service.getDetectorRelationshipTemplates(),
                this.service.getRefinementTopologyRelationshipTemplates()
            ]).subscribe(
                data => this.handleData(data),
                error => this.handleError(error)
            );
        }
    }

    detectorNodeSelected(node: SelectData) {
        this.mapping.detectorNode = node.id;
    }

    refinementNodeSelected(node: SelectData) {
        this.mapping.refinementNode = node.id;
    }

    // endregion

    // region ********** Remove Modal Callback **********
    onRemovePrmPropertyMapping() {
        this.service.deleteStayMapping(this.mapping)
            .subscribe(
                data => this.handleSave('Removed', data),
                error => this.handleError(error)
            );
    }

    // endregion

    // region ********** Private Methods *********
    private handleSave(type: string, data: StayMapping[]) {
        this.notify.success(type + ' Property Mapping ' + this.mapping.id);
        this.stayMappings = data;
        this.loading = false;
    }

    private handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.notify.error(error.message);
    }

    private handleData(data: [WineryTemplate[], WineryTemplate[]]) {
        this.loadingElements = false;
        this.detectorTemplates = data[0];
        this.refinementStructureTemplates = data [1];
    }

    // endregion
}
