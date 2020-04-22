/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';
import { InstanceService } from '../../instance.service';
import { InheritanceApiData } from './inheritanceApiData';
import { SelectData } from '../../../model/selectData';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

@Injectable()
export class InheritanceService {

    constructor(private http: HttpClient,
                private sharedData: InstanceService) {
    }

    getInheritanceData(): Observable<InheritanceApiData> {
        return this.http
            .get<InheritanceApiData>(
                this.sharedData.path + '/inheritance',
            );
    }

    getAvailableSuperClasses(): Observable<SelectData[]> {
        return this.http
            .get<SelectData[]>(
                backendBaseURL + '/' + this.sharedData.toscaComponent.toscaType + '?grouped=angularSelect&dev=true'
            );
    }

    saveInheritanceFromString(url: string, inheritFrom: string): Observable<HttpResponse<string>> {
        const inheritanceData = new InheritanceApiData();
        inheritanceData.isAbstract = 'no';
        inheritanceData.isFinal = 'no';
        inheritanceData.derivedFrom = inheritFrom;

        return this.saveInheritanceData(inheritanceData, url);
    }

    saveInheritanceData(inheritanceData: InheritanceApiData, url?: string): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        // create a copy to not send unnecessary data to the server
        const copy = new InheritanceApiData();
        copy.derivedFrom = inheritanceData.derivedFrom;
        copy.isAbstract = inheritanceData.isAbstract;
        copy.isFinal = inheritanceData.isFinal;

        return this.http
            .put(
                (url ? url : this.sharedData.path) + '/inheritance',
                JSON.stringify(copy),
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }
}
