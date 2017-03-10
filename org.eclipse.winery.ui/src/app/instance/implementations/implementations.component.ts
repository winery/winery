/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Nicole Keppler - initial API and implementation
 */

import { Component, OnInit, ViewChild } from '@angular/core';
import { ImplementationService } from './implementation.service';
import { ImplementationAPIData } from './implementationAPIData';
import { InstanceService } from '../instance.service';
import { WineryTableComponent } from '../../wineryTableModule/wineryTable.component';
import { ImplementationWithTypeAPIData } from './implementationWithTypeAPIData';
import { Response } from '@angular/http';
import { NotificationService } from '../../notificationModule/notificationservice';

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
    allNamespaces: string[] = [];
    selectedNamespace: string;
    columns: Array<any> = [
        {title: 'Namespace', name: 'namespace', sort: true},
        {title: 'Name', name: 'localname', sort: true},
    ];
    newImplementation: ImplementationAPIData = new ImplementationAPIData();

    @ViewChild('addModal') addImplModal: any;
    value: any = {};

    constructor(private sharedData: InstanceService,
                private service: ImplementationService,
                private notificationService: NotificationService) {
        this.implementationData = [];
    }

    ngOnInit() {
        this.service.setPath(this.sharedData.path);
        this.getImplementationData();
    }

    // region ######## table methods ########
    onCellSelected(data: any) {
        console.log('selected');
    }

    onRemoveClick(data: any) {
        console.log('remove');
    }

    onAddClick() {
        console.log('add');
        this.service.getAllNamespaces()
            .subscribe(
                data => this.allNamespaces = data,
                error => this.handleError(error)
            );
        this.newImplementation = new ImplementationAPIData();
        this.addImplModal.show();
        console.log(this.allNamespaces);
    }

    // endregion
    private handleData(impl: ImplementationAPIData[]) {
        this.implementationData = impl;
        this.loading = false;
        console.log(this.implementationData);
    }

    private handleError(error: any): void {
        this.loading = false;
        console.log(error);
    }

    private addNewImplementation(localname: string) {
        let typeNamespace = this.sharedData.selectedNamespace;
        let typeName = this.sharedData.selectedComponentId;
        let type = '{' + typeNamespace + '}' + typeName;
        console.log(type);
        let resource = new ImplementationWithTypeAPIData(this.selectedNamespace,
            localname,
            type);
        this.loading = true;
        this.service.postImplementation(resource).subscribe(
            data => this.handleResponse(data),
            error => console.log(error)
        );
    }

    private handleResponse(data: Response) {
        this.loading = false;
        if (data.ok) {
            this.getImplementationData();
            this.notificationService.success('Created new NodeType Implementation', 'Success');
        } else {
            this.notificationService.error('Failed to create NodeType Implementation', 'Creation Failed');
        }
    }

    private getImplementationData(): void {
        console.log('loaded');
        this.service.getImplementationData()
            .subscribe(
                data => this.handleData(data),
                error => this.handleError(error)
            );
    }

    private namespaceSelected(value: any) {
        console.log(value);
        this.selectedNamespace = value.text;
    }

    private namespaceRefresh(value: any) {
        this.value = value;
    }
}
