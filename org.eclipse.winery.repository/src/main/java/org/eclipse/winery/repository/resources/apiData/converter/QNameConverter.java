/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Nicole Keppler - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.apiData.converter;

import javax.xml.namespace.QName;

import org.eclipse.winery.repository.resources.apiData.QNameApiData;

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
