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
package org.eclipse.winery.repository.resources;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;

import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.PolicyTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.resources.entitytemplates.artifacttemplates.ArtifactTemplatesResource;
import org.eclipse.winery.repository.resources.entitytemplates.policytemplates.PolicyTemplatesResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.nodetypeimplementations.NodeTypeImplementationsResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.relationshiptypeimplementations.RelationshipTypeImplementationsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GenericComponentPageData {
	
	private static final Logger logger = LoggerFactory.getLogger(GenericComponentPageData.class);
	
	private final SortedSet<? extends TOSCAComponentId> componentInstanceIds;
	
	private final Class<? extends AbstractComponentsResource> resourceClass;
	
	
	public GenericComponentPageData(Class<? extends AbstractComponentsResource> resourceClass) {
		this.resourceClass = resourceClass;
		Class<? extends TOSCAComponentId> cIdClass = Utils.getComponentIdClassForComponentContainer(resourceClass);
		this.componentInstanceIds = Repository.INSTANCE.getAllTOSCAComponentIds(cIdClass);
	}
	
	/**
	 * Outputs the data for GenericComponentPage (Name / Id / Namespace) needed
	 * for the genericcomponentpage.jsp
	 */
	public SortedSet<? extends TOSCAComponentId> getComponentInstanceIds() {
		return this.componentInstanceIds;
	}
	
	public String getType() {
		return Utils.getTypeForComponentContainer(this.resourceClass);
	}
	
	public String getCSSclass() {
		// The resources do NOT know their CSS class
		// Layout is far away from a resource
		// Instead of a huge if/else-cascade, we derive the CSS name from the
		// class name
		String type = this.getType();
		// convention: first letter in small letters
		String res = type.substring(0, 1).toLowerCase() + type.substring(1);
		// this generated "xSDImport" as CSS class for XSDImport
		return res;
	}
	
	public String getLabel() {
		String type = this.getType();
		// E.g., convert ArtifactTemplate to Artifact Template
		String res = type.replaceAll("(\\p{Lower})(\\p{Upper})", "$1 $2");
		return res;
	}
	
	/**
	 * Required for genericcomponentpage.jsp -> addComponentInstance.jsp
	 * 
	 * May only be used if the component supports the type (e.g., artifact
	 * templates)
	 * 
	 * @return the list of all known <em>types</em>
	 */
	public Collection<? extends TOSCAComponentId> getTypeSelectorData() {
		Class<? extends TOSCAComponentId> typeIdClass;
		if (this.resourceClass.equals(ArtifactTemplatesResource.class)) {
			typeIdClass = ArtifactTypeId.class;
		} else if (this.resourceClass.equals(NodeTypeImplementationsResource.class)) {
			typeIdClass = NodeTypeId.class;
		} else if (this.resourceClass.equals(RelationshipTypeImplementationsResource.class)) {
			typeIdClass = RelationshipTypeId.class;
		} else if (this.resourceClass.equals(PolicyTemplatesResource.class)) {
			typeIdClass = PolicyTypeId.class;
		} else {
			return Collections.emptyList();
		}
		SortedSet<? extends TOSCAComponentId> allTOSCAcomponentIds = Repository.INSTANCE.getAllTOSCAComponentIds(typeIdClass);
		return allTOSCAcomponentIds;
	}
	
}
