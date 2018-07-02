/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
import { SectionData } from '../sectionData';
import { ToscaTypes } from '../../wineryInterfaces/enums';
import { backendBaseURL } from '../../configuration';
import { HttpClient, HttpResponse } from '@angular/common/http';

@Injectable()
export class EntityService {

    constructor(private http: HttpClient) {
    }

    deleteComponent(url: string): Observable<HttpResponse<string>> {
        return this.http.delete<string>(url + '/', { observe: 'response' });
    }

    getChangeLog(toscaType: ToscaTypes, base: SectionData, working: SectionData): Observable<string> {
        return this.http.get(backendBaseURL + '/' + toscaType + '/'
            + encodeURIComponent(encodeURIComponent(working.namespace)) + '/'
            + working.id
            + '?compareTo=' + base.id + '&asChangeLog=true',
            { responseType: 'text' }
        );
    }
}
