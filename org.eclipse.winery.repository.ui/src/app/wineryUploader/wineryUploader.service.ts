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
            this.fileUploader = new FileUploader({url: url});
            if (!isNullOrUndefined(this.method)) {
                this.fileUploader.onAfterAddingFile = (item) => {
                    item.method = this.method;
                };
            }
        } else {
            this.fileUploader.setOptions({url: url});
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
