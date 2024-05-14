/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
import { EdmmMappingItem } from './edmmMappings.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { BsModalRef, BsModalService, ModalDirective } from 'ngx-bootstrap';
import { SelectData } from '../../../model/selectData';
import { SelectItem } from 'ng2-select';
import { EdmmTypesService } from './edmmTypes.service';

export class EdmmTypesRow {
    value: string;

    constructor(value: string) {
        this.value = value;
    }

}

@Component({
    selector: 'winery-types-mappings',
    templateUrl: './edmmTypes.component.html',
    providers: []
})
export class EdmmTypesComponent implements OnInit {

    loading = true;
    edmmTypes: EdmmTypesRow[];

    @ViewChild('addModal') addModal: ModalDirective;
    @ViewChild('removeModal') removeModal: ModalDirective;
    addModalRef: BsModalRef;
    removeModalRef: BsModalRef;
    elementToEdit: EdmmTypesRow;
    columns = [
        { title: 'EDMM Type', name: 'value' }
    ];

    constructor(private service: EdmmTypesService, private notify: WineryNotificationService, private modalService: BsModalService) {
    }

    ngOnInit() {
        this.service.getEdmmTypes()
            .subscribe(
                data => this.handleData(data),
                error => this.handleError(error)
            );
    }

    public onAddClick() {
        this.elementToEdit = new EdmmTypesRow(null);
        this.addModalRef = this.modalService.show(this.addModal);
    }

    onAddType(newType: string) {
        this.edmmTypes.push(new EdmmTypesRow(newType));
        this.save();
    }

    public onRemoveClick(data: EdmmTypesRow) {
        if (!data) {
            return;
        } else {
            this.elementToEdit = data;
            this.removeModalRef = this.modalService.show(this.removeModal);
        }
    }

    public onRemoveConfirmed() {
        const index = this.edmmTypes.findIndex(value => value === this.elementToEdit);
        this.edmmTypes.splice(index, 1);
        this.save();
    }

    /***
     * Triggered at the end of an error to load the real list from the backend. Silent not to go into an infinite loop
     * if the GET operation is the source of the error!
     * @private
     */
    private silentReload() {
        this.service.getEdmmTypes().subscribe(
            data => this.handleData(data),
            () => this.loading = false
        );
    }

    private handleData(data: string[], saved = false) {
        if (saved) {
            this.notify.success('Successfully saved EDMM Types!');
        }

        this.loading = false;
        this.edmmTypes = data.map(i => new EdmmTypesRow(i));
    }

    private handleError(error: HttpErrorResponse) {
        this.loading = false;
        if (error.status === 400) {
            this.notify.error('Trying to add duplicate EDMM types!');
        } else if (error.status === 409) {
            this.notify.error('Trying to remove an EDMM type that is being used in a mapping. Remove the mapping first!');
        } else {
            this.notify.error(error.message);
        }

        this.silentReload();
    }

    private save() {
        this.service.updateEdmmTypes(this.edmmTypes.map(i => i.value))
            .subscribe(
                data => this.handleData(data, true),
                error => this.handleError(error)
            );
    }
}
