/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.common.validator;

import java.util.Map;

import org.eclipse.winery.model.tosca.yaml.TImportDefinition;
import org.eclipse.winery.model.tosca.yaml.TInterfaceType;
import org.eclipse.winery.model.tosca.yaml.TOperationDefinition;
import org.eclipse.winery.model.tosca.yaml.TRepositoryDefinition;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.model.tosca.yaml.tosca.datatypes.Credential;
import org.eclipse.winery.yaml.common.Defaults;
import org.eclipse.winery.yaml.common.exception.ImplementationArtifactInvalidOnInterfaceType;
import org.eclipse.winery.yaml.common.exception.InvalidTOSCAVersion;
import org.eclipse.winery.yaml.common.exception.MissingRequiredKeyname;
import org.eclipse.winery.yaml.common.exception.MissingTOSCAVersion;
import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.exception.ValueTypeMismatch;
import org.eclipse.winery.yaml.common.validator.support.ExceptionVisitor;
import org.eclipse.winery.yaml.common.validator.support.Parameter;
import org.eclipse.winery.yaml.common.validator.support.Result;

public class Validator extends ExceptionVisitor<Result, Parameter> {
    private final String path;

    public Validator(String path) {
        this.path = path;
    }

    public void validate(TServiceTemplate serviceTemplate, String namespace) throws MultiException {
        TypeValidator typeValidator = new TypeValidator(path, namespace);
        typeValidator.validate(serviceTemplate);

        DefinitionValidator definitionValidator = new DefinitionValidator(path);
        definitionValidator.validate(serviceTemplate);

        visit(serviceTemplate, new Parameter());

        if (this.hasExceptions()) {
            throw this.getException();
        }
    }

    @Override
    public Result visit(TInterfaceType node, Parameter parameter) {
        if (node.getOperations() != null) {
            for (Map.Entry<String, TOperationDefinition> entry : node.getOperations().entrySet()) {
                if (entry.getValue().getImplementation() != null) {
                    setException(new ImplementationArtifactInvalidOnInterfaceType("The InterfaceType \"" + parameter.getKey() + "\" MUST NOT include any implementations for defined operations"));
                }
            }
        }
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TImportDefinition node, Parameter parameter) {
        if (node.getFile() == null || node.getFile().isEmpty()) {
            String context = "Import Definition \"" + parameter.getKey() + "\"";
            String keyname = "file";
            setException(new MissingRequiredKeyname(keyname, context));
        }
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TServiceTemplate node, Parameter parameter) {
        if (node.getToscaDefinitionsVersion() == null) {
            String msg = "tosca_definition_version is missing";
            setException(new MissingTOSCAVersion(msg));
        }

        if (!node.getToscaDefinitionsVersion().matches(Defaults.TOSCA_DEFINITIONS_VERSION_PATTERN)) {
            String msg = "\"" + node.getToscaDefinitionsVersion()
                + "\" is an invalid tosca_definition_version, which does not follow the pattern: \""
                + Defaults.TOSCA_DEFINITIONS_VERSION_PATTERN + "\"";
            setException(new InvalidTOSCAVersion(msg));
        }
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TRepositoryDefinition node, Parameter parameter) {
        if (node == null) {
            return null;
        }

        String contextPrefix = "Repository definition \"";
        String contextPostfix = "\"";

        if (node.getUrl() == null || node.getUrl().isEmpty()) {
            String keyname = "url";
            setException(new MissingRequiredKeyname(keyname, contextPrefix + parameter.getKey() + contextPostfix));
        }

        if (node.getCredential() != null) {
            Credential credential = node.getCredential();
            if (credential.getToken() == null || credential.getToken().isEmpty()) {
                String keyname = "credential.token";
                setException(new MissingRequiredKeyname(keyname, contextPrefix + parameter.getKey() + contextPostfix));
            }
            if (credential.getTokenType() == null || credential.getTokenType().isEmpty()) {
                String keyname = "credential.token_type";
                setException(new MissingRequiredKeyname(keyname, contextPrefix + parameter.getKey() + contextPostfix));
            }
        }
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(Metadata node, Parameter parameter) {
        if (node == null) {
            return null;
        }

        if (node.containsKey("template_version")) {
            String templateVersion = node.get("template_version");
            String templateVersionMatch = "\\d+\\.\\d+(\\.\\d+(\\.\\w+(-\\d+)?)?)?";

            if (!templateVersion.matches(templateVersionMatch)) {
                String msg = "The value of the metadata field template_version is invalid";
                setException(new ValueTypeMismatch(msg));
            }
        }
        return super.visit(node, parameter);
    }
}
