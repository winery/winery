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

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.eclipse.winery.model.tosca.yaml.TImportDefinition;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.yaml.common.Defaults;
import org.eclipse.winery.yaml.common.Namespaces;
import org.eclipse.winery.yaml.common.Utils;
import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.exception.YAMLParserException;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.eclipse.winery.yaml.common.validator.support.ExceptionVisitor;
import org.eclipse.winery.yaml.common.validator.support.Parameter;
import org.eclipse.winery.yaml.common.validator.support.Result;

public class ImportVisitor extends ExceptionVisitor<Result, Parameter> {
    protected final Path path;
    protected String namespace;

    public ImportVisitor(String namespace, Path path) {
        this.path = path;
        this.namespace = namespace;
    }

    @Override
    public Result visit(TServiceTemplate node, Parameter parameter) {
        Reader reader = Reader.getReader();
        if (!this.namespace.equals(Namespaces.TOSCA_NS)) {
            TServiceTemplate serviceTemplate;
            try {
                Path outFilePath = Utils.getTmpDir(Paths.get("normative_file")).resolve(Defaults.TOSCA_NORMATIVE_TYPES);
                InputStream inputStream = this.getClass().getResourceAsStream(File.separator.concat(Defaults.TOSCA_NORMATIVE_TYPES));
                Files.copy(inputStream, outFilePath, StandardCopyOption.REPLACE_EXISTING);

                serviceTemplate = reader.parseSkipTest(outFilePath, Namespaces.TOSCA_NS);
                String tmpNamespace = this.namespace;
                this.namespace = Namespaces.TOSCA_NS;
                this.visit(serviceTemplate, new Parameter());
                this.namespace = tmpNamespace;
            } catch (YAMLParserException e) {
                setException(e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TImportDefinition node, Parameter parameter) {
        Reader reader = Reader.getReader();
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
