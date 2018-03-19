/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { RequirementOrCapability } from './requirementsOrCapabilitiesApiData';
import { RequirementsOrCapabilitiesService } from './requirementsOrCapabilities.service';
import { WineryNotificationService } from '../../../../wineryNotificationModule/wineryNotification.service';
import { isNullOrUndefined } from 'util';
import { ModalDirective } from 'ngx-bootstrap';
import { WineryTableColumn } from '../../../../wineryTableModule/wineryTable.component';

@Component({
    selector: 'winery-instance-requirements-or-capabilities',
    templateUrl: 'requirementsOrCapabilities.component.html',
    providers: [
        RequirementsOrCapabilitiesService
    ]
})
export class RequirementsOrCapabilitiesComponent implements OnInit {

    @Input() singleItem = '';
    @Input() title = '';

    @ViewChild('addReqOrCapModal') addReqOrCapModal: ModalDirective;
    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;

    columns: Array<any> = [
        { title: 'Name', name: 'name', sort: true },
        { title: 'Reference', name: 'ref', sort: true },
    ];

    reqOrCapToBeAdded: RequirementOrCapability;
    requirementsOrCapabilitiesList: RequirementOrCapability[];
    loading = true;
    currentSelected: RequirementOrCapability;
    edit = false;
    addOrChange: string;

    constructor(private service: RequirementsOrCapabilitiesService,
                private notify: WineryNotificationService) {
        this.reqOrCapToBeAdded = new RequirementOrCapability();
    }

    ngOnInit() {
        this.getRequirementsOrCapabilities();
    }

    // region ########## Template Callbacks ##########
    onCellSelected(selectedItem: any) {
        this.currentSelected = selectedItem.row;
    }

    onAddClick() {
        this.addOrChange = 'Add ';
        this.currentSelected = null;
        this.addReqOrCapModal.show();
    }

    onEditClick(reqOrCap: RequirementOrCapability) {
        if (!isNullOrUndefined(reqOrCap)) {
            this.addOrChange = 'Change ';
            this.edit = true;
            this.reqOrCapToBeAdded.name = reqOrCap.name;
            this.reqOrCapToBeAdded.ref = reqOrCap.ref;
            this.currentSelected = reqOrCap;
            this.addReqOrCapModal.show();
        } else {
            return;
        }
    }

    onDeleteClick() {
        if (!isNullOrUndefined(this.currentSelected)) {
            this.confirmDeleteModal.show();
        } else {
            return;
        }
    }

    onRemoveClick(reqOrCap: RequirementOrCapability) {
        if (!isNullOrUndefined(reqOrCap)) {
            this.currentSelected = reqOrCap;
            this.confirmDeleteModal.show();
        } else {
            return;
        }
    }

    cancelBtnClicked() {
        this.addReqOrCapModal.hide();
        this.edit = false;
    }

    removeConfirmed() {
        this.edit = false;
        this.addReqOrCapModal.hide();
        this.confirmDeleteModal.hide();
        this.deleteReqOrCap(this.currentSelected.id);
    }

    addConfirmed() {
        this.addReqOrCapModal.hide();
        this.addNewRequirementOrCapability();
    }

    updateConfirmed() {
        this.addReqOrCapModal.hide();
        this.addNewRequirementOrCapability();

    }

    // endregion

    // region ########## Private Methods ##########
    private getRequirementsOrCapabilities(): void {
        this.loading = true;
        this.service.getRequirementsOrCapabilities().subscribe(
            data => this.handleRequirementsOrCapabilitiesData(data),
            error => this.handleError(error)
        );
    }

    private handleRequirementsOrCapabilitiesData(data: RequirementOrCapability[]) {
        this.requirementsOrCapabilitiesList = data;
        this.requirementsOrCapabilitiesList = this.requirementsOrCapabilitiesList.map(
            obj => {
                if (obj.ref.id === null) {
                    obj.ref = '';
                } else {
                    obj.ref = obj.ref.id;
                }
                return obj;
            }
        );
        this.loading = false;
    }

    private handleError(error: any) {
        this.notify.error(error);
        this.loading = false;
    }

    private addNewRequirementOrCapability() {
        this.loading = false;
        this.service.sendPostRequest(this.reqOrCapToBeAdded).subscribe(
            data => this.handlePostResponse(),
            error => this.handleError(error)
        );
    }

    private handlePostResponse() {
        this.loading = false;
        if (this.edit) {
            this.deleteReqOrCap(this.currentSelected.id);
        } else {
            this.notify.success('new ' + this.singleItem + this.reqOrCapToBeAdded.name + ' added');
            this.getRequirementsOrCapabilities();
        }
    }

    private deleteReqOrCap(id: string) {
        this.service.deleteCapOrReqDef(id)
            .subscribe(
                data => this.handleCapOrReqDelete(),
                error => this.handleError(error)
            );
    }

    private handleCapOrReqDelete() {
        if (this.edit) {
            this.notify.success(this.singleItem + ' id: ' + this.currentSelected.id + ' updated');
        } else {
            this.notify.success(this.singleItem + ' id: ' + this.currentSelected.id + ' name: ' + this.currentSelected.name + ' deleted');
        }
        this.loading = false;
        this.currentSelected = null;
        this.edit = false;
        this.getRequirementsOrCapabilities();
    }

    // endregion
}
