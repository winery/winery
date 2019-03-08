/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
import { Injectable } from '@angular/core';
import { FileUploader } from 'ng2-file-upload';
import { isNullOrUndefined } from 'util';

@Injectable()
export class WineryUploaderService {

    private fileUploader: FileUploader;
    private method: string;

    get uploader(): FileUploader {
        return this.fileUploader;
    }

    set uploadUrl(url: string) {
        if (isNullOrUndefined(this.fileUploader)) {
            this.fileUploader = new FileUploader({ url: url });
            if (!isNullOrUndefined(this.method)) {
                this.fileUploader.onAfterAddingFile = (item) => {
                    item.method = this.method;
                };
            }
        } else {
            this.fileUploader.setOptions({ url: url });
        }
    }

    set uploadMethod(method: string) {
        this.method = method;

        if (!isNullOrUndefined(this.fileUploader)) {
            this.fileUploader.onAfterAddingFile = (item) => {
                item.method = this.method;
            };
        }
    }
}
