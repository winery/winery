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
import { Component, OnInit, Input } from '@angular/core';
import { ThreatAssessmentService } from './threatAssessment.service';
import { Utils } from '../../../wineryUtils/utils';
import { QName } from '../../../../../../shared/src/app/model/qName';

export interface ThreatProperties {
    description?: string;
}

export interface ThreatTarget {
    nodeType: string;
    nodeTemplate: string;
}

export interface ThreatInterface {
    name: string;
    mitigations?: [QName];
    severity?: string;
    properties?: ThreatProperties;
    targets?: [ThreatTarget];
}

@Component({
    selector: 'winery-threat-model',
    templateUrl: './threatAssessment.component.html',
    providers: [ThreatAssessmentService]
})

export class ThreatAssessmentComponent implements OnInit {
    @Input() threatAssessmentData = true;
    loading = true;
    threats: ThreatInterface[];
    svnfs: QName[];

    s2Q = QName.stringToQName;
    nodeTypeUrlForQName = Utils.nodeTypeUrlForQName;
    nodeTypeURL = Utils.nodeTypeURL;

    constructor(private service: ThreatAssessmentService) {
    }

    ngOnInit() {
        if (this.threatAssessmentData) {
            this.service.getThreatData()
                .subscribe(
                    data => this.handleThreatData(data),
                    error => this.handleError(error)
                );
        } else {
            this.loading = false;
        }
    }

    private handleThreatData(json: any) {
        this.loading = false;
        this.threatAssessmentData = json;
        const parsed = JSON.parse(json);
        this.threats = Object.keys(parsed.threats).map(name => {
            parsed.threats[name].name = name;
            parsed.threats[name].mitigations = parsed.threats[name].mitigations.map(QName.stringToQName);

            return <ThreatInterface>parsed.threats[name];
        });

        this.svnfs = parsed.svnfs.map(QName.stringToQName);
    }

    isMitigated(mitigation: QName): boolean {
        const mitigationSVNF = this.svnfs.find(svnf => svnf.nameSpace === mitigation.nameSpace && svnf.localName === mitigation.localName);
        const exists = typeof mitigationSVNF === 'undefined';
        return !exists;
    }

    private handleError(json: any) {
        this.loading = false;
        this.threatAssessmentData = json;
    }
}
