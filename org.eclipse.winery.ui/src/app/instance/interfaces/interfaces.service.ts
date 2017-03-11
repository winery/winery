/**
 * Copyright (c) 2017 University of Stuttgart.
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
import { Http, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs';
import { backendBaseUri } from '../../configuration';
import { InterfacesApiData } from './InterfacesApiData';

@Injectable()
export class InterfacesService {

    private path: string;

    constructor(private http: Http) {
    }

    setPath(path: string): void {
        this.path = path;
    }

    getInterfaces(): Observable<InterfacesApiData[]> {
        let headers = new Headers({'Accept': 'application/json'});
        let options = new RequestOptions({headers: headers});

        return this.http.get(backendBaseUri + this.path + '/interfaces/', options)
            .map(res => res.json());
    }

    save(interfacesData: InterfacesApiData[]) {
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        return this.http.post(backendBaseUri + this.path + '/interfaces/', JSON.stringify(interfacesData), options);
    }
}
