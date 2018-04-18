/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
import { InstanceStateService } from './instanceStates.service';
import { InstanceStateApiData } from './InstanceStateApiData';
import { Response } from '@angular/http';
import { isNullOrUndefined } from 'util';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { ModalDirective } from 'ngx-bootstrap';
import { InstanceService } from '../../instance.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

@Component({
    templateUrl: 'instanceStates.component.html',
    providers: [InstanceStateService]
})
export class InstanceStatesComponent implements OnInit {

    loading = true;
    instanceStates: InstanceStateApiData[];
    elementToRemove: InstanceStateApiData = null;
    selectedCell: InstanceStateApiData = null;
    newStateData: InstanceStateApiData = new InstanceStateApiData('');
    columns: Array<any> = [
        { title: 'Name', name: 'state', sort: false },
    ];

    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;
    @ViewChild('addModal') addModal: ModalDirective;

    constructor(public sharedData: InstanceService,
                private service: InstanceStateService, private notify: WineryNotificationService) {
    }

    ngOnInit() {
        this.getInstanceStatesApiData();
    }

    // region ######## table methods ########
    onCellSelected(data: any) {
        if (!isNullOrUndefined(data)) {
            this.selectedCell = new InstanceStateApiData(data.row.state);
        }
    }

    onRemoveClick(data: any) {
        if (isNullOrUndefined(data)) {
            return;
        } else {
            this.elementToRemove = new InstanceStateApiData(data.state);
            this.confirmDeleteModal.show();
        }
    }

    removeConfirmed() {
        this.confirmDeleteModal.hide();
        this.service.deleteState(this.elementToRemove)
            .subscribe(
                data => this.handleDeleteResponse(data),
                error => this.handleError(error)
            );
    }

    // endregion
    // region ######## event handler ########
    onAddClick() {
        this.newStateData = new InstanceStateApiData('');
        this.addModal.show();
    }

    addProperty() {
        this.loading = true;
        if (this.newStateData.state !== '') {
            this.service.addPropertyData(this.newStateData)
                .subscribe(
                    data => this.handleAddResponse(data),
                    error => this.handleError(error)
                );
        }
    }

    // endregion
    // region ######## private methods ########
    private getInstanceStatesApiData(): void {
        this.service.getInstanceStates()
            .subscribe(
                data => this.handleInstanceStateData(data),
                error => this.handleError(error)
            );
    }

    private handleInstanceStateData(instanceStates: InstanceStateApiData[]) {
        this.instanceStates = instanceStates;
        this.loading = false;
    }

    private handleAddResponse(data: HttpResponse<string>) {
        this.loading = true;
        if (data.status === 204) {
            this.getInstanceStatesApiData();
            this.notify.success('Successfully saved Instance State');
        }
    }

    private handleDeleteResponse(data: HttpResponse<string>) {
        this.loading = true;
        this.notify.success('Successfully deleted state \'' + this.elementToRemove.state + '\'');
        this.elementToRemove = null;
        this.getInstanceStatesApiData();
    }

    private handleError(error: HttpErrorResponse): void {
        this.loading = false;
        if (error.status === 406) {
            this.notify.error('Post request not acceptable due to empty state');
        } else {
            this.notify.error(error.message, error.statusText);
        }
    }

    // endregion
}
