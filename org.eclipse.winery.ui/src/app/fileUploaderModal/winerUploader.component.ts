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
 *     Niko Stadelmaier - module refactoring
 */
import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { isNullOrUndefined } from 'util';
import { WineryUploaderService } from './wineryUploader.service';
import { NotificationService } from '../notificationModule/notificationservice';


/**
 * This component provides a modal popup with a <code>title</code> and optional progress bar <code>showProgress</code> for file uploads.
 * The file will be uploaded to the given <code>uploadUrl</code>.
 *
 *
 * <label>Inputs</label>
 * <ul>
 *     <li><code>title: string</code> The title of the dialog.
 *     </li>
 *     <li><code>uploadUrl: string</code> the target destination for the upload - should be a valid url
 *     It can either ba string in the template or a property of type string of the component
 *     </li>
 *     <li><code>showProgress: boolean</code> either shows or hides the progress of the upload. It is shown by default.
 *     </li>
 * </ul>
 * <br>
 * <label>Methods</label>
 * <ul>
 *     <li><code>show</code> Shows the modal dialog - call this function in your component to display the uploader
 *     To call the function you should specify a template reference for the upload component. This way you can display the
 *     uploader in your template by calling <code>uploaderRef.show()</code>.
 *     To call the function in your component you could use <code>@ViewChild</code> to get the uploader component and then
 *     call its <code>show</code> method.
 *     </li>
 * </ul>
 * <br>
 * @example <caption>Basic usage with url in template</caption>
 * ```html
 * <winery-namespaceSelector
 *      #uploader
 *      [title]="Upload Files"
 *      [uploadUrl]="'http://upload.to.server'"
 *      [showProgress]="true">
 * </winery-namespaceSelector>
 * ```
 *
 * @example <caption>Usage with url parameter</caption>
 * ```html
 * <winery-namespaceSelector
 *      #uploader
 *      [title]="Upload Files"
 *      [uploadUrl]="serverUrl"
 *      [showProgress]="true">
 * </winery-namespaceSelector>
 * ```
 */
@Component({
    selector: 'winery-uploader',
    templateUrl: 'wineryUploader.component.html'
})
export class WineryUploader implements OnInit {

    fileOver: boolean = false;
    loading = false;

    @Input() uploadUrl: string;
    @Input() title: string;
    @Input() showProgress = true;
    @ViewChild('wineryUploader') wineryUploader: any;

    constructor(private service: WineryUploaderService,
                private notify: NotificationService) {
    }

    ngOnInit() {
        this.service.uploadUrl = this.uploadUrl;
    }

    uploadFile(event?: any) {
        if (!isNullOrUndefined(event) && isNullOrUndefined(this.service.uploader.queue[0])) {
            this.fileOver = event;
        } else {
            this.fileOver = false;
            this.loading = true;
            this.service.uploader.queue[0].upload();
            this.service.uploader.onCompleteItem = (item: any, response: string, status: number, headers: any) => {
                this.loading = false;
                this.service.uploader.clearQueue();

                if (status === 204) {
                    this.notify.success('Successfully saved component');
                    this.wineryUploader.hide();
                } else {
                    this.notify.error('Error while uploading CSAR file');
                }

                return {item, response, status, headers};
            };
        }
    }

    show() {
        this.wineryUploader.show();
    }

}
