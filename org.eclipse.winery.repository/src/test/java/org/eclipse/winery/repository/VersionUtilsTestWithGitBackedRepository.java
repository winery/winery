/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
package org.eclipse.winery.repository;

import java.util.Map;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.version.ToscaDiff;
import org.eclipse.winery.common.version.VersionState;
import org.eclipse.winery.common.version.VersionUtils;

import org.junit.Assert;
import org.junit.Test;

/**
 * This test class resides in the repository in order to make use of the git backed test suite.
 */
public class VersionUtilsTestWithGitBackedRepository extends TestWithGitBackedRepository {

    @Test
    public void calculateDifferences() throws Exception {
        this.setRevisionTo("origin/plain");

        ServiceTemplateId oldVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates", "ServiceTemplateWithFourPolicies", false);
        ServiceTemplateId newVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates", "ServiceTemplateWithFourPolicies_-w1-wip1", false);

        ToscaDiff diffNode = VersionUtils.calculateDifferences(repository.getElement(oldVersion), repository.getElement(newVersion));
        ToscaDiff name = diffNode.getChildrenMap().get("name");
        ToscaDiff id = diffNode.getChildrenMap().get("id");
        ToscaDiff topology = diffNode.getChildrenMap().get("topologyTemplate");

        Assert.assertEquals(VersionState.CHANGED, diffNode.getState());

        Assert.assertEquals(VersionState.CHANGED, name.getState());
        Assert.assertEquals("ServiceTemplateWithFourPolicies_-w1-wip1", name.getNewValue());
        Assert.assertEquals("ServiceTemplateWithFourPolicies", name.getOldValue());

        Assert.assertEquals(VersionState.CHANGED, name.getState());
        Assert.assertEquals("ServiceTemplateWithFourPolicies_-w1-wip1", id.getNewValue());
        Assert.assertEquals("ServiceTemplateWithFourPolicies", id.getOldValue());

        Assert.assertEquals(VersionState.CHANGED, topology.getState());
        Assert.assertEquals(2, topology.getChildren().size());
    }

    @Test
    public void noDifferencesInServiceTemplate() throws Exception {
        this.setRevisionTo("origin/plain");

        ServiceTemplateId oldVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates", "ServiceTemplateWithFourPolicies_-w1-wip1", false);
        ServiceTemplateId newVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates", "ServiceTemplateWithFourPolicies_-w1-wip1", false);

        ToscaDiff diffNode = VersionUtils.calculateDifferences(repository.getElement(oldVersion), repository.getElement(newVersion));

        Assert.assertEquals(VersionState.UNCHANGED, diffNode.getState());
    }

    @Test
    public void noDifferencesInRelationshipTemplates() throws Exception {
        this.setRevisionTo("origin/plain");

        ServiceTemplateId oldVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateWithFourPolicies_-w1-wip1", false);
        ServiceTemplateId newVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateWithFourPolicies_-w1-wip1", false);

        ToscaDiff diffNode = VersionUtils.calculateDifferences(repository.getElement(oldVersion).getTopologyTemplate().getRelationshipTemplate("con_129"),
            repository.getElement(newVersion).getTopologyTemplate().getRelationshipTemplate("con_129"));

        Assert.assertEquals(VersionState.UNCHANGED, diffNode.getState());
    }

    @Test
    public void noDifferencesInServiceTemplatesWithOnlyOneNodeTemplate() throws Exception {
        this.setRevisionTo("origin/plain");

        ServiceTemplateId oldVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateWithOneNodeTemplate_-w1-wip1", false);
        ServiceTemplateId newVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateWithOneNodeTemplate_-w1-wip1", false);

        ToscaDiff diffNode = VersionUtils.calculateDifferences(repository.getElement(oldVersion), repository.getElement(newVersion));

        Assert.assertEquals(VersionState.UNCHANGED, diffNode.getState());
    }

