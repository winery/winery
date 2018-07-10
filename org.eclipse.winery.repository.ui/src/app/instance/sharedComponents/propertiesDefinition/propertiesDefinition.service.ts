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
import { Response } from '@angular/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';
import { PropertiesDefinitionsResourceApiData } from './propertiesDefinitionsResourceApiData';
import { SelectData } from '../../../model/selectData';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

@Injectable()
export class PropertiesDefinitionService {

    constructor(private http: HttpClient,
                private route: Router) {
    }

    /**
     * Deletes all the properties definitions.
     *
     * @returns {Observable<Response>}
     */
    deletePropertiesDefinitions(): Observable<HttpResponse<string>> {
        return this.http
            .delete(
                backendBaseURL + this.route.url + '/',
                { observe: 'response', responseType: 'text' }
            );
    }

    /**
     * Gets the properties definitions data.
     *
     * @returns {Observable<PropertiesDefinitionsResourceApiData>}
     */
    getPropertiesDefinitionsData(): Observable<PropertiesDefinitionsResourceApiData> {
        return this.sendJsonRequest<PropertiesDefinitionsResourceApiData>('/');
    }

    /**
     * Gets the items for the select box for the XML Element.
     */
    getXsdElementDefinitions(): Observable<SelectData[]> {
        return this.sendJsonRequest<SelectData[]>('/element');
    }

    /**
     * Gets the items for the select box for the XML Type.
     */
    getXsdTypeDefinitions(): Observable<SelectData[]> {
        return this.sendJsonRequest<SelectData[]>('/type');
    }

    postPropertiesDefinitions(resourceApiData: PropertiesDefinitionsResourceApiData): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });

        return this.http
            .post(
                backendBaseURL + this.route.url + '/',
                resourceApiData,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }

    /**
     * Private method for DRY principle. It is used to get all kinds of data
     * for the specified sub path.
     *
     * @param requestPath string The path which is specific for each request.
     * @returns {Observable<any>}
     */
    private sendJsonRequest<T>(requestPath: string): Observable<T> {
        return this.http.get<T>(backendBaseURL + this.route.url + requestPath);
    }
}
