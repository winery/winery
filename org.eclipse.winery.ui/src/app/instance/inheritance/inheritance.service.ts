/**
 * Copyright (c) -2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */

import { Injectable } from '@angular/core';
import { InheritanceApiData } from './inheritanceApiData';
import { Observable } from 'rxjs';
import { Headers, RequestOptions, Http } from '@angular/http';
import { backendBaseUri } from '../../configuration';
import { QNameList } from '../../qNameSelector/qNameApiData';
import { InstanceService } from '../instance.service';

@Injectable()
export class InheritanceService {

    private path: string;

    constructor(private http: Http,
                private sharedData: InstanceService) {
        this.path = this.sharedData.path;
    }

    getInheritanceData(): Observable<InheritanceApiData> {
        let headers = new Headers({ 'Accept': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http.get(backendBaseUri + this.path + '/inheritance/', options)
            .map(res => res.json());
    }

    getAvailableSuperClasses(): Observable<QNameList> {
        let headers = new Headers({ 'Accept': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http.get(backendBaseUri + this.path + '/getAvailableSuperClasses', options)
            .map(res => res.json());
    }

    saveInheritanceData(inheritanceData: InheritanceApiData): Observable<any> {
        let headers = new Headers({ 'Content-Type': 'application/json', 'Accept': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        // create a copy to not send unnecessary data to the server
        let copy = new InheritanceApiData();
        copy.derivedFrom = inheritanceData.derivedFrom;
        copy.isAbstract = inheritanceData.isAbstract;
        copy.isFinal = inheritanceData.isFinal;

        return this.http.put(backendBaseUri + this.path + '/inheritance/', JSON.stringify(copy), options);
    }
}
