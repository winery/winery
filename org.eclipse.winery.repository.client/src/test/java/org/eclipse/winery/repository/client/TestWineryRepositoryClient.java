/*******************************************************************************
 * Copyright (c) 2012-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.client;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.HasName;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.AbstractFileBasedRepository;
import org.eclipse.winery.repository.rest.server.WineryUsingHttpServer;

import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests client methods with a pre-configured client stored in a local static field.
 * <p>
 * Client creation and multiple repositories are not tested. This should be subject to other test classes.
 */
public class TestWineryRepositoryClient extends TestWithGitBackedRepository {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TestWineryRepositoryClient.class);

    private static final String repositoryURI = "http://localhost:8080/winery";
    private static final boolean USE_PROXY = false;
    private static final IWineryRepositoryClient client = new WineryRepositoryClient(TestWineryRepositoryClient.USE_PROXY);

    static {
        TestWineryRepositoryClient.client.addRepository(TestWineryRepositoryClient.repositoryURI);
    }

    /**
     * The namespace to put new things in.
     * <p>
     * TODO: Is deleted completely after testing
     */
    private static final String NAMESPACE_FOR_NEW_ARTIFACTS = "http://www.example.org/test/wineryclient/";

    private static Server server;

    /**
     * Code adapted from {@link org.eclipse.winery.repository.rest.server.WineryUsingHttpServer#main(java.lang.String[])}
     */
    @BeforeAll
    public static void startWineryServer() throws Exception {
        server = WineryUsingHttpServer.createHttpServer();
        server.start();

        IRepository repository = RepositoryFactory.getRepository();
        if (repository instanceof AbstractFileBasedRepository) {
            LOGGER.debug("Using path " + repository.getRepositoryRoot());
        } else {
            LOGGER.debug("Repository is not filebased");
        }
    }

    @AfterAll
    public static void stopWineryServer() throws Exception {
        server.stop();
    }

    private static Stream<Arguments> getAllTypes() {
        return Stream.of(
            Arguments.of(TNodeType.class, 51, "NodeTypes"),
            Arguments.of(TRelationshipType.class, 9, "RelationshipTypes"),
            Arguments.of(TServiceTemplate.class, 44, "ServiceTemplates")
        );
    }

    @ParameterizedTest(name = "{index} => {2}")
    @MethodSource("getAllTypes")
    public void getAllNodeTypes(Class<? extends TExtensibleElements> clazz, int expectedSize, String description) throws Exception {
        this.setRevisionTo("origin/plain");

        Collection<? extends TExtensibleElements> allTypes = TestWineryRepositoryClient.client.getAllTypes(clazz);
        // We assert greater than, to avoid breaking the test if the test repository changes.
        assertTrue(allTypes.size() >= expectedSize);

        allTypes.stream()
            .filter(element -> element instanceof HasName)
            .forEach(element -> assertNotNull(((HasName) element).getName()));
    }

    @Test
    public void getAllNodeTypesWithAssociatedElements() throws Exception {
        this.setRevisionTo("origin/plain");
        Collection<TDefinitions> allTypes = TestWineryRepositoryClient.client.getAllTypesWithAssociatedElements(TNodeType.class);
        assertNotNull(allTypes);
    }

    @Test
    public void getAllRelationshipTypesWithAssociatedElements() throws Exception {
        this.setRevisionTo("origin/plain");
        Collection<TDefinitions> allTypes = TestWineryRepositoryClient.client.getAllTypesWithAssociatedElements(TRelationshipType.class);
        assertNotNull(allTypes);
    }

    @Test
    public void topologyTemplateOfServiceTemplateWithFourPoliciesIsNotNull() throws Exception {
        this.setRevisionTo("origin/plain");
        QName serviceTemplate = new QName("http://plain.winery.opentosca.org/servicetemplates", "ServiceTemplateWithFourPolicies");
        TTopologyTemplate topologyTemplate = TestWineryRepositoryClient.client.getTopologyTemplate(serviceTemplate);
        assertNotNull(topologyTemplate);
    }

    @Test
    public void getPropertiesOfTestTopologyTemplate() throws Exception {
        this.setRevisionTo("origin/plain");
        QName serviceTemplate = new QName("http://plain.winery.opentosca.org/servicetemplates", "ServiceTemplateMinimalExampleWithAllPropertyVariants");
        TTopologyTemplate topologyTemplate = TestWineryRepositoryClient.client.getTopologyTemplate(serviceTemplate);
        assertNotNull(topologyTemplate);
        final TNodeTemplate nodeTemplateWithTwoKVProperties = topologyTemplate.getNodeTemplate("NodeTypeWithTwoKVProperties");
        assertNotNull(nodeTemplateWithTwoKVProperties);
        TEntityTemplate.Properties properties = nodeTemplateWithTwoKVProperties.getProperties();
        assertNotNull(properties);
        final LinkedHashMap<String, String> kvProperties = properties.getKVProperties();
        assertNotNull(kvProperties);
        final String value = kvProperties.get("key1");
        assertEquals("value", value);
    }

    @Test
    public void artifactTypeForWarFiles() throws Exception {
        this.setRevisionTo("origin/plain");
        QName artifactType = TestWineryRepositoryClient.client.getArtifactTypeQNameForExtension("war");
        assertNotNull(artifactType, "Artifact Type for .war does not exist");
    }

    @Test
    public void createArtifactTemplate() throws Exception {
        this.setRevisionTo("origin/plain");

        // assure that the artifact type exists
        QName artifactTypeQName = TestWineryRepositoryClient.client.getArtifactTypeQNameForExtension("war");
        assertNotNull(artifactTypeQName, "Artifact Type for .war does not exist");

        // assure that the artifact template does not yet exist
        // one possibility is to delete the artifact template, the other
        // possibility is to

        QName artifactTemplateQName = new QName(TestWineryRepositoryClient.NAMESPACE_FOR_NEW_ARTIFACTS, "artifactTemplate");
        ArtifactTemplateId atId = new ArtifactTemplateId(artifactTemplateQName);

        // ensure that the template does not exist yet
        TestWineryRepositoryClient.client.forceDelete(atId);

        TestWineryRepositoryClient.client.createArtifactTemplate(artifactTemplateQName, artifactTypeQName);
    }
}
