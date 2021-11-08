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

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.yaml.YTServiceTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.IWrappingRepository;
import org.eclipse.winery.repository.export.entries.CsarEntry;
import org.eclipse.winery.repository.yaml.YamlRepository;
import org.eclipse.winery.repository.yaml.converter.FromCanonical;
import org.eclipse.winery.repository.converter.writer.YamlWriter;
import org.eclipse.winery.repository.exceptions.WineryRepositoryException;

// FIXME a YAML CSAR entry does not need to conform to the canonical model!
/**
 * @deprecated to be replaced with {@link org.eclipse.winery.repository.export.entries.DefinitionsBasedCsarEntry} working over a YamlRepository
 */
@Deprecated
public class YAMLDefinitionsBasedCsarEntry implements CsarEntry {
    private YTServiceTemplate definitions;

    public YAMLDefinitionsBasedCsarEntry(IRepository repo, TDefinitions definitions) {
        assert (definitions != null);
        try {
            FromCanonical c;
            if (repo instanceof IWrappingRepository) {
                // this handles both MultiRepository and GitBasedRepository
                IRepository wrapped = ((IWrappingRepository) repo).getRepository();
                c = new FromCanonical((YamlRepository)wrapped);
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

    public YTServiceTemplate getDefinitions() {
        return definitions;
    }

    public void setDefinitions(YTServiceTemplate definitions) {
        this.definitions = definitions;
    }
}
