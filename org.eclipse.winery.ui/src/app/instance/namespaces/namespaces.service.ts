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
import { NamespaceSelectorService } from '../../namespaceSelector/namespaceSelector.service';
import { Http } from '@angular/http';
import { Observable } from 'rxjs';

@Injectable()
export class NamespacesService {

    constructor(private http: Http, private namespaceService: NamespaceSelectorService) {
    }

    getAllNamespaces(): Observable<any[]> {
        return this.namespaceService.getAllNamespaces();
    };

    saveNamespaces() {

    }

}
