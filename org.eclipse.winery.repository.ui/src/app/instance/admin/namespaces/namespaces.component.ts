/*******************************************************************************
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
 *******************************************************************************/
import { Component, OnInit, ViewChild } from '@angular/core';
import { WineryNamespaceSelectorService } from '../../../wineryNamespaceSelector/wineryNamespaceSelector.service';
import { NamespacesService } from './namespaces.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { WineryValidatorObject } from '../../../wineryValidators/wineryDuplicateValidator.directive';
import { isNullOrUndefined } from 'util';
import { NamespaceProperties } from '../../../model/namespaceProperties';
import { ModalDirective } from 'ngx-bootstrap';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'winery-instance-namespaces',
    templateUrl: 'namespaces.component.html',
    providers: [WineryNamespaceSelectorService, NamespacesService],
})
export class NamespacesComponent implements OnInit {

    loading = true;
    modalTitle: string;
    addButtonTitle: string;
    adminNamespaces: Array<NamespaceProperties> = [];
    newNamespace: NamespaceProperties;
    validatorObjectPrefix: WineryValidatorObject;
    validatorObjectNamespace: WineryValidatorObject;

    columns = [
        { title: 'Prefix', name: 'prefix' },
        { title: 'Namespace', name: 'namespace' },
        { title: 'Description', name: 'readableName' },
        { title: 'Repository URL', name: 'upstreamRepository'},
        { title: 'Pattern NS', name: 'patternCollection' },
        { title: 'Secure NS', name: 'secureCollection' }
    ];
    elementToRemove: any;

    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;
    @ViewChild('addModal') addModal: ModalDirective;

    constructor(private service: NamespacesService,
                private notify: WineryNotificationService) {
    }

    getNamespaces() {
        this.service.getAllNamespaces().subscribe(
            data => {
                this.adminNamespaces = data;
                this.validatorObjectNamespace = new WineryValidatorObject(this.adminNamespaces, 'namespace');
                this.validatorObjectPrefix = new WineryValidatorObject(this.adminNamespaces, 'prefix');
                this.loading = false;
            },
            error => this.notify.error(error.toString())
        );
    }

    ngOnInit() {
        this.getNamespaces();
    }

    addNamespace() {
        this.adminNamespaces.push(this.newNamespace);
        this.save();
    }

    /**
     * handler for clicks on remove button
     * @param data
     */
    onRemoveClick(data: any) {
        if (isNullOrUndefined(data)) {
            return;
        } else {
            this.elementToRemove = data;
            this.confirmDeleteModal.show();
        }
    }

    /**
     * handler for clicks on the add button
     */
    onAddClick() {
        this.newNamespace = new NamespaceProperties(null, null, '', '', false, false);
        this.validatorObjectPrefix.isActive = true;
        this.modalTitle = 'Add new Namespace';
        this.addButtonTitle = 'Add';
        this.addModal.show();
    }

    onEdit(data: NamespaceProperties) {
        this.modalTitle = 'Edit Namespace';
        this.addButtonTitle = 'Update';
        // create a copy to enable "undo" by clicking cancel
        this.newNamespace = new NamespaceProperties(
            data.namespace,
            data.prefix,
            data.readableName,
            data.upstreamRepository,
            data.patternCollection,
            data.secureCollection
        );
        this.validatorObjectPrefix.isActive = false;
        this.addModal.show();
    }

    deleteNamespace() {
        this.confirmDeleteModal.hide();
        this.deleteItemFromPropertyDefinitionKvList(this.elementToRemove);
        this.elementToRemove = null;
        this.save();
    }

    save() {
        this.service.postNamespaces(this.adminNamespaces).subscribe(
            () => this.handleSave(),
            error => this.handleError(error)
        );
    }

    /**
     * Deletes a property from the table and model.
     * @param itemToDelete
     */
    private deleteItemFromPropertyDefinitionKvList(itemToDelete: NamespaceProperties): void {
        const list = this.adminNamespaces;
        for (let i = 0; i < list.length; i++) {
            if (list[i].namespace === itemToDelete.namespace) {
                list.splice(i, 1);
            }
        }
    }

    private handleSave() {
        this.handleSuccess();
        this.getNamespaces();
    }

    /**
     * Sets loading to false and shows error notification.
     *
     * @param error
     */
    private handleError(error: HttpErrorResponse): void {
        this.notify.error(error.message, 'Error');
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
