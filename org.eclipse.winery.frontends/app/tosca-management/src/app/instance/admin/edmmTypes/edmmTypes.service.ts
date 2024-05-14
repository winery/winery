/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { SectionData } from '../../../section/sectionData';
import { ToscaTypes } from '../../../model/enums';
import { EdmmMappingItem } from '../edmmMappings/edmmMappings.service';
import { map } from 'rxjs/internal/operators';

@Injectable()
export class EdmmTypesService {

    private readonly path: string;

    constructor(private http: HttpClient) {
        this.path = backendBaseURL + '/admin/edmmtypes';
    }

    getEdmmTypes(): Observable<string[]> {
        return this.http.get<string[]>(this.path).pipe(map(items => items.sort()));
    }

    updateEdmmTypes(mappings: string[]): Observable<string[]> {
        return this.http.put<string[]>(this.path, mappings);
    }

}
