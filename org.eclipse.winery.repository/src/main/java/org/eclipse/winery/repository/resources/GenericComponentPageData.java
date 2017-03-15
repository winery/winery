/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation, improvements
 *     Lukas Harzenetter - added showAllItems member
 *     Nicole Keppler - Bugfixes, added get-Method for TOSCAComponentId
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

	private static final Logger LOGGER = LoggerFactory.getLogger(GenericComponentPageData.class);

	private final SortedSet<? extends TOSCAComponentId> componentInstanceIds;

	private final Class<? extends AbstractComponentsResource> resourceClass;
	private String namespace;
	private final boolean showAllItems;

	private final Class<? extends TOSCAComponentId> cIdClass;

	public GenericComponentPageData(Class<? extends AbstractComponentsResource> resourceClass, boolean showAllItems) {
		this.resourceClass = resourceClass;
		this.showAllItems = showAllItems;
		this.cIdClass = Utils.getComponentIdClassForComponentContainer(resourceClass);
		this.componentInstanceIds = Repository.INSTANCE.getAllTOSCAComponentIds(cIdClass);
	}

	public GenericComponentPageData(Class<? extends AbstractComponentsResource> resourceClass, String namespace) {
		this(resourceClass, true);
		this.namespace = namespace;
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

	public Class<? extends TOSCAComponentId> getTOSCAComponentId() {
		return Utils.getComponentIdClassForComponentContainer(this.resourceClass);
	}

	public String getCSSclass() {
		// The resources do NOT know their CSS class
		// Layout is far away from a resource
		// Instead of a huge if/else-cascade, we derive the CSS name from the
		// class name
		String type = this.getType();
		// convention: first letter in small letters
		//noinspection UnnecessaryLocalVariable
		String res = type.substring(0, 1).toLowerCase() + type.substring(1);
		// this generated "xSDImport" as CSS class for XSDImport
		return res;
	}

	public String getLabel() {
		String type = this.getType();
		// E.g., convert ArtifactTemplate to Artifact Template
		return type.replaceAll("(\\p{Lower})(\\p{Upper})", "$1 $2");
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
		if (this.resourceClass == ArtifactTemplatesResource.class) {
			typeIdClass = ArtifactTypeId.class;
		} else if (this.resourceClass == NodeTypeImplementationsResource.class) {
			typeIdClass = NodeTypeId.class;
		} else if (this.resourceClass == RelationshipTypeImplementationsResource.class) {
			typeIdClass = RelationshipTypeId.class;
		} else if (this.resourceClass == PolicyTemplatesResource.class) {
			typeIdClass = PolicyTypeId.class;
		} else {
			return Collections.emptyList();
		}
		return Repository.INSTANCE.getAllTOSCAComponentIds(typeIdClass);
	}

	public boolean isShowAllItems() {
		return showAllItems;
	}

	public String getNamespace() {
		return namespace;
	}

	public Class<? extends TOSCAComponentId> getTOSCAComponentIdClass() {
		return cIdClass;
	}
}
