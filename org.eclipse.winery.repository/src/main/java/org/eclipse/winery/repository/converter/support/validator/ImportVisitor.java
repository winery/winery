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

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.eclipse.winery.model.tosca.yaml.TImportDefinition;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.repository.converter.support.Defaults;
import org.eclipse.winery.repository.converter.support.Namespaces;
import org.eclipse.winery.repository.converter.support.Utils;
import org.eclipse.winery.repository.converter.support.exception.MultiException;
import org.eclipse.winery.repository.converter.support.reader.YamlReader;
import org.eclipse.winery.repository.converter.support.validator.support.ExceptionVisitor;
import org.eclipse.winery.repository.converter.support.validator.support.Parameter;
import org.eclipse.winery.repository.converter.support.validator.support.Result;

public class ImportVisitor extends ExceptionVisitor<Result, Parameter> {
    protected final Path path;
    protected String namespace;

    public ImportVisitor(String namespace, Path path) {
        this.path = path;
        this.namespace = namespace;
    }

    @Override
    public Result visit(TServiceTemplate node, Parameter parameter) {
        YamlReader reader = new YamlReader();
        if (!this.namespace.equals(Namespaces.TOSCA_NS)) {
            Set<String> typeDefinitions = new HashSet<>(Arrays.asList(
                Defaults.TOSCA_NORMATIVE_TYPES, Defaults.TOSCA_NONNORMATIVE_TYPES));
            String tmpNamespace = this.namespace;
            this.namespace = Namespaces.TOSCA_NS;
            Path tmpDir = Utils.getTmpDir(Paths.get("types"));
            for (String typeDefinition : typeDefinitions) {
                try {
                    Path outFilePath = tmpDir.resolve(typeDefinition);
                    InputStream inputStream = this.getClass().getResourceAsStream(
                        // Do not use File.separator here (https://stackoverflow.com/a/41677152/8235252)
                        "/".concat(typeDefinition)
                    );
                    Files.copy(inputStream, outFilePath, StandardCopyOption.REPLACE_EXISTING);
                    TServiceTemplate serviceTemplate = reader.parseSkipTest(outFilePath, Namespaces.TOSCA_NS);
                    if (Objects.nonNull(serviceTemplate)) {
                        serviceTemplate.accept(this, new Parameter());
                    }
                } catch (MultiException e) {
                    setException(e);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.namespace = tmpNamespace;
        }

        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TImportDefinition node, Parameter parameter) {
        YamlReader reader = new YamlReader();
        String importNamespace = node.getNamespaceUri() == null ? this.namespace : node.getNamespaceUri();
        try {
            TServiceTemplate serviceTemplate = reader.parse(node, path, importNamespace);
            if (serviceTemplate != null) {
                String tmpNamespace = this.namespace;
                this.namespace = importNamespace;
                this.visit(serviceTemplate, new Parameter());
                this.namespace = tmpNamespace;
            }
            super.visit(node, parameter);
        } catch (MultiException e) {
            this.setException(e);
        }
        return null;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
