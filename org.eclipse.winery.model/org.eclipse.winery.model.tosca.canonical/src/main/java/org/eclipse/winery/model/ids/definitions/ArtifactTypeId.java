/*******************************************************************************
 * Copyright (c) 2013 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.ids.definitions;

import org.eclipse.winery.model.ids.Namespace;
import org.eclipse.winery.model.ids.XmlId;

import javax.xml.namespace.QName;
import java.util.Objects;

public class ArtifactTypeId extends EntityTypeId {

    public ArtifactTypeId(Namespace namespace, XmlId xmlId) {
        super(namespace, xmlId);
    }

    public ArtifactTypeId(String ns, String id, boolean URLencoded) {
        super(ns, id, URLencoded);
    }

    public ArtifactTypeId(QName qname) {
        this(new Namespace(Objects.requireNonNull(qname).getNamespaceURI(), false), new XmlId(Objects.requireNonNull(qname).getLocalPart(), false));
    }

    @Override
    public String getGroup() {
        return "ArtifactType";
    }
}
