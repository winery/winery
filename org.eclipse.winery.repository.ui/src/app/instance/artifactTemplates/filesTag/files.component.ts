/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */
import { Component, OnInit, ViewChild } from '@angular/core';
import { FilesApiData, FilesService } from './files.service.';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { backendBaseURL, hostURL } from '../../../configuration';
import { InstanceService } from '../../instance.service';

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
    baseUrl = hostURL;
    filesPath: string;

    @ViewChild('removeElementModal') removeElementModal: any;
    fileToRemove: FilesApiData;

    constructor(private service: FilesService, private sharedData: InstanceService, private notify: WineryNotificationService) {
        this.filesPath = backendBaseURL + this.sharedData.path + '/files/zip';
    }

    ngOnInit() {
        this.loadFiles();
        this.uploadUrl = this.service.uploadUrl;
    }

    loadFiles() {
        this.service.getFiles()
            .subscribe(
                data => this.filesList = data.files,
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

    private handleDelete() {
        this.notify.success('Successfully deleted ' + this.fileToRemove.name);
        this.fileToRemove = null;
        this.loadFiles();
    }

    private handleError(error: any) {
        this.loading = false;
        this.notify.error(error);
    }
}
