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
import { FileUploader } from 'ng2-file-upload';

@Component({
    selector: 'winery-instance-visualAppearance',
    templateUrl: 'visualAppearance.component.html',
    styleUrls: [
        'visualAppearance.component.css'
    ],
    providers: [VisualAppearanceService]
})
export class VisualAppearanceComponent implements OnInit {
    color: string = '#127bdc';
    img16Path: string;
    img50Path: string;
    img16uploader: FileUploader;
    img50uploader: FileUploader;
    hasImg16DropZoneOver: boolean = false;
    hasImg50DropZoneOver: boolean = false;
    @ViewChild('uploadModal') uploadModal: any;

    constructor(private service: VisualAppearanceService) {
        this.img16Path = service.getImg16x16Path();
        this.img50Path = service.getImg50x50Path();
        this.img16uploader = new FileUploader({url: this.service.getImg16x16Path()});
        this.img16uploader.autoUpload = true;
        this.img50uploader = new FileUploader({url: this.service.getImg50x50Path()});
    }

    ngOnInit() {
    }

    onImg16Hover(e: any) {
        this.hasImg16DropZoneOver = e;
    }

    onImg50Hover(e: any) {
        this.hasImg50DropZoneOver = e;
    }
    onUpload() {
        this.uploadModal.show();
    }

    getColor() {
    }

    saveColor() {
    }
}
