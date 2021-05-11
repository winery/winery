/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.backend.consistencycheck;

import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TEntityTypeImplementation;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.apache.commons.lang3.StringUtils;

public class QNameValidator extends Visitor {

    private final ErrorLogger errorLogger;
    private final Map<QName, TExtensibleElements> allQNameToElementMapping;

    public QNameValidator(ErrorLogger errorLogger, Map<QName, TExtensibleElements> allQNameToElementMapping) {
        this.errorLogger = errorLogger;
        this.allQNameToElementMapping = allQNameToElementMapping;
    }

    private void validateQName(QName qname) {
        if (qname != null && StringUtils.isEmpty(qname.getNamespaceURI())) {
            errorLogger.log(String.format("Referenced element \"%s\" is not a full QName", qname));
        }
        if (qname != null && !allQNameToElementMapping.containsKey(qname)) {
            errorLogger.log(String.format("Referenced element \"%s\" does not exist!", qname));
        }
    }

    @Override
    public void visit(TEntityType.YamlPropertyDefinition propertiesDefinition) {
        final QName element = propertiesDefinition.getType();
        validateQName(element);
        super.visit(propertiesDefinition);
    }

    @Override
    public void visit(TEntityTemplate entityTemplate) {
        checkType(entityTemplate.getType());
        super.visit(entityTemplate);
    }

    @Override
    public void visit(TDeploymentArtifact artifact) {
        checkType(artifact.getArtifactType());
        super.visit(artifact);
    }

    @Override
    public void visit(TImplementationArtifact artifact) {
        checkType(artifact.getArtifactType());
        super.visit(artifact);
    }

    @Override
    public void visit(TEntityTypeImplementation implementation) {
        checkType(implementation.getTypeAsQName());
    }

    public interface ErrorLogger {
        void log(String error);
    }

    private void checkType(QName artifactType) {
        if (artifactType == null) {
            errorLogger.log("type is null");
        }
        validateQName(artifactType);
    }
}
