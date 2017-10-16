/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.common.writer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.yaml.common.reader.BuilderTests;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.eclipse.winery.yaml.common.writer.yaml.Writer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class YamlWriterTest {

    private final Path yamlPath;
    private final TServiceTemplate serviceTemplate;

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws Exception {
        return Arrays.asList(getParameter(builderTests.toscaDefinitionsVersion()),
            getParameter(builderTests.metadata()),
            getParameter(builderTests.description()),
            getParameter(builderTests.dslDefinitions()),
            getParameter(builderTests.repositories()),
            getParameter(builderTests.artifactTypes()),
            getParameter(builderTests.dataTypes()),
            getParameter(builderTests.capabilityTypes()),
            getParameter(builderTests.interfaceTypes()),
            getParameter(builderTests.relationshipTypes()),
            getParameter(builderTests.nodeTypes()),
            getParameter(builderTests.groupTypes()),
            getParameter(builderTests.policyTypes()),
            getParameter(builderTests.example16()));
    }

    public static Object[] getParameter(Map.Entry<String, TServiceTemplate> entry) throws Exception {
        return new Object[]{getYamlPath(entry.getKey()), entry.getValue()};
    }

    private static final String FILE_TYPE = ".yml";

    private static final BuilderTests builderTests = new BuilderTests();

    private static Path temporaryFolder;

    static {
        try {
            temporaryFolder = Files.createTempDirectory("winery-yaml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Writer writer = new Writer();
    private Reader reader = new Reader();

    private static Path getYamlPath(String name) throws Exception {
        return temporaryFolder.resolve(name + FILE_TYPE);
    }

    public YamlWriterTest(Path yamlPath, TServiceTemplate serviceTemplate) {
        this.yamlPath = yamlPath;
        this.serviceTemplate = serviceTemplate;
    }

    @Test
    public void roundtrip() throws Exception {
        writer.write(serviceTemplate, yamlPath);
        TServiceTemplate out = reader.parse(yamlPath.getParent().toString(), yamlPath.getFileName().toString());
        Assert.assertEquals(serviceTemplate, out);
    }
}
