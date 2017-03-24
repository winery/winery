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
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Observable } from 'rxjs';
import { backendBaseUri } from '../../configuration';
import { Router } from '@angular/router';
import { NamespaceWithPrefix } from '../../interfaces/namespaceWithPrefix';
import { InstanceService } from '../instance.service';

export class TypeWithShortName {
    type: string;
    shortName: string;

    constructor(pType: string, pShortName: string) {
        this.type = pType;
        this.shortName = pShortName;
    }
}

@Injectable()
export class TypeWithShortNameService {

    private path: string;

    constructor(private http: Http,
                private route: Router,
                private instanceService: InstanceService) {
        this.path = decodeURIComponent(this.route.url);
    }

    getAllTypes(): Observable<TypeWithShortName[]> {
        let headers = new Headers({'Accept': 'application/json'});
        let options = new RequestOptions({headers: headers});

        return this.http.get(backendBaseUri + this.path + '/', options)
                   .map(res => res.json());
    };

    postTypes(types: TypeWithShortName[]): Observable<Response> {
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        return this.http.post(backendBaseUri + this.path + '/', JSON.stringify(types), options);
    }

}
