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
package org.eclipse.winery.yaml.converter.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XmlId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.yaml.common.Namespaces;
import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.writer.WriterUtils;
import org.eclipse.winery.yaml.converter.Converter;
import org.eclipse.winery.yaml.converter.yaml.support.AbstractTestY2X;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Showcases extends AbstractTestY2X {

    public Showcases() {
        super(Paths.get("src/test/resources/yaml/Showcase"));
    }

    public MultiException convert(String path, String namespace, Stream<String> files) throws Exception {
        MultiException exception = new MultiException();

        files
            .map(name -> {
                try {
                    return new LinkedHashMap.SimpleEntry<>(name, readServiceTemplate(path + File.separator + name));
                } catch (Exception e) {
                    exception.add(e);
                }
                return null;
            })
            .filter(Objects::nonNull)
            .map(entry -> new LinkedHashMap.SimpleEntry<>(entry.getKey(), convert(entry.getValue(), entry.getKey(), namespace)))
            .forEach(entry -> WriterUtils.saveDefinitions(entry.getValue(), outPath, namespace, entry.getKey()));
        if (exception.hasException()) {
            throw exception.getException();
        }

        return exception;
    }

    @Disabled
    @Test
    public void nodeTypesTest() throws Exception {
        String path = "nodetypes";
        String namespace = "http://placeholder.org/nodetypes";
        Stream<String> files = Stream.of(
        );

        assertTrue(convert(path, namespace, files).isEmpty());
    }

    @Disabled
    @Test
    public void serviceTemplateTest() throws Exception {
        String path = "servicetemplates";
        String namespace = "http://placeholder.org/servicetemplates";
        Stream<String> files = Stream.of(
        );

        assertTrue(convert(path, namespace, files).isEmpty());
    }

    @Test
    public void zipTypeTest() throws Exception {
        String name = "Showcase.csar";
        InputStream inputStream = new FileInputStream(path + File.separator + name);
        Converter converter = new Converter();
        converter.convertY2X(inputStream);
        TServiceTemplate serviceTemplate = RepositoryFactory.getRepository().getElement(
            new ServiceTemplateId(
                new Namespace(Namespaces.DEFAULT_NS, false),
                new XmlId("Showcase", false)
            )
        );

        assertNotNull(serviceTemplate);
    }
}
