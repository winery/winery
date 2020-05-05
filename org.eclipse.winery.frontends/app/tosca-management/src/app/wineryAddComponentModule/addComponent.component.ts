/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 ********************************************************************************/
import { ChangeDetectorRef, Component, Input, ViewChild } from '@angular/core';
import { SectionService } from '../section/section.service';
import { SelectData } from '../model/selectData';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { ToscaTypes } from '../model/enums';
import { Router } from '@angular/router';
import { Utils } from '../wineryUtils/utils';
import { SectionData } from '../section/sectionData';
import { ModalDirective, TooltipConfig } from 'ngx-bootstrap';
import { InheritanceService } from '../instance/sharedComponents/inheritance/inheritance.service';
import { WineryVersion } from '../model/wineryVersion';
import { AddComponentValidation } from './addComponentValidation';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ExistService } from '../wineryUtils/existService';
import { backendBaseURL } from '../configuration';
import { WineryAddComponentDataComponent } from '../wineryAddComponentDataModule/addComponentData.component';

@Component({
    selector: 'winery-add-component',
    templateUrl: 'addComponent.component.html',
    providers: [
        SectionService,
        InheritanceService,
    ]
})

export class WineryAddComponent {

    loading: boolean;

    @Input() toscaType: ToscaTypes;
    @Input() componentData: SectionData[];
    @Input() namespace: string;
    @Input() inheritFrom: string;

    addModalType: string;
    typeRequired = false;
    hideHelp: boolean;
    storage: Storage = localStorage;

    newComponentNamespace: string;
    newComponentName: string;
    newComponentFinalName: string;
    newComponentSelectedType: SelectData = new SelectData();
    newComponentVersion: WineryVersion = new WineryVersion('', 1, 1);

    validation: AddComponentValidation;

    types: SelectData[];

    @ViewChild('addModal') addModal: ModalDirective;
    @ViewChild('addComponentData') addComponentData: WineryAddComponentDataComponent;
    useStartNamespace = true;

    private readonly storageKey = 'hideVersionHelp';
    collapseVersioning: boolean;
    valid: boolean;

    constructor(private sectionService: SectionService,
                private existService: ExistService,
                private inheritanceService: InheritanceService,
                private change: ChangeDetectorRef,
                private notify: WineryNotificationService,
                private router: Router) {
        this.hideHelp = this.storage.getItem(this.storageKey) === 'true';
    }

    onAdd(componentType?: SelectData) {
        const typesUrl = Utils.getTypeOfTemplateOrImplementation(this.toscaType);
        this.addModalType = Utils.getToscaTypeNameFromToscaType(this.toscaType);
        this.useStartNamespace = !(this.namespace && this.namespace.length > 0);

        this.sectionService.setPath(this.toscaType);

        if (componentType) {
            this.typeRequired = true;
            this.types = [componentType];
        }

        if (typesUrl && !componentType) {
            this.loading = true;
            this.typeRequired = true;
            this.sectionService.getSectionData('/' + typesUrl + '?grouped=angularSelect')
                .subscribe(
                    data => this.handleTypes(data),
                    error => this.handleError(error)
                );
        } else {
            this.typeRequired = false;
        }

        if (!this.componentData) {
            this.loading = true;
            this.sectionService.getSectionData('/' + this.toscaType)
                .subscribe(
                    data => this.handleComponentData(data),
                    error => this.handleError(error)
                );
        }
        if (!this.loading) {
            this.showModal();
        }
    }

    addComponent() {
        this.loading = true;
        const compType = this.newComponentSelectedType ? this.newComponentSelectedType.id : null;

        this.newComponentVersion.wineryVersion = 1;
        this.newComponentVersion.workInProgressVersion = 1;

        this.sectionService.createComponent(this.newComponentFinalName, this.newComponentNamespace, compType)
            .subscribe(
                data => this.handleSaveSuccess(data),
                error => this.handleError(error)
            );
    }

    typeSelected(event: SelectData) {
        this.newComponentSelectedType = event;
    }

    showHelp() {
        if (this.hideHelp) {
            this.storage.removeItem(this.storageKey);
        } else {
            this.storage.setItem(this.storageKey, 'true');
        }
        this.hideHelp = !this.hideHelp;
    }

    private handleTypes(types: SelectData[]): void {
        this.types = types.length > 0 ? types : null;

        if (this.componentData) {
            this.showModal();
        }
    }

    private showModal() {
        this.loading = false;
        this.collapseVersioning = this.toscaType !== ToscaTypes.NodeType;

        this.newComponentVersion = new WineryVersion('', 1, 1);
        this.newComponentName = '';
        this.newComponentFinalName = '';

        // This is needed for the modal to correctly display the selected namespace
        if (!this.useStartNamespace) {
            this.newComponentNamespace = this.namespace;
        } else {
            this.newComponentNamespace = '';
        }
        this.change.detectChanges();

        this.newComponentSelectedType = this.types ?
            this.types[0].children ? this.types[0].children[0] : this.types[0]
            : null;
        if (this.newComponentSelectedType) {
            this.addComponentData.createNoteTypeImplementationName(this.newComponentSelectedType);
        }
        this.addModal.show();
    }

    private handleSaveSuccess(data: HttpResponse<any>) {
        this.newComponentName = this.newComponentName.replace(/\s/g, '-');
        const url = this.toscaType + '/'
            + encodeURIComponent(encodeURIComponent(this.newComponentNamespace)) + '/'
            + this.newComponentFinalName;

        if (!this.inheritFrom) {
            this.notify.success('Successfully saved component ' + this.newComponentName);
            this.router.navigateByUrl(url);
        } else {
            this.inheritanceService.saveInheritanceFromString(backendBaseURL + '/' + url, this.inheritFrom)
                .subscribe(
                    inheritanceData => this.handleSaveSuccess(inheritanceData),
                    error => this.handleError(error)
                );
            this.inheritFrom = null;
        }

        this.addModal.hide();
    }

    private handleError(error: HttpErrorResponse): void {
        this.loading = false;
        this.notify.error(error.message, error.statusText);
    }

    private handleComponentData(data: SectionData[]) {
        this.componentData = data;

        if (!this.typeRequired || this.types) {
            this.showModal();
        }
    }

    setNewComponentName(name: string) {
        this.newComponentFinalName = name;
    }

    setNewComponentNamespace(namespace: string) {
        this.newComponentNamespace = namespace;
    }

    setValid(valid: boolean) {
        this.valid = !valid;
    }
}
