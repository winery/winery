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

package org.eclipse.winery.topologygraph.matching.patterndetection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTAttributeMapping;
import org.eclipse.winery.model.tosca.extensions.OTBehaviorPatternMapping;
import org.eclipse.winery.model.tosca.extensions.OTDeploymentArtifactMapping;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTPermutationMapping;
import org.eclipse.winery.model.tosca.extensions.OTRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTRelationDirection;
import org.eclipse.winery.model.tosca.extensions.OTRelationMapping;
import org.eclipse.winery.model.tosca.extensions.OTStayMapping;
import org.eclipse.winery.model.tosca.extensions.OTTestRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTTopologyFragmentRefinementModel;
import org.eclipse.winery.model.tosca.extensions.kvproperties.OTPropertyKV;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatternDetectionUtilsTest {

    @Test
    public void swapDetectorWithRefinement() {
        TTopologyTemplate detector = new TTopologyTemplate();
        TTopologyTemplate refinementStructure = new TTopologyTemplate();
        TEntityTemplate detectorElement = new TNodeTemplate();
        TEntityTemplate refinementElement = new TNodeTemplate();
        OTRelationMapping relationMapping = new OTRelationMapping(new OTRelationMapping.Builder()
            .setDetectorElement(detectorElement)
            .setRefinementElement(refinementElement)
            .setDirection(OTRelationDirection.INGOING)
            .setRelationType("relationType")
            .setValidSourceOrTarget(QName.valueOf("validSourceOrTarget"))
        );
        OTPermutationMapping permutationMapping = new OTPermutationMapping(new OTPermutationMapping.Builder()
            .setDetectorElement(detectorElement)
            .setRefinementElement(refinementElement)
        );
        OTAttributeMapping attributeMapping = new OTAttributeMapping(new OTAttributeMapping.Builder()
            .setDetectorElement(detectorElement)
            .setRefinementElement(refinementElement)
            .setDetectorProperty("detectorProperty")
            .setRefinementProperty("refinementProperty")
        );
        OTStayMapping stayMapping = new OTStayMapping(new OTStayMapping.Builder()
            .setDetectorElement(detectorElement)
            .setRefinementElement(refinementElement)
        );
        OTDeploymentArtifactMapping deploymentArtifactMapping = new OTDeploymentArtifactMapping(new OTDeploymentArtifactMapping.Builder()
            .setDetectorElement(detectorElement)
            .setRefinementElement(refinementElement)
            .setArtifactType(QName.valueOf("artifactType"))
        );
        OTBehaviorPatternMapping behaviorPatternMapping = new OTBehaviorPatternMapping(new OTBehaviorPatternMapping.Builder("behaviorPatternMap0")
            .setDetectorElement(detectorElement)
            .setRefinementElement(refinementElement)
            .setBehaviorPattern("behaviorPattern")
            .setProperty(new OTPropertyKV("key", "value"))
        );
        OTPatternRefinementModel prm = new OTPatternRefinementModel(new OTPatternRefinementModel.Builder()
            .setDetector(detector)
            .setRefinementStructure(refinementStructure)
            .setRelationMappings(Collections.singletonList(relationMapping))
            .setPermutationMappings(Collections.singletonList(permutationMapping))
            .setAttributeMappings(Collections.singletonList(attributeMapping))
            .setStayMappings(Collections.singletonList(stayMapping))
            .setDeploymentArtifactMappings(Collections.singletonList(deploymentArtifactMapping))
            .setBehaviorPatternMappings(Collections.singletonList(behaviorPatternMapping))
        );

        PatternDetectionUtils.swapDetectorWithRefinement(prm);
        assertEquals(prm.getDetector(), refinementStructure);
        assertEquals(prm.getRefinementStructure(), detector);

        assertEquals(relationMapping.getDetectorElement(), refinementElement);
        assertEquals(relationMapping.getRefinementElement(), detectorElement);
        assertEquals(relationMapping.getDirection(), OTRelationDirection.INGOING);
        assertEquals(relationMapping.getRelationType(), QName.valueOf("relationType"));
        assertEquals(relationMapping.getValidSourceOrTarget(), QName.valueOf("validSourceOrTarget"));

        assertEquals(permutationMapping.getDetectorElement(), refinementElement);
        assertEquals(permutationMapping.getRefinementElement(), detectorElement);

        assertEquals(attributeMapping.getDetectorElement(), refinementElement);
        assertEquals(attributeMapping.getRefinementElement(), detectorElement);
        assertEquals(attributeMapping.getDetectorProperty(), "refinementProperty");
        assertEquals(attributeMapping.getRefinementProperty(), "detectorProperty");

        assertEquals(stayMapping.getDetectorElement(), refinementElement);
        assertEquals(stayMapping.getRefinementElement(), detectorElement);

        assertEquals(deploymentArtifactMapping.getDetectorElement(), refinementElement);
        assertEquals(deploymentArtifactMapping.getRefinementElement(), detectorElement);
        assertEquals(deploymentArtifactMapping.getArtifactType(), QName.valueOf("artifactType"));

        assertEquals(behaviorPatternMapping.getDetectorElement(), refinementElement);
        assertEquals(behaviorPatternMapping.getRefinementElement(), detectorElement);
        assertEquals(behaviorPatternMapping.getBehaviorPattern(), "behaviorPattern");
        assertEquals(behaviorPatternMapping.getProperty(), new OTPropertyKV("key", "value"));
    }

    @Test
    public void toPdrms() {
        List<OTRefinementModel> refinementModels = new ArrayList<>();
        refinementModels.add(new OTTestRefinementModel());
        PatternDetectionUtils.toPdrms(refinementModels);
        assertEquals(refinementModels.size(), 0);

        refinementModels.add(new OTTopologyFragmentRefinementModel());
        PatternDetectionUtils.toPdrms(refinementModels);
        assertEquals(refinementModels.size(), 0);

        OTPatternRefinementModel prm = new OTPatternRefinementModel();
        refinementModels.add(prm);
        PatternDetectionUtils.toPdrms(refinementModels);
        assertEquals(refinementModels.size(), 0);

        prm.setIsPdrm(true);
        refinementModels.add(prm);
        PatternDetectionUtils.toPdrms(refinementModels);
        assertEquals(refinementModels.size(), 1);

        prm.setIsPdrm(false);
        PatternDetectionUtils.toPdrms(refinementModels);
        assertEquals(refinementModels.size(), 0);
    }
}
