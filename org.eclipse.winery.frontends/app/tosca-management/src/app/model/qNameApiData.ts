/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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

export class QNameApiData {

    static fromQName(qName: QName) {
        return new QNameApiData(qName.localName, qName.nameSpace);
    }

    constructor(public localname: string, public namespace: string) {
    }

    equals(item: QNameApiData) {
        return this.localname === item.localname && this.namespace === item.namespace;
    }
}
