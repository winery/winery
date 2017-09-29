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
package org.eclipse.winery.yaml.common.reader;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;

import org.junit.Assert;
import org.junit.Test;

public class BuilderTests {

    public final static String PATH = "src/test/resources/builder";
    public final static String FILE_TYPE = ".yml";
    public final static Reader reader = new Reader();

    public String getName(String name) {
        return name + FILE_TYPE;
    }

    @Test
    public void toscaDefinitionsVersionTest() throws Exception {
        TServiceTemplate serviceTemplate = toscaDefinitionsVersion().getValue();
        Assert.assertNotNull(serviceTemplate);

        String tosca_definitions_version = serviceTemplate.getToscaDefinitionsVersion();
        assert ("http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0".equals(tosca_definitions_version)
            || "tosca_simple_yaml_1_0".equals(tosca_definitions_version)
            || "http://docs.oasis-open.org/tosca/ns/simple/yaml/1.1".equals(tosca_definitions_version)
            || "tosca_simple_yaml_1_1".equals(tosca_definitions_version));
    }

    @Test
    public void metadataTest() throws Exception {
        TServiceTemplate serviceTemplate = metadata().getValue();
        Assert.assertNotNull(serviceTemplate);

        Assert.assertEquals(serviceTemplate.getMetadata().get("template_author"), "kleinech");
    }

    @Test
    public void descriptionTest() throws Exception {
        TServiceTemplate serviceTemplate = description().getValue();
        Assert.assertNotNull(serviceTemplate);
    }

    @Test
    public void dslDefinitionsTest() throws Exception {
        TServiceTemplate serviceTemplate = dslDefinitions().getValue();
        Assert.assertNotNull(serviceTemplate);
    }

    @Test
    public void repositoriesTest() throws Exception {
        TServiceTemplate serviceTemplate = repositories().getValue();
        Assert.assertNotNull(serviceTemplate);
    }

    @Test
    public void importsTest() throws Exception {
        TServiceTemplate serviceTemplate = imports().getValue();
        Assert.assertNotNull(serviceTemplate);
    }

    @Test
    public void artifactTypesTest() throws Exception {
        TServiceTemplate serviceTemplate = artifactTypes().getValue();
        Assert.assertNotNull(serviceTemplate);
    }

    @Test
    public void dataTypesTest() throws Exception {
        TServiceTemplate serviceTemplate = dataTypes().getValue();
        Assert.assertNotNull(serviceTemplate);
    }

    @Test
    public void capabilityTypesTest() throws Exception {
        TServiceTemplate serviceTemplate = capabilityTypes().getValue();
        Assert.assertNotNull(serviceTemplate);
    }

    @Test
    public void interfaceTypesTest() throws Exception {
        TServiceTemplate serviceTemplate = interfaceTypes().getValue();
        Assert.assertNotNull(serviceTemplate);
    }

    @Test
    public void relationshipTypesTest() throws Exception {
        TServiceTemplate serviceTemplate = relationshipTypes().getValue();
        Assert.assertNotNull(serviceTemplate);
    }

    @Test
    public void nodeTypesTest() throws Exception {
        TServiceTemplate serviceTemplate = nodeTypes().getValue();
        Assert.assertNotNull(serviceTemplate);
    }

    @Test
    public void groupTypesTest() throws Exception {
        TServiceTemplate serviceTemplate = groupTypes().getValue();
        Assert.assertNotNull(serviceTemplate);
    }

    @Test
    public void policyTypesTest() throws Exception {
        TServiceTemplate serviceTemplate = policyTypes().getValue();
        Assert.assertNotNull(serviceTemplate);
    }

    @Test
    public void example16Test() throws Exception {
        TServiceTemplate serviceTemplate = example16().getValue();
        Assert.assertNotNull(serviceTemplate);
    }

    public Map.Entry<String, TServiceTemplate> toscaDefinitionsVersion() throws Exception {
        String name = "3_9_3_1-tosca_definitions_version-1_1";
        return new LinkedHashMap.SimpleEntry<>(name, reader.parse(PATH, getName(name)));
    }

    public Map.Entry<String, TServiceTemplate> metadata() throws Exception {
        String name = "3_9_3_2-metadata-1_1";
        return new LinkedHashMap.SimpleEntry<>(name, reader.parse(PATH, getName(name)));
    }

    public Map.Entry<String, TServiceTemplate> description() throws Exception {
        String name = "3_5_1_3-description-1_1";
        return new LinkedHashMap.SimpleEntry<>(name, reader.parse(PATH, getName(name)));
    }

    public Map.Entry<String, TServiceTemplate> dslDefinitions() throws Exception {
        String name = "3_9_3_7-dsl_definitions-1_1";
        return new LinkedHashMap.SimpleEntry<>(name, reader.parse(PATH, getName(name)));
    }

    public Map.Entry<String, TServiceTemplate> repositories() throws Exception {
        String name = "3_9_3_8-repositories-1_1";
        return new LinkedHashMap.SimpleEntry<>(name, reader.parse(PATH, getName(name)));
    }

    public Map.Entry<String, TServiceTemplate> imports() throws Exception {
        String name = "3_9_3_9-imports-1_1";
        return new LinkedHashMap.SimpleEntry<>(name, reader.parse(PATH, getName(name)));
    }

    public Map.Entry<String, TServiceTemplate> artifactTypes() throws Exception {
        String name = "3_9_3_10-artifact_types-1_1";
        return new LinkedHashMap.SimpleEntry<>(name, reader.parse(PATH, getName(name)));
    }

    public Map.Entry<String, TServiceTemplate> dataTypes() throws Exception {
        String name = "3_9_3_11-data_types-1_1";
        return new LinkedHashMap.SimpleEntry<>(name, reader.parse(PATH, getName(name)));
    }

    public Map.Entry<String, TServiceTemplate> capabilityTypes() throws Exception {
        String name = "3_9_3_12-capability_types-1_1";
        return new LinkedHashMap.SimpleEntry<>(name, reader.parse(PATH, getName(name)));
    }

    public Map.Entry<String, TServiceTemplate> interfaceTypes() throws Exception {
        String name = "3_9_3_13-interface_types-1_1";
        return new LinkedHashMap.SimpleEntry<>(name, reader.parse(PATH, getName(name)));
    }

    public Map.Entry<String, TServiceTemplate> relationshipTypes() throws Exception {
        String name = "3_9_3_14-relationship_types-1_1";
        return new LinkedHashMap.SimpleEntry<>(name, reader.parse(PATH, getName(name)));
    }

    public Map.Entry<String, TServiceTemplate> nodeTypes() throws Exception {
        String name = "3_9_3_15-node_types-1_1";
        return new LinkedHashMap.SimpleEntry<>(name, reader.parse(PATH, getName(name)));
    }

    public Map.Entry<String, TServiceTemplate> groupTypes() throws Exception {
        String name = "3_9_3_16-group_types-1_1";
        return new LinkedHashMap.SimpleEntry<>(name, reader.parse(PATH, getName(name)));
    }

    public Map.Entry<String, TServiceTemplate> policyTypes() throws Exception {
        String name = "3_9_3_17-policy_types-1_1";
        return new LinkedHashMap.SimpleEntry<>(name, reader.parse(PATH, getName(name)));
    }

    public Map.Entry<String, TServiceTemplate> example16() throws Exception {
        String name = "example_16-topology_templates-1_1";
        return new LinkedHashMap.SimpleEntry<>(name, reader.parse(PATH, getName(name)));
    }
}
