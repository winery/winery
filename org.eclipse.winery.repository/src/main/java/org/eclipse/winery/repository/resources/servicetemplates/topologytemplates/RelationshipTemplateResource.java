/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.servicetemplates.topologytemplates;

import java.util.List;

import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.repository.resources.entitytemplates.TEntityTemplateResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;

public class RelationshipTemplateResource extends TEntityTemplateResource<TRelationshipTemplate> {
	
	private final TRelationshipTemplate relationshipTemplate;
	
	
	public RelationshipTemplateResource(TRelationshipTemplate relationshipTemplate, List<TRelationshipTemplate> list, int idx, ServiceTemplateResource res) {
		super(relationshipTemplate, list, idx, res);
		this.relationshipTemplate = relationshipTemplate;
	}
	
}
