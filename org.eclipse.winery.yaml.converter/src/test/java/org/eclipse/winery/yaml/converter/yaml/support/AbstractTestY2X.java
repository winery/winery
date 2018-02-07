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
package org.eclipse.winery.yaml.converter.yaml.support;

import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.yaml.common.Utils;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.eclipse.winery.yaml.common.writer.WriterUtils;
import org.eclipse.winery.yaml.converter.Converter;
import org.junit.BeforeClass;

import javax.xml.bind.JAXBException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractTestY2X {

    private final static String fileExtension = ".yml";
    protected final Path path;
    protected final Path outPath;

    public AbstractTestY2X(Path path) {
        this.path = path;
        this.outPath = path.resolve("tmp");
    }

    @BeforeClass
    public static void setRepository() {
        RepositoryFactory.getRepository(Utils.getTmpDir(Paths.get("AbstractTests")));
    }

    public String getName(String name) {
        return name + fileExtension;
    }

    public TServiceTemplate readServiceTemplate(String name) throws Exception {
        Reader reader = Reader.getReader();
        return reader.parse(path, Paths.get(getName(name)));
    }

    public TServiceTemplate readServiceTemplate(String name, String namespace) throws Exception {
        Reader reader = Reader.getReader();
        return reader.parse(path, Paths.get(getName(name), namespace));
    }

    public TServiceTemplate readServiceTemplate(Path path, String name, String namespace) throws Exception {
        Reader reader = Reader.getReader();
        return reader.parse(this.path, path.resolve(name.concat(fileExtension)), namespace);
    }

    public Definitions convert(TServiceTemplate serviceTemplate, String name, String namespace) {
        Converter converter = new Converter();
        return converter.convertY2X(serviceTemplate, name, namespace, path, path.resolve("tmp"));
    }

    public void writeXml(Definitions definitions, String name, String namespace) throws JAXBException {
        WriterUtils.saveDefinitions(definitions, outPath, namespace, name);
    }
}
