/*******************************************************************************
 * Copyright (c) 2012-2018 Contributors to the Eclipse Foundation
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

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.FilebasedRepository;
import org.eclipse.winery.repository.rest.server.WineryUsingHttpServer;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

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
    @BeforeClass
    public static void startWineryServer() throws Exception {
        server = WineryUsingHttpServer.createHttpServer();
        server.start();

        IRepository repository = RepositoryFactory.getRepository();
        if (repository instanceof FilebasedRepository) {
            LOGGER.debug("Using path " + ((FilebasedRepository) repository).getRepositoryRoot());
        } else {
            LOGGER.debug("Repository is not filebased");
        }
    }

    @AfterClass
    public static void stopWineryServer() throws Exception {
        server.stop();
    }

    @Test
    public void getAllNodeTypes() throws Exception {
        this.setRevisionTo("origin/plain");
        Collection<TNodeType> allTypes = TestWineryRepositoryClient.client.getAllTypes(TNodeType.class);
        for (TNodeType type: allTypes) {
            Assert.assertNotNull("name is null", type.getName());
            Assert.assertNotNull("target namespace is null", type.getTargetNamespace());
        }
    }

    @Test
    public void getAllRelationshipTypes() throws Exception {
        this.setRevisionTo("origin/plain");
        Collection<TRelationshipType> allTypes = TestWineryRepositoryClient.client.getAllTypes(TRelationshipType.class);
        for (TRelationshipType type: allTypes) {
            Assert.assertNotNull("name is null", type.getName());
            Assert.assertNotNull("target namespace is null", type.getTargetNamespace());
        }
    }

    @Test
    public void getAllNodeTypesWithAssociatedElements() throws Exception {
        this.setRevisionTo("origin/plain");
        Collection<TDefinitions> allTypes = TestWineryRepositoryClient.client.getAllTypesWithAssociatedElements(TNodeType.class);
        Assert.assertNotNull(allTypes);
    }

    @Test
    public void getAllRelationshipTypesWithAssociatedElements() throws Exception {
        this.setRevisionTo("origin/plain");
        Collection<TDefinitions> allTypes = TestWineryRepositoryClient.client.getAllTypesWithAssociatedElements(TRelationshipType.class);
        Assert.assertNotNull(allTypes);
    }

    @Test
    public void getAllServiceTemplates() throws Exception {
        this.setRevisionTo("origin/plain");
        Collection<TServiceTemplate> allTypes = TestWineryRepositoryClient.client.getAllTypes(TServiceTemplate.class);
        Assert.assertNotEquals(0, allTypes.size());
        for (TServiceTemplate type: allTypes) {
            Assert.assertNotNull("name is null", type.getName());
            Assert.assertNotNull("target namespace is null", type.getTargetNamespace());
        }
    }

    @Test
    public void topologyTemplateOfServiceTemplateWithFourPoliciesIsNotNull() throws Exception {
        this.setRevisionTo("origin/plain");
        QName serviceTemplate = new QName("http://plain.winery.opentosca.org/servicetemplates", "ServiceTemplateWithFourPolicies");
        TTopologyTemplate topologyTemplate = TestWineryRepositoryClient.client.getTopologyTemplate(serviceTemplate);
        Assert.assertNotNull(topologyTemplate);
    }

    @Test
    public void getPropertiesOfTestTopologyTemplate() throws Exception {
        this.setRevisionTo("origin/plain");
        QName serviceTemplate = new QName("http://plain.winery.opentosca.org/servicetemplates", "ServiceTemplateMinimalExampleWithAllPropertyVariants");
        TTopologyTemplate topologyTemplate = TestWineryRepositoryClient.client.getTopologyTemplate(serviceTemplate);
        Assert.assertNotNull(topologyTemplate);
        @Nullable final TNodeTemplate nodeTemplateWithTwoKVProperties = topologyTemplate.getNodeTemplate("NodeTypeWithTwoKVProperties");
        final LinkedHashMap<String, String> kvProperties = nodeTemplateWithTwoKVProperties.getProperties().getKVProperties();
        final String value = kvProperties.get("key1");
        Assert.assertEquals("value", value);
    }

    @Test
    public void artifactTypeForWarfiles() throws Exception {
        this.setRevisionTo("origin/plain");
        QName artifactType = TestWineryRepositoryClient.client.getArtifactTypeQNameForExtension("war");
        Assert.assertNotNull("Artifact Type for .war does not exist", artifactType);
    }

    @Test
    public void createArtifactTemplate() throws Exception {
        this.setRevisionTo("origin/plain");

        // assure that the artifact type exists
        QName artifactTypeQName = TestWineryRepositoryClient.client.getArtifactTypeQNameForExtension("war");
        Assert.assertNotNull("Artifact Type for .war does not exist", artifactTypeQName);

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
