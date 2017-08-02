/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */
import { Injectable, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { Headers, Http, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../../configuration';
import { ModalDirective } from 'ngx-bootstrap';

export interface Property {
    serviceTemplatePropertyRef: string;
    targetObjectRef: any;
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

    constructor(private http: Http,
                private route: Router) {
        this.path = decodeURIComponent(this.route.url);
    }

    getPropertyMappings(): Observable<PropertyMappingsApiData> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.get(backendBaseURL + this.path + '/', options)
                   .map(res => res.json());
    }

    addPropertyMapping(propertyMapping: Property) {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.post(backendBaseURL + this.path + '/', JSON.stringify(propertyMapping), options);
    }

    removePropertyMapping(elementToDelete: string) {
        return this.http.delete(backendBaseURL + this.path + '/' +  encodeURIComponent(encodeURIComponent(elementToDelete)));
    }

}
