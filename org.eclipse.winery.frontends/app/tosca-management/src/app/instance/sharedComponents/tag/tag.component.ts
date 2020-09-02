/*******************************************************************************
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
 *******************************************************************************/
import { Component, OnInit, ViewChild } from '@angular/core';
import { TagService } from './tag.service';
import { TagsAPIData } from './tagsAPIData';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { isNullOrUndefined } from 'util';
import { WineryValidatorObject } from '../../../wineryValidators/wineryDuplicateValidator.directive';
import { ModalDirective } from 'ngx-bootstrap';
import { InstanceService } from '../../instance.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'winery-instance-tag',
    templateUrl: 'tag.component.html',
    providers: [TagService, WineryNotificationService]
})
export class TagComponent implements OnInit {
    loading = false;
    tagsData: TagsAPIData[] = [];
    newTag: TagsAPIData = new TagsAPIData();
    selectedCell: any;
    validatorObject: WineryValidatorObject;

    columns: Array<any> = [
        { title: 'name', name: 'name', sort: true },
        { title: 'value', name: 'value', sort: true },
    ];
    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;
    @ViewChild('addModal') addModal: ModalDirective;

    public constructor(private service: TagService,
                       private noteService: WineryNotificationService,
                       public sharedData: InstanceService) {
    }

    ngOnInit(): void {
        this.getTagsData();
    }

    onCellSelected(event: TagsAPIData) {
        this.selectedCell = event;
    }

    onRemoveClick() {
        if (isNullOrUndefined(this.selectedCell)) {
            this.noteService.error('no cell selected!');
        } else {
            this.confirmDeleteModal.show();
        }
    }

    onAddClick() {
        this.validatorObject = new WineryValidatorObject(this.tagsData, 'name');
        this.newTag = new TagsAPIData();
        this.addModal.show();
    }

    getTagsData() {
        this.service.getTagsData().subscribe(
            data => this.handleTagsData(data),
            error => this.handleError(error)
        );
    }

    addNewTag() {
        this.service.postTag(this.newTag).subscribe(
            data => this.handleSuccess(data),
            error => this.handleError(error)
        );
    }

    removeConfirmed() {
        this.service.removeTagData(this.selectedCell).subscribe(
            data => this.handleRemoveSuccess(),
            error => this.handleError(error)
        );
    }

    handleSuccess(data: string) {
        this.newTag.id = data;
        this.tagsData.push(this.newTag);
        this.noteService.success('Added new Tag');
    }

    handleRemoveSuccess() {
        this.selectedCell = null;
        this.getTagsData();
        this.noteService.success('Removed Tag');
    }

    private handleTagsData(data: TagsAPIData[]) {
        this.tagsData = data;
        this.loading = false;
    }

    private handleError(error: HttpErrorResponse): void {
        this.loading = false;
        this.noteService.error(error.message);
    }
}
