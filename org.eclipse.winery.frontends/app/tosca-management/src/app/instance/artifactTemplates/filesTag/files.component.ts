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
import { FilesApiData, FilesService } from './files.service.';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { InstanceService } from '../../instance.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    templateUrl: 'files.component.html',
    styleUrls: [
        'files.component.css'
    ],
    providers: [
        FilesService
    ]
})
export class FilesComponent implements OnInit {

    loading = true;
    uploadUrl: string;
    filesList: FilesApiData[];
    filesPath: string;

    @ViewChild('removeElementModal') removeElementModal: any;
    fileToRemove: FilesApiData;

    constructor(private service: FilesService, public sharedData: InstanceService, private notify: WineryNotificationService) {
        this.filesPath = this.sharedData.path + '/files/zip';
    }

    ngOnInit() {
        this.loadFiles();
        this.uploadUrl = this.service.uploadUrl;
    }

    loadFiles() {
        this.service.getFiles()
            .subscribe(
                data => this.handleLoadFiles(data.files, data.paths),
                error => this.handleError(error)
            );
    }

    deleteFile(file: FilesApiData) {
        this.fileToRemove = file;
        this.removeElementModal.show();
    }

    onRemoveElement() {
        this.loading = true;
        this.service.delete(this.fileToRemove)
            .subscribe(
                data => this.handleDelete(),
                error => this.handleError(error)
            );
    }

    private handleLoadFiles(files: FilesApiData[], paths: string[]) {
        for (let i = 0; i < paths.length; i++) {
            files[i].subDirectory = paths[i];
        }
        this.filesList = files;
        this.loading = false;
    }

    private handleDelete() {
        this.notify.success('Successfully deleted ' + this.fileToRemove.name);
        this.fileToRemove = null;
        this.loadFiles();
    }

    private handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.notify.error(error.message);
    }
}
