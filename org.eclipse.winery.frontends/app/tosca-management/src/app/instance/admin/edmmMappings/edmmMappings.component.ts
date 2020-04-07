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
import { Component, OnInit, ViewChild } from '@angular/core';
import { EdmmMappingItem, EdmmMappingsService, EdmmType } from './edmmMappings.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { HttpErrorResponse } from '@angular/common/http';
import { BsModalRef, BsModalService, ModalDirective } from 'ngx-bootstrap';
import { SelectData } from '../../../model/selectData';
import { forkJoin } from 'rxjs';
import { ToscaTypes } from '../../../model/enums';
import { SectionData } from '../../../section/sectionData';
import { SelectItem } from 'ng2-select';

@Component({
    selector: 'winery-edmm-mappings',
    templateUrl: './edmmMappings.component.html',
    providers: [
        EdmmMappingsService
    ]
})
export class EdmmMappingsComponent implements OnInit {

    loading = true;

    edmmTypes: string[];

    @ViewChild('addModal') addModal: ModalDirective;
    @ViewChild('removeModal') removeModal: ModalDirective;
    addModalRef: BsModalRef;
    removeModalRef: BsModalRef;

    typesSelect: SelectData[];
    edmmMappings: EdmmMappingItem[];
    elementToEdit: EdmmMappingItem;
    columns = [
        { title: 'Tosca QName', name: 'toscaType' },
        { title: 'EDMM Type', name: 'edmmType' },
    ];

    constructor(private service: EdmmMappingsService, private notify: WineryNotificationService, private modalService: BsModalService) {
        this.edmmTypes = [];
        for (const edmmType of Object.keys(EdmmType)) {
            this.edmmTypes.push(edmmType.toLowerCase());
        }
    }

    ngOnInit() {
        this.service.getMappings()
            .subscribe(
                data => this.handleData(data),
                error => this.handleError(error)
            );
        forkJoin(
            this.service.getTypes(ToscaTypes.NodeType),
            this.service.getTypes(ToscaTypes.RelationshipType)
        ).subscribe(
            (data: [SectionData[], SectionData[]]) => {
                const nodeTypes = new SelectData();
                nodeTypes.id = 'nodeTypes';
                nodeTypes.text = 'Node Types';
                nodeTypes.children = [];
                data[0].forEach(element => nodeTypes.children.push({ id: element.qName, text: element.name }));

                const relationshipTypes = new SelectData();
                relationshipTypes.id = 'relationshipTypes';
                relationshipTypes.text = 'Relationship Types';
                relationshipTypes.children = [];
                data[1].forEach(element => relationshipTypes.children.push({ id: element.qName, text: element.name }));

                this.typesSelect = [nodeTypes, relationshipTypes];
            },
            error => this.handleError(error)
        );
    }

    public onAddClick() {
        this.elementToEdit = new EdmmMappingItem();
        this.addModalRef = this.modalService.show(this.addModal);
    }

    onAddEdmmMapping() {
        this.edmmMappings.push(this.elementToEdit);
        this.save();
    }

    public onRemoveClick(data: EdmmMappingItem) {
        if (!data) {
            return;
        } else {
            this.elementToEdit = data;
            this.removeModalRef = this.modalService.show(this.removeModal);
        }
    }

    public onRemoveConfirmed() {
        const index = this.edmmMappings.findIndex(value => value.toscaType === this.elementToEdit.toscaType);
        this.edmmMappings.splice(index, 1);
        this.save();
    }

    private handleData(data: EdmmMappingItem[], saved = false) {
        if (saved) {
            this.notify.success('Successfully saved EDMM Mappings!');
        }

        this.loading = false;
        this.edmmMappings = data;
    }

    private handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.notify.error(error.message);
    }

    private save() {
        this.service.updateEdmmMapping(this.edmmMappings)
            .subscribe(
                data => this.handleData(data, true),
                error => this.handleError(error)
            );
    }

    edmmTypeSelected(data: SelectData) {
        this.elementToEdit.edmmType = <EdmmType>data.id;
    }

    toscaTypeSelected(data: SelectItem) {
        this.elementToEdit.toscaType = data.id;
    }
}
