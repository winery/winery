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
import { TableComponent } from '../../tableModule/table.component';

@Component({
    selector: 'winery-instance-implementations',
    templateUrl: 'implementations.component.html',
    providers: [ ImplementationService ],
})
export class ImplementationsComponent implements OnInit {
    implementationData: ImplementationAPIData[];
    loading: boolean = true;
    selectedCell: any;
    allNamespaces: string[];
    selectedNamespace: string;
    columns: Array<any> = [
        {title: 'Namespace', name: 'namespace', sort: true},
        {title: 'Name', name: 'localname', sort: true},
    ];
    newImplementation: ImplementationAPIData;
    @ViewChild('addModal') addImplModal: any;

    constructor(
        private sharedData: InstanceService,
        private service: ImplementationService,
    ) {
        this.implementationData = [];
    }

    ngOnInit() {
        this.service.setPath(this.sharedData.path);
        console.log('ngOnInit');
        this.service.getImplementationData()
            .subscribe(
                data => this.handleData(data),
                error => this.handleError(error)
            );
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
        this.newImplementation = new ImplementationAPIData();
        this.newImplementation.localname="";
        this.addImplModal.show();
    }
    // endregion
    private handleData( impl: ImplementationAPIData[]) {
        this.implementationData = impl;
        this.loading = false;
        console.log(this.implementationData);
    }
    private handleError(error: any): void {
        this.loading = false;
        console.log(error);
    }
    private addNewImplementation(localname: string, selectedNamespace: string) {
    }

}
