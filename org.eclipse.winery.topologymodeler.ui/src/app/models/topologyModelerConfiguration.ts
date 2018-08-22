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
 *******************************************************************************/
import { QName } from './qname';

export class TopologyModelerConfiguration {

    public readonly webSocketUrl: string;
    public readonly definitionsElement: QName;

    constructor(public readonly id: string,
                public readonly ns: string,
                public readonly repositoryURL: string,
                public readonly uiURL: string,
                public readonly compareTo?: string,
                public readonly isReadonly?: boolean,
                public readonly parentPath = 'servicetemplates',
                public readonly elementPath = 'topologytemplate') {
        this.webSocketUrl = repositoryURL.replace(/(^https?)/, 'ws');
        this.definitionsElement = new QName('{' + ns + '}' + id);
    }
}
