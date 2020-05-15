/*******************************************************************************
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
 *******************************************************************************/
package org.eclipse.winery.model.threatmodeling;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.model.tosca.TBoolean;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreatModelingUtilsWithGitBackendRepositoryTest extends TestWithGitBackedRepository {

    @Test
    public void checkThreatModelingPrerequisits() throws Exception {
        this.setRevisionTo("origin/plain");

        ThreatModelingUtils tmu = new ThreatModelingUtils(repository);

        assertFalse(tmu.checkPrerequisites());

        tmu.setupThreatModelingTypes();

        assertTrue(tmu.checkPrerequisites());
    }

    @Test
    public void checkBoundaryDefinitionsOfServiceTemplate() throws Exception {
        this.setRevisionTo("origin/plain");

        ThreatModelingUtils tmu = new ThreatModelingUtils(repository);
        TServiceTemplate st = new TServiceTemplate.Builder("MyExample", new TTopologyTemplate.Builder().build()).build();

        assertFalse(tmu.hasBoundaryDefinitionMitigationPolicy(st));

        TPolicyTemplate myMitigation = new TPolicyTemplate
            .Builder("myMitigation", QName.valueOf(ThreatModelingConstants.MITIGATION_POLICY_ID))
            .build();

        TPolicy mitigationPolicy = new TPolicy.Builder(myMitigation.getTypeAsQName()).build();

        TBoundaryDefinitions stBoundaryDefinitions = new TBoundaryDefinitions
            .Builder()
            .addPolicies(mitigationPolicy)
            .build();

        st.setBoundaryDefinitions(stBoundaryDefinitions);

        assertTrue(tmu.hasBoundaryDefinitionMitigationPolicy(st));
    }

    @Test
    public void checkIsOfTypeOrInheritsFromSVNF() throws Exception {
        this.setRevisionTo("origin/plain");

        String namespace = "http://example.com";

        QName base = new QName(namespace, "BASE");
        QName otherBase = new QName(namespace, "OTHER_BASE");
        QName childA = new QName(namespace, "CHILD_A");
        QName childB = new QName(namespace, "CHILD_B");

        TNodeType baseType = new TNodeType.Builder("BASE")
            .setDerivedFrom(QName.valueOf(ThreatModelingConstants.SVNF_NODE_TYPE))
            .build();

        TNodeType otherBaseType = new TNodeType.Builder("BASE")
            .build();

        TNodeType childAType = new TNodeType.Builder("CHILD_A").setAbstract(TBoolean.YES).setDerivedFrom(base).build();
        TNodeType childBType = new TNodeType.Builder("CHILD_B").setAbstract(TBoolean.YES).setDerivedFrom(otherBase).build();

        repository.setElement(new NodeTypeId(base), baseType);
        repository.setElement(new NodeTypeId(otherBase), otherBaseType);
        repository.setElement(new NodeTypeId(childA), childAType);
        repository.setElement(new NodeTypeId(childB), childBType);

        ThreatModelingUtils tmu = new ThreatModelingUtils(repository);

        assertTrue(tmu.isOfTypeOrInheritsFromSVNF(base));
        assertTrue(tmu.isOfTypeOrInheritsFromSVNF(childA));
        assertFalse(tmu.isOfTypeOrInheritsFromSVNF(otherBase));
        assertFalse(tmu.isOfTypeOrInheritsFromSVNF(childB));
    }

    @Test
    public void checkFindFirstAbstractTypeThatInheritsFromSVNF() throws Exception {
        this.setRevisionTo("origin/plain");

        String namespace = "http://example.com";

        QName base = new QName(namespace, "base");
        QName childA = new QName(namespace, "childA");
        QName childB = new QName(namespace, "childB");
        QName notDerivedOrAbstract = new QName(namespace, "notDerivedOrAbstract");

        TNodeType baseType = new TNodeType.Builder("base")
            .setDerivedFrom(QName.valueOf(ThreatModelingConstants.SVNF_NODE_TYPE))
            .setAbstract(TBoolean.YES)
            .build();

        TNodeType childAType = new TNodeType.Builder("childA").setDerivedFrom(base).build();
        TNodeType childBType = new TNodeType.Builder("childB").setDerivedFrom(childA).build();

        TNodeType notDerivedOrAbstractType = new TNodeType.Builder("notDerivedOrAbstract").build();

        repository.setElement(new NodeTypeId(base), baseType);
        repository.setElement(new NodeTypeId(childA), childAType);
        repository.setElement(new NodeTypeId(childB), childBType);
        repository.setElement(new NodeTypeId(notDerivedOrAbstract), notDerivedOrAbstractType);

        ThreatModelingUtils tmu = new ThreatModelingUtils(repository);

        assertEquals(base, tmu.findFirstAbstractType(base));
        assertEquals(base, tmu.findFirstAbstractType(childA));
        assertEquals(base, tmu.findFirstAbstractType(childB));

        assertThrows(Exception.class, () -> tmu.findFirstAbstractType(notDerivedOrAbstract));
    }

    @Test
    public void checkThreatMitigationPairCreation() throws Exception {
        this.setRevisionTo("origin/plain");
        ThreatModelingUtils tmu = new ThreatModelingUtils(repository);

        PolicyTemplateId threat = new PolicyTemplateId(ThreatModelingConstants.THREATMODELING_NAMESPACE, "MyThreat", false);
        PolicyTemplateId mitigation = new PolicyTemplateId(ThreatModelingConstants.THREATMODELING_NAMESPACE, "MITIGATE_MyThreat", false);

        ThreatCreationApiData newThreat = new ThreatCreationApiData();
        newThreat
            .setName("MyThreat");
        newThreat.setDescription("My Description");
        newThreat.setStride("Spoofing");
        newThreat.setSeverity("Low");
        tmu.createThreatAndMitigationTemplates(newThreat);

        assertTrue(repository.exists(threat));
        assertTrue(repository.exists(mitigation));

        TPolicyTemplate mitigationPolicy = repository.getElement(mitigation);
        assertEquals(threat.getQName().toString(), ModelUtilities.getPropertiesKV(mitigationPolicy).get("ThreatReference"));
    }

    @Test
    public void checkThreatCatalog() throws Exception {
        this.setRevisionTo("origin/plain");
        ThreatModelingUtils tmu = new ThreatModelingUtils(repository);
        assertTrue(tmu.getThreatCatalogue().isEmpty());

        PolicyTemplateId threatAID = new PolicyTemplateId("http://example.com", "MyThreat", false);

        TPolicyTemplate threatA = new TPolicyTemplate
            .Builder("myThreat", QName.valueOf(ThreatModelingConstants.THREAT_POLICY_ID))
            .setName("MyThreat")
            .build();

        repository.setElement(threatAID, threatA);
        tmu = new ThreatModelingUtils(repository);

        assertFalse(tmu.getThreatCatalogue().isEmpty());
        assertEquals("MyThreat", tmu.getThreatCatalogue().get(0).getTemplateName());
    }
}