    @Test
    public void noDifferencesInServiceTemplatesWithTwoNodeTemplates() throws Exception {
        this.setRevisionTo("origin/plain");

        ServiceTemplateId oldVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateWithTwoNodeTemplates_-w1-wip1", false);
        ServiceTemplateId newVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateWithTwoNodeTemplates_-w1-wip1", false);

        ToscaDiff diffNode = VersionUtils.calculateDifferences(repository.getElement(oldVersion), repository.getElement(newVersion));

        Assert.assertEquals(VersionState.UNCHANGED, diffNode.getState());
    }

    @Test
    public void differencesInServiceTemplatesWithTwoNodeTemplatesAndOneDifferentNodeTemplate() throws Exception {
        this.setRevisionTo("origin/plain");

        ServiceTemplateId oldVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateWithTwoNodeTemplates_-w1-wip1", false);
        ServiceTemplateId newVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateWithTwoNodeTemplates_-w1-wip2", false);

        ToscaDiff diffNode = VersionUtils.calculateDifferences(repository.getElement(oldVersion), repository.getElement(newVersion));
        Map<String, ToscaDiff> nodeTemplateDiff = diffNode.getChildrenMap().get("topologyTemplate")
            .getChildrenMap().get("nodeTemplates").getChildrenMap();
        ToscaDiff element1 = nodeTemplateDiff.get("0");
        ToscaDiff element2 = nodeTemplateDiff.get("1");

        Assert.assertEquals(VersionState.CHANGED, diffNode.getState());

        Assert.assertEquals("NodeTypeWithXmlElementProperty", element1.getElement());
        Assert.assertEquals(VersionState.ADDED, element1.getState());

        Assert.assertEquals("NodeTypeWithOneReqCapPairWithoutProperties", element2.getElement());
        Assert.assertEquals(VersionState.REMOVED, element2.getState());
    }

    @Test
    public void differencesInServiceTemplatesWithTwoNodeTemplatesAndOneDifferentNodeTemplateReversed() throws Exception {
        this.setRevisionTo("origin/plain");

        ServiceTemplateId oldVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateWithTwoNodeTemplates_-w1-wip2", false);
        ServiceTemplateId newVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateWithTwoNodeTemplates_-w1-wip1", false);

        ToscaDiff diffNode = VersionUtils.calculateDifferences(repository.getElement(oldVersion), repository.getElement(newVersion));
        Map<String, ToscaDiff> nodeTemplateDiff = diffNode.getChildrenMap().get("topologyTemplate")
            .getChildrenMap().get("nodeTemplates").getChildrenMap();
        ToscaDiff element1 = nodeTemplateDiff.get("0");
        ToscaDiff element2 = nodeTemplateDiff.get("1");

        Assert.assertEquals("NodeTypeWithOneReqCapPairWithoutProperties", element1.getElement());
        Assert.assertEquals(VersionState.ADDED, element1.getState());

        Assert.assertEquals("NodeTypeWithXmlElementProperty", element2.getElement());
        Assert.assertEquals(VersionState.REMOVED, element2.getState());

        Assert.assertEquals(VersionState.CHANGED, diffNode.getState());
    }

    @Test
    public void differencesInServiceTemplateWithChangesInANodeTemplate() throws Exception {
        this.setRevisionTo("origin/plain");

        ServiceTemplateId oldVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateWithTwoNodeTemplates_-w1-wip3", false);
        ServiceTemplateId newVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateWithTwoNodeTemplates_-w1-wip4", false);

        ToscaDiff diffNode = VersionUtils.calculateDifferences(repository.getElement(oldVersion), repository.getElement(newVersion));

        Map<String, ToscaDiff> nodeTemplateDiff = diffNode.getChildrenMap().get("topologyTemplate").getChildrenMap().get("nodeTemplates").getChildrenMap();
        ToscaDiff element1 = nodeTemplateDiff.get("2");

        Assert.assertEquals("NodeTypeWithTwoKVProperties", element1.getElement());
        Assert.assertEquals(VersionState.CHANGED, element1.getState());
    }

