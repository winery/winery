/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.model.tosca.yaml.visitor;

import org.eclipse.winery.model.tosca.yaml.TArtifactDefinition;
import org.eclipse.winery.model.tosca.yaml.TArtifactType;
import org.eclipse.winery.model.tosca.yaml.TAttributeAssignment;
import org.eclipse.winery.model.tosca.yaml.TAttributeDefinition;
import org.eclipse.winery.model.tosca.yaml.TCapabilityAssignment;
import org.eclipse.winery.model.tosca.yaml.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.yaml.TCapabilityType;
import org.eclipse.winery.model.tosca.yaml.TConstraintClause;
import org.eclipse.winery.model.tosca.yaml.TDataType;
import org.eclipse.winery.model.tosca.yaml.TEntityType;
import org.eclipse.winery.model.tosca.yaml.TEntrySchema;
import org.eclipse.winery.model.tosca.yaml.TGroupDefinition;
import org.eclipse.winery.model.tosca.yaml.TGroupType;
import org.eclipse.winery.model.tosca.yaml.TImplementation;
import org.eclipse.winery.model.tosca.yaml.TImportDefinition;
import org.eclipse.winery.model.tosca.yaml.TInterfaceAssignment;
import org.eclipse.winery.model.tosca.yaml.TInterfaceDefinition;
import org.eclipse.winery.model.tosca.yaml.TInterfaceType;
import org.eclipse.winery.model.tosca.yaml.TNodeFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.TNodeType;
import org.eclipse.winery.model.tosca.yaml.TOperationDefinition;
import org.eclipse.winery.model.tosca.yaml.TParameterDefinition;
import org.eclipse.winery.model.tosca.yaml.TPolicyDefinition;
import org.eclipse.winery.model.tosca.yaml.TPolicyType;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.TPropertyDefinition;
import org.eclipse.winery.model.tosca.yaml.TPropertyFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.TRelationshipAssignment;
import org.eclipse.winery.model.tosca.yaml.TRelationshipDefinition;
import org.eclipse.winery.model.tosca.yaml.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.yaml.TRelationshipType;
import org.eclipse.winery.model.tosca.yaml.TRepositoryDefinition;
import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.TRequirementDefinition;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.TSubstitutionMappings;
import org.eclipse.winery.model.tosca.yaml.TTopologyTemplateDefinition;
import org.eclipse.winery.model.tosca.yaml.TVersion;
import org.eclipse.winery.model.tosca.yaml.support.Metadata;

public interface IVisitor<R extends AbstractResult, P extends AbstractParameter> {
    R visit(TArtifactDefinition node, P parameter);

    R visit(TArtifactType node, P parameter);

    R visit(TAttributeAssignment node, P parameter);

    R visit(TAttributeDefinition node, P parameter);

    R visit(TCapabilityAssignment node, P parameter);

    R visit(TCapabilityDefinition node, P parameter);

    R visit(TCapabilityType node, P parameter);

    R visit(TConstraintClause node, P parameter);

    R visit(TDataType node, P parameter);

    R visit(TEntityType node, P parameter);

    R visit(TEntrySchema node, P parameter);

    R visit(TGroupDefinition node, P parameter);

    R visit(TGroupType node, P parameter);

    R visit(TImplementation node, P parameter);

    R visit(TImportDefinition node, P parameter);

    R visit(TInterfaceAssignment node, P parameter);

    R visit(TInterfaceDefinition node, P parameter);

    R visit(TInterfaceType node, P parameter);

    R visit(TNodeFilterDefinition node, P parameter);

    R visit(TNodeTemplate node, P parameter);

    R visit(TNodeType node, P parameter);

    R visit(TOperationDefinition node, P parameter);

    R visit(TParameterDefinition node, P parameter);

    R visit(TPolicyDefinition node, P parameter);

    R visit(TPolicyType node, P parameter);

    R visit(TPropertyAssignment node, P parameter);

    R visit(TPropertyDefinition node, P parameter);

    R visit(TPropertyFilterDefinition node, P parameter);

    R visit(TRelationshipAssignment node, P parameter);

    R visit(TRelationshipDefinition node, P parameter);

    R visit(TRelationshipTemplate node, P parameter);

    R visit(TRelationshipType node, P parameter);

    R visit(TRepositoryDefinition node, P parameter);

    R visit(TRequirementAssignment node, P parameter);

    R visit(TRequirementDefinition node, P parameter);

    R visit(TServiceTemplate node, P parameter);

    R visit(TSubstitutionMappings node, P parameter);

    R visit(TTopologyTemplateDefinition node, P parameter);

    R visit(TVersion node, P parameter);

    R visit(Metadata node, P parameter);
}
