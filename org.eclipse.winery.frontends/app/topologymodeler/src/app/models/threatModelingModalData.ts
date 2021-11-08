/********************************************************************************
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
 ********************************************************************************/
import { ThreatCreation } from './threatCreation';
import { QName } from '../../../../shared/src/app/model/qName';

/**
 * Encompasses the threat modelling data defined by the user when using the modal
 */

export interface ThreatAssessmentApiData {
    msg: string;
    svnfs: Array<string>;
    threats: {
        [qname: string]: {
            mitigations: Array<string>;
            templateName: string,
            namespace: string,
            properties: ThreatProperties,
            targets: [
                {
                    nodeTemplate: string,
                    nodeType: string
                }
                ]

        }
    };
}

export interface ThreatProperties {
    description: string;
    severity: string;
    strideClassification: string;
}

export interface Threat {
    templateName: string;
    namespace: string;
    properties: ThreatProperties;
}

export class ThreatModelingModalData {
    constructor(public threatCatalog: Array<Threat> = [],
                public mitigations: Set<QName> = new Set(),
                public threatAssessment: Object = {},
                public threatCreation: ThreatCreation = new ThreatCreation()) {
    }
}
