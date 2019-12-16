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
import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { isNullOrUndefined } from 'util';
import { WineryUploaderService } from './wineryUploader.service';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { FileUploader } from 'ng2-file-upload';

/**
 * This component provides a modal popup with a <code>title</code> and optional progress bar <code>showProgress</code>
 * for file uploads. The file will be uploaded to the given <code>uploadUrl</code>.
 *
 *
 * <label>Inputs</label>
 * <ul>
 *     <li><code>uploadUrl: string</code> the target destination for the upload - should be a valid url
 *     It can either ba string in the template or a property of type string of the component
 *     </li>
 *     <li><code>showProgress: boolean</code> either shows or hides the progress of the upload. It is shown by default.
 *     </li>
 *     <li><code>modalRef</code> The reference to the modal.
 *     </li>
 *     <li><code>uploadImmediately</code> This flag is set to true by default. If no immediate upload is desired, you
 *     can set this to false. However, if set to false, you need to call the upload method yourself.
 *     </li>
 *     <li><code>uploadMethod</code> Specifies the http method used to upload the file. By default POST is used.
 *     </li>
 *     <li><code>allowMultipleFiles</code> This parameter specifies whether multiple files can be selected. False by default.
 *     </li>
 * </ul>
 * <br>
 * <br>
 * @example <caption>Basic usage with url in template</caption>
 * ```html
 * <winery-uploader
 *      [uploadUrl]="'http://upload.to.server'"
 *      [showProgress]="true">
 * </winery-uploader>
 * ```
 */
@Component({
    selector: 'winery-uploader',
    templateUrl: 'wineryUploader.component.html',
    styleUrls: [
        'wineryUploader.component.css'
    ],
    providers: [WineryUploaderService],
})
export class WineryUploaderComponent implements OnInit, OnChanges {

    fileOver = false;
    loading = false;
    error = false;

    errorMessage = '';

    @Input() uploadUrl: string;
    @Input() showProgress = true;
    @Input() modalRef: any = null;
    @Input() uploadImmediately = true;
    @Input() uploadMethod = 'POST';
    @Input() allowMultipleFiles = false;
    @Input() isEditable: boolean;

    @Output() onFileDropped = new EventEmitter();
    @Output() onSuccess = new EventEmitter();
    @Output() onError = new EventEmitter();

    constructor(public service: WineryUploaderService,
                private notify: WineryNotificationService) {
    }

    ngOnInit() {
        this.service.uploadMethod = this.uploadMethod;
        this.service.uploadUrl = this.uploadUrl;
    }

    ngOnChanges() {
        this.service.uploadUrl = this.uploadUrl;
    }

    getUploader(): FileUploader {
        return this.service.uploader;
    }

    dropFile(event?: any) {
        if (this.isEditable) {
            if (!isNullOrUndefined(event) && isNullOrUndefined(this.service.uploader.queue[0])) {
                this.fileOver = event;
            } else {
                this.fileOver = false;
                this.onFileDropped.emit(this.service.uploader.queue[0]);
                if (this.uploadImmediately) {
                    this.upload();
                }
            }
        }
    }

    upload(uploadTo?: string) {
        this.loading = true;
        if (!isNullOrUndefined(uploadTo) && uploadTo !== this.uploadUrl) {
            this.service.uploadUrl = uploadTo;

        }

        this.service.uploader.onBeforeUploadItem = (item) => {
            item.withCredentials = false;
        };

        this.service.uploader.onCompleteItem = (item: any, response: string, status: number, headers: any) => {
            this.loading = false;

            if (status >= 200 && status <= 204) {
                this.notify.success('Successfully uploaded file ' + item.file.name);
                if (!isNullOrUndefined(this.modalRef)) {
                    this.modalRef.hide();
                }
                this.onSuccess.emit(response);
            } else {
                if (response) {
                    this.error = true;
                    this.errorMessage = response;
                    this.notify.error('<pre>'.concat(this.errorMessage, '</pre>'), 'Error while uploading file ' + item.file.name);
                } else {
                    this.notify.error('Error while uploading file ' + item.file.name);
                }
                this.onError.emit(response);
            }

            return { item, response, status, headers };
        };

        this.service.uploader.onCompleteAll = () => {
            this.service.uploader.clearQueue();

        };

        this.service.uploader.uploadAll();
    }
}
