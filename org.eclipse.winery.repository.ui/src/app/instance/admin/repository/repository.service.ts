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
import { Http } from '@angular/http';
import { backendBaseURL } from '../../../configuration';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

@Injectable()
export class RepositoryService {

    path: string;

    constructor(private http: Http,
                private route: Router) {
        this.path = decodeURIComponent(this.route.url);
    }

    clearRepository(): Observable<any> {
        return this.http.delete(backendBaseURL + this.path + '/');
    }

}
