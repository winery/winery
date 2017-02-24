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
import { Observable } from 'rxjs';
import { Headers, RequestOptions, Http, Response } from '@angular/http';
import { backendBaseUri } from '../../configuration';
import { PropertiesDefinitionsResourceApiData } from './propertiesDefinitionsResourceApiData';
import { XsdDefinitionsApiData } from './XsdDefinitionsApiData';

@Injectable()
export class PropertiesDefinitionService {

    private path: string;

    constructor(private http: Http) {
    }

    /**
     * Deletes all the properties definitions.
     *
     * @returns {Observable<Response>}
     */
    deletePropertiesDefinitions(): Observable<Response> {
        let headers = new Headers({ 'Accept': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http.delete(backendBaseUri + this.path + 'propertiesdefinition/', options);
    }

    /**
     * Gets the properties definitions data.
     *
     * @returns {Observable<PropertiesDefinitionsResourceApiData>}
     */
    getPropertiesDefinitionsData(): Observable<PropertiesDefinitionsResourceApiData> {
        return this.sendJsonRequest('propertiesdefinition/');
    }

    /**
     * Gets the items for the select box for the XML Element.
     *
     * @returns {Observable<XsdDefinitionsApiData>}
     */
    getXsdElementDefinitions(): Observable<XsdDefinitionsApiData> {
        return this.sendJsonRequest('propertiesdefinition/element');
    }

    /**
     * Gets the items for the select box for the XML Type.
     *
     * @returns {Observable<XsdDefinitionsApiData>}
     */
    getXsdTypeDefinitions(): Observable<XsdDefinitionsApiData> {
        return this.sendJsonRequest('propertiesdefinition/type');
    }

    /**
     * Sets the path this service should use as base path.
     *
     * @param path string
     */
    setPath(path: string): void {
        this.path = path;
    }

    /**
     * Private method for DRY principle. It is used to get all kinds of data
     * for the specified sub path.
     *
     * @param requestPath string The path which is specific for each request.
     * @returns {Observable<any>}
     */
    private sendJsonRequest(requestPath: string): Observable<any> {
        let headers = new Headers({ 'Accept': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http.get(backendBaseUri + this.path + requestPath, options)
            .map(res => res.json());
    }

    // saveInheritanceData(inheritanceData: InheritanceApiData): Observable<any> {
    //     let headers = new Headers({ 'Content-Type': 'application/json', 'Accept': 'application/json' });
    //     let options = new RequestOptions({ headers: headers });
    //
    //     // create a copy to not send unnecessary data to the server
    //     let copy = new InheritanceApiData();
    //     copy.derivedFrom = inheritanceData.derivedFrom;
    //     copy.isAbstract = inheritanceData.isAbstract;
    //     copy.isFinal = inheritanceData.isFinal;
    //
    //     return this.http.put(backendBaseUri + decodeURIComponent(this.path), JSON.stringify(copy), options);
    // }
}
