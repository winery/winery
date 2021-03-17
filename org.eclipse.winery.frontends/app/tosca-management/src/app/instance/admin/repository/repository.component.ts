/*******************************************************************************
 * Copyright (c) 2017-2019 Contributors to the Eclipse Foundation
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
import { RepositoryService } from './repository.service';
import { Repository } from './repository';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { backendBaseURL } from '../../../configuration';
import { ModalDirective } from 'ngx-bootstrap';
import { HttpErrorResponse } from '@angular/common/http';
import { WineryValidatorObject } from '../../../wineryValidators/wineryDuplicateValidator.directive';
import { WineryRepositoryConfigurationService } from '../../../wineryFeatureToggleModule/WineryRepositoryConfiguration.service';

@Component({
    selector: 'winery-instance-repository',
    templateUrl: 'repository.component.html',
    providers: [RepositoryService],
})
export class RepositoryComponent implements OnInit {

    loading = false;
    repositories: Array<Repository> = [];
    newRepository: Repository = new Repository();
    validatorObjectName: WineryValidatorObject;
    validatorObjectUrl: WineryValidatorObject;
    validatorObjectBranch: WineryValidatorObject;
    columns = [
        { title: 'Name', name: 'name' },
        { title: 'Git Repository', name: 'url' },
        { title: 'Branch', name: 'branch' }
    ];
    elementToRemove: any;
    cloning = false;

    @ViewChild('uploaderModal') uploaderModal: ModalDirective;
    @ViewChild('addModal') addModal: ModalDirective;
    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;
    path: string;

    constructor(private service: RepositoryService,
                private notify: WineryNotificationService,
                public configurationService: WineryRepositoryConfigurationService) {
    }

    getRepositories() {
        this.service.getAllRepositories().subscribe(
            (data: Repository[]) => {
                this.repositories = data;
                this.validatorObjectName = new WineryValidatorObject(this.repositories, 'name');
                this.validatorObjectUrl = new WineryValidatorObject(this.repositories, 'url');
                this.validatorObjectBranch = new WineryValidatorObject(this.repositories, 'branch');
            },
            (error: HttpErrorResponse) => this.notify.error(error.toString())
        );
    }

    ngOnInit() {
        this.path = backendBaseURL + this.service.path + '/';
        this.getRepositories();
    }

    addRepository() {
        this.repositories.push(this.newRepository);
        this.save();
    }

    onAddClick() {
        this.newRepository = new Repository(null, null, null);
        this.validatorObjectName.isActive = true;
        this.addModal.show();
    }

    onRemoveClick(data: any) {
        if (!data) {
            return;
        } else {
            this.elementToRemove = data;
            this.confirmDeleteModal.show();
        }
    }

    save() {
        this.cloning = true;
        this.service.postRepositories(this.repositories).subscribe(
            () => this.handleSave(),
            (error: HttpErrorResponse) => this.handleError(error)
        );
    }

    deleteRepository(repository: Repository) {
        this.confirmDeleteModal.hide();
        this.service.deleteRepository(repository).subscribe(
            () => this.handleRemove(),
            (error: HttpErrorResponse) => this.handleError(error)
        );
    }

    private deleteItem(itemToDelete: Repository): void {
        const list = this.repositories;
        for (let i = 0; i < list.length; i++) {
            if (list[i].name === itemToDelete.name) {
                list.splice(i, 1);
            }
        }
    }

    private handleSave() {
        this.cloning = false;
        this.handleSuccess('Saved changes on server');
        this.getRepositories();
    }

    private handleRemove() {
        this.deleteItem(this.elementToRemove);
        this.elementToRemove = null;
        this.save();
        this.getRepositories();
    }

    clearRepository() {
        this.loading = true;
        this.service.clearRepository().subscribe(
            () => this.handleSuccess('Repository cleared'),
            (error: HttpErrorResponse) => this.handleError(error)
        );
    }

    handleSuccess(message: string) {
        this.loading = false;
        this.notify.success(message);
    }

    handleError(error: HttpErrorResponse) {
        this.notify.error(error.message, 'Error');
    }

    touchAllDefinitions() {
        this.loading = true;
        this.service.touchAllDefinitions().subscribe(() => {
            this.loading = false;
            this.notify.success('Touch all definitions completed');
        });
    }
}
