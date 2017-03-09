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
import { Response } from '@angular/http';

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
    }


    ngOnInit() {
        this.service.setPath(this.sharedData.path);
        this.getInstanceStatesApiData();
    }

    onCellSelected(data: any) {
        console.log('selected');
        this.deleteStateModal.show();
    }

    onRemoveClick(data: any) {
        console.log('remove');
    }
    // region ######## event handler ########
    onAddClick() {
        this.newStateData = new InstanceStateApiData('');
        this.addStateModal.show();
    }
    addProperty(state: string) {
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
        } else if (data.status === 406) {
            this.loading = false;
            console.log('Post request not acceptable due to empty state');
        }
    }
    private handleError(error: any): void {
        this.loading = false;
        console.log(error);
    }
    // endregion


}
