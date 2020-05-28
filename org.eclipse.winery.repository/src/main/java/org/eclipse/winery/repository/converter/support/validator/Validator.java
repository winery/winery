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
package org.eclipse.winery.repository.converter.support.validator;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import org.eclipse.winery.model.tosca.yaml.TImportDefinition;
import org.eclipse.winery.model.tosca.yaml.TInterfaceType;
import org.eclipse.winery.model.tosca.yaml.TOperationDefinition;
import org.eclipse.winery.model.tosca.yaml.TRepositoryDefinition;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.model.tosca.yaml.tosca.datatypes.Credential;
import org.eclipse.winery.repository.converter.support.Defaults;
import org.eclipse.winery.repository.converter.support.exception.InvalidToscaSyntax;
import org.eclipse.winery.repository.converter.support.exception.InvalidToscaVersion;
import org.eclipse.winery.repository.converter.support.exception.MultiException;
import org.eclipse.winery.repository.converter.support.exception.UndefinedFile;
import org.eclipse.winery.repository.converter.support.exception.UndefinedRequiredKeyname;
import org.eclipse.winery.repository.converter.support.exception.UndefinedToscaVersion;
import org.eclipse.winery.repository.converter.support.validator.support.ExceptionVisitor;
import org.eclipse.winery.repository.converter.support.validator.support.Parameter;
import org.eclipse.winery.repository.converter.support.validator.support.Result;

public class Validator extends ExceptionVisitor<Result, Parameter> {
    private final Path path;

    public Validator(Path path) {
        this.path = path;
    }

    public void validate(TServiceTemplate serviceTemplate, String namespace) throws MultiException {
        if (Objects.isNull(serviceTemplate)) return;

        TypeValidator typeValidator = new TypeValidator(path, namespace);
        typeValidator.validate(serviceTemplate);

        DefinitionValidator definitionValidator = new DefinitionValidator(path);
        definitionValidator.validate(serviceTemplate);

        serviceTemplate.accept(this, new Parameter());

        if (this.hasExceptions()) throw this.getException();
    }

    @Override
    public Result visit(TInterfaceType node, Parameter parameter) {
        if (Objects.nonNull(node.getOperations())) {
            for (Map.Entry<String, TOperationDefinition> entry : node.getOperations().entrySet()) {
                if (Objects.nonNull(entry.getValue().getImplementation())) {
                    setException(new InvalidToscaSyntax(
                            "The InterfaceType '{}' MUST NOT include any implementations for defined operations",
                            parameter.getKey()
                        ).setContext(parameter.getContext())
                    );
                }
            }
        }
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TImportDefinition node, Parameter parameter) {
        if (Objects.isNull(node.getFile()) || node.getFile().isEmpty()) {
            setException(new UndefinedFile("Field 'file' is undefined")
                .setContext(parameter.getContext())
            );
        }
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TServiceTemplate node, Parameter parameter) {
        if (Objects.isNull(node.getToscaDefinitionsVersion())) {
            setException(new UndefinedToscaVersion(
                    "The field 'tosca_definition_version' is undefined"
                ).setContext(parameter.getContext())
            );
        }

        if (!node.getToscaDefinitionsVersion().matches(Defaults.TOSCA_DEFINITIONS_VERSION_PATTERN)) {
            setException(new InvalidToscaVersion(
                    "The value '{}' is invalid for 'tosca_definition_version' \nValid values match the pattern '{}'",
                    node.getToscaDefinitionsVersion(),
                    Defaults.TOSCA_DEFINITIONS_VERSION_PATTERN
                ).setContext(parameter.getContext())
            );
        }
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TRepositoryDefinition node, Parameter parameter) {
        if (Objects.isNull(node)) return null;

        if (Objects.isNull(node.getUrl()) || node.getUrl().isEmpty()) {
            setException(new UndefinedRequiredKeyname("Field 'url' is required")
                .setContext(parameter.getContext())
            );
        }

        if (Objects.nonNull(node.getCredential())) {
            Credential credential = node.getCredential();
            if (Objects.isNull(credential.getToken()) || credential.getToken().isEmpty()) {
                setException(new UndefinedRequiredKeyname("Field 'credential.token' is required")
                    .setContext(parameter.getContext())
                );
            }
            if (Objects.isNull(credential.getTokenType()) || credential.getTokenType().isEmpty()) {
                setException(new UndefinedRequiredKeyname("Field 'credential.token_type' is required")
                    .setContext(parameter.getContext())
                );
            }
        }
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(Metadata node, Parameter parameter) {
        if (Objects.isNull(node)) return null;

        if (node.containsKey("template_version")) {
            String templateVersion = node.get("template_version");
            String templateVersionMatch = "\\d+\\.\\d+(\\.\\d+(\\.\\w+(-\\d+)?)?)?";

            if (!templateVersion.matches(templateVersionMatch)) {
                setException(new InvalidToscaSyntax(
                        "Invalid value '{}' for field template_version\nValid version pattern matches '{}'",
                        templateVersion,
                        templateVersionMatch
                    ).setContext(parameter.getContext())
                );
            }
        }
        return super.visit(node, parameter);
    }
}
