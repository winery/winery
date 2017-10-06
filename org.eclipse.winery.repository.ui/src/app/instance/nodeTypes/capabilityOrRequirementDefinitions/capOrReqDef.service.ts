/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions, Response } from '@angular/http';
import { Observable } from 'rxjs';
import { CapabilityOrRequirementDefinition, CapOrReqDefinition, Constraint } from './capOrReqDefResourceApiData';
import { Router } from '@angular/router';
import { backendBaseURL } from '../../../configuration';
import { NameAndQNameApiData } from '../../../wineryQNameSelector/wineryNameAndQNameApiData';
import { TypeWithShortName } from '../../admin/typesWithShortName/typeWithShortName.service';

@Injectable()
export class CapabilityOrRequirementDefinitionsService {

    private path: string;

    constructor(private http: Http,
                private route: Router) {
        this.path = this.route.url;
    }

    /**
     * Gets the capability or requirements definitions data.
     *
     * @returns {Observable<CapabilityOrRequirementDefinition[]>} data
     */
    getCapOrReqDefinitionsData(): Observable<CapabilityOrRequirementDefinition[]> {
        return this.sendJsonRequest(this.path);
    }

    /**
     * Gets all available capability types
     * @returns {Observable<NameAndQNameApiData>}
     */
    getAllCapOrReqTypes(types: string): Observable<NameAndQNameApiData[]> {
        return this.sendJsonRequest('/' + types);
    }

    /**
     * Sends a GET request to get list of all constraint types
     * @returns {Observable<TypeWithShortName[]>} list of constraint types
     */
    getConstraintTypes(): Observable<TypeWithShortName[]> {
        return this.sendJsonRequest('/admin/constrainttypes/');
    }

    /**
     * Sends a GET request to get a list of all constraints from one capability definition
     * @param capabilityDefinition of which the constraints list is to be obtained
     * @returns {Observable<Constraint[]>} list of constraints
     */
    getConstraints(capabilityDefinition: string): Observable<Constraint[]> {
        return this.sendJsonRequest(this.path + '/' + capabilityDefinition + '/constraints/');
    }

    /**
     * Sends a PUT request to update constraints data
     * @param capabilityDefinition which has the constraint
     * @param id of the constraint
     * @param data to be updated
     * @returns {Observable<string>} new id of the modified constraint
     */
    updateConstraint(capabilityDefinition: string, id: string, data: string): Observable<string> {
        const headers = new Headers({ 'Content-Type': 'text/xml' });
        const options = new RequestOptions({ headers: headers });

        return this.http.put(backendBaseURL + this.path + '/' + capabilityDefinition
            + '/constraints/' + id + '/', data, options).map(res => res.text());
    }

    /**
     * Sends a POST request to create new constraint
     * @param capabilityDefinition which contains the new constraint
     * @param constraintData of the new constraint
     * @returns {Observable<string>} provides id of the newly created constraint
     */
    createConstraint(capabilityDefinition: string, constraintData: string): Observable<string> {
        const headers = new Headers({ 'Accept': 'text/plain', 'Content-Type': 'text/xml' });
        const options = new RequestOptions({ headers: headers });

        return this.http.post(backendBaseURL + this.path + '/' + capabilityDefinition
            + '/constraints/', constraintData, options).map(res => res.text());
    }

    /**
     * Sends a DELETE request to delete a constraint
     * @param capabilityDefinition containing the constraint
     * @param id of the constraint, which is to be deleted
     * @returns {Observable<Response>}
     */
    deleteConstraint(capabilityDefinition: string, id: string): Observable<Response> {
        const headers = new Headers({ 'Accept': 'application/json' });
        const options = new RequestOptions({ headers: headers });
        return this.http.delete(backendBaseURL + this.path + '/' + capabilityDefinition + '/constraints/' + id + '/', options);
    }

    /**
     * Sends a POST request to add new CapabilityDefinition
     * @param capDef the CapabilityDefinition to be added
     * @returns {Observable<Response>}
     */
    sendPostRequest(capDef: CapOrReqDefinition): Observable<Response> {
        const headers = new Headers({ 'Content-Type': 'application/json' });
        const options = new RequestOptions({ headers: headers });

        return this.http.post(backendBaseURL + this.path + '/', capDef, options);
    }

    /**
     * Deletes a capabilityDefinition.
     *
     * @returns {Observable<Response>}
     */
    deleteCapOrReqDef(id: any): Observable<Response> {
        const headers = new Headers({ 'Accept': 'application/json' });
        const options = new RequestOptions({ headers: headers });
        return this.http.delete(backendBaseURL + this.path + '/' + id + '/', options);
    }

    /**
     * Private method for DRY principle. It is used to get all kinds of data
     * for the specified sub path.
     *
     * @param requestPath string The path which is specific for each request.
     * @returns {Observable<any>}
     */
    private sendJsonRequest(requestPath: string): Observable<any> {
        const headers = new Headers({ 'Accept': 'application/json' });
        const options = new RequestOptions({ headers: headers });

        return this.http.get(backendBaseURL + requestPath, options)
            .map(res => res.json());
    }

}
