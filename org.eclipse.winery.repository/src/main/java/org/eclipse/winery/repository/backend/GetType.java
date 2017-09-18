/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.backend;

import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.common.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.PolicyTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.RequirementTypeId;
import org.eclipse.winery.common.interfaces.IWineryRepositoryCommon;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TCapabilityType;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TPolicyType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementType;

/**
 * Offers getType for each subclass {@link org.eclipse.winery.model.tosca.HasType}
 */
public class GetType {

	public static TArtifactType getType(IWineryRepositoryCommon repository, TArtifactTemplate template) {
		return repository.getElement(new ArtifactTypeId(template.getTypeAsQName()));
	}

	public static TCapabilityType getType(IWineryRepositoryCommon repository, TCapability template) {
		return repository.getElement(new CapabilityTypeId(template.getTypeAsQName()));
	}

	public static TNodeType getType(IWineryRepositoryCommon repository, TNodeTemplate template) {
		return repository.getElement(new NodeTypeId(template.getTypeAsQName()));
	}

	public static TNodeType getType(IWineryRepositoryCommon repository, TNodeTypeImplementation template) {
		return repository.getElement(new NodeTypeId(template.getTypeAsQName()));
	}

	public static TPolicyType getType(IWineryRepositoryCommon repository, TPolicyTemplate template) {
		return repository.getElement(new PolicyTypeId(template.getTypeAsQName()));
	}

	public static TRelationshipType getType(IWineryRepositoryCommon repository, TRelationshipTemplate template) {
		return repository.getElement(new RelationshipTypeId(template.getTypeAsQName()));
	}

	public static TRelationshipType getType(IWineryRepositoryCommon repository, TRelationshipTypeImplementation template) {
		return repository.getElement(new RelationshipTypeId(template.getTypeAsQName()));
	}

	public static TRequirementType getType(IWineryRepositoryCommon repository, TRequirement template) {
		return repository.getElement(new RequirementTypeId(template.getTypeAsQName()));
	}

	public static TEntityType getType(IWineryRepositoryCommon repository, TEntityTemplate template) {
		if (template instanceof TArtifactTemplate) {
			return getType(repository, (TArtifactTemplate) template);
		} else if (template instanceof TCapability) {
			return getType(repository, (TCapability) template);
		} else if (template instanceof TNodeTemplate) {
			return getType(repository, (TNodeTemplate) template);
		} else if (template instanceof TPolicyTemplate) {
			return getType(repository, (TPolicyTemplate) template);
		} else if (template instanceof TRelationshipTemplate) {
			return getType(repository, (TRelationshipTemplate) template);
		}

		return null;
	}
}
