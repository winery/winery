/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Component, OnInit, ViewChild } from '@angular/core';
import { InstanceStateService } from './instanceStates.service';
import { InstanceStateApiData } from './InstanceStateApiData';
import { Response } from '@angular/http';
import { isNullOrUndefined } from 'util';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { ModalDirective } from 'ngx-bootstrap';

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
        {title: 'Name', name: 'state', sort: false},
    ];

    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;
    @ViewChild('addModal') addModal: ModalDirective;

    constructor(private service: InstanceStateService, private notify: WineryNotificationService) {
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
        this.elementToRemove = null;
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

    private handleAddResponse(data: Response) {
        this.loading = true;
        if (data.status === 204) {
            this.getInstanceStatesApiData();
            this.notify.success('Successfully saved Instance State');
        } else if (data.status === 406) {
            this.handleError('Post request not acceptable due to empty state');
        }
    }

    private handleDeleteResponse(data: Response) {
        this.loading = true;
        if (data.status === 204) {
            this.getInstanceStatesApiData();
        } else {
            this.handleError(data);
        }
    }

    private handleError(error: any): void {
        this.loading = false;
        this.notify.error(error);
    }

    // endregion
}
