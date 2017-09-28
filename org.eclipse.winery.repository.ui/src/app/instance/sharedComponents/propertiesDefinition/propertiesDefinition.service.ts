/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 */
import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions, Response } from '@angular/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';
import { PropertiesDefinitionsResourceApiData } from './propertiesDefinitionsResourceApiData';
import { SelectData } from '../../../wineryInterfaces/selectData';

@Injectable()
export class PropertiesDefinitionService {

    constructor(private http: Http,
                private route: Router) {
    }

    /**
     * Deletes all the properties definitions.
     *
     * @returns {Observable<Response>}
     */
    deletePropertiesDefinitions(): Observable<Response> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.delete(backendBaseURL + this.route.url + '/', options);
    }

    /**
     * Gets the properties definitions data.
     *
     * @returns {Observable<PropertiesDefinitionsResourceApiData>}
     */
    getPropertiesDefinitionsData(): Observable<PropertiesDefinitionsResourceApiData> {
        return this.sendJsonRequest('/');
    }

    /**
     * Gets the items for the select box for the XML Element.
     */
    getXsdElementDefinitions(): Observable<SelectData[]> {
        return this.sendJsonRequest('/element');
    }

    /**
     * Gets the items for the select box for the XML Type.
     */
    getXsdTypeDefinitions(): Observable<SelectData[]> {
        return this.sendJsonRequest('/type');
    }

    postPropertiesDefinitions(resourceApiData: PropertiesDefinitionsResourceApiData): Observable<Response> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.post(backendBaseURL + this.route.url + '/', JSON.stringify(resourceApiData), options);
    }

    /**
     * Private method for DRY principle. It is used to get all kinds of data
     * for the specified sub path.
     *
     * @param requestPath string The path which is specific for each request.
     * @returns {Observable<any>}
     */
    private sendJsonRequest(requestPath: string): Observable<any> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.get(backendBaseURL + this.route.url + requestPath, options)
            .map(res => res.json());
    }
}
