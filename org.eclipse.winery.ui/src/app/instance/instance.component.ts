/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter, Niko Stadelmaier - initial API and implementation
 */
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { InstanceService } from './instance.service';
import { NotificationService } from '../notificationModule/notification.service';
import { backendBaseUri } from '../configuration';
import { RemoveWhiteSpacesPipe } from '../pipes/removeWhiteSpaces.pipe';
import { ExistService } from '../util/existService';
import { subscribeOn } from 'rxjs/operator/subscribeOn';

@Component({
    templateUrl: 'instance.component.html',
    providers: [
        InstanceService,
        RemoveWhiteSpacesPipe,
    ]
})
export class InstanceComponent implements OnInit, OnDestroy {

    availableTabs: string[];
    selectedResource: string;
    selectedComponentId: string;
    selectedNamespace: string;
    imageUrl: string;

    routeSub: Subscription;

    constructor(private route: ActivatedRoute,
                private router: Router,
                private service: InstanceService,
                private notify: NotificationService, private existService: ExistService) {
    }

    ngOnInit(): void {
        this.routeSub = this.route
            .data
            .subscribe(data => {
                    this.selectedResource = data['resolveData'].section;
                    this.selectedNamespace = data['resolveData'].namespace;
                    this.selectedComponentId = data['resolveData'].instanceId;

                    this.service.setSharedData(this.selectedResource, this.selectedNamespace, this.selectedComponentId);
                    if (this.selectedResource === 'nodeType') {
                        const img = backendBaseUri + this.service.path + '/visualappearance/50x50';
                        this.existService.check(img)
                            .subscribe(
                                () => this.imageUrl = img,
                                () => this.imageUrl = null,
                            );
                    }

                    this.availableTabs = this.service.getSubMenuByResource();

                    // redirect to first element in the menu
                    if (!this.router.url.includes('/admin') && this.router.url.split('/').length < 5) {
                            this.router.navigate([this.service.path + '/' + this.availableTabs[0].toLowerCase().replace(/ /g, '')]);
                    }
                },
                error => this.handleError(error)
            );
    }

    delete() {
        this.service.deleteComponent().subscribe(data => this.handleDelete(), error => this.handleError(error));
    }

    handleDelete() {
        this.notify.success('Successfully deleted ' + this.selectedComponentId);
        this.router.navigate(['/' + this.selectedResource.toLowerCase() + 's']);
    }

    handleError(error: any) {
        this.notify.error(error.toString(), 'Error');
    }

    ngOnDestroy(): void {
        this.routeSub.unsubscribe();
    }
}
