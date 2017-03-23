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
import { NamespaceSelectorService } from '../../namespaceSelector/namespaceSelector.service';
import { NamespacesService } from './namespaces.service';
import { NotificationService } from '../../notificationModule/notification.service';
import { ValidatorObject } from '../../validators/duplicateValidator.directive';
import { isNullOrUndefined } from 'util';
import { NamespaceWithPrefix } from '../../interfaces/namespaceWithPrefix';
import { Response } from '@angular/http';

@Component({
    selector: 'winery-instance-namespaces',
    templateUrl: 'namespaces.component.html',
    providers: [NamespaceSelectorService, NamespacesService],
})
export class NamespacesComponent implements OnInit {

    loading = true;
    adminNamespaces: Array<any> = [];
    newNamespace: any = {namespace: '', prefix: ''};
    validatorObjectPrefix: ValidatorObject;
    validatorObjectNamespace: ValidatorObject;
    itemToDelete: NamespaceWithPrefix = null;
    columns = [
        {title: 'Prefix', name: 'prefix'},
        {title: 'Namespace', name: 'namespace'}
    ];
    elementToRemove: any;

    @ViewChild('confirmDeleteModal') deleteNamespaceModal: any;
    @ViewChild('addModal') addNamespaceModal: any;

    constructor(private service: NamespacesService,
                private notify: NotificationService) {
    }

    getNamespaces() {
        this.service.getAllNamespaces().subscribe(
            data => {
                this.adminNamespaces = data;
                this.validatorObjectNamespace = new ValidatorObject(this.adminNamespaces, 'namespace');
                this.validatorObjectPrefix = new ValidatorObject(this.adminNamespaces, 'prefix');
                this.loading = false;
            },
            error => this.notify.error(error.toString())
        );
    }

    ngOnInit() {
        this.getNamespaces();
    }

    addNamespace(namespace: string, prefix: string) {
        this.adminNamespaces.push({
            namespace: namespace,
            prefix: prefix
        });
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
            this.deleteNamespaceModal.show();
        }
    }

    /**
     * handler for clicks on the add button
     */
    onAddClick() {
        this.addNamespaceModal.show();
    }

    deleteNamespace() {
        this.deleteNamespaceModal.hide();
        this.deleteItemFromPropertyDefinitionKvList(this.elementToRemove);
        this.elementToRemove = null;
    }

    save() {
        this.service.postNamespaces(this.adminNamespaces).subscribe(
            data => this.handleSave(data),
            error => this.handleError(error)
        );
    }

    /**
     * Deletes a property from the table and model.
     * @param itemToDelete
     */
    private deleteItemFromPropertyDefinitionKvList(itemToDelete: NamespaceWithPrefix): void {
        let list = this.adminNamespaces;
        for (let i = 0; i < list.length; i++) {
            if (list[i].namespace === itemToDelete.namespace) {
                list.splice(i, 1);
            }
        }
    }

    private handleSave(data: Response) {
        this.handleSuccess();
        this.getNamespaces();
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
