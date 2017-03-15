/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Balzer - initial API and implementation
 */

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Headers, RequestOptions, Http, Response } from '@angular/http';
import { backendBaseUri } from '../../configuration';
import { Router } from '@angular/router';

@Injectable()
export class VisualAppearanceService {

    private path: string;

    constructor(private http: Http,
                private route: Router) {
        this.path = decodeURIComponent(this.route.url);
    }

    getImg16x16Path(): string {
        return backendBaseUri + this.path + '/16x16';
    }
    getImg50x50Path(): string {
        return backendBaseUri + this.path + '/50x50';
    }
}
