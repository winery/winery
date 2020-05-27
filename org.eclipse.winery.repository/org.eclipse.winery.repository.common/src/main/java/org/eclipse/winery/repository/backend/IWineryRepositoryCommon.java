/*******************************************************************************
 * Copyright (c) 2013-2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.backend;

import java.io.IOException;

import org.eclipse.winery.model.ids.GenericId;
import org.eclipse.winery.model.ids.Namespace;
import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.model.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.model.ids.definitions.ComplianceRuleId;
import org.eclipse.winery.model.ids.definitions.DataTypeId;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.ids.extensions.PatternRefinementModelId;
import org.eclipse.winery.model.ids.extensions.TestRefinementModelId;
import org.eclipse.winery.model.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.model.ids.definitions.PolicyTypeId;
import org.eclipse.winery.model.ids.extensions.RefinementId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.model.ids.definitions.RequirementTypeId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.ids.extensions.TopologyFragmentRefinementModelId;
import org.eclipse.winery.model.tosca.extensions.OTTopologyFragmentRefinementModel;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TCapabilityType;
import org.eclipse.winery.model.tosca.extensions.OTComplianceRule;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTTestRefinementModel;
import org.eclipse.winery.model.tosca.TDataType;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
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
import org.eclipse.winery.model.tosca.TServiceTemplate;

/**
 * Enables access to the winery repository via Ids defined in package {@link org.eclipse.winery.model.ids}
 * <p>
 * Methods are moved from @see org.eclipse.winery.repository.backend.IGenericRepository to here as soon there is an
 * implementation for them. The ultimate goal is to eliminate IGenericRepository
 * <p>
 * These methods are shared between {@link IWineryRepository} and @see org.eclipse.winery.repository.backend.IRepository
 */
public interface IWineryRepositoryCommon {

    /**
     * Loads the TDefinition element belonging to the given id.
     * <p>
     * Even if the given id does not exist in the repository (<code>!exists(id)</code>), an empty wrapper definitions
     * with an empty element is generated
     *
     * @param id the DefinitionsChildId to load
     * @return the definitions belonging to the id
     * @throws IllegalStateException if repository cannot provide the content (e.g., due to file reading errors)
     */
    TDefinitions getDefinitions(DefinitionsChildId id);

    // in case one needs a new element, just copy and paste one of the following methods and adapt it.

    default <T extends DefinitionsChildId, S extends TExtensibleElements> S getElement(T id) {
        return (S) this.getDefinitions(id).getElement();
    }

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
    
    default TDataType getElement(DataTypeId id) {
        return (TDataType) this.getDefinitions(id).getElement();
    }

    default TRequirementType getElement(RequirementTypeId id) {
        return (TRequirementType) this.getDefinitions(id).getElement();
    }

    default TPolicyType getElement(PolicyTypeId id) {
        return (TPolicyType) this.getDefinitions(id).getElement();
    }

    default OTComplianceRule getElement(ComplianceRuleId id) {
        return (OTComplianceRule) this.getDefinitions(id).getElement();
    }

    default OTPatternRefinementModel getElement(PatternRefinementModelId id) {
        return (OTPatternRefinementModel) this.getDefinitions(id).getElement();
    }
    
    default OTTopologyFragmentRefinementModel getElement(TopologyFragmentRefinementModelId id) {
        return (OTTopologyFragmentRefinementModel) this.getDefinitions(id).getElement();
    }

    default OTTestRefinementModel getElement(TestRefinementModelId id) {
        return (OTTestRefinementModel) this.getDefinitions(id).getElement();
    }

    default OTRefinementModel getElement(RefinementId id) {
        return (OTRefinementModel) this.getDefinitions(id).getElement();
    }

    /**
     * Deletes the TOSCA element <b>and all sub elements</b> referenced by the given id from the repository
     * <p>
     * We assume that each id is a directory
     */
    void forceDelete(GenericId id) throws IOException;

    /**
     * Renames a definition child id
     *
     * @param oldId the old id
     * @param newId the new id
     */
    default void rename(DefinitionsChildId oldId, DefinitionsChildId newId) throws IOException {
        this.duplicate(oldId, newId);
        this.forceDelete(oldId);
    }

    /**
     * Copies a definition and renames it to the newId.
     *
     * @param from  the source id
     * @param newId the destination id
     */
    void duplicate(DefinitionsChildId from, DefinitionsChildId newId) throws IOException;

    /**
     * Deletes all definition children nested in the given namespace
     *
     * @param definitionsChildIdClazz the type of definition children to delete
     * @param namespace               the namespace to delete
     */
    void forceDelete(Class<? extends DefinitionsChildId> definitionsChildIdClazz, Namespace namespace) throws IOException;

    /**
     * Returns the stored type for the given template
     *
     * @param template the template to determine the type for
     **/
    default TEntityType getTypeForTemplate(TEntityTemplate template) {
        if (template instanceof TArtifactTemplate) {
            return getType(((TArtifactTemplate) template));
        } else if (template instanceof TCapability) {
            return getType(((TCapability) template));
        } else if (template instanceof TNodeTemplate) {
            return getType((TNodeTemplate) template);
        } else if (template instanceof TPolicyTemplate) {
            return getType((TPolicyTemplate) template);
        } else if (template instanceof TRelationshipTemplate) {
            return getType((TRelationshipTemplate) template);
        }
        return null;
    }

    default TArtifactType getType(TArtifactTemplate template) {
        return getElement(new ArtifactTypeId(template.getTypeAsQName()));
    }

    default TCapabilityType getType(TCapability template) {
        return getElement(new CapabilityTypeId(template.getTypeAsQName()));
    }

    default TNodeType getType(TNodeTemplate template) {
        return getElement(new NodeTypeId(template.getTypeAsQName()));
    }

    default TNodeType getType(TNodeTypeImplementation template) {
        return getElement(new NodeTypeId(template.getTypeAsQName()));
    }

    default TPolicyType getType(TPolicyTemplate template) {
        return getElement(new PolicyTypeId(template.getTypeAsQName()));
    }

    default TRelationshipType getType(TRelationshipTemplate template) {
        return getElement(new RelationshipTypeId(template.getTypeAsQName()));
    }

    default TRelationshipType getType(TRelationshipTypeImplementation template) {
        return getElement(new RelationshipTypeId(template.getTypeAsQName()));
    }

    default TRequirementType getType(TRequirement template) {
        return getElement(new RequirementTypeId(template.getTypeAsQName()));
    }
}
