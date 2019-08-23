/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

export enum EdmmType {
    compute = 'compute',
    database = 'database',
    dbms = 'dbms',
    mysql_database = 'mysql_database',
    mysql_dbms = 'mysql_dbms',
    software_component = 'software_component',
    tomcat = 'tomcat',
    web_application = 'web_application',
    web_server = 'web_server',

    // relation types
    connects_to = 'connects_to',
    depends_on = 'depends_on',
    hosted_on = 'hosted_on'
}

export class EdmmMappingItem {
    edmmType: EdmmType;
    toscaType: string;
}

@Injectable()
export class EdmmMappingsService {

    private readonly path: string;

    constructor(private http: HttpClient,
                private route: Router) {
        this.path = backendBaseURL + decodeURIComponent(this.route.url);
    }

    getTypes(type: ToscaTypes): Observable<SectionData[]> {
        return this.http.get<SectionData[]>(backendBaseURL + '/' + type + '/');
    }

    getMappings(): Observable<EdmmMappingItem[]> {
        return this.http.get<EdmmMappingItem[]>(this.path);
    }

    updateEdmmMapping(mappings: EdmmMappingItem[]): Observable<EdmmMappingItem[]> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http.put<EdmmMappingItem[]>(this.path, mappings);
    }

}