    @Test
    public void getDifferencesList() throws Exception {
        this.setRevisionTo("origin/plain");

        ServiceTemplateId oldVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateWithTwoNodeTemplates_-w1-wip3", false);
        ServiceTemplateId newVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateWithTwoNodeTemplates_-w1-wip4", false);

        ToscaDiff diffNode = VersionUtils.calculateDifferences(repository.getElement(oldVersion), repository.getElement(newVersion));

        Assert.assertEquals("## Changes from version -w1-wip3 to -w1-wip4\n" +
                "\n" +
                "### Added\n" +
                "- topologyTemplate/nodeTemplates/NodeTypeWithTwoKVProperties_2\n" +
                "- topologyTemplate/nodeTemplates/NodeTypeWithTwoKVProperties/deploymentArtifacts\n" +
                "- topologyTemplate/relationshipTemplates/NodeTypeWithTwoKVProperties_RelationshlpTypeWithValidSourceAndTarget_-w1-wip1_NodeTypeWithTwoKVProperties_2\n" +
                "\n" +
                "### Changed\n" +
                "- id\n" +
                "  changed from \"ServiceTemplateWithTwoNodeTemplates_-w1-wip3\" to \"ServiceTemplateWithTwoNodeTemplates_-w1-wip4\"\n" +
                "- name\n" +
                "  changed from \"ServiceTemplateWithTwoNodeTemplates_-w1-wip3\" to \"ServiceTemplateWithTwoNodeTemplates_-w1-wip4\"\n" +
                "- topologyTemplate/nodeTemplates/NodeTypeWithTwoKVProperties/otherAttributes/{x}\n" +
                "  changed from \"399\" to \"400\"\n" +
                "- topologyTemplate/nodeTemplates/NodeTypeWithTwoKVProperties/otherAttributes/{{http://www.opentosca.org/winery/extensions/tosca/2013/02/12}x}\n" +
                "  changed from \"399\" to \"400\"\n" +
                "- topologyTemplate/nodeTemplates/NodeTypeWithTwoKVProperties/x\n" +
                "  changed from \"399\" to \"400\"\n" +
                "\n" +
                "### Removed\n" +
                "- topologyTemplate/nodeTemplates/NodeTypeWithoutProperties\n" +
                "- topologyTemplate/relationshipTemplates/NodeTypeWithTwoKVProperties_RelationshipTypeWithoutProperties_NodeTypeWithoutProperties",
            diffNode.getChangeLog());
    }

    @Test
    public void getDifferencesListWithOnlyOneChange() throws Exception {
        this.setRevisionTo("origin/plain");

        NodeTypeId oldVersion = new NodeTypeId("http://opentosca.org/nodetypes", "NodeTypeWith5Versions_0.3.0-w2", false);
        NodeTypeId newVersion = new NodeTypeId("http://opentosca.org/nodetypes", "NodeTypeWith5Versions_0.3.4-w1", false);

        ToscaDiff toscaDiff = VersionUtils.calculateDifferences(repository.getElement(oldVersion), repository.getElement(newVersion));

        Assert.assertEquals("## Changes from version 0.3.0-w2 to 0.3.4-w1\n" +
                "\n" +
                "### Changed\n" +
                "- name\n" +
                "  changed from \"NodeTypeWith5Versions_0.3.0-w2\" to \"NodeTypeWith5Versions_0.3.4-w1\"",
            toscaDiff.getChangeLog());
    }

