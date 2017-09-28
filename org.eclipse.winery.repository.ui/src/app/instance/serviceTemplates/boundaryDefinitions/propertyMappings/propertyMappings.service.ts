/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
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
import {
    PropertiesDefinition,
    PropertiesDefinitionsResourceApiData
} from '../../../sharedComponents/propertiesDefinition/propertiesDefinitionsResourceApiData';

export class Property {
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
        this.path = backendBaseURL + this.route.url + '/';
    }

    getPropertyMappings(): Observable<PropertyMappingsApiData> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.get(this.path, options)
            .map(res => res.json());
    }

    addPropertyMapping(propertyMapping: Property) {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.post(this.path, JSON.stringify(propertyMapping), options);
    }

    removePropertyMapping(elementToDelete: string) {
        return this.http.delete(this.path + encodeURIComponent(encodeURIComponent(elementToDelete)));
    }

    getPropertiesOfServiceTemplate(): Observable<string> {
        const headers = new Headers({'Accept': 'application/xml'});
        const options = new RequestOptions({headers: headers});

        const newPath: string = this.path.replace('propertymappings', 'properties');

        return this.http.get(newPath + '/', options)
            .map(res => res.text());
    }

    getTemplatesOfType(type: string): Observable <any> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.get(backendBaseURL + '/' + type + '/', options)
            .map(res => res.json());
    }

    getTargetObjKVProperties(targetPath: string): Observable<PropertiesDefinitionsResourceApiData> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.get(backendBaseURL + '/' + targetPath + '/' + 'propertiesdefinition', options)
            .map(res => res.json());
    }

}
