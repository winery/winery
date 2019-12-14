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
package org.eclipse.winery.model.threatmodeling;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TBoolean;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreatModelingTestWithGitBackedRepository extends TestWithGitBackedRepository {
    private String demoNamespace = "http://example.com";

    @Test
    public void testForPresentSVNFinServiceTemplate() throws Exception {

        this.setRevisionTo("origin/plain");
        ThreatModelingUtils tmu = new ThreatModelingUtils(repository);
        try {
            tmu.setupThreatModelingTypes();
            ThreatCreationApiData newThreat = new ThreatCreationApiData();
            newThreat
                .setName("MyThreat");
            newThreat.setDescription("My Description");
            newThreat.setStride("Spoofing");
            newThreat.setSeverity("Low");
            tmu.createThreatAndMitigationTemplates(newThreat);
        } catch (Exception e) {
            return;
        }

        TNodeType svnfGroupA = new TNodeType
            .Builder("MyGroupA")
            .setTargetNamespace(demoNamespace)
            .setAbstract(TBoolean.YES)
            .setDerivedFrom(QName.valueOf(ThreatModelingConstants.SVNF_NODE_TYPE))
            .build();

        TNodeType svnfGroupB = new TNodeType
            .Builder("MyGroupB")
            .setTargetNamespace(demoNamespace)
            .setAbstract(TBoolean.YES)
            .setDerivedFrom(QName.valueOf(ThreatModelingConstants.SVNF_NODE_TYPE))
            .build();

        TTopologyTemplate emptyTopology = new TTopologyTemplate.Builder().build();

        TPolicy boundaryThreat = new TPolicy
            .Builder(QName.valueOf(ThreatModelingConstants.MITIGATION_POLICY_ID))
            .setPolicyRef(new QName(ThreatModelingConstants.THREATMODELING_NAMESPACE, "MITIGATE_MyThreat"))
            .setName("MITIGATE_MyThreat")
            .build();

        TBoundaryDefinitions boundaryDefinitions = new TBoundaryDefinitions
            .Builder()
            .addPolicies(boundaryThreat)
            .build();

        TServiceTemplate networkServiceA = new TServiceTemplate.Builder("NetworkServiceA", emptyTopology)
            .setTargetNamespace(demoNamespace)
            .setBoundaryDefinitions(boundaryDefinitions)
            .setSubstitutableNodeType(new QName(demoNamespace, "MyGroupA"))
            .build();

        TServiceTemplate networkServiceB = new TServiceTemplate.Builder("NetworkServiceB", emptyTopology)
            .setTargetNamespace(demoNamespace)
            .setBoundaryDefinitions(boundaryDefinitions)
            .setSubstitutableNodeType(new QName(demoNamespace, "MyGroupB"))
            .build();

        TNodeType myNodeType = new TNodeType
            .Builder("MyNode")
            .setTargetNamespace(demoNamespace)
            .build();

        TPolicy nodeTemplateThreat = new TPolicy
            .Builder(QName.valueOf(ThreatModelingConstants.THREAT_POLICY_ID))
            .setPolicyRef(new QName(ThreatModelingConstants.THREATMODELING_NAMESPACE, "MyThreat"))
            .setName("MyThreat")
            .build();

        TNodeTemplate myNodeTemplate = new TNodeTemplate
            .Builder("myNodeTemplate", new QName(demoNamespace, "MyNode"))
            .addPolicies(nodeTemplateThreat)
            .build();

        TTopologyTemplate myTopology = new TTopologyTemplate.Builder().addNodeTemplates(myNodeTemplate).build();

        TServiceTemplate myService = new TServiceTemplate.Builder("MyService", myTopology)
            .setTargetNamespace(demoNamespace)
            .build();

        repository.setElement(new ServiceTemplateId(demoNamespace, "NetworkServiceA", false), networkServiceA);
        repository.setElement(new ServiceTemplateId(demoNamespace, "NetworkServiceB", false), networkServiceB);
        repository.setElement(new NodeTypeId(demoNamespace, "MyGroupA", false), svnfGroupA);
        repository.setElement(new NodeTypeId(demoNamespace, "MyGroupB", false), svnfGroupB);
        repository.setElement(new NodeTypeId(demoNamespace, "MyNode", false), myNodeType);
        repository.setElement(new ServiceTemplateId(demoNamespace, "MyService", false), myService);

        ThreatModeling tm = new ThreatModeling(new ServiceTemplateId(demoNamespace, "MyService", false), repository);

        ThreatAssessment assessment = tm.getServiceTemplateThreats();
        Threat asessedThreat = assessment.getThreats().get(new QName(ThreatModelingConstants.THREATMODELING_NAMESPACE, "MyThreat"));

        assertEquals("MyThreat", asessedThreat.getTemplateName());
        assertTrue(asessedThreat.getMitigations().contains(new QName(demoNamespace, "MyGroupA")));
        assertTrue(asessedThreat.getMitigations().contains(new QName(demoNamespace, "MyGroupB")));

        assertTrue(assessment.getSVNFs().isEmpty());
    }
}
