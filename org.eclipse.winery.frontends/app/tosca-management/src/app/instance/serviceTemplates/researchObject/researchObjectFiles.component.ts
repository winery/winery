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
import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { FileOrFolderElement } from '../../../model/fileOrFolderElement';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { ResearchObjectService } from './researchObject.service';

@Component({
    selector: 'winery-research-object-file-handler',
    templateUrl: 'researchObjectFiles.component.html',
    styleUrls: ['researchObjectFiles.component.css']
})

export class ResearchObjectFilesComponent implements OnInit {

    pathToElementsMap: Map<string, FileOrFolderElement[]>;
    loading = true;
    panelOpenState = true;
    STATUS = ['downloading', 'success', 'error'];
    downloaderStatus;


    constructor(public service: ResearchObjectService, private notify: WineryNotificationService, public dialog: MatDialog) {
    }

    ngOnInit() {
        this.getAllDirsAndFiles();
    }

    getAllDirsAndFiles() {
        this.service.getDirsAndFiles().subscribe(
            (data) => {
                this.handleData(data);
            },
            (error) => {
                this.handleError(error);
            }
        );
    }

    handleData(data: Map<string, FileOrFolderElement[]>) {
        this.pathToElementsMap = this.createMap(data);
        this.loading = false;
    }

    createNewFolder(folderPath: string) {
        this.service.createDirectory(folderPath).subscribe(
            (data) => {
                this.getAllDirsAndFiles();
            },
            (error) => {
                this.handleError(error);
            }
        );
    }

    delete(element: string) {
        this.service.delete(element).subscribe(
            (data) => {
                this.getAllDirsAndFiles();
            },
            (error) => {
                this.handleError(error);
            }
        );
    }

    move(element: { oldPath: string, newPath: string }) {
        this.service.move(element.oldPath, element.newPath).subscribe(
            (data) => {
                this.getAllDirsAndFiles();
            },
            (error) => {
                this.handleError(error);
            }
        );
    }

    handleSuccess(message: string) {
        this.notify.success(message);
        this.loading = false;
    }

    handleError(error: HttpErrorResponse) {
        this.notify.error(error.message);
        this.loading = false;
    }

    download(repo: string, branch: string, targetPath: string) {
        this.downloaderStatus = this.STATUS[0];
        repo = repo.replace('https://github.com/', '');
        if (repo.endsWith('/')) {
            repo = repo.slice(0, -1);
        }
        repo = 'https://api.github.com/repos/' + repo + '/zipball/' + branch;
        this.service.move(repo, targetPath).subscribe(
            (data) => {
                this.downloaderStatus = this.STATUS[1];
                this.getAllDirsAndFiles();
            },
            (error) => {
                this.downloaderStatus = this.STATUS[2];
                this.handleError(error);
            });
    }

    correctURL(url: string) {
        return url.startsWith('https://github.com/');
    }

    createMap(data: any): Map<string, FileOrFolderElement[]> {
        const map: Map<string, FileOrFolderElement[]> = new Map();
        for (const path of Object.keys(data)) {
            map.set(path, data[path]);
        }
        return map;
    }

}
