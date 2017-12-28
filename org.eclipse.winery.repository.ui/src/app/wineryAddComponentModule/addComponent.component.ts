/********************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
import { ChangeDetectorRef, Component, Input, OnInit, ViewChild } from '@angular/core';
import { SectionService } from '../section/section.service';
import { SelectData } from '../wineryInterfaces/selectData';
import { WineryValidatorObject } from '../wineryValidators/wineryDuplicateValidator.directive';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { ToscaTypes } from '../wineryInterfaces/enums';
import { Router } from '@angular/router';
import { Response } from '@angular/http';
import { NgForm } from '@angular/forms';
import { Utils } from '../wineryUtils/utils';
import { isNullOrUndefined } from 'util';
import { SectionData } from '../section/sectionData';
import { ModalDirective } from 'ngx-bootstrap';
import { WineryNamespaceSelectorComponent } from '../wineryNamespaceSelector/wineryNamespaceSelector.component';
import { InheritanceService } from '../instance/sharedComponents/inheritance/inheritance.service';

@Component({
    selector: 'winery-add-component',
    templateUrl: 'addComponent.component.html',
    providers: [
        SectionService,
        InheritanceService,
    ]
})

export class WineryAddComponent implements OnInit {

    loading: boolean;

    @Input() toscaType: ToscaTypes;
    @Input() componentData: SectionData[];
    @Input() namespace: string;
    @Input() inheritFrom: string;

    addModalType: string;
    newComponentNamespace: string;
    newComponentName: string;
    newComponentSelectedType: SelectData = new SelectData();
    validatorObject: WineryValidatorObject;
    types: SelectData[];

    @ViewChild('addComponentForm') addComponentForm: NgForm;
    @ViewChild('addModal') addModal: ModalDirective;
    @ViewChild('namespaceInput') namespaceInput: WineryNamespaceSelectorComponent;
    useStartNamespace = true;

    constructor(private sectionService: SectionService,
                private inheritanceService: InheritanceService,
                private change: ChangeDetectorRef,
                private notify: WineryNotificationService,
                private router: Router) {
    }

    ngOnInit() {
    }

    onAdd() {
        const typesUrl = Utils.getTypeOfTemplateOrImplementation(this.toscaType);
        this.addModalType = Utils.getToscaTypeNameFromToscaType(this.toscaType);
        this.useStartNamespace = !(!isNullOrUndefined(this.namespace) && this.namespace.length > 0);

        this.sectionService.setPath(this.toscaType);

        if (!isNullOrUndefined(typesUrl)) {
            this.loading = true;
            this.sectionService.getSectionData('/' + typesUrl + '?grouped=angularSelect')
                .subscribe(
                    data => this.handleTypes(data),
                    error => this.handleError(error)
                );
        } else {
            this.showModal();
        }
    }

    addComponent() {
        this.loading = true;
        const compType = this.newComponentSelectedType ? this.newComponentSelectedType.id : null;
        this.sectionService.createComponent(this.newComponentName, this.newComponentNamespace, compType)
            .subscribe(
                data => this.handleSaveSuccess(),
                error => this.handleError(error)
            );
    }

    typeSelected(event: SelectData) {
        this.newComponentSelectedType = event;
    }

    private handleTypes(types: SelectData[]): void {
        this.loading = false;
        this.types = types.length > 0 ? types : null;
        this.showModal();
    }

    private showModal() {
        this.validatorObject = new WineryValidatorObject(this.componentData, 'id');

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

    private handleSaveSuccess() {
        this.newComponentName = this.newComponentName.replace(/\s/g, '-');
        const url = '/' + this.toscaType + '/'
            + encodeURIComponent(encodeURIComponent(this.newComponentNamespace)) + '/'
            + this.newComponentName;

        if (isNullOrUndefined(this.inheritFrom)) {
            this.notify.success('Successfully saved component ' + this.newComponentName);
            this.router.navigateByUrl(url);
        } else {
            this.inheritanceService.saveInheritanceFromString(url, this.inheritFrom)
                .subscribe(() => this.handleSaveSuccess(), error => this.handleError(error));
            this.inheritFrom = null;
        }
    }

    private handleError(error: Response): void {
        this.loading = false;
        this.notify.error(error.toString());
    }
}
