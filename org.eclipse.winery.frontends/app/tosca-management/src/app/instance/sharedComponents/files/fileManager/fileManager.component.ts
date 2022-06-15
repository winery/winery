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
import { Component, Input, OnChanges, Output, SimpleChanges, ViewChild, EventEmitter } from '@angular/core';
import { FileOrFolderElement } from '../../../../model/fileOrFolderElement';
import { MatTable, Sort } from '@angular/material';
import { RenameDialogComponent } from './dialogs/renameDialog.component';
import { NewFolderDialogComponent } from './dialogs/newFolderDialog.component';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuTrigger } from '@angular/material';
import { ConfirmDialogComponent } from './dialogs/confirmDialog.component';

@Component({
    selector: 'winery-file-manager',
    templateUrl: 'fileManager.component.html',
    styleUrls: ['fileManager.component.css']
})

export class FileManagerComponent implements OnChanges {

    @Input() pathToElementsMap: Map<string, FileOrFolderElement[]>;

    @Output() currentPathChange = new EventEmitter<string>();
    @Output() folderCreated = new EventEmitter<string>();
    @Output() elementDeleted = new EventEmitter<string>();
    @Output() elementUpdated = new EventEmitter<{ oldPath: string, newPath: string }>();
    @Output() updateRequested = new EventEmitter();

    @ViewChild(MatTable) table: MatTable<any>;
    @ViewChild('contextMenuTrigger') contextMenu: MatMenuTrigger;

    currentPath: string;
    baseDir: string;
    listView = true;
    contextMenuPosition = { x: '0px', y: '0px' };

    private dirsAndFilesList: FileOrFolderElement[];
    private lastSort: Sort = { active: 'size', direction: 'asc' };

    private readonly SEPARATOR = '\\';
    private readonly COLUMNS: string[] = ['isFile', 'name', 'size', 'modified'];
    private readonly UNITS = ['Bytes', 'KB', 'MB', 'GB', 'TB'];

