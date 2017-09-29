/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Christoph Kleine - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.yaml.common.writer;

import java.io.File;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.yaml.common.reader.BuilderTests;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.eclipse.winery.yaml.common.writer.yaml.Writer;

import org.junit.Assert;
import org.junit.Test;

public class YamlWriterTest {
    public final static String PATH = "src/test/resources/writer/tmp";
    public final static String FILE_TYPE = ".yml";
    public final static Writer writer = new Writer();
    public final static Reader reader = new Reader();
    public BuilderTests builderTests = new BuilderTests();

    public static String getName(String name) {
        return PATH + File.separator + name;
    }

    @Test
    public void toscaDefinitionsVersion() throws Exception {
        String name = builderTests.toscaDefinitionsVersion().getKey() + FILE_TYPE;
        TServiceTemplate serviceTemplate = builderTests.toscaDefinitionsVersion().getValue();
        writer.write(serviceTemplate, getName(name));
        TServiceTemplate out = reader.parse(PATH, name);

        Assert.assertEquals(serviceTemplate, out);
    }

    @Test
    public void metadata() throws Exception {
        String name = builderTests.metadata().getKey() + FILE_TYPE;
        TServiceTemplate serviceTemplate = builderTests.metadata().getValue();
        writer.write(serviceTemplate, getName(name));
        TServiceTemplate out = reader.parse(PATH, name);

        Assert.assertEquals(serviceTemplate, out);
    }

    @Test
    public void description() throws Exception {
        String name = builderTests.description().getKey() + FILE_TYPE;
        TServiceTemplate serviceTemplate = builderTests.description().getValue();
        writer.write(serviceTemplate, getName(name));
        TServiceTemplate out = reader.parse(PATH, name);

        Assert.assertEquals(serviceTemplate, out);
    }

    @Test
    public void dslDefinitions() throws Exception {
        String name = builderTests.dslDefinitions().getKey() + FILE_TYPE;
        TServiceTemplate serviceTemplate = builderTests.dslDefinitions().getValue();
        writer.write(serviceTemplate, getName(name));
        TServiceTemplate out = reader.parse(PATH, name);

        Assert.assertEquals(serviceTemplate, out);
    }

    @Test
    public void repositories() throws Exception {
        String name = builderTests.repositories().getKey() + FILE_TYPE;
        TServiceTemplate serviceTemplate = builderTests.repositories().getValue();
        writer.write(serviceTemplate, getName(name));
        TServiceTemplate out = reader.parse(PATH, name);

        Assert.assertEquals(serviceTemplate, out);
    }

    @Test
    public void artifactTypes() throws Exception {
        String name = builderTests.artifactTypes().getKey() + FILE_TYPE;
        TServiceTemplate serviceTemplate = builderTests.artifactTypes().getValue();
        writer.write(serviceTemplate, getName(name));
        TServiceTemplate out = reader.parse(PATH, name);

        Assert.assertEquals(serviceTemplate, out);
    }

    @Test
    public void dataTypes() throws Exception {
        String name = builderTests.dataTypes().getKey() + FILE_TYPE;
        TServiceTemplate serviceTemplate = builderTests.dataTypes().getValue();
        writer.write(serviceTemplate, getName(name));
        TServiceTemplate out = reader.parse(PATH, name);

        Assert.assertEquals(serviceTemplate, out);
    }

    @Test
    public void capabilityTypes() throws Exception {
        String name = builderTests.capabilityTypes().getKey() + FILE_TYPE;
        TServiceTemplate serviceTemplate = builderTests.capabilityTypes().getValue();
        writer.write(serviceTemplate, getName(name));
        TServiceTemplate out = reader.parse(PATH, name);

        Assert.assertEquals(serviceTemplate, out);
    }

    @Test
    public void interfaceTypes() throws Exception {
        String name = builderTests.interfaceTypes().getKey() + FILE_TYPE;
        TServiceTemplate serviceTemplate = builderTests.interfaceTypes().getValue();
        writer.write(serviceTemplate, getName(name));
        TServiceTemplate out = reader.parse(PATH, name);

        Assert.assertEquals(serviceTemplate, out);
    }

    @Test
    public void relationshipTypes() throws Exception {
        String name = builderTests.relationshipTypes().getKey() + FILE_TYPE;
        TServiceTemplate serviceTemplate = builderTests.relationshipTypes().getValue();
        writer.write(serviceTemplate, getName(name));
        TServiceTemplate out = reader.parse(PATH, name);

        Assert.assertEquals(serviceTemplate, out);
    }

    @Test
    public void nodeTypes() throws Exception {
        String name = builderTests.nodeTypes().getKey() + FILE_TYPE;
        TServiceTemplate serviceTemplate = builderTests.nodeTypes().getValue();
        writer.write(serviceTemplate, getName(name));
        TServiceTemplate out = reader.parse(PATH, name);

        Assert.assertEquals(serviceTemplate, out);
    }

    @Test
    public void groupTypes() throws Exception {
        String name = builderTests.groupTypes().getKey() + FILE_TYPE;
        TServiceTemplate serviceTemplate = builderTests.groupTypes().getValue();
        writer.write(serviceTemplate, getName(name));
        TServiceTemplate out = reader.parse(PATH, name);

        Assert.assertEquals(serviceTemplate, out);
    }

    @Test
    public void policyTypes() throws Exception {
        String name = builderTests.policyTypes().getKey() + FILE_TYPE;
        TServiceTemplate serviceTemplate = builderTests.policyTypes().getValue();
        writer.write(serviceTemplate, getName(name));
        TServiceTemplate out = reader.parse(PATH, name);

        Assert.assertEquals(serviceTemplate, out);
    }

    @Test
    public void example16() throws Exception {

        String name = builderTests.example16().getKey() + FILE_TYPE;
        TServiceTemplate serviceTemplate = builderTests.example16().getValue();
        writer.write(serviceTemplate, getName(name));
        TServiceTemplate out = reader.parse(PATH, name);

        Assert.assertEquals(serviceTemplate, out);
    }
}
