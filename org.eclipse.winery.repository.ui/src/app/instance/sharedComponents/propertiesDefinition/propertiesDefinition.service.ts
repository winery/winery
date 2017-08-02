/**
 * Copyright (c) -2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */
import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions, Response } from '@angular/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';
import { PropertiesDefinitionsResourceApiData } from './propertiesDefinitionsResourceApiData';
import { XsdDefinitionsApiData } from './xsdDefinitionsApiData';

@Injectable()
export class PropertiesDefinitionService {

    private path: string;

    constructor(private http: Http,
                private route: Router) {
        this.path = decodeURIComponent(this.route.url);
    }

    /**
     * Deletes all the properties definitions.
     *
     * @returns {Observable<Response>}
     */
    deletePropertiesDefinitions(): Observable<Response> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.delete(backendBaseURL + this.path + '/', options);
    }

    /**
     * Gets the properties definitions data.
     *
     * @returns {Observable<PropertiesDefinitionsResourceApiData>}
     */
    getPropertiesDefinitionsData(): Observable<PropertiesDefinitionsResourceApiData> {
        return this.sendJsonRequest(this.path + '/');
    }

    /**
     * Gets the items for the select box for the XML Element.
     *
     * @returns {Observable<XsdDefinitionsApiData>}
     */
    getXsdElementDefinitions(): Observable<XsdDefinitionsApiData> {
        return this.sendJsonRequest(this.path + '/element');
    }

    /**
     * Gets the items for the select box for the XML Type.
     *
     * @returns {Observable<XsdDefinitionsApiData>}
     */
    getXsdTypeDefinitions(): Observable<XsdDefinitionsApiData> {
        return this.sendJsonRequest(this.path + '/type');
    }

    postPropertiesDefinitions(resourceApiData: PropertiesDefinitionsResourceApiData): Observable<Response> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.post(backendBaseURL + this.path + '/', JSON.stringify(resourceApiData), options);
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

        return this.http.get(backendBaseURL + requestPath, options)
            .map(res => res.json());
    }
}
