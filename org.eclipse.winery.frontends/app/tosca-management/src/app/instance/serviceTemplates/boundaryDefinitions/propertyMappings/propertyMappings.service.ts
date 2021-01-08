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
import { Injectable, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../../configuration';
import { ModalDirective } from 'ngx-bootstrap';
import { PropertiesDefinitionsResourceApiData } from '../../../sharedComponents/propertiesDefinition/propertiesDefinitionsResourceApiData';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

export class Property {
    serviceTemplatePropertyRef: string;
    targetObjectRef: string;
    targetPropertyRef: string;
}

export interface PropertyMappings {
    propertyMapping: Property[];
}

export interface PropertyMappingsApiData {
    propertyMappings: PropertyMappings;
}

@Injectable()
export class PropertyMappingService {

    @ViewChild('browseForServiceTemplatePropertyDiag') browseForServiceTemplatePropertyDiag: ModalDirective;
    private path: string;

    constructor(private http: HttpClient,
                private route: Router) {
        this.path = backendBaseURL + this.route.url + '/';
    }

    getPropertyMappings(): Observable<PropertyMappingsApiData> {
        return this.http.get<PropertyMappingsApiData>(this.path);
    }

    addPropertyMapping(propertyMapping: Property): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http
            .post(
                this.path,
                propertyMapping,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }

    removePropertyMapping(elementToDelete: string): Observable<HttpResponse<string>> {
        return this.http
            .delete(
                this.path + encodeURIComponent(encodeURIComponent(elementToDelete)),
                { observe: 'response', responseType: 'text' }
            );
    }

    getPropertiesOfServiceTemplate(): Observable<string> {
        const headers = new HttpHeaders({ 'Accept': 'application/xml' });
        const newPath: string = this.path.replace('propertymappings', 'properties');

        return this.http
            .get(newPath,
                { headers: headers, responseType: 'text' }
            );
    }

    getTargetObjKVProperties(targetPath: string): Observable<PropertiesDefinitionsResourceApiData> {
        return this.http
            .get<PropertiesDefinitionsResourceApiData>(
                backendBaseURL + '/' + targetPath + '/' + 'propertiesdefinition'
            );
    }

}
