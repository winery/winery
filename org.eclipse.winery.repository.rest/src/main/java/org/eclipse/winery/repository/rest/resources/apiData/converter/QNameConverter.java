/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.apiData.converter;

import javax.xml.namespace.QName;

import org.eclipse.winery.repository.rest.resources.apiData.QNameApiData;

public class QNameConverter implements Converter<QNameApiData, QName> {

    @Override
    public QName unmarshal(QNameApiData v) {
        return new QName(v.namespace, v.localname);
    }

    @Override
    public QNameApiData marshal(QName v) {
        QNameApiData result = new QNameApiData();
        result.localname = v.getLocalPart();
        result.namespace = v.getNamespaceURI();
        return result;
    }

}
