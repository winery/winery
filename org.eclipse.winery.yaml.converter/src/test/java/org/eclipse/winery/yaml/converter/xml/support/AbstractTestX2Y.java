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
package org.eclipse.winery.yaml.converter.xml.support;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.yaml.common.Utils;
import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.reader.xml.Reader;
import org.eclipse.winery.yaml.converter.Converter;

import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractTestX2Y {
    protected final Path path;
    protected final Path outPath;
    private final String fileExtension = ".xml";

    public AbstractTestX2Y(Path path) {
        this.path = path;
        this.outPath = path.resolve("tmp");
    }

    @BeforeAll
    public static void setRepository() {
        RepositoryFactory.getRepository(Utils.getTmpDir(Paths.get("AbstractTests")));
    }

    public Definitions readDefinitions(String name) throws JAXBException {
        Reader reader = new Reader();
        return reader.parse(path.resolve(name.concat(fileExtension)));
    }

    public Map<Path, TServiceTemplate> convert(Definitions serviceTemplate, Path outPath) throws MultiException {
        Converter converter = new Converter();
        return converter.convertX2Y(serviceTemplate, outPath);
    }
}
