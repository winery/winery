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

package org.eclipse.winery.model.adaptation.substitution.refinement.topologyrefinement;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.configuration.DARefinementConfigurationObject;
import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.extensions.OTDeploymentArtifactMapping;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateFilesDirectoryId;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aMultipart;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WireMockTest()
class TopologyFragmentRefinementTest extends TestWithGitBackedRepository {

    @Test
    public void testDeploymentArtifactTranslation(WireMockRuntimeInfo mockServer) throws Exception {
        this.setRevisionTo("origin/plain");

        TopologyFragmentRefinement refinement = new TopologyFragmentRefinement();

        QName sourceType = QName.valueOf("{https://exammple.org/artifactTypes}TestType");
        QName targetType = QName.valueOf("{https://exammple.org/artifactTypes}TargetType");
        OTDeploymentArtifactMapping testMapping = new OTDeploymentArtifactMapping.Builder("test")
            .setArtifactType(sourceType)
            .setTargetArtifactType(targetType)
            .build();

        TDeploymentArtifact testDa = new TDeploymentArtifact.Builder("TestDa", sourceType)
            .setArtifactRef(QName.valueOf("{http://plain.winery.opentosca.org/artifacttemplates}ArtifactTemplateWithFilesAndSources-ArtifactTypeWithoutProperties"))
            .build();

        DARefinementConfigurationObject.DARefinementService service = new DARefinementConfigurationObject.DARefinementService();
        service.url = mockServer.getHttpBaseUrl() + "/endpoint";
        service.canRefine = new DARefinementConfigurationObject.TransformationCapabilities();
        service.canRefine.from = new ArrayList<>();
        service.canRefine.from.add(sourceType.getLocalPart());
        service.canRefine.to = new ArrayList<>();
        service.canRefine.to.add(targetType.getLocalPart());

        Map<String, DARefinementConfigurationObject.DARefinementService> configMap = new HashMap<>();
        configMap.put("testRefinementService", service);

        DARefinementConfigurationObject refinementConfig = new DARefinementConfigurationObject(new YAMLConfiguration());
        refinementConfig.setRefinementServices(configMap);

        // region ********* mock server setup **********
        stubFor(post("/endpoint")
            .willReturn(aResponse()
                .withHeader("Content-Disposition", "inline; filename=test.zip")
                .withHeader("Content-Type", "application/zip")
                .withBodyFile("test.zip")
            )
        );
        // endregion

        TDeploymentArtifact transformationResult = refinement.translateDeploymentArtifact(testMapping, testDa, refinementConfig);

        // region ********* mock server tests **********
        verify(postRequestedFor(urlEqualTo("/endpoint"))
            .withRequestBodyPart(
                aMultipart("file").build()
            )
            .withRequestBodyPart(
                aMultipart("inputFormat")
                    .withBody(equalTo("TestType"))
                    .build()
            )
            .withRequestBodyPart(
                aMultipart("outputFormat")
                    .withBody(equalTo("TargetType"))
                    .build()

            )
        );
        // endregion

        assertNotNull(transformationResult);
        assertNotEquals(testDa, transformationResult);
        assertEquals(targetType, transformationResult.getArtifactType());
        assertEquals(testDa.getName() + "-translated", transformationResult.getName());

        ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId(transformationResult.getArtifactRef());

        TArtifactTemplate element = repository.getElement(artifactTemplateId);
        assertEquals(targetType, element.getType());
        assertEquals("ArtifactTemplateWithFilesAndSources-ArtifactTypeWithoutProperties_TargetType-w1-wip1", element.getId());
        assertEquals("ArtifactTemplateWithFilesAndSources-ArtifactTypeWithoutProperties_TargetType-w1-wip1", element.getName());

        ArtifactTemplateFilesDirectoryId filesDirectoryId = new ArtifactTemplateFilesDirectoryId(artifactTemplateId);
        RepositoryFileReference repositoryFileReference = new RepositoryFileReference(filesDirectoryId, "test.zip");
        assertEquals(
            Files.size(Paths.get(ClassLoader.getSystemClassLoader().getResource("__files/test.zip").toURI())), // 128 bytes
            repository.getSize(repositoryFileReference)
        );
    }
}
