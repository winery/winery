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

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { InstanceService } from '../../instance.service';
import { concat, Observable } from 'rxjs';
import { last } from 'rxjs/operators';

@Injectable()
export class SupportedFileTypesService {
    private readonly path: string;

    constructor(private http: HttpClient,
                private instanceService: InstanceService) {
        this.path = this.instanceService.path;
    }

    public getMimeType(): Observable<string> {
        return this.http.get(this.path + '/mimetype', { responseType: 'text' });
    }

    public setMimeType(mimeType: string): Observable<any> {
        return this.http.put<any>(this.path + '/mimetype', mimeType);
    }

    public getFileExtensions(): Observable<string[]> {
        return this.http.get<string[]>(this.path + '/fileextensions');
    }

    public setFileExtensions(fileExtensions: string[]): Observable<any> {
        return this.http.put<any>(this.path + '/fileextensions', fileExtensions);
    }

    public set(mimeType: string, fileExtensions: string[]): Observable<any> {
        return concat(this.setMimeType(mimeType), this.setFileExtensions(fileExtensions)).pipe(last());
    }
}
