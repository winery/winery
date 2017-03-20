/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 *     Lukas Balzer - added fileUploader and color picker component
 */

import { Component, OnInit, ViewChild } from '@angular/core';
import { VisualAppearanceService } from './visualAppearance.service';
import { FileUploader, FileItem } from 'ng2-file-upload';
import { ColorPickerService } from 'angular2-color-picker';
import { NotificationService } from '../../notificationModule/notificationservice';
import { isNullOrUndefined } from 'util';

@Component({
    selector: 'winery-instance-visualAppearance',
    templateUrl: 'visualAppearance.component.html',
    styleUrls: [
        'visualAppearance.component.css'
    ],
    providers: [VisualAppearanceService]
})
export class VisualAppearanceComponent implements OnInit {
    colorMap: Map<string, {loaded: boolean, color: string}> = new Map<string, {loaded: boolean, color: string}>();
    arrowMap: Map<string, {selected: boolean}> = new Map<string, {selected: boolean}>();
    loading: boolean = true;
    img16uploader: FileUploader;
    img50uploader: FileUploader;
    img16Path: string;
    img50Path: string;
    hasImg16DropZoneOver: boolean = false;
    hasImg50DropZoneOver: boolean = false;
    @ViewChild('upload16Modal') upload16Modal: any;
    @ViewChild('upload50Modal') upload50Modal: any;

    fileItem: FileItem;

    constructor(private service: VisualAppearanceService,
                private notify: NotificationService,
                private cpService: ColorPickerService) {
    }

    ngOnInit() {
        this.img16Path = this.service.getImg16x16Path();
        this.img50Path = this.service.getImg50x50Path();
        this.img16uploader = this.service.getUploader(this.img16Path);
        this.img50uploader = this.service.getUploader(this.img50Path);
        this.colorMap.set('/bordercolor', {loaded: false, color: '#f00'});
        this.colorMap.set('/color', {loaded: false, color: '#f00'});
        this.colorMap.set('/hovercolor', {loaded: false, color: '#f00'});
        if (this.service.isNodeType) {
            this.getColorFromServer('/bordercolor');
        } else {
            this.getColorFromServer('/color');
            this.getColorFromServer('/hovercolor');
        }
    }

    getColorFromServer(type: string) {
        if (this.colorMap.has(type)) {
            this.service.getColor(type).subscribe(
                data => this.handleColorData(data, type),
                error => this.handleError(error)
            );
        }
    }

    selectArrowItem(type?: string, value?: any) {
        let isOpen = true;
        if (isNullOrUndefined(type)) {
            this.arrowMap.forEach(function (value, index, map) {
                value.selected = false;
            });
        } else {
            if (!this.arrowMap.has(type)) {
                this.arrowMap.set(type, {selected: true});
            } else if (this.arrowMap.get(type).selected) {
                isOpen = false;
            }
            this.arrowMap.forEach(function (value, index, map) {
                value.selected = false;
            });
            if (isOpen) {
                this.arrowMap.get(type).selected = true;
            }
        }
    }

    handleColorData(data: any, type: string) {
        this.loading = false;
        this.colorMap.get(type).color = data;
    }

    onUpload(uploader: FileUploader, event: any, modal?: any): boolean {
        if (!isNullOrUndefined(uploader.queue[0])) {
            this.loading = true;
            this.fileItem = uploader.queue[0];
            if (!this.fileItem._file.type.includes('image')) {
                uploader.clearQueue();
                this.loading = false;
                this.notify.error('Please upload an image file');
            } else {
                this.fileItem.upload();
                uploader.onCompleteItem = (item: any, response: string, status: number, headers: any) => {
                    uploader.clearQueue();
                    this.loading = false;

                    if (!isNullOrUndefined(modal)) {
                        modal.hide();
                    }
                    if (status === 204) {
                        this.notify.success('Successfully saved Icon');
                    } else {
                        this.notify.error('Error while uploading Icon');
                    }
                    return {item, response, status, headers};
                };
            }
        }
        return event;
    }

    saveToServer(type: string) {
        if (this.colorMap.has(type)) {
            this.service.saveColor(this.colorMap.get(type).color, type)
                .subscribe(
                    data => this.handleResponse(data),
                    error => this.handleError(error)
                );
        }
    }

    colorChange(event: any, type: string) {
        if (this.colorMap.has(type)) {
            console.log('has ' + type);
            if (this.colorMap.get(type).loaded) {
                this.colorMap.get(type).color = event;
            } else {
                this.colorMap.get(type).loaded = true;
                this.getColorFromServer(type);
            }
            console.log(this.colorMap);
        }
    }

    private handleResponse(response: any) {
        this.loading = false;
        this.notify.success('Successfully saved bordercolor!');
    }

    private handleError(error: any): void {
        this.loading = false;
        this.notify.error(error);
    }

}
