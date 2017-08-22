/*******************************************************************************
 * Copyright (c) 2013-2016 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Lukas Harzenetter, Nicole Keppler - forceDelete for Namespaces
 *     Tino Stadlmaier, Philipp Meyer - rename for id/namespace
 *******************************************************************************/
package org.eclipse.winery.common.interfaces;

import java.io.IOException;

import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.common.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.RequirementTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TCapabilityType;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TServiceTemplate;

/**
 * Enables access to the winery repository via Ids defined in package {@link org.eclipse.winery.common.ids}
 *
 * Methods are moved from {@link org.eclipse.winery.repository.backend.IGenericRepository} to here as soon there is an
 * implementation for them. The ultimate goal is to eliminate IGenericRepository
 *
 * These methods are shared between {@link IWineryRepository} and {@link org.eclipse.winery.repository.backend.IRepository}
 */
public interface IWineryRepositoryCommon {

	/**
	 * Loads the TDefinition element belonging to the given id.
	 *
	 * Even if the given id does not exist in the repository (<code>!exists(id)</code>), an empty wrapper definitions
	 * with an empty element is generated
	 *
	 * @param id the TOSCAComponentId to load
	 */
	Definitions getDefinitions(TOSCAComponentId id);

	// in case one needs a new element, just copy and paste one of the following methods and adapt it.

	default TNodeTypeImplementation getElement(NodeTypeImplementationId id) {
		return (TNodeTypeImplementation) this.getDefinitions(id).getElement();
	}

	default TRelationshipTypeImplementation getElement(RelationshipTypeImplementationId id) {
		return (TRelationshipTypeImplementation) this.getDefinitions(id).getElement();
	}

	default TNodeType getElement(NodeTypeId id) {
		return (TNodeType) this.getDefinitions(id).getElement();
	}

	default TRelationshipType getElement(RelationshipTypeId id) {
		return (TRelationshipType) this.getDefinitions(id).getElement();
	}

	default TServiceTemplate getElement(ServiceTemplateId id) {
		return (TServiceTemplate) this.getDefinitions(id).getElement();
	}

	default TArtifactTemplate getElement(ArtifactTemplateId id) {
		return (TArtifactTemplate) this.getDefinitions(id).getElement();
	}

	default TArtifactType getElement(ArtifactTypeId id) {
		return (TArtifactType) this.getDefinitions(id).getElement();
	}

	default TPolicyTemplate getElement(PolicyTemplateId id) {
		return (TPolicyTemplate) this.getDefinitions(id).getElement();
	}

	default TCapabilityType getElement(CapabilityTypeId id) {
		return (TCapabilityType) this.getDefinitions(id).getElement();
	}

	default TRequirementType getElement(RequirementTypeId id) {
		return (TRequirementType) this.getDefinitions(id).getElement();
	}


	/**
	 * Deletes the TOSCA element <b>and all sub elements</b> referenced by the given id from the repository
	 *
	 * We assume that each id is a directory
	 */
	void forceDelete(GenericId id) throws IOException;

	/**
	 * Renames a TOSCA component id
	 *
	 * @param oldId the old id
	 * @param newId the new id
	 */
	void rename(TOSCAComponentId oldId, TOSCAComponentId newId) throws IOException;

	/**
	 * Deletes all TOSCA components nested in the given namespace
	 *
	 * @param toscaComponentIdClazz the type of TOSCA components to delete
	 * @param namespace             the namespace to delete
	 */
	void forceDelete(Class<? extends TOSCAComponentId> toscaComponentIdClazz, Namespace namespace) throws IOException;
}
