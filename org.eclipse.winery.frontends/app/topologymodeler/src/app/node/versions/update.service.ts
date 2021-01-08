/********************************************************************************
 * Copyright(c) 2019 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { UpdateInfo } from '../../models/UpdateInfo';
import { TopologyModelerConfiguration } from '../../models/topologyModelerConfiguration';
import { Observable } from 'rxjs';
import { BackendService } from '../../services/backend.service';
import { TTopologyTemplate } from '../../models/ttopology-template';
import { PropertyDiffList } from '../../models/propertyDiffList';

@Injectable()
export class UpdateService {

    configuration: TopologyModelerConfiguration;

    headers = new HttpHeaders().set('Content-Type', 'application/json');

    url = this.backendService.configuration.repositoryURL
        + '/' + this.backendService.configuration.parentPath + '/'
        + encodeURIComponent(encodeURIComponent(this.backendService.configuration.ns))
        + '/' + this.backendService.configuration.id
        + '/' + this.backendService.configuration.elementPath;

    constructor(private http: HttpClient,
                private backendService: BackendService) {

    }

    update(updateInfo: UpdateInfo): Observable<TTopologyTemplate> {
        const url = this.url + '/update';
        return this.http.post<TTopologyTemplate>(url, updateInfo, { headers: this.headers });
    }

    getKVComparison(updateInfo: UpdateInfo): Observable<PropertyDiffList> {
        const url = this.url + '/kvcomparison';
        return this.http.post<PropertyDiffList>(url, updateInfo, { headers: this.headers });
    }

}
