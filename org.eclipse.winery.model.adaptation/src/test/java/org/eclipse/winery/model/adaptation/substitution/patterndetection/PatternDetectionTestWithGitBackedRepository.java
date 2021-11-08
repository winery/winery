/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.substitution.patterndetection;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.substitution.refinement.DefaultRefinementChooser;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatternDetectionTestWithGitBackedRepository extends TestWithGitBackedRepository {

    @Test
    public void detectPatterns() throws Exception {
        this.setRevisionTo("origin/plain");
        ServiceTemplateId testTemplateId = new ServiceTemplateId(
            "http://www.example.org/tosca/servicetemplates",
            "PatternDetectionTest_w1-wip1",
            false
        );
        PatternDetection detection = new PatternDetection(new DefaultRefinementChooser());
        ServiceTemplateId serviceTemplateId = detection.refineServiceTemplate(testTemplateId);
        TTopologyTemplate topology = RepositoryFactory.getRepository().getElement(serviceTemplateId)
            .getTopologyTemplate();
        assertNotNull(topology);

        QName java = QName.valueOf("{http://winery.opentosca.org/test/nodetypes}Java_8-Servelet-w1");
        QName firstPattern = QName.valueOf("{http://plain.winery.opentosca.org/patterns}FirstPattern_w1");
        QName secondPattern = QName.valueOf("{http://plain.winery.opentosca.org/patterns}SecondPattern_w1");
        QName thirdPattern = QName.valueOf("{http://plain.winery.opentosca.org/patterns}ThirdPattern_w1");
        QName secureSql = QName.valueOf("{http://www.example.org/tosca/relationshiptypes}Secure-SQL-Con_w1-wip1");
        QName hostedOn = QName.valueOf("{http://winery.opentosca.org/test/relationshiptypes}hostedOn");

        List<TNodeTemplate> nodeTemplates = topology.getNodeTemplates();
        assertEquals(nodeTemplates.size(), 4);
        Set<QName> nodeTypes = nodeTemplates.stream()
            .map(TNodeTemplate::getType)
            .collect(Collectors.toSet());
        assertEquals(nodeTypes.size(), 4);
        assertTrue(nodeTypes.contains(java));
        assertTrue(nodeTypes.contains(firstPattern));
        assertTrue(nodeTypes.contains(secondPattern));
        assertTrue(nodeTypes.contains(thirdPattern));

        List<TRelationshipTemplate> relationshipTemplates = topology.getRelationshipTemplates();
        assertEquals(relationshipTemplates.size(), 4);
        assertTrue(relationshipTemplates.stream()
            .anyMatch(rt -> rt.getType().equals(secureSql)
                && rt.getSourceElement().getRef().getType().equals(java)
                && rt.getTargetElement().getRef().getType().equals(secondPattern))
        );
        assertTrue(relationshipTemplates.stream()
            .anyMatch(rt -> rt.getType().equals(hostedOn)
                && rt.getSourceElement().getRef().getType().equals(java)
                && rt.getTargetElement().getRef().getType().equals(firstPattern))
        );
        assertTrue(relationshipTemplates.stream()
            .anyMatch(rt -> rt.getType().equals(hostedOn)
                && rt.getSourceElement().getRef().getType().equals(firstPattern)
                && rt.getTargetElement().getRef().getType().equals(thirdPattern))
        );
        assertTrue(relationshipTemplates.stream()
            .anyMatch(rt -> rt.getType().equals(hostedOn)
                && rt.getSourceElement().getRef().getType().equals(secondPattern)
                && rt.getTargetElement().getRef().getType().equals(thirdPattern))
        );
    }
}
