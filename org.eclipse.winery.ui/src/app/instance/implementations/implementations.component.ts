/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Nicole Keppler, Lukas Balzer - initial API and implementation
 */

import { Component, OnInit, ViewChild } from '@angular/core';
import { ImplementationService } from './implementation.service';
import { ImplementationAPIData } from './implementationAPIData';
import { InstanceService } from '../instance.service';
import { ImplementationWithTypeAPIData } from './implementationWithTypeAPIData';
import { isNullOrUndefined } from 'util';
import { NotificationService } from '../../notificationModule/notificationservice';
import { Router } from '@angular/router';
import { Response } from '@angular/http';
import { ValidatorObject } from '../../validators/duplicateValidator.directive';

@Component({
    selector: 'winery-instance-implementations',
    templateUrl: 'implementations.component.html',
    providers: [ImplementationService,
        NotificationService],
})
export class ImplementationsComponent implements OnInit {
    implementationData: ImplementationAPIData[];
    loading: boolean = true;
    selectedCell: any;
    newImplementation: ImplementationAPIData = new ImplementationAPIData('', '');
    elementToRemove: ImplementationAPIData;
    allNamespaces: Array<string> = [];
    defaultNamespace: Array<string> = [];
    refreshedNamespace: any = {};
    selectedNamespace: string;
    validatorObject: ValidatorObject;
    columns: Array<any> = [
        {title: 'Namespace', name: 'namespace', sort: true},
        {title: 'Name', name: 'localname', sort: true},
    ];
    @ViewChild('confirmDeleteModal') deleteImplModal: any;
    @ViewChild('addModal') addImplModal: any;

    constructor(private sharedData: InstanceService,
                private service: ImplementationService,
                private notificationService: NotificationService,
                private router: Router) {
        this.implementationData = [];
    }

    ngOnInit() {
        this.service.setPath(this.sharedData.path);
        this.getImplementationData();
    }

    // region ######## table methods ########
    onCellSelected(data: any) {
        if (!isNullOrUndefined(data)) {
            this.selectedCell = data.row;
        }
    }

    onAddClick() {
        this.service.getAllNamespaces()
            .subscribe(
                data => this.handleNamespaces(data),
                error => this.handleError(error)
            );
        this.validatorObject = new ValidatorObject(this.implementationData, 'localname');
        this.newImplementation = new ImplementationAPIData('', '');
        this.addImplModal.show();
    }

    onRemoveClick(data: any) {
        if (isNullOrUndefined(data)) {
            return;
        } else {
            this.elementToRemove = new ImplementationAPIData(data.namespace, data.localname);
            this.deleteImplModal.show();
        }
    }

    removeConfirmed() {
        this.deleteImplModal.hide();
        this.loading = true;
        this.service.deleteImplementations(this.elementToRemove)
            .subscribe(
                data => this.handleDeleteResponse(data),
                error => this.handleError(error)
            );
        this.elementToRemove = null;
    }

    private handleNamespaces(data: any) {
        this.allNamespaces = data;
        this.defaultNamespace = [this.allNamespaces[0], this.allNamespaces[0]];
        this.selectedNamespace = this.allNamespaces[0];
    }

    private namespaceSelected(selectedNamespace: any) {
        this.selectedNamespace = selectedNamespace.text;
    }

    private namespaceRefresh(refreshedNamespace: any) {
        this.refreshedNamespace = refreshedNamespace;
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
        this.loading = false;
    }

    private handleError(error: any): void {
        this.loading = false;
        this.notificationService.error('Action caused an error:\n', error);
    }

    private addNewImplementation(localname: string) {
        this.loading = true;
        let typeNamespace = this.sharedData.selectedNamespace;
        let typeName = this.sharedData.selectedComponentId;
        let type = '{' + typeNamespace + '}' + typeName;
        let resource = new ImplementationWithTypeAPIData(this.selectedNamespace,
            localname,
            type);
        this.service.postImplementation(resource).subscribe(
            data => this.handlePostResponse(data),
            error => this.handleError(error)
        );
    }

    private handlePostResponse(data: Response) {
        this.loading = false;
        if (data.ok) {
            this.getImplementationData();
            this.notificationService.success('Created new NodeType Implementation', 'Success');
        } else {
            this.notificationService.error('Failed to create NodeType Implementation', 'Creation Failed');
        }
    }

    private handleDeleteResponse(data: Response) {
        this.loading = false;
        if (data.ok) {
            this.getImplementationData();
            this.notificationService.success('Deletion of NodeType Implementationb Successful', 'Success');
        } else {
            this.notificationService.error('Failed to delete NodeType Implementation failed', 'Deletion Failed');
        }
    }

    // endregion
}
