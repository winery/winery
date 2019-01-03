/*******************************************************************************
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

import { RefinementMappingsService } from '../refinementMappings.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { BsModalRef, BsModalService, ModalDirective } from 'ngx-bootstrap';
import { WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';
import { PrmPropertyMapping, PrmPropertyMappingType } from './prmPropertyMapping';
import { NodeTemplate } from '../../../model/wineryComponent';
import { Component, OnInit, ViewChild } from '@angular/core';
import { forkJoin } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { SelectData } from '../../../model/selectData';
import {
    PropertiesDefinitionKVElement, PropertiesDefinitionsResourceApiData
} from '../../sharedComponents/propertiesDefinition/propertiesDefinitionsResourceApiData';
import { InstanceService } from '../../instance.service';

@Component({
    templateUrl: 'prmPropertyMappings.component.html',
    providers: [
        RefinementMappingsService
    ]
})
export class PrmPropertyMappingsComponent implements OnInit {

    readonly propertyMappingTypes = PrmPropertyMappingType;

    loading = true;
    columns: Array<WineryTableColumn> = [
        { title: 'Id', name: 'id', sort: true },
        { title: 'Detector Node', name: 'detectorNode', sort: true },
        { title: 'Refinement Node', name: 'refinementNode', sort: true },
        { title: 'Type', name: 'type', sort: true },
        { title: 'Detector Node Property', name: 'detectorProperty', sort: true },
        { title: 'Refinement Node Property', name: 'refinementProperty', sort: true },
    ];

    propertyMappings: PrmPropertyMapping[];
    detectorNodeTemplates: NodeTemplate[];
    detectorProperties: PropertiesDefinitionKVElement[];
    refinementStructureNodeTemplates: NodeTemplate[];
    refinementProperties: PropertiesDefinitionKVElement[];

    @ViewChild('addModal') addModal: ModalDirective;
    @ViewChild('removeModal') removeModal: ModalDirective;
    addModalRef: BsModalRef;
    removeModalRef: BsModalRef;

    mapping: PrmPropertyMapping;
    selectedDetectorNode: NodeTemplate;
    selectedRefinementNode: NodeTemplate;
    loadingRefinementProperties = false;
    loadingDetectorProperties = false;

    constructor(private service: RefinementMappingsService,
                private notify: WineryNotificationService,
                private sharedData: InstanceService,
                private modalService: BsModalService) {
    }

    ngOnInit(): void {
        forkJoin(
            this.service.getPropertyMappings(),
            this.service.getDetectorNodeTemplates(),
            this.service.getRefinementTopologyNodeTemplates(),
        ).subscribe(
            data => this.handleData(data),
            error => this.handleError(error)
        );
    }

    // region ********** Table Callbacks **********
    onAddButtonClicked() {
        let id = 0;
        this.propertyMappings.forEach(value => {
            const number = Number(value.id.split(PrmPropertyMapping.idPrefix)[1]);
            if (!isNaN(number) && number >= id) {
                id = number;
                if (number === id) {
                    id++;
                }
            }
        });

        this.mapping = new PrmPropertyMapping(id);
        this.cleanProperties();
        this.addModalRef = this.modalService.show(this.addModal);
    }

    onRemoveButtonClicked(selected: PrmPropertyMapping) {
        this.mapping = selected;
        this.removeModalRef = this.modalService.show(this.removeModal);
    }

    // endregion

    // region ********** Add Modal Callbacks **********
    onAddPrmPropertyMapping() {
        this.loading = true;
        this.service.addPrmPropertyMapping(this.mapping)
            .subscribe(
                data => this.handleSave('Added', data),
                error => this.handleError(error)
            );
    }

    propertyTypeSelected(type: PrmPropertyMappingType) {
        this.mapping.type = type;
        if (type === PrmPropertyMappingType.ALL) {
            this.cleanProperties();
        }
        this.getProperties();
    }

    detectorNodeSelected(node: SelectData) {
        this.mapping.detectorNode = node.id;
        this.selectedDetectorNode = this.detectorNodeTemplates
            .find(value => value.id === node.id);
        this.getProperties();
    }

    refinementNodeSelected(node: SelectData) {
        this.mapping.refinementNode = node.id;
        this.selectedRefinementNode = this.refinementStructureNodeTemplates
            .find(value => value.id === node.id);
        this.getProperties();
    }

    // endregion

    // region ********** Remove Modal Callback **********
    onRemovePrmPropertyMapping() {
        this.service.deletePrmPropertyMapping(this.mapping)
            .subscribe(
                data => this.handleSave('Removed', data),
                error => this.handleError(error)
            );
    }

    // endregion

    // region ********** Private Methods *********
    private handleSave(type: string, data: PrmPropertyMapping[]) {
        this.notify.success(type + ' Property Mapping ' + this.mapping.id);
        this.propertyMappings = data;
        this.loading = false;
    }

    private getProperties() {
        if (this.mapping.type && this.mapping.type === PrmPropertyMappingType.SELECTIVE) {
            if (this.selectedRefinementNode) {
                this.loadingRefinementProperties = true;
                this.service.getNodeTypeProperties(this.selectedRefinementNode.type)
                    .subscribe(
                        data => this.handleProperties(data),
                        error => this.handleError(error)
                    );
            }
            if (this.selectedDetectorNode) {
                this.service.getNodeTypeProperties(this.selectedDetectorNode.type)
                    .subscribe(
                        data => this.handleProperties(data, true),
                        error => this.handleError(error)
                    );
            }
        }
    }

    private handleData(data: any) {
        this.loading = false;
        this.propertyMappings = data[0];
        this.detectorNodeTemplates = data[1];
        this.refinementStructureNodeTemplates = data[2];
    }

    private handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.notify.error(error.message);
    }

    private handleProperties(data: PropertiesDefinitionsResourceApiData, isDetector = false) {
        this.loadingRefinementProperties = false;
        if (!data.winerysPropertiesDefinition) {
            this.notify.error('Mapping of non-winery properties is currently not supported!', 'No Winery Properties Definitions!');
        } else {
            if (isDetector) {
                this.detectorProperties = data.winerysPropertiesDefinition.propertyDefinitionKVList;
            } else {
                this.refinementProperties = data.winerysPropertiesDefinition.propertyDefinitionKVList;
            }
        }
    }

    private cleanProperties() {
        delete this.mapping.detectorProperty;
        delete this.mapping.refinementProperty;
        delete this.refinementProperties;
        delete this.detectorProperties;
    }

    // endregion
}
