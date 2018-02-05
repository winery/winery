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

import {Injectable} from '@angular/core';
import {Headers, Http, RequestOptions, Response} from '@angular/http';
import {Router} from '@angular/router';
import {Observable} from 'rxjs';
import {backendBaseURL} from '../../../configuration';
import {ImplementationAPIData} from './implementationAPIData';
import {ImplementationWithTypeAPIData} from './implementationWithTypeAPIData';
import {InstanceService} from '../../instance.service';
import {Utils} from '../../../wineryUtils/utils';

@Injectable()
export class ImplementationService {

    private implementationOrTemplateType: string;

    constructor(private http: Http,
                private route: Router, private sharedData: InstanceService) {
        this.implementationOrTemplateType = '/' + Utils.getImplementationOrTemplateOfType(this.sharedData.toscaComponent.toscaType) + '/';
    }

    getImplementationData(): Observable<ImplementationAPIData[]> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});
        return this.http.get(backendBaseURL + this.route.url + '/', options)
            .map(res => res.json());
    }

    postImplementation(implApiData: ImplementationWithTypeAPIData): Observable<Response> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});
        return this.http.post(backendBaseURL + this.implementationOrTemplateType, JSON.stringify(implApiData), options);
    }

    deleteImplementations(implToDelete: ImplementationAPIData) {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});
        const pathAddition = this.implementationOrTemplateType
            + encodeURIComponent(encodeURIComponent(implToDelete.namespace)) + '/'
            + implToDelete.localname + '/';
        return this.http.delete(backendBaseURL + pathAddition, options);
    }
}
