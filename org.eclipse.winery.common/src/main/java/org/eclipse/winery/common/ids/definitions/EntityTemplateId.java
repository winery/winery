/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.common.ids.definitions;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XMLId;
import org.eclipse.winery.common.ids.elements.TOSCAElementId;

/**
 * ArtifactTemplates and PolicyTemplates are <em>directly nested</em> in a
 * Definitions element. RelationshipTemplates and NodeTemplates are not. When
 * approaching an EntityTemplateId, it is a thing directly nested in a
 * Definitions element.
 * 
 * The others have {@link TOSCAElementId} as parent
 */
public abstract class EntityTemplateId extends TOSCAComponentId {
	
	public EntityTemplateId(Namespace namespace, XMLId xmlId) {
		super(namespace, xmlId);
	}
	
	public EntityTemplateId(String ns, String id, boolean URLencoded) {
		super(ns, id, URLencoded);
	}
	
	public EntityTemplateId(QName qname) {
		super(qname);
	}
	
}
