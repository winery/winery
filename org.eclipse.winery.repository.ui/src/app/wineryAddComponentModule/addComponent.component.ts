/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
import { SelectData } from '../wineryInterfaces/selectData';
import { WineryValidatorObject } from '../wineryValidators/wineryDuplicateValidator.directive';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { ToscaTypes } from '../wineryInterfaces/enums';
import { Router } from '@angular/router';
import { AbstractControl, NgForm, ValidatorFn } from '@angular/forms';
import { Utils } from '../wineryUtils/utils';
import { isNullOrUndefined } from 'util';
import { SectionData } from '../section/sectionData';
import { ModalDirective, TooltipConfig } from 'ngx-bootstrap';
import { WineryNamespaceSelectorComponent } from '../wineryNamespaceSelector/wineryNamespaceSelector.component';
import { InheritanceService } from '../instance/sharedComponents/inheritance/inheritance.service';
import { WineryVersion } from '../wineryInterfaces/wineryVersion';
import { AddComponentValidation } from './addComponentValidation';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ExistService } from '../wineryUtils/existService';

export function getToolTip(): TooltipConfig {
    return Object.assign(new TooltipConfig(), { placement: 'right' });
}

@Component({
    selector: 'winery-add-component',
    templateUrl: 'addComponent.component.html',
    providers: [
        SectionService,
        InheritanceService,
        {
            provide: TooltipConfig,
            useFactory: getToolTip
        }
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

    validatorObject: WineryValidatorObject;
    validation: AddComponentValidation;

    types: SelectData[];

    @ViewChild('addComponentForm') addComponentForm: NgForm;
    @ViewChild('addModal') addModal: ModalDirective;
    @ViewChild('namespaceInput') namespaceInput: WineryNamespaceSelectorComponent;
    useStartNamespace = true;

    private readonly storageKey = 'hideVersionHelp';

    constructor(private sectionService: SectionService,
                private existService: ExistService,
                private inheritanceService: InheritanceService,
                private change: ChangeDetectorRef,
                private notify: WineryNotificationService,
                private router: Router) {
        this.hideHelp = this.storage.getItem(this.storageKey) === 'true';
    }

    onAdd() {
        const typesUrl = Utils.getTypeOfTemplateOrImplementation(this.toscaType);
        this.addModalType = Utils.getToscaTypeNameFromToscaType(this.toscaType);
        this.useStartNamespace = !(!isNullOrUndefined(this.namespace) && this.namespace.length > 0);

        this.sectionService.setPath(this.toscaType);

        if (!isNullOrUndefined(typesUrl)) {
            this.loading = true;
            this.typeRequired = true;
            this.sectionService.getSectionData('/' + typesUrl + '?grouped=angularSelect')
                .subscribe(
                    data => this.handleTypes(data),
                    error => this.handleError(error)
                );
        } else {
            this.typeRequired = false;
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

    validateComponentName(compareObject: WineryValidatorObject): ValidatorFn {
        return (control: AbstractControl): { [key: string]: any } => {
            this.validation = new AddComponentValidation();
            this.newComponentFinalName = this.newComponentName;

            if (this.typeRequired && isNullOrUndefined(this.newComponentSelectedType)) {
                this.validation.noTypeAvailable = true;
                return { noTypeAvailable: true };
            }

            if (!isNullOrUndefined(this.newComponentFinalName) && this.newComponentFinalName.length > 0) {
                this.newComponentFinalName += WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + this.newComponentVersion.toString();
                const duplicate = this.componentData.find((component) => component.name.toLowerCase() === this.newComponentFinalName.toLowerCase());

                if (!isNullOrUndefined(duplicate)) {
                    const namespace = this.newComponentNamespace.endsWith('/') ? this.newComponentNamespace.slice(0, -1) : this.newComponentNamespace;

                    if (duplicate.namespace === namespace) {
                        if (duplicate.name === this.newComponentFinalName) {
                            this.validation.noDuplicatesAllowed = true;
                            return { noDuplicatesAllowed: true };
                        } else {
                            this.validation.differentCaseDuplicateWarning = true;
                        }
                    } else {
                        this.validation.differentNamespaceDuplicateWarning = true;
                    }
                }
            }

            if (this.newComponentVersion.componentVersion) {
                this.validation.noUnderscoresAllowed = this.newComponentVersion.componentVersion.includes('_');
                if (this.validation.noUnderscoresAllowed) {
                    return { noUnderscoresAllowed: true };
                }
            }

            this.validation.noVersionProvidedWarning = isNullOrUndefined(this.newComponentVersion.componentVersion)
                || this.newComponentVersion.componentVersion.length === 0;

            return null;
        };
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
        this.loading = false;
        this.types = types.length > 0 ? types : null;
        this.showModal();
    }

    private showModal() {
        this.newComponentVersion = new WineryVersion('', 1, 1);
        this.newComponentName = '';
        this.newComponentFinalName = '';

        this.validatorObject = new WineryValidatorObject(this.componentData, 'id');
        this.validatorObject.validate = (compareObject: WineryValidatorObject) => this.validateComponentName(compareObject);

        // This is needed for the modal to correctly display the selected namespace
        if (!this.useStartNamespace) {
            this.newComponentNamespace = this.namespace;
        } else {
            this.newComponentNamespace = '';

            if (!isNullOrUndefined(this.addComponentForm)) {
                this.addComponentForm.reset();
            }
        }
        this.change.detectChanges();

        this.newComponentSelectedType = this.types ? this.types[0].children[0] : null;
        this.namespaceInput.writeValue(this.newComponentNamespace);

        this.addModal.show();
    }

    private handleSaveSuccess(data: HttpResponse<any>) {
        this.newComponentName = this.newComponentName.replace(/\s/g, '-');
        const url = '/' + this.toscaType + '/'
            + encodeURIComponent(encodeURIComponent(this.newComponentNamespace)) + '/'
            + this.newComponentFinalName;

        if (isNullOrUndefined(this.inheritFrom)) {
            this.notify.success('Successfully saved component ' + this.newComponentName);
            this.router.navigateByUrl(url);
        } else {
            this.inheritanceService.saveInheritanceFromString(url, this.inheritFrom)
                .subscribe(
                    inheritanceData => this.handleSaveSuccess(inheritanceData),
                    error => this.handleError(error)
                );
            this.inheritFrom = null;
        }
    }

    private handleError(error: HttpErrorResponse): void {
        this.loading = false;
        this.notify.error(error.message, error.statusText);
    }
}