    @Test
    public void getDifferencesInKVPropertiesAsChangeLog() throws Exception {
        this.setRevisionTo("d14754c455e4b4e4dbf0afd396cd36d89b02f8a8");

        ServiceTemplateId oldVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateMinimalExampleWithAllPropertyVariants", false);
        ServiceTemplateId newVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateMinimalExampleWithAllPropertyVariants_-w1-wip1", false);

        ToscaDiff toscaDiff = VersionUtils.calculateDifferences(repository.getElement(oldVersion), repository.getElement(newVersion));

        Assert.assertEquals("## Changes from version  to -w1-wip1\n" +
                "\n" +
                "### Added\n" +
                "- topologyTemplate/relationshipTemplates/NodeTypeWithoutProperties_RelationshipTypeWithoutProperties_NodeTypeWithTwoKVProperties\n" +
                "- topologyTemplate/relationshipTemplates/NodeTypeWithoutProperties_RelationshipTypeWithTwoKvPropertie_NodeTypeWithXmlElementProperty\n" +
                "- topologyTemplate/relationshipTemplates/NodeTypeWithTwoKVProperties_RelationshipTypeWithXmlElementProperty_NodeTypeWithXmlElementProperty\n" +
                "\n" +
                "### Changed\n" +
                "- id\n" +
                "  changed from \"ServiceTemplateMinimalExampleWithAllPropertyVariants\" to \"ServiceTemplateMinimalExampleWithAllPropertyVariants_-w1-wip1\"\n" +
                "- name\n" +
                "  changed from \"ServiceTemplateMinimalExampleWithAllPropertyVariants\" to \"ServiceTemplateMinimalExampleWithAllPropertyVariants_-w1-wip1\"\n" +
                "- topologyTemplate/nodeTemplates/NodeTypeWithoutProperties/otherAttributes/{{http://www.opentosca.org/winery/extensions/tosca/2013/02/12}location}\n" +
                "  changed from \"undefined\" to \"\"\n" +
                "- topologyTemplate/nodeTemplates/NodeTypeWithXmlElementProperty/otherAttributes/{{http://www.opentosca.org/winery/extensions/tosca/2013/02/12}location}\n" +
                "  changed from \"undefined\" to \"\"\n" +
                "- topologyTemplate/nodeTemplates/NodeTypeWithTwoKVProperties/otherAttributes/{{http://www.opentosca.org/winery/extensions/tosca/2013/02/12}location}\n" +
                "  changed from \"undefined\" to \"\"\n" +
                "- topologyTemplate/nodeTemplates/NodeTypeWithTwoKVProperties/properties/KVProperties/{key1}\n" +
                "  changed from \"value\" to \"testValue\"\n" +
                "\n" +
                "### Removed\n" +
                "- topologyTemplate/relationshipTemplates/con_16\n" +
                "- topologyTemplate/relationshipTemplates/con_28\n" +
                "- topologyTemplate/relationshipTemplates/con_40",
            toscaDiff.getChangeLog());
    }

    @Test
    public void ensureRelationshipTemplatesAreNotChangedWhenSourceOrTargetElementIsChanged() throws Exception {
        this.setRevisionTo("origin/plain");

        ServiceTemplateId oldVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateWithTwoNodeTemplates_-w2-wip1", false);
        ServiceTemplateId newVersion = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateWithTwoNodeTemplates_-w2-wip2", false);

        ToscaDiff toscaDiff = VersionUtils.calculateDifferences(repository.getElement(oldVersion), repository.getElement(newVersion));

        Assert.assertEquals("## Changes from version -w2-wip1 to -w2-wip2\n" +
                "\n" +
                "### Changed\n" +
                "- id\n" +
                "  changed from \"ServiceTemplateWithTwoNodeTemplates_-w2-wip1\" to \"ServiceTemplateWithTwoNodeTemplates_-w2-wip2\"\n" +
                "- name\n" +
                "  changed from \"ServiceTemplateWithTwoNodeTemplates_-w2-wip1\" to \"ServiceTemplateWithTwoNodeTemplates_-w2-wip2\"\n" +
                "- topologyTemplate/nodeTemplates/NodeTypeWithTwoKVProperties/properties/KVProperties/{key1}\n" +
                "  changed from \"\" to \"MyKeyElement\"",
            toscaDiff.getChangeLog());
    }
}
