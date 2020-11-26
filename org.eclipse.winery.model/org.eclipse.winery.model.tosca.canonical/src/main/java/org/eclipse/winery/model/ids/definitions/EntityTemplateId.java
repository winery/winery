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
import org.eclipse.winery.model.ids.elements.ToscaElementId;

import javax.xml.namespace.QName;

/**
 * ArtifactTemplates, PolicyTemplates, and ServiceTemplates are
 * <em>directly nested</em> in a Definitions element. RelationshipTemplates and
 * NodeTemplates are not. When approaching an EntityTemplateId, it is a thing
 * directly nested in a Definitions element.
 * <p>
 * The others have {@link ToscaElementId} as parent
 */
public abstract class EntityTemplateId extends DefinitionsChildId {

    public EntityTemplateId(Namespace namespace, XmlId xmlId) {
        super(namespace, xmlId);
    }

    public EntityTemplateId(String ns, String id, boolean URLencoded) {
        super(ns, id, URLencoded);
    }

    public EntityTemplateId(QName qname) {
        super(qname);
    }

}
