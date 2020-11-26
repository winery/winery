/********************************************************************************
 * Copyright (c) 2018-2019 Contributors to the Eclipse Foundation
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
import { QName } from '../../../../shared/src/app/model/qName';

export class TopologyModelerConfiguration {

    public readonly webSocketUrl: string;
    public readonly definitionsElement: QName;
    public readonly idPrefix;
    public readonly relationshipPrefix;
    public readonly parentElementUrl: string;
    public readonly elementUrl: string;

    constructor(public readonly id: string,
                public readonly ns: string,
                public readonly repositoryURL: string,
                public readonly uiURL: string,
                public readonly compareTo?: string,
                public readonly isReadonly?: boolean,
                public readonly parentPath = 'servicetemplates',
                public readonly elementPath = 'topologytemplate',
                public readonly topologyProDecURL = 'http://localhost:8090') {
        this.webSocketUrl = repositoryURL.replace(/(^https?)/, 'ws');
        this.definitionsElement = new QName('{' + ns + '}' + id);

        if (this.elementPath === 'detector') {
            this.idPrefix = 'd_';
        } else if (this.elementPath === 'refinementstructure') {
            this.idPrefix = 'rs_';
        } else if (this.elementPath === 'testfragment') {
            this.idPrefix = 'test_';
        } else {
            this.idPrefix = '';
        }

        this.relationshipPrefix = this.idPrefix + 'con';
        this.parentElementUrl = this.repositoryURL + '/' + this.parentPath + '/'
            + encodeURIComponent(encodeURIComponent(this.ns)) + '/' + this.id + '/';
        this.elementUrl = this.parentElementUrl + this.elementPath;
    }
}
