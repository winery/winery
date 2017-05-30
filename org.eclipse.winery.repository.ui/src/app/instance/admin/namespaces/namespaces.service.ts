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
import { WineryNamespaceSelectorService } from '../../../wineryNamespaceSelector/wineryNamespaceSelector.service';
import { Headers, Http, RequestOptions, Response } from '@angular/http';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';
import { Router } from '@angular/router';
import { NamespaceWithPrefix } from '../../../wineryInterfaces/namespaceWithPrefix';

@Injectable()
export class NamespacesService {

    private path: string;

    constructor(private http: Http,
                private namespaceService: WineryNamespaceSelectorService,
                private route: Router) {
        this.path = decodeURIComponent(this.route.url);
    }

    getAllNamespaces(): Observable<any[]> {
        return this.namespaceService.getAllNamespaces();
    };

    postNamespaces(namespaces: NamespaceWithPrefix[]): Observable<Response> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.post(backendBaseURL + this.path + '/', JSON.stringify(namespaces), options);
    }

}
