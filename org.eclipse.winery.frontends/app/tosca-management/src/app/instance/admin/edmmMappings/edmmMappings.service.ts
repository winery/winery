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
    COMPUTE = 'compute',
    DATABASE = 'database',
    DMBS = 'dbms',
    MYSQL_DATABASE = 'mysql_database',
    MYSQL_DBMS = 'mysql_dbms',
    SOFTWARE_COMPONENT = 'software_component',
    TOMCAT = 'tomcat',
    WEB_APPLICATION = 'web_application',
    WEB_SERVER = 'web_server',

    // relation types
    CONNECTS_TO = 'connects_to',
    DEPENDS_ON = 'depends_on',
    HOSTED_ON = 'hosted_on'
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
