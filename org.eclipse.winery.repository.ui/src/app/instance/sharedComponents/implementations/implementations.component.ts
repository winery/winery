/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Component, OnInit, ViewChild } from '@angular/core';
import { Response } from '@angular/http';
import { isNullOrUndefined } from 'util';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { WineryValidatorObject } from '../../../wineryValidators/wineryDuplicateValidator.directive';
import { InstanceService } from '../../instance.service';
import { ImplementationAPIData } from './implementationAPIData';
import { ImplementationService } from './implementations.service';
import { ImplementationWithTypeAPIData } from './implementationWithTypeAPIData';
import { ModalDirective } from 'ngx-bootstrap';
import { Utils } from '../../../wineryUtils/utils';
import { WineryRowData, WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';
import { ToscaTypes } from '../../../wineryInterfaces/enums';

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
    newImplementation: ImplementationAPIData = new ImplementationAPIData('', '');
    elementToRemove: ImplementationAPIData;
    implementationOrTemplate: ToscaTypes;
    selectedNamespace: string;
    validatorObject: WineryValidatorObject;
    columns: Array<WineryTableColumn> = [
        { title: 'Namespace', name: 'namespace', sort: true },
        { title: 'Name', name: 'displayName', sort: true },
    ];
    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;
    @ViewChild('addModal') addModal: ModalDirective;

    constructor(private sharedData: InstanceService,
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
        this.validatorObject = new WineryValidatorObject(this.implementationData, 'localname');
        this.newImplementation = new ImplementationAPIData('', '');
        this.addModal.show();
    }

    addNewImplementation(localname: string) {
        this.loading = true;
        const type = '{' + this.sharedData.toscaComponent.namespace + '}' + this.sharedData.toscaComponent.localName;
        const resource = new ImplementationWithTypeAPIData(this.selectedNamespace, localname, type);
        this.service.postImplementation(resource).subscribe(
            data => this.handlePostResponse(data),
            error => this.handleError(error)
        );
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

    private handleError(error: any): void {
        this.loading = false;
        this.notificationService.error('Action caused an error:\n', error);
    }

    private handlePostResponse(data: Response) {
        this.loading = false;
        if (data.ok) {
            this.getImplementationData();
            this.notificationService.success('Created new ' + Utils.getToscaTypeNameFromToscaType(this.implementationOrTemplate));
        } else {
            this.notificationService.error('Failed to create ' + Utils.getToscaTypeNameFromToscaType(this.implementationOrTemplate));
        }
    }

    private handleDeleteResponse(data: Response) {
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
