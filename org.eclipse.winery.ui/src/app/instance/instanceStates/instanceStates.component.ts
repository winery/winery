/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Huixin Liu, Nicole Keppler - initial API and implementation
 *     Lukas Balzer - initial component visuals
 */

import { Component, OnInit, ViewChild } from '@angular/core';
import { InstanceStateService } from './instanceState.service';
import { InstanceService } from '../instance.service';
import { InstanceStateApiData } from './InstanceStateApiData';

@Component({
    selector: 'winery-instance-instanceStates',
    templateUrl: 'instanceStates.component.html',
    providers: [InstanceStateService]
})
export class InstanceStatesComponent implements OnInit {
    loading: boolean = true;
    instanceStates: InstanceStateApiData[];
    newStateData: InstanceStateApiData = new InstanceStateApiData('');
    @ViewChild('confirmDeleteModal') deleteStateModal: any;

    @ViewChild('addModal') addStateModal: any;
    columns: Array<any> = [
        {title: 'Name', name: 'state', sort: false},
    ];
    constructor(
        private sharedData: InstanceService,
        private service: InstanceStateService
    ) {
        console.log('constructor');
    }


    ngOnInit() {
        this.service.setPath(this.sharedData.path);
        this.service.getInstanceStates()
            .subscribe(
                data => this.handleInstanceStateData(data),
                error => this.handleError(error)
            );
        console.log('onInit');
    }

    onCellSelected(data: any) {
        console.log('selected');
        this.deleteStateModal.show();
    }

    onRemoveClick(data: any) {
        console.log('remove');
    }

    onAddClick() {
        console.log('add');
        this.newStateData = new InstanceStateApiData('');
        this.addStateModal.show();
    }

    addProperty(state: string) {
        console.log(state);
    }
    private handleInstanceStateData(instanceStates: InstanceStateApiData[]) {
        console.log('instanceStatesResolved');
        console.log(instanceStates);
        this.instanceStates = instanceStates;
        this.loading = false;
    }
    private handleError(error: any): void {
        this.loading = false;
        console.log(error);
    }


}
