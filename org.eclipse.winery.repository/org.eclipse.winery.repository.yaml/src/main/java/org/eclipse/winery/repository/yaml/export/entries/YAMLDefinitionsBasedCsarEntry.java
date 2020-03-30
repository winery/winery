/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.yaml.export.entries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.export.entries.CsarEntry;
import org.eclipse.winery.repository.yaml.YamlRepository;
import org.eclipse.winery.repository.yaml.converter.FromCanonical;
import org.eclipse.winery.repository.converter.writer.YamlWriter;
import org.eclipse.winery.repository.exceptions.WineryRepositoryException;

public class YAMLDefinitionsBasedCsarEntry implements CsarEntry {
    private TServiceTemplate definitions;

    public YAMLDefinitionsBasedCsarEntry(IRepository repo, Definitions definitions) {
        assert (definitions != null);
        try {
            FromCanonical c;
            if (repo instanceof GitBasedRepository) {
                GitBasedRepository wrapper = (GitBasedRepository) repo;
                // this is a logical assumption
                c = new FromCanonical((YamlRepository) wrapper.getRepository());
            } else if (repo instanceof YamlRepository) {
                c = new FromCanonical((YamlRepository) repo);
            } else {
                throw new WineryRepositoryException("The chosen repository mode is incompatible with YAML-based export");
            }
            this.definitions = c.convert(definitions, true);
        } catch (WineryRepositoryException e) {
            e.printStackTrace();
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        YamlWriter writer = new YamlWriter();
        InputStream is = writer.writeToInputStream(definitions);
        if (Objects.nonNull(is)) {
            return is;
        }
        throw new IOException("Failed to convert XML to YAML");
    }

    @Override
    public void writeToOutputStream(OutputStream outputStream) throws IOException {
        YamlWriter writer = new YamlWriter();
        outputStream.write(writer.visit(definitions, new YamlWriter.Parameter(0)).toString().getBytes());
    }

    public TServiceTemplate getDefinitions() {
        return definitions;
    }

    public void setDefinitions(TServiceTemplate definitions) {
        this.definitions = definitions;
    }
}
