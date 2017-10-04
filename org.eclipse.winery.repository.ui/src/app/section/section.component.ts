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
import { SelectData } from '../wineryInterfaces/selectData';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { WineryValidatorObject } from '../wineryValidators/wineryDuplicateValidator.directive';
import { SectionService } from './section.service';
import { SectionData } from './sectionData';
import { backendBaseURL } from '../configuration';
import { isNullOrUndefined } from 'util';
import { NgForm } from '@angular/forms';
import { ModalDirective } from 'ngx-bootstrap';
import { Response } from '@angular/http';
import { ToscaTypes } from '../wineryInterfaces/enums';
import { Utils } from '../wineryUtils/utils';

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
    types: SelectData[];

    importXsdSchemaType: string;

    newComponentName: string;
    newComponentNamespace: string;
    newComponentSelectedType: SelectData = new SelectData();
    validatorObject: WineryValidatorObject;

    fileUploadUrl = backendBaseURL + '/';

    @ViewChild('addModal') addModal: ModalDirective;
    @ViewChild('removeElementModal') removeElementModal: ModalDirective;
    @ViewChild('addComponentForm') addComponentForm: NgForm;
    @ViewChild('addCsarModal') addCsarModal: ModalDirective;

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
        this.validatorObject = new WineryValidatorObject(this.componentData, 'id');

        // This is needed for the modal to correctly display the selected namespace
        this.newComponentNamespace = '';
        this.change.detectChanges();
        this.addComponentForm.reset();

        this.newComponentNamespace = (this.showNamespace !== 'all' && this.showNamespace !== 'group') ? this.showNamespace : '';
        this.newComponentSelectedType = this.types ? this.types[0].children[0] : null;
        this.addModal.show();
    }

    typeSelected(event: SelectData) {
        this.newComponentSelectedType = event;
    }

    addComponent() {
        const compType = this.newComponentSelectedType ? this.newComponentSelectedType.id : null;
        this.service.createComponent(this.newComponentName, this.newComponentNamespace, compType)
            .subscribe(
                data => this.handleSaveSuccess(),
                error => this.handleError(error)
            );
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
        this.types = null;

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

        const typesUrl = Utils.getTypeOfTemplateOrImplementation(this.toscaType);

        if (!isNullOrUndefined(typesUrl)) {
            this.service.getSectionData('/' + typesUrl + '?grouped=angularSelect')
                .subscribe(
                    data => this.handleTypes(data),
                    error => this.handleError(error)
                );
        } else {
            this.loading = false;
        }
    }

    private handleTypes(types: SelectData[]): void {
        this.loading = false;
        this.types = types.length > 0 ? types : null;
    }

    private handleSaveSuccess() {
        this.newComponentName = this.newComponentName.replace(/\s/g, '_');
        this.notify.success('Successfully saved component ' + this.newComponentName);
        this.router.navigateByUrl('/' + this.toscaType + '/'
            + encodeURIComponent(encodeURIComponent(this.newComponentNamespace)) + '/'
            + this.newComponentName);
    }

    private handleError(error: Response): void {
        this.loading = false;
        this.notify.error(error.toString());
    }
}
