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
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { PlansApiData } from './plansApiData';
import { backendBaseURL } from '../../../configuration';
import { SelectData } from '../../../model/selectData';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

@Injectable()
export class PlansService {

    readonly path: string;

    constructor(private http: HttpClient,
                private route: Router) {
        this.path = backendBaseURL + this.route.url + '/';
    }

    getPlansData(url?: string): Observable<PlansApiData[]> {
        return this.getJson(url ? backendBaseURL + url : this.path);
    }

    getPlanTypes(): Observable<SelectData[]> {
        return this.getJson(backendBaseURL + '/admin/plantypes/?ngSelect=true');
    }

    getPlanLanguages(): Observable<SelectData[]> {
        return this.getJson(backendBaseURL + '/admin/planlanguages/?ngSelect=true');
    }

    addPlan(newPlan: PlansApiData): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http
            .post(
                this.path,
                newPlan,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }

    deletePlan(id: string): Observable<HttpResponse<string>> {
        return this.http
            .delete(
                this.path + id + '/',
                { observe: 'response', responseType: 'text' }
            );
    }

    private getJson<T>(path: string): Observable<T> {
        return this.http.get<T>(path);
    }

    updatePlan(plan: PlansApiData): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http
            .put(
                this.path + plan.id + '/',
                plan,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }

    public generatePlans(): Observable<HttpResponse<string>> {
        const url = this.path + 'generate';
        const headers = new HttpHeaders({ 'Content-Type': 'text/plain' });
        return this.http.post(url, null,
            { headers: headers, observe: 'response', responseType: 'text' }
        );
    }
}
