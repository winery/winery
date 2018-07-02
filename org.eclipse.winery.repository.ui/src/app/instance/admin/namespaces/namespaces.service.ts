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
import { WineryNamespaceSelectorService } from '../../../wineryNamespaceSelector/wineryNamespaceSelector.service';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';
import { Router } from '@angular/router';
import { NamespaceWithPrefix } from '../../../wineryInterfaces/namespaceWithPrefix';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

@Injectable()
export class NamespacesService {

    private readonly path: string;

    constructor(private http: HttpClient,
                private namespaceService: WineryNamespaceSelectorService,
                private route: Router) {
        this.path = decodeURIComponent(this.route.url);
    }

    getAllNamespaces(): Observable<NamespaceWithPrefix[]> {
        return this.namespaceService.getNamespaces(true);
    }

    postNamespaces(namespaces: NamespaceWithPrefix[]): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http.post<string>(
            backendBaseURL + this.path + '/',
            JSON.stringify(namespaces),
            { headers: headers, observe: 'response' }
        );
    }

}
