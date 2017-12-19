/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { SectionResolverData } from '../wineryInterfaces/resolverData';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { WineryValidatorObject } from '../wineryValidators/wineryDuplicateValidator.directive';
import { SectionService } from './section.service';
import { SectionData } from './sectionData';
import { backendBaseURL } from '../configuration';
import { ModalDirective } from 'ngx-bootstrap';
import { Response } from '@angular/http';
import { ToscaTypes } from '../wineryInterfaces/enums';
import { WineryUploaderComponent } from '../wineryUploader/wineryUploader.component';
import { WineryAddComponent } from '../wineryAddComponentModule/addComponent.component';

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
    toscaType: ToscaTypes;
    toscaTypes = ToscaTypes;
    routeSub: Subscription;
    filterString = '';
    itemsPerPage = 10;
    currentPage = 1;
    showNamespace = 'all';
    changeViewButtonTitle = showGrouped;
    componentData: SectionData[];
    elementToRemove: SectionData;
    overwriteValue = false;

    importXsdSchemaType: string;

    newComponentNamespace: string;
    validatorObject: WineryValidatorObject;

    fileUploadUrl = backendBaseURL + '/';

    @ViewChild('addModal') addModal: WineryAddComponent;
    @ViewChild('addCsarModal') addCsarModal: ModalDirective;
    @ViewChild('removeElementModal') removeElementModal: ModalDirective;
    @ViewChild('addYamlModal') addYamlModal: ModalDirective;
    @ViewChild('fileUploader') fileUploader: WineryUploaderComponent;

    constructor(private route: ActivatedRoute,
                private change: ChangeDetectorRef,
                private router: Router,
                private service: SectionService,
                private notify: WineryNotificationService) {
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
                data => this.handleResolverData(data),
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

        localStorage.setItem(this.toscaType + '_showNamespace', this.showNamespace);
    }

    onAdd() {
        this.addModal.namespace = (this.showNamespace !== 'all' && this.showNamespace !== 'group') ? this.showNamespace : '';
        this.addModal.onAdd();
    }

    showSpecificNamespaceOnly(): boolean {
        return !(this.showNamespace === 'group' || this.showNamespace === 'all');
    }

    getSectionsData() {
        let url = '/' + this.toscaType;
        if (this.toscaType === ToscaTypes.Imports) {
            url += '/' + encodeURIComponent(encodeURIComponent(this.importXsdSchemaType));
        }
        this.service.getSectionData(url)
            .subscribe(
                res => this.handleData(res),
                error => this.handleError(error)
            );
    }

    onPageChange(page: number) {
        this.currentPage = page;
    }

    onRemoveElement() {
    }

    overwriteValueChanged() {

        this.fileUploader.getUploader().setOptions({
            url: this.fileUploadUrl,
            additionalParameter: { 'overwrite': this.overwriteValue }
        });
    }

    /**
     * Handle the resolved data.
     * @param data needs to be of type any because there is no specific type specified by angular
     */
    private handleResolverData(data: any) {
        const resolved: SectionResolverData = data.resolveData;

        this.toscaType = resolved.section;
        this.importXsdSchemaType = resolved.xsdSchemaType;

        const storedNamespace = localStorage.getItem(this.toscaType + '_showNamespace') !== null ?
            localStorage.getItem(this.toscaType + '_showNamespace') : 'all';
        this.showNamespace = resolved.namespace ? resolved.namespace : storedNamespace;

        this.service.setPath(resolved.path);
        this.getSectionsData();
    }

    private handleData(resources: SectionData[]) {
        this.componentData = resources;

        if (!this.showSpecificNamespaceOnly() && (this.componentData.length > 50)) {
            this.showNamespace = 'group';
            this.changeViewButtonTitle = showAll;
        } else if (!this.showSpecificNamespaceOnly()) {
            if (this.showNamespace === 'group') {
                this.changeViewButtonTitle = showAll;
            } else {
                this.changeViewButtonTitle = showGrouped;
            }
        } else {
            this.changeViewButtonTitle = showGrouped;
        }
        this.loading = false;
    }

    private handleError(error: Response): void {
        this.loading = false;
        this.notify.error(error.toString());
    }

}
