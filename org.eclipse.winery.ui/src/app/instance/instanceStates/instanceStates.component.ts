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
 */

import { Component, OnInit } from '@angular/core';
import { InstanceStateService } from './instanceState.service';
import { InstanceService } from '../instance.service';

@Component({
    selector: 'winery-instance-instanceStates',
    templateUrl: 'instanceStates.component.html',
    providers: [InstanceStateService],
})
export class InstanceStatesComponent implements OnInit {
    loading: boolean = true;
    instanceStates: string[];

    constructor(
        private sharedData: InstanceService,
        private service: InstanceStateService
    ) {
        this.instanceStates = [];
        this.instanceStates[0] = 'defaultValue';
    }


    ngOnInit() {
        this.service.setPath(this.sharedData.path);
        this.service.getInstanceStates()
            .subscribe(
                data => this.handleInstanceStateData(data),
                error => this.handleError(error)
            );
    }

    private handleInstanceStateData(instanceStates: Array<string>) {
        this.instanceStates = instanceStates;
    }
    private handleError(error: any): void {
        this.loading = false;
        console.log(error);
    }
}
