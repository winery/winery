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
 *******************************************************************************/

package org.eclipse.winery.model.substitution.pattern.refinement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPatternRefinementModel;
import org.eclipse.winery.model.tosca.TRelationDirection;
import org.eclipse.winery.model.tosca.TRelationMapping;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.topologygraph.matching.ToscaIsomorphismMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaTypeMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

import com.google.common.collect.Iterators;
import org.jgrapht.GraphMapping;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PatternRefinementTest {

    @Test
    void testIsApplicable() {
        // region *** topology ***
        TNodeTemplate nt1 = new TNodeTemplate();
        nt1.setType("{http://ex.org}nodeType_1");
        nt1.setId("1");
        TRelationshipTemplate.SourceOrTargetElement sourceOrTargetElement1 = new TRelationshipTemplate.SourceOrTargetElement();
        sourceOrTargetElement1.setRef(nt1);

        TNodeTemplate nt2 = new TNodeTemplate();
        nt2.setType("{http://ex.org}nodeType_2");
        nt2.setId("2");
        TRelationshipTemplate.SourceOrTargetElement sourceOrTargetElement2 = new TRelationshipTemplate.SourceOrTargetElement();
        sourceOrTargetElement2.setRef(nt2);

        TNodeTemplate nt3 = new TNodeTemplate();
        nt3.setType("{http://ex.org}nodeType_3");
        nt3.setId("3");
        TRelationshipTemplate.SourceOrTargetElement sourceOrTargetElement3 = new TRelationshipTemplate.SourceOrTargetElement();
        sourceOrTargetElement3.setRef(nt3);

        TNodeTemplate nt4 = new TNodeTemplate();
        nt4.setType("{http://ex.org}nodeType_4");
        nt4.setId("4");
        TRelationshipTemplate.SourceOrTargetElement sourceOrTargetElement4 = new TRelationshipTemplate.SourceOrTargetElement();
        sourceOrTargetElement4.setRef(nt4);

        TRelationshipTemplate rst21 = new TRelationshipTemplate();
        rst21.setType("{http://ex.org}relType_1");
        rst21.setId("5");
        rst21.setSourceElement(sourceOrTargetElement2);
        rst21.setTargetElement(sourceOrTargetElement1);

        TRelationshipTemplate rst32 = new TRelationshipTemplate();
        rst32.setType("{http://ex.org}relType_1");
        rst32.setId("6");
        rst32.setSourceElement(sourceOrTargetElement3);
        rst32.setTargetElement(sourceOrTargetElement2);

        TRelationshipTemplate rst24 = new TRelationshipTemplate();
        rst24.setType("{http://ex.org}relType_2");
        rst24.setId("7");
        rst24.setSourceElement(sourceOrTargetElement2);
        rst24.setTargetElement(sourceOrTargetElement4);

        TTopologyTemplate topology = new TTopologyTemplate();
        topology.addNodeTemplate(nt1);
        topology.addNodeTemplate(nt2);
        topology.addNodeTemplate(nt3);
        topology.addNodeTemplate(nt4);
        topology.addRelationshipTemplate(rst21);
        topology.addRelationshipTemplate(rst32);
        topology.addRelationshipTemplate(rst24);
        // endregion

        // region *** detector ***
        TNodeTemplate dNt1 = new TNodeTemplate();
        dNt1.setType("{http://ex.org}nodeType_2");
        dNt1.setId("1231");
        TRelationshipTemplate.SourceOrTargetElement sOt1 = new TRelationshipTemplate.SourceOrTargetElement();
        sOt1.setRef(dNt1);

        TNodeTemplate dNt2 = new TNodeTemplate();
        dNt2.setType("{http://ex.org}nodeType_4");
        dNt2.setId("11");
        TRelationshipTemplate.SourceOrTargetElement sOt2 = new TRelationshipTemplate.SourceOrTargetElement();
        sOt2.setRef(dNt2);

        TRelationshipTemplate dRst12 = new TRelationshipTemplate();
        dRst12.setType("{http://ex.org}relType_2");
        dRst12.setId("465");
        dRst12.setSourceElement(sOt1);
        dRst12.setTargetElement(sOt2);

        TTopologyTemplate detector = new TTopologyTemplate();
        detector.addNodeTemplate(dNt1);
        detector.addNodeTemplate(dNt2);
        detector.addRelationshipTemplate(dRst12);
        // endregion

        // region *** relation Mapping ***
        TRelationMapping rm1 = new TRelationMapping();
        rm1.setDetectorNode(dNt1);
        rm1.setRelationType(QName.valueOf("{http://ex.org}relType_1"));
        rm1.setDirection(TRelationDirection.INGOING);
        rm1.setValidSourceOrTarget(QName.valueOf("{http://ex.org}nodeType_3"));

        TRelationMapping rm2 = new TRelationMapping();
        rm2.setDetectorNode(dNt1);
        rm2.setRelationType(QName.valueOf("{http://ex.org}relType_1"));
        rm2.setDirection(TRelationDirection.OUTGOING);
        rm2.setValidSourceOrTarget(QName.valueOf("{http://ex.org}nodeType_1"));
        // endregion

        // region *** PRM ***
        TPatternRefinementModel prm = new TPatternRefinementModel();
        prm.setDetector(detector);

        ToscaGraph topologyGraph = ToscaTransformer.createTOSCAGraph(topology);
        ToscaGraph detectorGraph = ToscaTransformer.createTOSCAGraph(prm.getDetector());

        ToscaIsomorphismMatcher matcher = new ToscaIsomorphismMatcher();
        List<GraphMapping<ToscaNode, ToscaEdge>> mappings = new ArrayList<>();
        Iterators.addAll(mappings, matcher.findMatches(detectorGraph, topologyGraph, new ToscaTypeMatcher()));

        TPatternRefinementModel.TRelationMappings relationMappings = new TPatternRefinementModel.TRelationMappings();
        relationMappings.getRelationMapping().add(rm1);
        relationMappings.getRelationMapping().add(rm2);
        prm.setRelationshipMappings(relationMappings);

        PatternRefinementCandidate candidate = new PatternRefinementCandidate(prm, mappings, detectorGraph);
        // endregion

        PatternRefinement patternRefinement = new PatternRefinement();

        assertTrue(patternRefinement.isApplicable(candidate, topology));
    }
}
