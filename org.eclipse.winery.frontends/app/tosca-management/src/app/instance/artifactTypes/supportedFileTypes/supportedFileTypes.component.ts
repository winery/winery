/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
import { InstanceService } from '../../instance.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { HttpErrorResponse } from '@angular/common/http';
import { SupportedFileTypesService } from './supportedFileTypes.service';
import { forkJoin } from 'rxjs';

@Component({
    selector: 'winery-supported-file-types',
    templateUrl: 'supportedFileTypes.component.html',
    styleUrls: [
        'supportedFileTypes.component.css'
    ],
    providers: [
        SupportedFileTypesService
    ]
})
export class SupportedFileTypesComponent implements OnInit {
    loading: boolean;
    currentMimeType = '';
    allFileExtensions: { value: string }[] = [];
    fileExtensionInput = '';
    selectedFileExtension: { value: string };
    columns = [{
        title: 'File Extension', name: 'value', sort: true
    }];

    constructor(public sharedData: InstanceService,
                private service: SupportedFileTypesService,
                private notify: WineryNotificationService) {
    }

    ngOnInit(): void {
        this.loading = true;
        forkJoin(
            this.service.getFileExtensions(),
            this.service.getMimeType()
        ).subscribe(
            ([fileExtensions, mimeType]) => {
                this.loading = false;
                if (fileExtensions) {
                    this.allFileExtensions = fileExtensions.map(ext => {
                        return {
                            value: ext
                        };
                    });
                } else {
                    this.allFileExtensions = [];
                }

                this.currentMimeType = mimeType ? mimeType : '';
                this.loading = false;
            },
            error => this.handleError(error)
        );

    }

    private handleError(error: HttpErrorResponse): void {
        this.loading = false;
        this.notify.error(error.message, 'Error');
    }

    saveToServer() {
        this.loading = true;
        this.service.set(this.currentMimeType, this.allFileExtensions.map(ext => ext.value))
            .subscribe(() => {
                    this.loading = false;
                    this.notify.success('Allowed file types saved.', 'Operation Successful');
                }, e => this.handleError(e)
            );
    }

    onFileExtensionInputChanged($event: KeyboardEvent) {
        this.fileExtensionInput = (event.target as HTMLInputElement).value;
    }

    onMimeTypeInputChanged($event: KeyboardEvent) {
        this.currentMimeType = (event.target as HTMLInputElement).value;
    }

    addExtension() {
        if (!this.allFileExtensions.some(ext => ext.value === this.fileExtensionInput)) {
            this.allFileExtensions.push({ value: this.fileExtensionInput });
        } else {
            this.notify.warning('This file extension is already added to the list!', 'Cannot Add File Extension');
        }
    }

    removeFileExtension($event: { value: string }) {
        this.allFileExtensions = this.allFileExtensions.filter(ext => ext.value !== $event.value);
    }

}

