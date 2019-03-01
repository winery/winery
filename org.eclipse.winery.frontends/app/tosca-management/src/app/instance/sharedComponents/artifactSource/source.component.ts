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
import { FilesApiData, SourceService } from './source.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { WineryEditorComponent } from '../../../wineryEditorModule/wineryEditor.component';
import { SourceApiData } from './sourceApiData';
import { WineryValidatorObject } from '../../../wineryValidators/wineryDuplicateValidator.directive';
import { hostURL } from '../../../configuration';
import { InstanceService } from '../../instance.service';
import { ToscaTypes } from '../../../model/enums';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    templateUrl: 'source.component.html',
    styleUrls: [
        'source.component.css'
    ],
    providers: [
        SourceService
    ]
})

export class SourceComponent implements OnInit {

    loading = true;
    enableCopyToFiles = true;
    uploadUrl: string;
    filesList: FilesApiData[];
    baseUrl = hostURL;
    fileMap = new Map<string, FilesApiData[]>();
    paths: string[];
    @ViewChild('removeElementModal') removeElementModal: any;
    srcPath: string;
    selectedPath: string;

    @ViewChild('saveCurrentFileModal') saveCurrentFileModal: any;
    @ViewChild('createNewFileModal') createNewFileModel: any;
    @ViewChild('renameFileModal') renameFileModal: any;
    @ViewChild('artifactsEditor') editor: WineryEditorComponent;

    validatorObject: WineryValidatorObject;
    pathValidatorObject: WineryValidatorObject;
    renameFileName: string;
    fileContent: string;
    newFileName: string;
    newFileDir: string;
    selectedFile: FilesApiData = null;
    loadingFileContent: boolean;

    constructor(private service: SourceService,
                private notify: WineryNotificationService,
                public sharedData: InstanceService) {
        this.srcPath = this.service.getSourcePath + '/zip';
        this.enableCopyToFiles = this.sharedData.toscaComponent.toscaType !== ToscaTypes.ServiceTemplate;
    }

    ngOnInit() {
        this.loadFiles();
        this.uploadUrl = this.service.getSourcePath;
        this.validatorObject = new WineryValidatorObject(this.filesList, 'name');
        this.pathValidatorObject = new WineryValidatorObject([]);
        const regExp = /^(|[\w-_]+([\\][\w-_]+)*)$/;
        this.pathValidatorObject.setRegExp(new RegExp(regExp));
    }

    loadFiles() {
        this.loading = true;
        this.service.getFiles()
            .subscribe(
                data => this.handleLoadFiles(data.files, data.paths),
                error => this.handleError(error)
            );
    }

    selectPath(path: string) {
        if (this.selectedPath != null && this.selectedPath === path) {
            this.selectedPath = null;
        } else {
            this.selectedPath = path;
        }
    }

    editFile(file: FilesApiData) {
        if (this.selectedFile == null || (file.name !== this.selectedFile.name && this.checkFileChanges())) {
            this.loadingFileContent = true;
            this.service.getFile(file)
                .subscribe(
                    data => this.handleEditorChange(file, data),
                    error => this.handleError(error)
                );
        }
    }

    saveEditorContent() {
        if (this.fileContent != null && this.fileContent !== this.editor.getData()) {
            this.fileContent = this.editor.getData();
            const fileAPI = new SourceApiData();
            fileAPI.setContent(this.fileContent);
            fileAPI.setFileName(this.selectedFile.name);
            fileAPI.setSubDirectory(this.selectedFile.subDirectory);
            this.service.postToSources(fileAPI)
                .subscribe(
                    () => this.handleSave(),
                    error => this.handleError(error)
                );
        }
    }

    copyAllSrc() {
        this.service.copySourcesToFiles()
            .subscribe(
                () => this.handleCopySuccess(),
                error => this.handleError(error)
            );
    }

    openRenameDialog() {
        this.renameFileName = this.selectedFile.name;
        this.renameFileModal.show();
    }

    renameSelection() {
        const apiData = new SourceApiData();
        apiData.setFileName(this.renameFileName);
        apiData.setContent(this.fileContent);
        apiData.setSubDirectory(this.selectedFile.subDirectory);
        this.service.postToSources(apiData)
            .subscribe(
                () => this.onRename(),
                error => this.handleError(error)
            );
    }

    undoFileChanges() {
        this.editor.setData(this.fileContent);
        this.saveCurrentFileModal.hide();
    }

    onCreateNewFile() {
        this.newFileName = '';
        this.newFileDir = '';
        if (this.selectedPath != null) {
            this.newFileDir = this.selectedPath;
        }
        this.createNewFileModel.show();
    }

    createNewFile() {
        this.loading = true;
        const newFile = new SourceApiData();
        newFile.setFileName(this.newFileName);
        newFile.setSubDirectory(this.newFileDir);
        this.service.postToSources(newFile)
            .subscribe(
                () => this.handleCreate(),
                error => this.handleError(error)
            );
    }

    onRemoveElement() {
        this.loading = true;
        this.service.deleteFile(this.selectedFile)
            .subscribe(
                () => this.handleDelete(),
                error => this.handleError(error)
            );
    }

    private onRename() {
        this.service.deleteFile(this.selectedFile).subscribe(
            () => this.handleRename(),
            error => this.handleError(error)
        );
    }

    private checkFileChanges(): boolean {
        if (this.fileContent != null && this.fileContent !== this.editor.getData()) {
            this.saveCurrentFileModal.show();
            return false;
        }
        return true;
    }

    private pushToFiles(fileName: string, content: string) {
        const apiData = new SourceApiData();
        apiData.setContent(content);
        apiData.setFileName(fileName);
        this.service.postToFiles(apiData)
            .subscribe(
                () => this.handleSave(),
                error => this.handleError(error)
            );
    }

    private handleCreate() {
        this.notify.success('Successfully Created ' + this.newFileName);
        this.loadFiles();
        this.loading = false;
    }

    private handleEditorChange(file: FilesApiData, content: string) {
        this.loadingFileContent = false;
        this.fileContent = content ? content : '';
        this.selectedFile = file;
        this.editor.setData(content);
    }

    private handleRename() {
        this.notify.success('Successfully renamed ' + this.selectedFile.name + ' to ' + this.renameFileName);
        this.selectedFile = null;
        this.renameFileName = null;
        this.loadFiles();
        this.invalidateEditor();
        this.loading = false;
    }

    private handleDelete() {
        this.notify.success('Successfully deleted ' + this.selectedFile.name);
        this.selectedFile = null;
        this.loadFiles();
        this.invalidateEditor();
        this.loading = false;
    }

    private handleSave() {
        this.notify.success('Successfully Saved ' + this.selectedFile.name);
    }

    private handleLoadFiles(files: FilesApiData[], paths: string[]) {
        this.fileMap.clear();
        this.paths = [];
        for (let i = 0; i < paths.length; i++) {
            files[i].subDirectory = paths[i];
            if (this.fileMap.has(paths[i])) {
                this.fileMap.get(paths[i]).push(files[i]);
            } else {
                this.fileMap.set(paths[i], [files[i]]);
                this.paths.push(paths[i]);
            }
        }
        this.paths.sort((a: string, b: string) => {
            if (a > b) {
                return -1;
            }
            if (a < b) {
                return 1;
            }
            return 0;
        });
        this.filesList = files;
        this.loading = false;
    }

    private handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.loadingFileContent = false;
        this.notify.error(error.message);
    }

    private invalidateEditor() {
        this.editor.setData('');
        this.selectedFile = null;
        this.fileContent = null;
    }

    private handleCopySuccess() {
        this.notify.success('Copied source files to the files directory');
    }
}
