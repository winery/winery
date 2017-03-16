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
import { NotificationService } from '../../notificationModule/notificationservice';

@Component({
    selector: 'winery-instance-visualAppearance',
    templateUrl: 'visualAppearance.component.html',
    styleUrls: [
        'visualAppearance.component.css'
    ],
    providers: [VisualAppearanceService]
})
export class VisualAppearanceComponent implements OnInit {
    color: string = '#fff';
    testColor: string;
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
                private notify: NotificationService) {
    }

    ngOnInit() {
        this.img16Path = this.service.getImg16x16Path();
        this.img50Path = this.service.getImg50x50Path();
        this.img16uploader = this.service.getUploader(this.img16Path);
        this.img50uploader = this.service.getUploader(this.img50Path);
        this.service.getColor().subscribe(
            data => this.handleColorData(data),
            error => this.handleError(error)
        );
    }

    handleColorData(data: any) {
        this.color = data;
        this.testColor = data;
        console.log('Get ' + data + ' ' + this.color);
    }

    onImg16Hover(e: any) {
        this.hasImg16DropZoneOver = e;
    }

    onImg50Hover(e: any) {
        this.hasImg50DropZoneOver = e;
    }

    onUpload16() {
        this.img16uploader.clearQueue();
        this.upload16Modal.show();
    }

    onUpload50() {
        this.upload50Modal.clearQueue();
        this.upload50Modal.show();
    }

    test() {
        this.color = this.testColor;
    }

    saveToServer() {
        this.service.saveColor(this.color)
            .subscribe(
                data => this.handleResponse(data),
                error => this.handleError(error)
            );
        this.service.getColor().subscribe(
            data => this.handleColorData(data),
            error => this.handleError(error)
        );
    }

    colorChange(event: any) {
        this.color = event;
        console.log(event + '-' + this.color);
    }

    private handleResponse(response: any) {
        this.notify.success('Successfully saved bordercolor!');
    }

    private handleError(error: any): void {
        this.notify.error(error);
    }

}
