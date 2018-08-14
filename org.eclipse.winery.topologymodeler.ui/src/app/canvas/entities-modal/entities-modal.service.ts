/********************************************************************************
 * Copyright(c) 2017 Contributors to the Eclipse Foundation
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
import { backendBaseURL } from '../../models/configuration';
import { Observable } from 'rxjs/Rx';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Subject } from 'rxjs';
import { ModalVariant } from './modal-model';
import { TopologyModelerConfiguration } from '../../models/topologyModelerConfiguration';

@Injectable()
export class EntitiesModalService {

    readonly headers = new HttpHeaders({'Accept': 'application/json'});

    openModalEvent = new Subject<OpenModalEvent>();
    openModalEvent$ = this.openModalEvent.asObservable();

    configuration: TopologyModelerConfiguration;

    constructor(private http: HttpClient) {
    }

    /**
     * Requests all namespaces from the backend
     * @returns json of namespaces
     */
    requestNamespaces(all: boolean = false): Observable<any> {
        let URL: string;
        if (all) {
            URL = backendBaseURL + '/admin/namespaces/?all';
        } else {
            URL = backendBaseURL + '/admin/namespaces/';
        }
        return this.http.get(URL, {headers: this.headers});
    }

}

export class OpenModalEvent {
    constructor(public currentNodeId: string,
                public modalVariant: ModalVariant,
                public modalName: string,
                public modalTemplateName: string,
                public modalTemplateNameSpace: string,
                public modalType: string) {
    }
}
