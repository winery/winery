/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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
import { WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';
import { YamlRequirementDefinitionsService } from './yamlRequirementDefinitions.service';
import { SelectData } from '../../../model/selectData';
import { YamlRequirementDefinitionApiData } from './yamlRequirementDefinitionApiData';
import { forkJoin } from 'rxjs';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { HttpErrorResponse } from '@angular/common/http';
import { YamlRequirementDefinitionTableData } from './yamlRequirementDefinitionTableData';
import { InstanceService } from '../../instance.service';
import { BsModalRef, BsModalService, ModalDirective } from 'ngx-bootstrap';
import { SelectItem } from 'ng2-select';
import { QName } from '../../../../../../shared/src/app/model/qName';

@Component({
    selector: 'winery-req-definitions',
    templateUrl: 'yamlRequirementDefinitions.html',
    styleUrls: ['yamlRequirementDefinitions.style.css'],
    providers: [
        YamlRequirementDefinitionsService
    ]
})
export class YamlRequirementDefinitionsComponent implements OnInit {
    readonly noneElement: SelectData[] = [
        { text: 'None', id: 'none', children: [{ text: '(none)', id: '(none)' }] }
    ];

    readonly anyElement: SelectData[] = [
        { text: 'Any', id: 'any', children: [{ text: '(any)', id: '(any)' }] }
    ];
    columns: Array<WineryTableColumn> = [
        { title: 'Name', name: 'name' },
        { title: 'Capability Type', name: 'capability' },
        { title: 'Node Type', name: 'node' },
        { title: 'Relationship Type', name: 'relationship' },
        { title: 'Lower Bound', name: 'lowerBound' },
        { title: 'Upper Bound', name: 'upperBound' },
    ];

    allNodeTypes: SelectData[] = [];
    initialNodeType = this.anyElement;
    allCapabilityTypes: SelectData[] = [];
    initialCapabilityType = this.noneElement;
    allRelationshipTypes: SelectData[] = [];
    initialRelationshipType = this.anyElement;
    requirementDefinitions: YamlRequirementDefinitionApiData[] = [];
    tableData: YamlRequirementDefinitionTableData[] = [];
    reqDefToBeAdded: YamlRequirementDefinitionApiData;
    lowerBound: number;
    upperBound: number;
    isUnboundedSelected: boolean;
    loading: boolean;
    elementToRemove: YamlRequirementDefinitionTableData;
    @ViewChild('addModal') addModal: ModalDirective;
    addModalRef: BsModalRef;
    @ViewChild('removeModal') removeModal: ModalDirective;
    removeModalRef: BsModalRef;
    enableAddItemButton = false;

    constructor(public sharedData: InstanceService,
                private service: YamlRequirementDefinitionsService,
                private notify: WineryNotificationService,
                private modalService: BsModalService) {
    }

    ngOnInit(): void {
        this.loading = true;
        forkJoin(
            this.service.getGroupedNodeTypes(),
            this.service.getCapabilityTypes(),
            this.service.getRelationshipTypes(),
            this.service.getAllRequirementDefinitions()
        ).subscribe((result) => {
            this.loading = false;
            this.allNodeTypes = this.noneElement.concat(result[0]);
            this.allCapabilityTypes = this.noneElement.concat(result[1]);
            this.allRelationshipTypes = this.noneElement.concat(result[2]);
            this.handleRequirementDefinitions(result[3]);

        }, error => this.handleError(error));
    }

    private handleError(error: HttpErrorResponse): void {
        this.loading = false;
        this.notify.error(error.message, 'Error');
    }

    private handleRequirementDefinitions(defs: YamlRequirementDefinitionApiData[]) {
        this.requirementDefinitions = defs;

        if (this.requirementDefinitions) {
            this.tableData = this.requirementDefinitions.map(def => {
                    const nodeHref = def.node ? this.typeToHref(QName.stringToQName(def.node), 'nodetypes') : 'ANY';
                    const relationshipHref = def.relationship ? this.typeToHref(QName.stringToQName(def.relationship), 'relationshiptypes') : 'ANY';
                    return new YamlRequirementDefinitionTableData(
                        def.name,
                        this.typeToHref(QName.stringToQName(def.capability), 'capabilitytypes'),
                        def.lowerBound,
                        def.upperBound,
                        nodeHref,
                        relationshipHref);
                }
            );
        } else {
            this.tableData = [];
        }
    }

    onRemoveClick(reqDef: YamlRequirementDefinitionTableData) {
        if (reqDef) {
            this.elementToRemove = reqDef;
            this.removeModalRef = this.modalService.show(this.removeModal);
        }
    }

    removeConfirmed() {
        this.service.deleteRequirementDefinition(this.elementToRemove)
            .subscribe(next => {
                this.notify.success('Deleted Requirement Definition');
                this.tableData = this.tableData.filter(item => item !== this.elementToRemove);
                this.elementToRemove = null;
            },
            error => {
                this.notify.error('Could not delete Requirement Definition');
                this.elementToRemove = null;
            });
    }

    onAddClick() {
        this.lowerBound = 1;
        this.upperBound = 1;
        this.isUnboundedSelected = false;
        this.enableAddItemButton = false;
        this.reqDefToBeAdded = new YamlRequirementDefinitionApiData();
        this.addModalRef = this.modalService.show(this.addModal);
    }

    addRequirementDefinition() {
        // fill in the lower and upper bound values. The other values are already filled
        this.reqDefToBeAdded.lowerBound = this.lowerBound.toString(10);
        if (this.isUnboundedSelected) {
            this.reqDefToBeAdded.upperBound = 'UNBOUNDED';
        } else {
            this.reqDefToBeAdded.upperBound = this.upperBound.toString(10);
        }

        this.service.saveRequirementDefinition(this.reqDefToBeAdded)
            .flatMap(() => this.service.getAllRequirementDefinitions())
            .subscribe(
                (data) => {
                    this.handleRequirementDefinitions(data);
                    this.notify.success(`Requirement definition ${this.reqDefToBeAdded.name} added successfully!`);
                },
                error => this.handleError(error)
            );
    }

    private typeToHref(typeQName: QName, refType: string): string {
        // no need to encode the namespace since we assume dotted namespaces in YAML mode
        const absoluteURL = `/#/${refType}/${typeQName.nameSpace}/${typeQName.localName}`;
        return '<a href="' + absoluteURL + '">' + typeQName.localName + '</a>';
    }

    onSelectedCapTypeChanged(value: SelectItem) {
        this.reqDefToBeAdded.capability = value.id;
        this.enableAddItemButton = value.id !== '(none)';
    }

    onSelectedNodeTypeChanged(value: SelectItem) {
        this.reqDefToBeAdded.node = value.id === '(any)' ? undefined : value.id;
    }

    onSelectedRelTypeChanged(value: SelectItem) {
        this.reqDefToBeAdded.relationship = value.id === '(any)' ? undefined : value.id;
    }

    unboundedToggle() {
        this.isUnboundedSelected = !this.isUnboundedSelected;
    }
}
