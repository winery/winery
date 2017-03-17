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
 *     Lukas Balzer -
 */

import { Component, OnInit, ViewChild } from '@angular/core';
import { VisualAppearanceService } from './visualAppearance.service';
import { FileUploader, FileItem } from 'ng2-file-upload';
import { ColorPickerDirective, ColorPickerService } from 'angular2-color-picker';
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
    color: string = '#f00';
    isColorLoaded: boolean= false;
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
        this.getColorFromServer();
    }

    getColorFromServer() {
        this.service.getColor().subscribe(
            data => this.handleColorData(data),
            error => this.handleError(error)
        );
    }

    handleColorData(data: any) {
        this.loading = false;
        this.color = data;
    }

    onUpload(uploader: FileUploader, modal?: any) {
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
    }

    saveToServer() {
        this.service.saveColor(this.color)
            .subscribe(
                data => this.handleResponse(data),
                error => this.handleError(error)
            );
    }

    colorChange(event: any) {
        if (this.isColorLoaded) {
            this.color = event;
        } else {
            this.isColorLoaded = true;
            this.getColorFromServer();
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
