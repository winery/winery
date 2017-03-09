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

import { Component, OnInit } from '@angular/core';
import { ImplementationService } from './implementation.service';
import { ImplementationAPIData } from './implementationAPIData';
import { InstanceService } from '../instance.service';

@Component({
    selector: 'winery-instance-implementations',
    templateUrl: 'implementations.component.html',
    providers: [ ImplementationService ],
})
export class ImplementationsComponent implements OnInit {
    implementationData: ImplementationAPIData[];
    loading: boolean = true;

    constructor(
        private sharedData: InstanceService,
        private service: ImplementationService,
    ) {
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

    private handleData( impl: ImplementationAPIData[]) {
        this.implementationData = impl;
        this.loading = false;
        console.log(this.implementationData);
    }
    private handleError(error: any): void {
        this.loading = false;
        console.log(error);
    }
}