    constructor(public dialog: MatDialog) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (this.pathToElementsMap) {
            if (this.baseDir === undefined) {
                this.determineBaseDir();
                this.setCurrentPath(this.baseDir);
            }
            this.updateDirsAndFilesList();
        }
    }

    determineBaseDir() {
        const keys = Array.from(this.pathToElementsMap.keys());
        keys.sort((a, b) => a.length > b.length ? 1 : -1);
        this.baseDir = keys[0];
    }

    setCurrentPath(newPath: string) {
        this.currentPath = newPath;
        this.currentPathChange.emit(newPath);
        this.updateDirsAndFilesList();
    }

    getAllDirsAndFiles() {
        this.updateRequested.emit();
    }

    updateDirsAndFilesList() {
        this.dirsAndFilesList = this.pathToElementsMap.get(this.currentPath);
        if (!this.dirsAndFilesList) {
            this.dirsAndFilesList = <FileOrFolderElement[]>[];
        }
        this.sortData(this.lastSort, false);
    }

    createNewFolder() {
        const dialog = this.dialog.open(NewFolderDialogComponent);
        dialog.afterClosed().subscribe(name => {
            if (name) {
                const newDir = this.getPathOfElement(name);
                this.folderCreated.emit(newDir);
            }
        });
    }

    openRenameDialog(element: FileOrFolderElement) {
        const oldPath = this.getPathOfElement(element.name);
        const dialog = this.dialog.open(RenameDialogComponent, {
            data: {
                isFile: element.isFile,
                name: element.name,
            }
        });
        dialog.afterClosed().subscribe(name => {
            if (name) {
                const newPath = this.getPathOfElement(name);
                this.elementUpdated.emit({ oldPath, newPath });
            }
        });
    }

    delete(element: FileOrFolderElement) {
        const dialog = this.dialog.open(ConfirmDialogComponent, {
            data: {
                isFile: element.isFile,
                name: element.name,
            },
            autoFocus: false
        });
        dialog.afterClosed().subscribe(res => {
            if (res === 'delete') {
                const toDelete = this.getPathOfElement(element.name);
                this.elementDeleted.emit(toDelete);
            }
        });
    }

    move(source: FileOrFolderElement, target: string) {
        const oldPath = this.getPathOfElement(source.name);
        const newPath = target + this.SEPARATOR + source.name;
        this.elementUpdated.emit({ oldPath, newPath });
    }

    getFolders(source: FileOrFolderElement) {
        const ownPath = this.getPathOfElement(source.name);
        const folder = [];
        let entryPath;
        this.pathToElementsMap.forEach((value: FileOrFolderElement[], key: string) => {
            if (folder.indexOf(key) === -1 && key !== ownPath && key !== this.currentPath) {
                folder.push(key);
            }
            for (const entry of value) {
                entryPath = key + this.SEPARATOR + entry.name;
                if (!entry.isFile && folder.indexOf(entryPath) === -1 && entryPath !== ownPath && entryPath !== this.currentPath) {
                    folder.push(entryPath);
                }
            }
        });
        return folder.sort((a, b) => (this.depth(a) > this.depth(b) ? 1 : this.depth(a) === this.depth(b) ? (a.toUpperCase() > b.toUpperCase() ? 1 : -1) : -1));
    }

    depth(path: string): number {
        return (path.match(/\\/g) || []).length;
    }

    openFolder(folder: FileOrFolderElement) {
        if (!folder.isFile) {
            this.setCurrentPath(this.getPathOfElement(folder.name));
        }
    }

    navigateBack() {
        this.setCurrentPath(this.currentPath.slice(0, this.currentPath.lastIndexOf(this.SEPARATOR)));
    }

    goRoot() {
        this.setCurrentPath(this.baseDir);
    }

    getSize(item: FileOrFolderElement) {
        if (item.size === 0) {
            return '-';
        }
        let i = 0;
        let size = item.size;
        while (size >= 1024 && ++i) {
            size = size / 1024;
        }
        return (size.toFixed(size < 10 && i > 0 ? 1 : 0) + ' ' + this.UNITS[i]);
    }

    onContextMenu(event: MouseEvent, item: FileOrFolderElement) {
        event.preventDefault();
        this.contextMenuPosition.x = event.clientX + 'px';
        this.contextMenuPosition.y = event.clientY + 'px';
        this.contextMenu.menuData = { 'item': item };
        this.contextMenu.menu.focusFirstItem('mouse');
        this.contextMenu.openMenu();
    }

    sortData($event: Sort, updateRequired: boolean) {
        if ($event.direction === '' || $event.active === 'isFile') {
            this.sortSize($event.direction.toString());
        } else if ($event.active === 'name') {
            this.sortName($event.direction.toString());
        } else if ($event.active === 'size') {
            this.sortSize($event.direction.toString());
        } else if ($event.active === 'modified') {
            this.sortModified($event.direction.toString());
        }
        this.lastSort = $event;
        if (updateRequired) {
            this.table.renderRows();
        }
    }

    sortName(sort: string) {
        if (sort === 'desc') {
            this.dirsAndFilesList.sort((a, b) => (a.name.toUpperCase() < b.name.toUpperCase() ? 1 : -1));
        } else {
            this.dirsAndFilesList.sort((a, b) => (a.name.toUpperCase() > b.name.toUpperCase() ? 1 : -1));
        }
    }

    sortSize(sort: string) {
        if (sort === 'desc') {
            this.dirsAndFilesList.sort((a, b) => (a.size < b.size ? 1 : a.size > b.size ? -1 : (a.name.toUpperCase() > b.name.toUpperCase() ? 1 : -1)));
        } else {
            this.dirsAndFilesList.sort((a, b) => (a.size > b.size ? 1 : a.size < b.size ? -1 : (a.name.toUpperCase() < b.name.toUpperCase() ? 1 : -1)));
        }
    }

    sortModified(sort: string) {
        if (sort === 'desc') {
            this.dirsAndFilesList.sort((a, b) => (Date.parse(b.modified) - Date.parse(a.modified)));
        } else {
            this.dirsAndFilesList.sort((a, b) => (Date.parse(a.modified) - Date.parse(b.modified)));
        }
    }

    getPathOfElement(element: string): string {
        if (this.currentPath === undefined) {
            return element;
        }
        return this.currentPath + this.SEPARATOR + element;
    }

}
