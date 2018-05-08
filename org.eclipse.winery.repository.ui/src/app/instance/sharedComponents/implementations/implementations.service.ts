/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';
import { ImplementationAPIData } from './implementationAPIData';
import { ImplementationWithTypeAPIData } from './implementationWithTypeAPIData';
import { InstanceService } from '../../instance.service';
import { Utils } from '../../../wineryUtils/utils';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

@Injectable()
export class ImplementationService {

    private readonly implementationOrTemplateType: string;

    constructor(private http: HttpClient,
                private route: Router, private sharedData: InstanceService) {
        this.implementationOrTemplateType = '/' + Utils.getImplementationOrTemplateOfType(this.sharedData.toscaComponent.toscaType) + '/';
    }

    getImplementationData(): Observable<ImplementationAPIData[]> {
        return this.http
            .get<ImplementationAPIData[]>(
                backendBaseURL + this.route.url + '/'
            );
    }

    postImplementation(implApiData: ImplementationWithTypeAPIData): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http
            .post(
                backendBaseURL + this.implementationOrTemplateType,
                JSON.stringify(implApiData),
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }

    deleteImplementations(implToDelete: ImplementationAPIData): Observable<HttpResponse<string>> {
        const pathAddition = this.implementationOrTemplateType
            + encodeURIComponent(encodeURIComponent(implToDelete.namespace)) + '/'
            + implToDelete.localname + '/';
        return this.http
            .delete(
                backendBaseURL + pathAddition,
                { observe: 'response', responseType: 'text' }
            );
    }
}
