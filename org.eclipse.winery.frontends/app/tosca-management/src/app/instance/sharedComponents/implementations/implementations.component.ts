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
import { isNullOrUndefined } from 'util';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { InstanceService } from '../../instance.service';
import { ImplementationAPIData } from './implementationAPIData';
import { ImplementationService } from './implementations.service';
import { ModalDirective } from 'ngx-bootstrap';
import { Utils } from '../../../wineryUtils/utils';
import { WineryRowData, WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';
import { ToscaTypes } from '../../../model/enums';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { WineryAddComponent } from '../../../wineryAddComponentModule/addComponent.component';

@Component({
    selector: 'winery-instance-implementations',
    templateUrl: 'implementations.component.html',
    providers: [ImplementationService,
        WineryNotificationService],
})
export class ImplementationsComponent implements OnInit {

    implementationData: ImplementationAPIData[] = [];
    loading = true;
    selectedCell: any;
    elementToRemove: ImplementationAPIData;
    implementationOrTemplate: ToscaTypes;
    columns: Array<WineryTableColumn> = [
        { title: 'Namespace', name: 'namespace', sort: true },
        { title: 'Name', name: 'displayName', sort: true },
    ];
    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;
    @ViewChild('addComponent') addComponent: WineryAddComponent;

    constructor(public sharedData: InstanceService,
                private service: ImplementationService,
                private notificationService: WineryNotificationService) {
    }

    ngOnInit() {
        this.getImplementationData();
        this.implementationOrTemplate = Utils.getImplementationOrTemplateOfType(this.sharedData.toscaComponent.toscaType);
    }

    // region ######## table methods ########
    onCellSelected(data: WineryRowData) {
        if (!isNullOrUndefined(data)) {
            this.selectedCell = data.row;
        }
    }

    onAddClick() {
        const type = {
            id: this.sharedData.toscaComponent.getQName(),
            text: this.sharedData.toscaComponent.localName
        };
        this.addComponent.onAdd(type);
    }

    onRemoveClick(data: ImplementationAPIData) {
        if (isNullOrUndefined(data)) {
            return;
        } else {
            this.elementToRemove = data;
            this.confirmDeleteModal.show();
        }
    }

    removeConfirmed() {
        this.confirmDeleteModal.hide();
        this.loading = true;
        this.service.deleteImplementations(this.elementToRemove)
            .subscribe(
                data => this.handleDeleteResponse(data),
                error => this.handleError(error)
            );
        this.elementToRemove = null;
    }

    // endregion

    // region ######## call service methods and subscription handlers ########
    private getImplementationData(): void {
        this.service.getImplementationData()
            .subscribe(
                data => this.handleData(data),
                error => this.handleError(error)
            );
    }

    private handleData(impl: ImplementationAPIData[]) {
        this.implementationData = impl;
        this.implementationData = this.implementationData.map(item => {
            const url = '/#/' + this.implementationOrTemplate
                + '/' + encodeURIComponent(encodeURIComponent(item.namespace))
                + '/' + item.localname;
            item.displayName = '<a href="' + url + '">' + item.localname + '</a>';
            return item;
        });
        this.loading = false;
    }

    private handleError(error: HttpErrorResponse): void {
        this.loading = false;
        this.notificationService.error('Action caused an error:\n', error.message);
    }

    private handleDeleteResponse(data: HttpResponse<string>) {
        this.loading = false;
        if (data.ok) {
            this.getImplementationData();
            this.notificationService.success('Deletion of ' + Utils.getToscaTypeNameFromToscaType(this.implementationOrTemplate) + ' Successful');
        } else {
            this.notificationService.error('Failed to delete ' + Utils.getToscaTypeNameFromToscaType(this.implementationOrTemplate) + ' failed');
        }
    }

    // endregion
}
