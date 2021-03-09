/*******************************************************************************
 * Copyright (c) 2019-2021 Contributors to the Eclipse Foundation
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
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BackendService } from '../services/backend.service';
import { TTopologyTemplate } from '../models/ttopology-template';
import 'rxjs/add/operator/mergeMap';

export interface EdmmTechnologyTransformationCheck {
    id: string;
    name: string;
    supports: number;
    replacementRules: ReplacementRules[];
}

interface ReplacementRules {
    reason: string;
    unsupportedComponents: string[];
    toTopology: Map<string, any>;
}

@Injectable()
export class EdmmTransformationCheckService {

    constructor(private http: HttpClient,
                private backendService: BackendService) {
    }

    doTransformationCheck(topologyTemplate: TTopologyTemplate): Observable<EdmmTechnologyTransformationCheck[]> {
        const edmmUrl = this.backendService.configuration.parentElementUrl + 'edmm/check-model-support';
        return this.backendService.saveTopologyTemplate(topologyTemplate)
            .flatMap(() =>
                // this will directly return the info about the supported technologies
                this.http.get<EdmmTechnologyTransformationCheck[]>(edmmUrl)
            );
    }

    /**
     * The function will call the transformation util of edmm and it will give back a zip
     * with the deployment specific files
     */
    doTransformation(target: String) {
        const edmmUrl = this.backendService.configuration.parentElementUrl
            + 'edmm/transform?target=' + target;
        window.open(edmmUrl, '_blank');
    }

    /**
     * Returns a map with the edmm types as keys and the qname types
     * as values, e.g., { hosted_on: HostedOn, web_server: Web_Server } etc.
     */
    getOneToOneMap(): Observable<Map<string, string>> {
        const edmmUrl = this.backendService.configuration.parentElementUrl + 'edmm/one-to-one-map';
        return this.http.get<Map<string, string>>(edmmUrl);
    }
}
