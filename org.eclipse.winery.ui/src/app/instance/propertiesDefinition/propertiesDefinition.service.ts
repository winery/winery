/*******************************************************************************
 * Copyright (c) -2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 *******************************************************************************/

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Headers, RequestOptions, Http } from '@angular/http';
import { backendBaseUri } from '../../configuration';
import { PropertiesDefinitonsResourceApiData } from './propertiesDefinitionsResourceApiData';

@Injectable()
export class PropertiesDefinitionService {

    private path: string;

    constructor(private http: Http) {
    }

    getPropertiesDefinitionsData(): Observable<PropertiesDefinitonsResourceApiData> {
        let headers = new Headers({ 'Accept': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http.get(backendBaseUri + this.path + 'propertiesdefinition/', options)
            .map(res => res.json());
    }

    setPath(path: string): void {
        this.path = path;
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
