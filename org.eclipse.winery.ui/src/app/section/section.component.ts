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
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { SectionService } from './section.service';
import { SectionData } from './sectionData';
import { NotificationService } from '../notificationModule/notification.service';
import { ValidatorObject } from '../validators/duplicateValidator.directive';
import { isNullOrUndefined } from 'util';
import { SectionResolverData } from '../interfaces/resolverData';

const showAll = 'Show all Items';
const showGrouped = 'Group by Namespace';

@Component({
    selector: 'winery-section-component',
    templateUrl: 'section.component.html',
    styleUrls: [
        'section.component.css'
    ],
    providers: [
        SectionService,
    ]
})
export class SectionComponent implements OnInit, OnDestroy {

    loading = true;
    selectedResource: string;
    routeSub: Subscription;
    filterString = '';
    itemsPerPage = 10;
    showNamespace = 'all';
    changeViewButtonTitle: string = showGrouped;
    componentData: SectionData[];

    newComponentName: string;
    newComponentNamespace: string;
    validatorObject: ValidatorObject;

    fileOver = false;

    @ViewChild('addModal') addModal: any;
    @ViewChild('addComponentForm') addComponentForm: any;
    @ViewChild('addCsarModal') addCsarModal: any;

    constructor(private route: ActivatedRoute,
                private router: Router,
                private service: SectionService,
                private notify: NotificationService) {
    }

    /**
     * @override
     *
     * Subscribe to the url on initialisation in order to get the corresponding resource type.
     */
    ngOnInit(): void {
        this.loading = true;
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

    onChangeView() {
        if (this.showNamespace === 'group') {
            this.changeViewButtonTitle = showGrouped;
            this.showNamespace = 'all';
        } else {
            this.changeViewButtonTitle = showAll;
            this.showNamespace = 'group';
        }
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

    uploadFile(event?: any) {
        if (!isNullOrUndefined(event) && isNullOrUndefined(this.service.uploader.queue[0])) {
            this.fileOver = event;
        } else {
            this.fileOver = false;
            this.loading = true;
            this.addCsarModal.hide();
            this.service.uploader.queue[0].upload();
            this.service.uploader.onCompleteItem = (item: any, response: string, status: number, headers: any) => {
                this.loading = false;
                this.service.uploader.clearQueue();

                if (status === 204) {
                    this.notify.success('Successfully saved component');
                } else {
                    this.notify.error('Error while uploading CSAR file');
                }

                return { item, response, status, headers };
            };
        }
    }

    showSpecificNamespaceOnly(): boolean {
        return !(this.showNamespace === 'group' || this.showNamespace === 'all');
    }

    private getComponentData(data: any) {
        let resolved: SectionResolverData = data['resolveData'];

        this.selectedResource = resolved.section;
        this.showNamespace = resolved.namespace !== 'undefined' ? resolved.namespace : this.showNamespace;

        this.service.setPath(resolved.path);
        this.service.getSectionData()
            .subscribe(
                res => this.handleData(res),
                error => this.handleError(error)
            );
    }

    private handleData(resources: SectionData[]) {
        this.loading = false;
        this.componentData = resources;

        if (!this.showSpecificNamespaceOnly() && (this.componentData.length > 50)) {
            this.showNamespace = 'group';
            this.changeViewButtonTitle = showAll;
        } else if (!this.showSpecificNamespaceOnly()) {
            this.showNamespace = 'all';
            this.changeViewButtonTitle = showGrouped;
        }
    }

    private handleSaveSuccess() {
        this.notify.success('Successfully saved component ' + this.newComponentName);
        // redirect to this new component
        this.router.navigateByUrl('/'
            + this.selectedResource.toLowerCase() + 's/'
            + encodeURIComponent(encodeURIComponent(this.newComponentNamespace)) + '/'
            + this.newComponentName);
    }

    private handleError(error: any): void {
        this.loading = false;
        this.notify.error(error.toString());
    }
}
