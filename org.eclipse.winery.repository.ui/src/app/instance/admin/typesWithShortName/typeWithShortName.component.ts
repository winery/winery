/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */
import { Component, OnInit, ViewChild } from '@angular/core';
import { WineryNamespaceSelectorService } from '../../../wineryNamespaceSelector/wineryNamespaceSelector.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { WineryValidatorObject } from '../../../wineryValidators/wineryDuplicateValidator.directive';
import { Response } from '@angular/http';
import { TypeWithShortName, TypeWithShortNameService } from './typeWithShortName.service';
import { ModalDirective } from 'ngx-bootstrap';
import { Router } from '@angular/router';

@Component({
    selector: 'winery-instance-type-with-short-name',
    templateUrl: 'typeWithShortName.component.html',
    providers: [WineryNamespaceSelectorService, TypeWithShortNameService],
})
export class TypeWithShortNameComponent implements OnInit {

    loading = true;
    types: Array<any> = [];
    newTypeWithShortName: TypeWithShortName = new TypeWithShortName();
    validatorObjectShortName: WineryValidatorObject;
    validatorObjectType: WineryValidatorObject;
    columns = [
        { title: 'Short Name', name: 'shortName' },
        { title: 'Long Name', name: 'type' }
    ];
    elementToRemove: TypeWithShortName = null;
    title: string;

    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;
    @ViewChild('addModal') addModal: ModalDirective;

    constructor(private service: TypeWithShortNameService,
                private notify: WineryNotificationService,
                private router: Router) {
    }

    getTypes() {
        this.service.getAllTypes().subscribe(
            data => {
                this.types = data;
                this.validatorObjectType = new WineryValidatorObject(this.types, 'type');
                this.validatorObjectShortName = new WineryValidatorObject(this.types, 'shortName');
                this.loading = false;
            },
            error => this.notify.error(error.toString())
        );
    }

    ngOnInit() {
        if (this.router.url.includes('planlanguages')) {
            this.title = 'Plan Languages';
        } else if (this.router.url.includes('plantypes')) {
            this.title = 'Plan Types';
        } else {
            this.title = 'Constraint Types';
        }

        this.getTypes();
    }

    addType(type: string, shortName: string) {
        this.types.push({ type: type, shortName: shortName });
        this.saveType();
    }

    /**
     * handler for clicks on remove button
     * @param data
     */
    onRemoveClick(data: any) {
        this.notify.warning('Not yet implemented!');
        // future functionality
        // return;
        // if (isNullOrUndefined(data)) {
        //     this.notify.warning('Nothing to remove. Please select a element');
        //     return;
        // } else {
        //     this.elementToRemove = data;
        //     this.confirmDeleteModal.show();
        // }
    }

    /**
     * handler for clicks on the add button
     */
    onAddClick() {
        this.addModal.show();
    }

    deleteType() {
        this.confirmDeleteModal.hide();
        this.deleteItemFromTypesWithShortNameList(this.elementToRemove);
        this.elementToRemove = null;
    }

    saveAll() {
        this.service.postTypes(this.types).subscribe(
            data => this.handleSave(data),
            error => this.handleError(error)
        );
    }

    saveType() {
        this.service.postType(this.newTypeWithShortName).subscribe(
            data => this.handleSave(data),
            error => this.handleError(error)
        );
    }

    /**
     * Deletes a property from the table and model.
     * @param itemToDelete
     */
    private deleteItemFromTypesWithShortNameList(itemToDelete: TypeWithShortName): void {
        const list = this.types;
        for (let i = 0; i < list.length; i++) {
            if (list[i].type === itemToDelete.type) {
                list.splice(i, 1);
            }
        }
    }

    private handleSave(data: Response) {
        this.handleSuccess();
        this.getTypes();
    }

    /**
     * Sets loading to false and shows error notification.
     *
     * @param error
     */
    private handleError(error: any): void {
        this.notify.error(error.toString(), 'Error');
    }

    /**
     * Set loading to false and show success notification.
     *
     */
    private handleSuccess(): void {
        this.loading = false;

        this.notify.success('Saved changes on server', 'Success');

    }
}
