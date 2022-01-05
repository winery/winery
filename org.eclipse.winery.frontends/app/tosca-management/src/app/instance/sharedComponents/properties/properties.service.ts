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
import { InstanceService } from '../../instance.service';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Properties, PropertiesData } from './properties.types';
import { PropertiesDefinitionsResourceApiData } from '../propertiesDefinition/propertiesDefinitionsResourceApiData';
import { backendBaseURL } from '../../../configuration';
import { Utils } from '../../../wineryUtils/utils';
import { QName } from '../../../../../../shared/src/app/model/qName';


@Injectable()
export class PropertiesService {

    propertiesUrl: string;

    constructor(private http: HttpClient,
                private sharedData: InstanceService) {
        this.propertiesUrl = this.sharedData.path + '/properties/';
    }

    public getProperties(): Observable<PropertiesData> {
        return this.http.get(this.propertiesUrl, { observe: 'response', responseType: 'text' })
            .pipe(map(res => {
                if (res.headers.get('Content-Type') === 'application/json') {
                    return {
                        isXML: false, properties: JSON.parse(res.body)
                    };
                } else {
                    return { isXML: true, properties: res.body };
                }
            }));
    }

    getPropertiesDefinitions(instance: InstanceService): Observable<PropertiesDefinitionsResourceApiData> {
        const entityType =  new QName(instance.instance.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].type);
        const namespace = Utils.doubleEncodeNamespace(entityType.nameSpace);
        const entityTypeToscaType = instance.toscaComponent.toscaType.replace('template', 'type');
        const url  = backendBaseURL + ['', entityTypeToscaType, namespace, entityType.localName, 'propertiesdefinition', 'merged'].join('/');
        return this.http.get<PropertiesDefinitionsResourceApiData>(url);
    }

    public saveProperties(properties: Properties, isXML: boolean): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders();
        headers.set('Content-Type', isXML ? 'application/xml' : 'application/json');
        return this.http
            .put(
                this.propertiesUrl,
                properties,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }
}
