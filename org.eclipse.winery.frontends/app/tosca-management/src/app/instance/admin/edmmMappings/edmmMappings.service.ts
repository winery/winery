/*******************************************************************************
 * Copyright (c) 2019-2021 Contributors to the Eclipse Foundation
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
import { EdmmTypesService } from '../edmmTypes/edmmTypes.service';

export class EdmmMappingItem {
    edmmType: string;
    toscaType: string;
}

@Injectable()
export class EdmmMappingsService {

    private readonly path: string;

    constructor(private http: HttpClient,
                private route: Router) {
        this.path = backendBaseURL + decodeURIComponent(this.route.url);
    }

    getToscaTypes(type: ToscaTypes): Observable<SectionData[]> {
        return this.http.get<SectionData[]>(backendBaseURL + '/' + type + '/');
    }

    getMappings(): Observable<EdmmMappingItem[]> {
        return this.http.get<EdmmMappingItem[]>(this.path);
    }

    updateEdmmMapping(mappings: EdmmMappingItem[]): Observable<EdmmMappingItem[]> {
        return this.http.put<EdmmMappingItem[]>(this.path, mappings);
    }

}
