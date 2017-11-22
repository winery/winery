/********************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.yaml.common.validator;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.TImportDefinition;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.yaml.common.Namespaces;
import org.eclipse.winery.yaml.common.exception.MissingRepositoryDefinition;
import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.validator.support.ExceptionVisitor;
import org.eclipse.winery.yaml.common.validator.support.Parameter;
import org.eclipse.winery.yaml.common.validator.support.Result;

public class DefinitionValidator extends ExceptionVisitor<Result, Parameter> {
    private DefinitionsVisitor definitionsVisitor;

    public DefinitionValidator(Path path) {
        definitionsVisitor = new DefinitionsVisitor(Namespaces.DEFAULT_NS, path);
    }

    public void validate(TServiceTemplate serviceTemplate) throws MultiException {
        definitionsVisitor.visit(serviceTemplate, new Parameter());
        if (hasExceptions()) {
            throw getException();
        }
    }

    @Override
    public Result visit(TImportDefinition node, Parameter parameter) {
        if (!isDefined(node.getRepository(), definitionsVisitor.getRepositoryDefinitions())) {
            String msg = "No Repository definition for property repository \"" +
                node.getRepository() + "\" found! \n" + print(parameter.getContext());
            setException(new MissingRepositoryDefinition(msg));
        }
        return super.visit(node, parameter);
    }

    private Boolean isDefined(QName name, Map<String, List<String>> map) {
        return name == null || map.containsKey(name.getNamespaceURI()) && map.get(name.getNamespaceURI()).contains(name.getLocalPart());
    }

    private String print(List<String> list) {
        return "Context::INLINE = " + String.join(":", list);
    }
}
