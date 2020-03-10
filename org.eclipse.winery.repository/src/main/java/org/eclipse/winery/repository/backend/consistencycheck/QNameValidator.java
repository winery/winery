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

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.apache.commons.lang3.StringUtils;

public class QNameValidator extends Visitor {

    private final ErrorLogger errorLogger;

    public QNameValidator(ErrorLogger errorLogger) {
        this.errorLogger = errorLogger;
    }

    private void validateQName(QName qname) {
        if (qname != null && StringUtils.isEmpty(qname.getNamespaceURI())) {
            errorLogger.log(String.format("Referenced element \"%s\" is not a full QName", qname));
        }
    }

    @Override
    public void visit(TEntityType.PropertyDefinition propertiesDefinition) {
        final QName element = propertiesDefinition.getType();
        validateQName(element);
        super.visit(propertiesDefinition);
    }

    @Override
    public void visit(TEntityTemplate entityTemplate) {
        QName type = entityTemplate.getType();
        if (type == null) {
            errorLogger.log("type is null");
        }
        validateQName(type);
        super.visit(entityTemplate);
    }

    @Override
    public void visit(TDeploymentArtifact artifact) {
        QName type = artifact.getArtifactType();
        if (type == null) {
            errorLogger.log("type is null");
        }
        validateQName(type);
        super.visit(artifact);
    }

    @Override
    public void visit(TImplementationArtifact artifact) {
        QName type = artifact.getArtifactType();
        if (type == null) {
            errorLogger.log("type is null");
        }
        validateQName(type);
        super.visit(artifact);
    }

    public interface ErrorLogger {
        void log(String error);
    }
}
