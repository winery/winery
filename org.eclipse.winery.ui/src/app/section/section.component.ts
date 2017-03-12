/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */

import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { SectionService } from './section.service';
import { SectionData } from './sectionData';
import { NotificationService } from '../notificationModule/notificationservice';
import { ValidatorObject } from '../validators/duplicateValidator.directive';

@Component({
    selector: 'winery-section-component',
    templateUrl: 'section.component.html',
    providers: [
        SectionService,
    ]
})
export class SectionComponent implements OnInit, OnDestroy {

    loading: boolean = true;
    selectedResource: string;
    routeSub: Subscription;
    filterString: string = '';
    filteredComponents: SectionData[];
    itemsPerPage: number = 10;

    newComponentName: string;
    newComponentNamespace: string;

    validatorObject: ValidatorObject;

    @ViewChild('addModal') addModal: any;
    @ViewChild('addComponentForm') addComponentForm: any;

    private componentData: SectionData[];

    constructor(private route: ActivatedRoute,
                private service: SectionService,
                private notify: NotificationService) {
    }

    /**
     * @override
     *
     * Subscribe to the url on initialisation in order to get the corresponding resource type.
     */
    ngOnInit(): void {
        this.routeSub = this.route
            .data
            .subscribe(
                data => this.getComponentData(data),
                error => this.handleError(error)
            );
    }

    ngOnDestroy(): void {
        this.routeSub.unsubscribe();
    }

    filter() {
        this.filteredComponents = this.componentData.filter((value: SectionData, index: number, array: SectionData[]) => {
            const containsId = value.id.toLowerCase().includes(this.filterString.toLowerCase());
            const containsNamespace = value.namespace.toLowerCase().includes(this.filterString.toLowerCase());
            return containsId || containsNamespace;
        });
    }

    onAdd() {
        this.validatorObject = new ValidatorObject(this.componentData, 'id');
        this.addComponentForm.reset();
        this.newComponentNamespace = '';
        this.addModal.show();
    }

    addComponent() {
        this.service.createComponent(this.newComponentName, this.newComponentNamespace)
            .subscribe(
                data => this.handleSaveSuccess(),
                error => this.handleError(error)
            );
    }

    private getComponentData(data: any) {
        let resolved = data['resolveData'];
        this.selectedResource = resolved.section;
        this.service.getSectionData(resolved.path)
            .subscribe(
                res => this.handleData(res),
                error => this.handleError(error)
            );
    }

    private handleData(resources: SectionData[]) {
        this.componentData = this.filteredComponents = resources;
        this.loading = false;
    }

    private handleSaveSuccess() {
        this.notify.success('Successfully saved component ' + this.newComponentName);
        // redirect to this new component
    }

    private handleError(error: any): void {
        this.loading = false;
        this.notify.error(error.toString());
    }
}
