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
import { Http } from '@angular/http';
import { FileUploader } from 'ng2-file-upload';

@Injectable()
export class WineryUploaderService {

    url: string;

    private fileUploader: FileUploader;


    constructor(private http: Http) {

    }

    get uploader(): FileUploader {
        return this.fileUploader;
    }

    set uploadUrl(url: string) {
        this.url = url;
        this.fileUploader = new FileUploader({url: url});
    }
}
