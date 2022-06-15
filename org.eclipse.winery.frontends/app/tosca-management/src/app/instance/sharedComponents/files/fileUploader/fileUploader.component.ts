/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FileItem, FileUploader } from 'ng2-file-upload';
import { Router } from '@angular/router';
import { InstanceService } from '../../../instance.service';

@Component({
    selector: 'winery-file-uploader',
    templateUrl: 'fileUploader.component.html',
    styleUrls: ['fileUploader.component.css']
})
export class FileUploaderComponent implements OnInit {

    @Input() currentPath: string;
    @Output() updateRequested = new EventEmitter();

    uploader: FileUploader;
    hasDropZoneOver: boolean;
    url: string;
    math = Math;

    constructor(private iService: InstanceService, private router: Router) {
        this.hasDropZoneOver = false;
        this.url = this.iService.path + this.router.url.replace(this.iService.toscaComponent.path, '');
    }

    ngOnInit() {
        this.uploader = new FileUploader({
            url: this.url,
            method: 'PUT'
        });
        this.uploader.onAfterAddingFile = (file) => {
            file.withCredentials = false;
        };
        this.uploader.onBuildItemForm = (fileItem, form) => {
            form.append('path', fileItem.formData.path);
            return { fileItem, form };
        };
        this.uploader.onSuccessItem = (item, response, status, headers) => this.updateRequested.emit();
    }

    public fileOverBase(e: any): void {
        this.hasDropZoneOver = e;
    }

    public uploadSingle(fileItem: FileItem) {
        this.setPath(fileItem);
        this.uploader.uploadItem(fileItem);
    }

    public uploadThemAll() {
        this.uploader.queue.forEach((item) => {
            if (!(item.isUploaded || item.isUploading)) {
                this.setPath(item);
            }
        });
        this.uploader.uploadAll();
    }

    public setPath(fileItem: FileItem) {
        fileItem.formData = {
            path: this.currentPath
        };
    }
}
