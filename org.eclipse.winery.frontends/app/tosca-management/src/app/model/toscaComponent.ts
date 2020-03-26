/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
import { isNullOrUndefined } from 'util';
import { ToscaTypes } from './enums';
import { backendBaseURL } from '../configuration';
import { Utils } from '../wineryUtils/utils';

export class ToscaComponent {

    readonly localNameWithoutVersion: string;
    readonly backendPath: string;
    readonly path: string;
    readonly xmlPath: string;
    readonly yamlPath: string;
    readonly xmlCsarPath: string;
    readonly provenanceCsarPath: string;
    readonly yamlCsarPath: string;
    readonly edmmExportPath: string;

    constructor(public readonly toscaType: ToscaTypes,
                public readonly namespace: string,
                public readonly localName: string,
                public readonly xsdSchemaType: string = null) {
        this.path = '/' + this.toscaType;
        if (this.namespace) {
            this.path += '/' + encodeURIComponent(encodeURIComponent(this.namespace));
            if (this.localName) {
                this.path += '/' + this.localName;
                this.backendPath = backendBaseURL + this.path;
                this.xmlPath = this.backendPath + '/?xml';
                this.yamlPath = this.backendPath + '/?yaml';
                this.xmlCsarPath = this.backendPath + '/?csar';
                this.provenanceCsarPath = this.xmlCsarPath + '&addToProvenance';
                this.yamlCsarPath = this.backendPath + '/?yaml&csar';
                this.edmmExportPath = this.backendPath + '/?edmm';
            }
        }
        if (this.localName) {
            this.localNameWithoutVersion = Utils.getNameWithoutVersion(this.localName);
        }
    }

    getQName(): string {
        return '{' + this.namespace + '}' + this.localName;
    }
}
