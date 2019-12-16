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
import { Observable } from 'rxjs';
import { CapabilityOrRequirementDefinition, CapOrReqDefinition, Constraint } from './capOrReqDefResourceApiData';
import { Router } from '@angular/router';
import { backendBaseURL } from '../../../configuration';
import { NameAndQNameApiData } from '../../../wineryQNameSelector/wineryNameAndQNameApiData';
import { TypeWithShortName } from '../../admin/typesWithShortName/typeWithShortName.service';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

@Injectable()
export class CapabilityOrRequirementDefinitionsService {

    private path: string;

    constructor(private http: HttpClient,
                private route: Router) {
        this.path = this.route.url;
    }

    /**
     * Gets the capability or requirements definitions data.
     *
     * @returns {Observable<CapabilityOrRequirementDefinition[]>} data
     */
    getCapOrReqDefinitionsData(): Observable<CapabilityOrRequirementDefinition[]> {
        return this.sendJsonRequest<CapabilityOrRequirementDefinition[]>(this.path);
    }

    /**
     * Gets all available capability types
     * @returns {Observable<NameAndQNameApiData>}
     */
    getAllCapOrReqTypes(types: string): Observable<NameAndQNameApiData[]> {
        return this.sendJsonRequest<NameAndQNameApiData[]>('/' + types);
    }

    /**
     * Sends a GET request to get list of all constraint types
     * @returns {Observable<TypeWithShortName[]>} list of constraint types
     */
    getConstraintTypes(): Observable<TypeWithShortName[]> {
        return this.sendJsonRequest<TypeWithShortName[]>('/admin/constrainttypes/');
    }

    /**
     * Sends a GET request to get a list of all constraints from one capability definition
     * @param capabilityDefinition of which the constraints list is to be obtained
     * @returns {Observable<Constraint[]>} list of constraints
     */
    getConstraints(capabilityDefinition: string): Observable<Constraint[]> {
        return this.sendJsonRequest<Constraint[]>(this.path + '/' + capabilityDefinition + '/constraints/');
    }

    /**
     * Sends a PUT request to update constraints data
     * @param capabilityDefinition which has the constraint
     * @param id of the constraint
     * @param data to be updated
     * @returns {Observable<string>} new id of the modified constraint
     */
    updateConstraint(capabilityDefinition: string, id: string, data: string): Observable<string> {
        const headers = new HttpHeaders({ 'Content-Type': 'text/xml' });
        return this.http
            .put(
                backendBaseURL + this.path + '/' + capabilityDefinition + '/constraints/' + id + '/',
                data,
                { headers: headers, responseType: 'text' }
            );
    }

    /**
     * Sends a POST request to create new constraint
     * @param capabilityDefinition which contains the new constraint
     * @param constraintData of the new constraint
     * @returns {Observable<string>} provides id of the newly created constraint
     */
    createConstraint(capabilityDefinition: string, constraintData: string): Observable<string> {
        const headers = new HttpHeaders({ 'Accept': 'text/plain', 'Content-Type': 'text/xml' });
        return this.http
            .post(
                backendBaseURL + this.path + '/' + capabilityDefinition + '/constraints/',
                constraintData,
                { headers: headers, responseType: 'text' }
            );
    }

    /**
     * Sends a DELETE request to delete a constraint
     * @param capabilityDefinition containing the constraint
     * @param id of the constraint, which is to be deleted
     * @returns {Observable<Response>}
     */
    deleteConstraint(capabilityDefinition: string, id: string): Observable<HttpResponse<string>> {
        return this.http
            .delete(
                backendBaseURL + this.path + '/' + capabilityDefinition + '/constraints/' + id + '/',
                { observe: 'response', responseType: 'text' }
            );
    }

    /**
     * Sends a POST request to add new CapabilityDefinition
     * @param capDef the CapabilityDefinition to be added
     * @returns {Observable<Response>}
     */
    sendPostRequest(capDef: CapOrReqDefinition): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http
            .post(
                backendBaseURL + this.path + '/',
                capDef,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }

    /**
     * Deletes a capabilityDefinition.
     *
     * @returns {Observable<Response>}
     */
    deleteCapOrReqDef(id: any): Observable<HttpResponse<string>> {
        return this.http
            .delete(
                backendBaseURL + this.path + '/' + id + '/',
                { observe: 'response', responseType: 'text' }
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
        return this.http.get<T>(backendBaseURL + requestPath);
    }

}
