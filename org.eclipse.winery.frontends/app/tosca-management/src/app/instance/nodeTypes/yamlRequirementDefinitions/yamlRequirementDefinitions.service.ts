/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';
import { SelectData } from '../../../model/selectData';
import { ToscaTypes } from '../../../model/enums';
import { Router } from '@angular/router';
import { YamlRequirementDefinitionApiData, YamlRequirementDefinitionPostApiData } from './yamlRequirementDefinitionApiData';

@Injectable()
export class YamlRequirementDefinitionsService {
    private readonly path: string;

    constructor(private http: HttpClient,
                private route: Router) {
        // we have the same path in the backend for both xml and yaml
        this.path = this.route.url.replace('yaml', '');
    }

    getAllRequirementDefinitions(): Observable<YamlRequirementDefinitionApiData[]> {
        const url = backendBaseURL + this.path;
        return this.http.get<YamlRequirementDefinitionApiData[]>(url);
    }

    getGroupedNodeTypes(): Observable<SelectData[]> {
        return this.getAllTypes(ToscaTypes.NodeType);
    }

    getCapabilityTypes(): Observable<SelectData[]> {
        return this.getAllTypes(ToscaTypes.CapabilityType);
    }

    getRelationshipTypes(): Observable<SelectData[]> {
        return this.getAllTypes(ToscaTypes.RelationshipType);
    }

    getAllTypes(type: ToscaTypes): Observable<SelectData[]> {
        const url = backendBaseURL + '/' + type + '?grouped=angularSelect&dev=true/';
        return this.http.get<SelectData[]>(url);
    }

    saveRequirementDefinition(reqDef: YamlRequirementDefinitionApiData): Observable<any> {
        const postData = YamlRequirementDefinitionPostApiData.fromData(reqDef);
        const url = backendBaseURL + this.path;
        return this.http.post<any>(url, postData);
    }
}
