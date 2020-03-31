/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.backend;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.model.ids.EncodingUtil;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.version.ToscaDiff;
import org.eclipse.winery.common.version.VersionState;
import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.apache.tika.mime.MediaType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BackendUtilsTestWithGitBackedRepository extends TestWithGitBackedRepository {

    @Test
    public void initializePropertiesGeneratesCorrectKvProperties() throws Exception {
        this.setRevisionTo("origin/plain");

        PolicyTemplateId policyTemplateId = new PolicyTemplateId("http://www.example.org", "policytemplate", false);

        // create prepared policy template
        final Definitions definitions = BackendUtils.createWrapperDefinitionsAndInitialEmptyElement(repository, policyTemplateId);
        final TPolicyTemplate policyTemplate = (TPolicyTemplate) definitions.getElement();
        QName policyTypeQName = new QName("http://plain.winery.opentosca.org/policytypes", "PolicyTypeWithTwoKvProperties");
        policyTemplate.setType(policyTypeQName);

        BackendUtils.initializeProperties(repository, policyTemplate);

        assertNotNull(policyTemplate.getProperties());

        LinkedHashMap<String, String> kvProperties = policyTemplate.getProperties().getKVProperties();
        LinkedHashMap<String, String> expectedPropertyKVS = new LinkedHashMap<>();
        expectedPropertyKVS.put("key1", "");
        expectedPropertyKVS.put("key2", "");
        assertEquals(expectedPropertyKVS, kvProperties);
    }

    @Test
    public void initializePropertiesDoesNothingInTheCaseOfXmlElementProperties() throws Exception {
        this.setRevisionTo("origin/plain");

        PolicyTemplateId policyTemplateId = new PolicyTemplateId("http://www.example.org", "policytemplate", false);

        // create prepared policy template
        final Definitions definitions = BackendUtils.createWrapperDefinitionsAndInitialEmptyElement(repository, policyTemplateId);
        final TPolicyTemplate policyTemplate = (TPolicyTemplate) definitions.getElement();
        QName policyTypeQName = new QName("http://plain.winery.opentosca.org/policytypes", "PolicyTypeWithXmlElementProperty");
        policyTemplate.setType(policyTypeQName);

        BackendUtils.initializeProperties(repository, policyTemplate);

        assertNull(policyTemplate.getProperties());
    }

    @Test
    public void getVersionsOfOneDefinition() throws Exception {
        this.setRevisionTo("origin/plain");

        DefinitionsChildId id = new NodeTypeId("http://opentosca.org/nodetypes", "NodeTypeWith5Versions_0.3.4-w3", false);
        List<WineryVersion> versions = WineryVersionUtils.getAllVersionsOfOneDefinition(id, repository);

        assertEquals(5, versions.size());
    }

    @Test
    public void getVersionsOfOneDefinitionWithComponentThatDoesNotHaveAVersion() throws Exception {
        this.setRevisionTo("origin/plain");

        DefinitionsChildId id = new RelationshipTypeId("http://plain.winery.opentosca.org/relationshiptypes", "RelationshipTypeWithoutProperties", false);
        List<WineryVersion> versions = WineryVersionUtils.getAllVersionsOfOneDefinition(id, repository);

        assertEquals(1, versions.size());
        assertEquals("", versions.get(0).toString());
    }

    @Test
    public void getVersionWithNonEditableFlag() throws Exception {
        this.setRevisionTo("origin/plain");

        DefinitionsChildId id = new NodeTypeId("http://opentosca.org/nodetypes", "NodeTypeWith5Versions_0.3.4-w3", false);
        List<WineryVersion> versions = WineryVersionUtils.getAllVersionsOfOneDefinition(id, repository);

        versions.forEach(wineryVersion -> assertFalse(wineryVersion.isEditable()));
    }

    @Test
    public void getVersionWithEditableFlag() throws Exception {
        this.setRevisionTo("origin/plain");

        NodeTypeId id = new NodeTypeId("http://opentosca.org/nodetypes", "NodeTypeWith5Versions_0.3.4-w3", false);

        // Make some changes to the file
        makeSomeChanges(id);

        List<WineryVersion> versions = WineryVersionUtils.getAllVersionsOfOneDefinition(id, repository);

        assertTrue(versions.get(0).isEditable());

        List<WineryVersion> collect = versions.stream()
            .filter(item -> !item.isEditable())
            .collect(Collectors.toList());
        assertEquals(4, collect.size());
    }

    @Test
    public void getVersionWithEditableFlagAndChangesInAFileWhichIsNotTheToscaFile() throws Exception {
        this.setRevisionTo("origin/plain");

        NodeTypeId id = new NodeTypeId("http://opentosca.org/nodetypes", "NodeTypeWith5Versions_0.3.4-w3", false);

        // Make some changes to an associated file
        RepositoryFileReference ref = new RepositoryFileReference(id, EncodingUtil.URLdecode("README.md"));
        RepositoryFactory.getRepository().putContentToFile(ref, "someUnguessableContent", MediaType.TEXT_PLAIN);

        List<WineryVersion> versions = WineryVersionUtils.getAllVersionsOfOneDefinition(id, repository);

        assertTrue(versions.get(0).isEditable());

        List<WineryVersion> collect = versions.stream()
            .filter(item -> !item.isEditable())
            .collect(Collectors.toList());
        assertEquals(4, collect.size());
    }

    @Test
    public void getVersionWithEditableFlagFromComponentWithoutAVersion() throws Exception {
        this.setRevisionTo("origin/plain");

        PolicyTemplateId policyTemplateId = new PolicyTemplateId("http://plain.winery.opentosca.org/policytemplates", "PolicyTemplateWithoutProperties", false);

        List<WineryVersion> versions = WineryVersionUtils.getAllVersionsOfOneDefinition(policyTemplateId, repository);

        // For convenience, we accept editing already existing components without versions
        assertTrue(versions.get(0).isEditable());
    }

    @Test
    public void getVersionListOfAnOldComponentVersionWhichIsReleasable() throws Exception {
        this.setRevisionTo("origin/plain");

        NodeTypeId id = new NodeTypeId("http://opentosca.org/nodetypes", "NodeTypeWithALowerReleasableManagementVersion_2-w2-wip1", false);

        List<WineryVersion> versionList = WineryVersionUtils.getAllVersionsOfOneDefinition(id, repository);
        WineryVersion version = versionList.get(versionList.size() - 2);

        assertFalse(version.isEditable());
        assertTrue(version.isReleasable());
    }

    @Test
    public void getVersionListOfAnOldComponentVersionWhichIsEditable() throws Exception {
        this.setRevisionTo("origin/plain");

        NodeTypeId id = new NodeTypeId("http://opentosca.org/nodetypes", "NodeTypeWithALowerReleasableManagementVersion_2-w2-wip1", false);

        // instead of creating a new NodeType, just make some changes to this element, which should create the same state
        makeSomeChanges(id);

        List<WineryVersion> versionList = WineryVersionUtils.getAllVersionsOfOneDefinition(id, repository);
        WineryVersion version = versionList.get(versionList.size() - 2);

        assertTrue(version.isEditable());
        assertTrue(version.isReleasable());
    }

    @Test
    public void detectChangesInANodeTemplate() throws Exception {
        this.setRevisionTo("origin/plain");

        NodeTypeId newVersion = new NodeTypeId("http://plain.winery.opentosca.org/nodetypes", "NodeTypeWithThreeReqCapPairsCoveringAllReqCapVariants_w1-wip1", false);
        WineryVersion oldVersion = new WineryVersion("", 0, 0);

        ToscaDiff toscaDiff = BackendUtils.compare(newVersion, oldVersion, repository);
        ToscaDiff properties = toscaDiff.getChildrenMap().get("winerysPropertiesDefinition");

        assertEquals(VersionState.CHANGED, toscaDiff.getState());
        assertEquals(VersionState.ADDED, properties.getState());
    }

    @Test
    public void detectPropertyChangesInANodeTemplate() throws Exception {
        this.setRevisionTo("origin/plain");

        NodeTypeId newVersion = new NodeTypeId("http://plain.winery.opentosca.org/nodetypes", "NodeTypeWithThreeReqCapPairsCoveringAllReqCapVariants_w1-wip2", false);
        WineryVersion oldVersion = new WineryVersion("", 1, 1);

        ToscaDiff toscaDiff = BackendUtils.compare(newVersion, oldVersion, repository);
        ToscaDiff properties = toscaDiff.getChildrenMap().get("winerysPropertiesDefinition").getChildrenMap().get("propertyDefinitionKVList");

        assertEquals(VersionState.CHANGED, toscaDiff.getState());
        assertEquals(VersionState.CHANGED, properties.getState());
        assertEquals(3, properties.getChildren().size());
    }
}

