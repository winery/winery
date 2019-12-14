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

package org.eclipse.winery.repository.rest.resources.apiData;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;

@XmlRootElement(name = "QName")
public class QNameApiData {
    public String localname;
    public String namespace;

    public QNameApiData() {
    }

    public QNameApiData(DefinitionsChildId newId) {
        this.localname = newId.getXmlId().getDecoded();
        this.namespace = newId.getNamespace().getDecoded();
    }

    public QName asQName() {
        return new QName(namespace, localname);
    }

    public static QNameApiData fromQName(QName original) {
        QNameApiData result = new QNameApiData();
        result.localname = original.getLocalPart();
        result.namespace = original.getNamespaceURI();

        return result;
    }
}
