/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.yaml.converter.yaml.support;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.yaml.common.Utils;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.eclipse.winery.yaml.common.writer.WriterUtils;
import org.eclipse.winery.yaml.converter.Converter;

import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractTestY2X {

    private final static String fileExtension = ".yml";
    protected final Path path;
    protected final Path outPath;

    /**
     * @param path Base path where all tests are located within
     */
    public AbstractTestY2X(Path path) {
        this.path = path;
        this.outPath = path.resolve("tmp");
    }

    @BeforeAll
    public static void setRepository() {
        RepositoryFactory.getRepository(Utils.getTmpDir(Paths.get("AbstractTests")));
    }

    public String getName(String name) {
        return name + fileExtension;
    }

    public TServiceTemplate readServiceTemplate(String name) throws Exception {
        Reader reader = Reader.getReader();
        return reader.parse(this.path, Paths.get(getName(name)));
    }

    public TServiceTemplate readServiceTemplate(String name, String namespace) throws Exception {
        Reader reader = Reader.getReader();
        return reader.parse(this.path, Paths.get(getName(name)), namespace);
    }

    public TServiceTemplate readServiceTemplate(Path path, String name, String namespace) throws Exception {
        Reader reader = Reader.getReader();
        return reader.parse(this.path, path.resolve(name.concat(fileExtension)), namespace);
    }

    public Definitions convert(TServiceTemplate serviceTemplate, String name, String namespace) {
        Converter converter = new Converter();
        return converter.convertY2X(serviceTemplate, name, namespace, this.path, this.outPath);
    }

    public void writeXml(Definitions definitions, String name, String namespace) {
        WriterUtils.saveDefinitions(definitions, this.outPath, namespace, name);
    }
}
