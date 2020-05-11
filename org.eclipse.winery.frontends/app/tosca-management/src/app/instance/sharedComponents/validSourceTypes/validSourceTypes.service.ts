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
import { HttpClient } from '@angular/common/http';
import { InstanceService } from '../../instance.service';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ValidSourceTypesApiData } from './validSourceTypesApiData';
import { backendBaseURL } from '../../../configuration';
import { SelectData } from '../../../model/selectData';
import { ToscaTypes } from '../../../model/enums';

@Injectable()
export class ValidSourceTypesService {
    private readonly path: string;

    constructor(private http: HttpClient,
                private instanceService: InstanceService) {
        this.path = this.instanceService.path;
    }

    getValidSourceTypes(resourceName: string): Observable<ValidSourceTypesApiData> {
        return this.http.get<ValidSourceTypesApiData>(this.path + '/' + resourceName);
    }

    getAvailableValidSourceTypes(): Observable<SelectData[]> {
        const url = backendBaseURL + '/' + ToscaTypes.NodeType + '?grouped=angularSelect&dev=true/';
        return this.http.get<SelectData[]>(url);
    }

    getValidSourceTypesForCapabilityDefinition(type: string, resourceName: string): Observable<ValidSourceTypesApiData> {
        const path = '/' + ToscaTypes.CapabilityType + type;
        return this.http.get<ValidSourceTypesApiData>(this.path + '/' + resourceName);
    }

    saveValidSourceTypes(v: ValidSourceTypesApiData, resourceName: string): Observable<any> {
        return this.http.put<any>(this.path + '/' + resourceName, v);
    }
}
