/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
import { definitionType, urlElement } from '../models/enums';
import { QName } from '../models/qname';
import { Observable } from 'rxjs/Rx';
import { backendBaseURL } from '../models/configuration';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable()
export class ReqCapService {
    // Logic for fetching the requirement, capability definitions of a node type
    readonly options = {
        headers: new HttpHeaders({
            'Accept': 'application/json'
        })
    };

    constructor(private http: HttpClient) {
    }

    /**
     * Requests all requirement definitions of a node type from the backend
     * @param   nodeType - the node type of the node template
     * @returns
     */
    requestRequirementDefinitionsOfNodeType(nodeType: string): Observable<any> {
        const qName = new QName(nodeType);
        const url = backendBaseURL + urlElement.NodeTypeURL +
            encodeURIComponent(encodeURIComponent(qName.nameSpace)) + '/' + qName.localName + definitionType.RequirementDefinitions;
        return this.http.get(url, this.options);
    }

    /**
     * Requests all capability definitions of a node type from the backend
     * @param   nodeType - the node type of the node template
     * @returns
     */
    requestCapabilityDefinitionsOfNodeType(nodeType: string): Observable<any> {
        const qName = new QName(nodeType);
        const url = backendBaseURL + urlElement.NodeTypeURL +
            encodeURIComponent(encodeURIComponent(qName.nameSpace)) + '/' + qName.localName + definitionType.CapabilityDefinitions;
        return this.http.get(url, this.options);
    }
}
