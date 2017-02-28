/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter - initial API and implementation
 */

import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { InstanceService } from './instance.service';

@Component({
    templateUrl: 'instance.component.html',
    providers: [
        InstanceService
    ]
})
export class InstanceComponent implements OnInit, OnDestroy {

    availableTabs: string[];
    selectedResource: string;
    selectedComponentId: string;
    selectedNamespace: string;

    routeSub: Subscription;

    constructor(private route: ActivatedRoute,
                private service: InstanceService) {
    }

    ngOnInit(): void {
        this.routeSub = this.route
            .data
            .subscribe(data => {
                this.selectedResource = data['resolveData'].section;
                this.selectedNamespace = data['resolveData'].namespace;
                this.selectedComponentId = data['resolveData'].instanceId;

                this.service.setSharedData(this.selectedResource, this.selectedNamespace, this.selectedComponentId);

                this.availableTabs = this.service.getSubMenuByResource();
            });
    }

    ngOnDestroy(): void {
        this.routeSub.unsubscribe();
    }
}
