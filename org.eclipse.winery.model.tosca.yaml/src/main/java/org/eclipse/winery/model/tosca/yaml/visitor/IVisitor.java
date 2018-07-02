/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.tosca.yaml.visitor;

import org.eclipse.winery.model.tosca.yaml.*;
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
