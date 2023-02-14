/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.edmm.model;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EdmmImporterTest extends TestWithGitBackedRepository {

    @Test
    void importComponentTypesTest() throws Exception {
        this.setRevisionTo("origin/plain");
        EdmmImporter edmmImporter = new EdmmImporter();

        File edmmFile = loadFromClasspath("edmmModels/componentTypes.yaml");
        assertTrue(
            edmmImporter.transform(edmmFile.toPath(), true)
        );

        TNodeType ubuntuType = repository.getElement(new NodeTypeId(
            QName.valueOf("{http://opentosca.org/example/applications/nodetypes}test_ubuntu_w1-wip1")
        ));
        assertNotNull(ubuntuType);
        assertNotNull(ubuntuType.getWinerysPropertiesDefinition());
        List<PropertyDefinitionKV> properties = ubuntuType.getWinerysPropertiesDefinition().getPropertyDefinitions();
        assertNotNull(properties);
        assertEquals(1, properties.size());
        PropertyDefinitionKV ipProperty = properties.get(0);
        assertEquals("ip-address", ipProperty.getKey());
        assertEquals("string", ipProperty.getType());
        assertNotNull(ubuntuType.getDerivedFrom());
        assertEquals(
            QName.valueOf("{http://opentosca.org/baseelements/nodetypes}VM"),
            ubuntuType.getDerivedFrom().getType()
        );

        TRelationshipType customRelType = repository.getElement(new RelationshipTypeId(
            QName.valueOf("{https://opentosca.org/edmm/imported/relationshipTypes}customType")
        ));
        assertNotNull(customRelType);
        assertNotNull(customRelType.getWinerysPropertiesDefinition());
        List<PropertyDefinitionKV> relProperties = customRelType.getWinerysPropertiesDefinition().getPropertyDefinitions();
        assertNotNull(relProperties);
        assertEquals(1, relProperties.size());
        PropertyDefinitionKV myProperty = relProperties.get(0);
        assertEquals("myProperty", myProperty.getKey());
        assertEquals("string", ipProperty.getType());
        assertNotNull(customRelType.getDerivedFrom());
        assertEquals(
            QName.valueOf("{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}DependsOn"),
            customRelType.getDerivedFrom().getType()
        );
    }

    @Test
    void importWholeDeploymentModelTest() throws Exception {
        EdmmImporter edmmImporter = new EdmmImporter();

        File edmmFile = loadFromClasspath("edmmModels/MySQL-OpenStack.yml");
        assertTrue(
            edmmImporter.transform(edmmFile.toPath(), true)
        );

        TServiceTemplate serviceTemplate = repository.getElement(new ServiceTemplateId(
            QName.valueOf("{https://opentosca.org/edmm/imported/serviceTemplates}MySQL-OpenStack")
        ));
        assertNotNull(serviceTemplate);
        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
        assertNotNull(topologyTemplate);

        assertEquals(3, topologyTemplate.getNodeTemplates().size());
    }

    private File loadFromClasspath(String path) throws URISyntaxException {
        URL resource = ClassLoader.getSystemClassLoader().getResource(path);
        if (resource == null) {
            throw new RuntimeException("Could not find file " + path + " on classpath!");
        }
        return new File(resource.toURI());
    }
}
