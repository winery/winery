/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Balzer, Nicole Keppler - initial API and implementation
 */
import { Component, OnInit, ViewChild } from '@angular/core';
import { TagService } from './tag.service';
import { TagsAPIData } from './tagsAPIData';
import { NotificationService } from '../../notificationModule/notificationservice';
import { isNullOrUndefined } from 'util';
import { ValidatorObject } from '../../validators/duplicateValidator.directive';

@Component({
    selector: 'winery-instance-tag',
    templateUrl: 'tag.component.html',
    providers: [TagService, NotificationService]
})
export class TagComponent implements OnInit {
    loading: boolean = false;
    tagsData: TagsAPIData[] = [];
    newTag: TagsAPIData = new TagsAPIData();
    selectedCell: any;
    validatorObject: ValidatorObject;

    columns: Array<any> = [
        {title: 'id', name: 'id', sort: true},
        {title: 'name', name: 'name', sort: true},
        {title: 'value', name: 'value', sort: true},
    ];
    @ViewChild('confirmDeleteModal') deleteTagModal: any;
    @ViewChild('addModal') addTagModal: any;

    public constructor(private service: TagService,
                       private noteService: NotificationService) {
    }

    ngOnInit(): void {
        this.getTagsData();
    }

    onCellSelected(event: any) {
        this.selectedCell = event.row;
    }

    onRemoveClick(event: any) {
        if (isNullOrUndefined(this.selectedCell)) {
            this.noteService.error('no cell selected!');
        } else {
          this.deleteTagModal.show();
        }
    }

    onAddClick() {
        this.validatorObject = new ValidatorObject(this.tagsData, 'name');
        this.newTag = new TagsAPIData();
        this.addTagModal.show();
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

    private handleError(error: any): void {
        this.loading = false;
        this.noteService.error('Action caused an error:\n', error);
    }
}
