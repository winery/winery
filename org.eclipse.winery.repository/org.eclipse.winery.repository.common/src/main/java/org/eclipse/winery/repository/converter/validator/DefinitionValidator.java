/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.converter.validator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.converter.support.Namespaces;
import org.eclipse.winery.model.converter.support.exception.MultiException;
import org.eclipse.winery.model.converter.support.exception.UndefinedField;
import org.eclipse.winery.model.converter.support.exception.UndefinedFile;
import org.eclipse.winery.repository.converter.validator.support.ExceptionVisitor;
import org.eclipse.winery.repository.converter.validator.support.Parameter;
import org.eclipse.winery.repository.converter.validator.support.Result;
import org.eclipse.winery.model.tosca.yaml.TArtifactDefinition;
import org.eclipse.winery.model.tosca.yaml.TImportDefinition;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;

public class DefinitionValidator extends ExceptionVisitor<Result, Parameter> {
    public final Path path;
    private DefinitionsVisitor definitionsVisitor;

    public DefinitionValidator(Path path) {
        definitionsVisitor = new DefinitionsVisitor(Namespaces.DEFAULT_NS, path);
        this.path = path;
    }

    public void validate(TServiceTemplate serviceTemplate) throws MultiException {
        serviceTemplate.accept(definitionsVisitor, new Parameter());
        serviceTemplate.accept(this, new Parameter());
        if (hasExceptions()) {
            throw getException();
        }
    }

    @Override
    public Result visit(TImportDefinition node, Parameter parameter) {
        if (!isDefined(node.getRepository(), definitionsVisitor.getRepositoryDefinitions())) {
            setException(new UndefinedField(
                    "Repository definition '{}' is undefined",
                    node.getRepository()
                ).setContext(parameter.getContext())
            );
        }
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TArtifactDefinition node, Parameter parameter) {
        String file = node.getFile();
        if (!Files.exists(path.resolve(file))) {
            setException(new UndefinedFile(
                "Artifact file '{}' is undefined",
                path.resolve(file)
            ));
        }

        return super.visit(node, parameter);
    }

    private Boolean isDefined(QName name, Map<String, List<String>> map) {
        return Objects.isNull(name) || map.containsKey(name.getNamespaceURI()) && map.get(name.getNamespaceURI()).contains(name.getLocalPart());
    }
}
