/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.substitution.refinement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.OTAttributeMapping;
import org.eclipse.winery.model.tosca.OTAttributeMappingType;
import org.eclipse.winery.model.tosca.OTDeploymentArtifactMapping;
import org.eclipse.winery.model.tosca.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.OTPermutationMapping;
import org.eclipse.winery.model.tosca.OTRelationDirection;
import org.eclipse.winery.model.tosca.OTRelationMapping;
import org.eclipse.winery.model.tosca.OTStayMapping;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;

public abstract class PermutationHelper {

    static OTPatternRefinementModel generatePrmWithStayMapping() {
        /*                                             
        ########                 ######## 
        #  (1) #----stay-------> # (11) # 
        ########                 ######## 
            |                    (2) |
            | (2)                   \/ 
           \/                    ########
        ########---------------> # (13) #
        #  (2) #--------+        ########
        ########        |            | (2)
            |           |           \/   
            | (2        +------> ########
           \/                    # (15) #
        ########                 ########
        #  (3) #--------+            | (2)
        ########        |           \/   
                        |        ########
                        +------> # (16) #
                                 ########
         */

        TTopologyTemplate detector = generateDetectorWithThreePatterns();

        // region refinement structure
        TNodeTemplate refinementNode_11 = new TNodeTemplate();
        refinementNode_11.setType("{http://ex.org}nodeType_11");
        refinementNode_11.setId("11");

        TNodeTemplate refinementNode_13 = new TNodeTemplate();
        refinementNode_13.setType("{http://ex.org}nodeType_13");
        refinementNode_13.setId("13");

        TNodeTemplate refinementNode_15 = new TNodeTemplate();
        refinementNode_15.setType("{http://ex.org}nodeType_15");
        refinementNode_15.setId("15");

        TNodeTemplate refinementNode_16 = new TNodeTemplate();
        refinementNode_16.setType("{http://ex.org}nodeType_16");
        refinementNode_16.setId("16");

        TRelationshipTemplate node11_hostedOn_node13 = ModelUtilities.createRelationshipTemplate(
            refinementNode_11, refinementNode_13, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node13_hostedOn_node15 = ModelUtilities.createRelationshipTemplate(
            refinementNode_13, refinementNode_15, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node15_hostedOn_node16 = ModelUtilities.createRelationshipTemplate(
            refinementNode_15, refinementNode_16, QName.valueOf("{http://ex.org}relType_hostedOn"));

        TTopologyTemplate refinementStructure = new TTopologyTemplate();
        refinementStructure.addNodeTemplate(refinementNode_11);
        refinementStructure.addNodeTemplate(refinementNode_13);
        refinementStructure.addNodeTemplate(refinementNode_15);
        refinementStructure.addNodeTemplate(refinementNode_16);
        refinementStructure.addRelationshipTemplate(node11_hostedOn_node13);
        refinementStructure.addRelationshipTemplate(node13_hostedOn_node15);
        refinementStructure.addRelationshipTemplate(node15_hostedOn_node16);
        // endregion

        // region mappings
        OTStayMapping pattern1_to_node11 = new OTStayMapping();
        pattern1_to_node11.setId("p1_to_n11");
        pattern1_to_node11.setDetectorElement(detector.getNodeTemplate("1"));
        pattern1_to_node11.setRefinementElement(refinementNode_11);

        OTRelationMapping pattern2_to_node13 = new OTRelationMapping();
        pattern2_to_node13.setId("p2_to_n13");
        pattern2_to_node13.setRelationType(QName.valueOf("{http://ex.org}relType_hostedOn"));
        pattern2_to_node13.setDirection(OTRelationDirection.INGOING);
        pattern2_to_node13.setDetectorElement(detector.getNodeTemplate("2"));
        pattern2_to_node13.setRefinementElement(refinementNode_13);

        OTPermutationMapping pattern2_to_node15 = new OTPermutationMapping();
        pattern2_to_node15.setId("p2_to_n15");
        pattern2_to_node15.setDetectorElement(detector.getNodeTemplate("2"));
        pattern2_to_node15.setRefinementElement(refinementNode_15);

        OTRelationMapping pattern3_to_node16 = new OTRelationMapping();
        pattern3_to_node16.setId("p3_to_n16");
        pattern3_to_node16.setRelationType(QName.valueOf("{http://ex.org}relType_connectsTo"));
        pattern3_to_node16.setDirection(OTRelationDirection.INGOING);
        pattern3_to_node16.setDetectorElement(detector.getNodeTemplate("3"));
        pattern3_to_node16.setRefinementElement(refinementNode_16);
        // endregion

        // region prm
        OTPatternRefinementModel refinementModel = new OTPatternRefinementModel();
        refinementModel.setId("PrmWithStaying");
        refinementModel.setName("PrmWithStaying");
        refinementModel.setTargetNamespace("http://ex.org");
        refinementModel.setDetector(detector);
        refinementModel.setRefinementTopology(refinementStructure);
        refinementModel.setRelationMappings(Arrays.asList(pattern2_to_node13, pattern3_to_node16));
        refinementModel.setStayMappings(Collections.singletonList(pattern1_to_node11));
        ArrayList<OTPermutationMapping> mappings = new ArrayList<>();
        mappings.add(pattern2_to_node15);
        refinementModel.setPermutationMappings(mappings);
        // endregion

        return refinementModel;
    }

    static OTPatternRefinementModel generatePrmWithoutPermutationMaps() {
        /*
        ########                 ########        ######## 
        #  (1) #---------------> # (11) # -----> # (12) # 
        ########                 ########        ######## 
            |                    (2) |______________| (2)
            | (2)                   \/    
           \/                    ########
        ########---------------> # (13) #
        #  (2) #                 ########
        ########                     | (2)
            |                       \/   
            | (2)                ########
           \/                    # (15) #
        ########---------------> ########
        #  (3) #
        ########
         */

        TTopologyTemplate detector = generateDetectorWithThreePatterns();

        // region refinement structure
        TNodeTemplate refinementNode_11 = new TNodeTemplate();
        refinementNode_11.setType("{http://ex.org}nodeType_11");
        refinementNode_11.setId("11");

        TNodeTemplate refinementNode_12 = new TNodeTemplate();
        refinementNode_12.setType("{http://ex.org}nodeType_12");
        refinementNode_12.setId("12");

        TNodeTemplate refinementNode_13 = new TNodeTemplate();
        refinementNode_13.setType("{http://ex.org}nodeType_13");
        refinementNode_13.setId("13");

        TNodeTemplate refinementNode_14 = new TNodeTemplate();
        refinementNode_14.setType("{http://ex.org}nodeType_14");
        refinementNode_14.setId("14");

        TNodeTemplate refinementNode_15 = new TNodeTemplate();
        refinementNode_15.setType("{http://ex.org}nodeType_15");
        refinementNode_15.setId("15");

        TRelationshipTemplate node11_hostedOn_node13 = ModelUtilities.createRelationshipTemplate(
            refinementNode_11, refinementNode_13, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node12_hostedOn_node13 = ModelUtilities.createRelationshipTemplate(
            refinementNode_12, refinementNode_13, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node13_hostedOn_node14 = ModelUtilities.createRelationshipTemplate(
            refinementNode_13, refinementNode_14, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node14_hostedOn_node15 = ModelUtilities.createRelationshipTemplate(
            refinementNode_14, refinementNode_15, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node11_connectsTo_node12 = ModelUtilities.createRelationshipTemplate(
            refinementNode_11, refinementNode_12, QName.valueOf("{http://ex.org}relType_connectsTo"));

        TTopologyTemplate refinementStructure = new TTopologyTemplate();
        refinementStructure.addNodeTemplate(refinementNode_11);
        refinementStructure.addNodeTemplate(refinementNode_12);
        refinementStructure.addNodeTemplate(refinementNode_13);
        refinementStructure.addNodeTemplate(refinementNode_14);
        refinementStructure.addNodeTemplate(refinementNode_15);
        refinementStructure.addRelationshipTemplate(node11_hostedOn_node13);
        refinementStructure.addRelationshipTemplate(node12_hostedOn_node13);
        refinementStructure.addRelationshipTemplate(node13_hostedOn_node14);
        refinementStructure.addRelationshipTemplate(node14_hostedOn_node15);
        refinementStructure.addRelationshipTemplate(node11_connectsTo_node12);
        // endregion

        // region mappings
        OTRelationMapping pattern1_to_node11 = new OTRelationMapping();
        pattern1_to_node11.setId("p1_to_n11");
        pattern1_to_node11.setRelationType(QName.valueOf("{http://ex.org}relType_connectsTo"));
        pattern1_to_node11.setDirection(OTRelationDirection.INGOING);
        pattern1_to_node11.setDetectorElement(detector.getNodeTemplate("1"));
        pattern1_to_node11.setRefinementElement(refinementNode_11);

        OTRelationMapping pattern2_to_node13 = new OTRelationMapping();
        pattern2_to_node13.setId("p2_to_n13");
        pattern2_to_node13.setRelationType(QName.valueOf("{http://ex.org}relType_hostedOn"));
        pattern2_to_node13.setDirection(OTRelationDirection.INGOING);
        pattern2_to_node13.setDetectorElement(detector.getNodeTemplate("2"));
        pattern2_to_node13.setRefinementElement(refinementNode_13);

        OTRelationMapping pattern3_to_node14 = new OTRelationMapping();
        pattern3_to_node14.setId("p3_to_n14");
        pattern3_to_node14.setRelationType(QName.valueOf("{http://ex.org}relType_connectsTo"));
        pattern3_to_node14.setDirection(OTRelationDirection.INGOING);
        pattern3_to_node14.setDetectorElement(detector.getNodeTemplate("3"));
        pattern3_to_node14.setRefinementElement(refinementNode_14);
        // endregion

        // region prm
        OTPatternRefinementModel refinementModel = new OTPatternRefinementModel();
        refinementModel.setId("SimplePrm");
        refinementModel.setName("SimplePrm");
        refinementModel.setTargetNamespace("http://ex.org");
        refinementModel.setDetector(detector);
        refinementModel.setRefinementTopology(refinementStructure);
        refinementModel.setRelationMappings(Arrays.asList(pattern1_to_node11, pattern2_to_node13, pattern3_to_node14));
        // endregion

        return refinementModel;
    }

    static OTPatternRefinementModel generatePrmWithComplexRelationMaps() {
        /*
        ########                 ########        ######## 
        #  (1) #---------------> # (11) # -----> # (12) # 
        ########                 ########        ######## 
            |                    (2) |______________| (2)
            | (2)                   \/    
           \/                    ########
        ########---------------> # (13) #
        #  (2) #                 ########
        ########                     | (2)
                                    \/   
                                 ########
                                 # (14) #
                                 ########
         */

        TTopologyTemplate detector = generateDetectorWithTwoPatterns();

        // region refinement structure
        TNodeTemplate refinementNode_11 = new TNodeTemplate();
        refinementNode_11.setType("{http://ex.org}nodeType_11");
        refinementNode_11.setId("11");

        TNodeTemplate refinementNode_12 = new TNodeTemplate();
        refinementNode_12.setType("{http://ex.org}nodeType_12");
        refinementNode_12.setId("12");

        TNodeTemplate refinementNode_13 = new TNodeTemplate();
        refinementNode_13.setType("{http://ex.org}nodeType_13");
        refinementNode_13.setId("13");

        TNodeTemplate refinementNode_14 = new TNodeTemplate();
        refinementNode_14.setType("{http://ex.org}nodeType_14");
        refinementNode_14.setId("14");

        TRelationshipTemplate node11_hostedOn_node13 = ModelUtilities.createRelationshipTemplate(
            refinementNode_11, refinementNode_13, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node12_hostedOn_node13 = ModelUtilities.createRelationshipTemplate(
            refinementNode_12, refinementNode_13, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node13_hostedOn_node14 = ModelUtilities.createRelationshipTemplate(
            refinementNode_13, refinementNode_14, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node11_connectsTo_node12 = ModelUtilities.createRelationshipTemplate(
            refinementNode_11, refinementNode_12, QName.valueOf("{http://ex.org}relType_connectsTo"));

        TTopologyTemplate refinementStructure = new TTopologyTemplate();
        refinementStructure.addNodeTemplate(refinementNode_11);
        refinementStructure.addNodeTemplate(refinementNode_12);
        refinementStructure.addNodeTemplate(refinementNode_13);
        refinementStructure.addNodeTemplate(refinementNode_14);
        refinementStructure.addRelationshipTemplate(node11_hostedOn_node13);
        refinementStructure.addRelationshipTemplate(node12_hostedOn_node13);
        refinementStructure.addRelationshipTemplate(node13_hostedOn_node14);
        refinementStructure.addRelationshipTemplate(node11_connectsTo_node12);
        // endregion

        // region mappings
        OTPermutationMapping pattern1_to_node11 = new OTPermutationMapping();
        pattern1_to_node11.setId("1_to_11");
        pattern1_to_node11.setRefinementElement(refinementNode_11);
        pattern1_to_node11.setDetectorElement(detector.getNodeTemplate("1"));

        OTPermutationMapping pattern2_to_node13 = new OTPermutationMapping();
        pattern2_to_node13.setId("2_to_13");
        pattern2_to_node13.setDetectorElement(detector.getNodeTemplate("2"));
        pattern2_to_node13.setRefinementElement(refinementNode_13);
        // endregion

        // region prm
        OTPatternRefinementModel refinementModel = new OTPatternRefinementModel();
        refinementModel.setId("PrmWithComplexRelationMappings");
        refinementModel.setName("PrmWithComplexRelationMappings");
        refinementModel.setTargetNamespace("http://ex.org");
        refinementModel.setDetector(detector);
        refinementModel.setRefinementTopology(refinementStructure);
        List<OTPermutationMapping> list = new ArrayList<>();
        list.add(pattern1_to_node11);
        list.add(pattern2_to_node13);
        refinementModel.setPermutationMappings(list);
        // endregion

        return refinementModel;
    }

    static OTPatternRefinementModel generatePrmWithTwoPatternsHostedOnAThird() {
       /*                   +----------------------------------+
            +---------------(------------------+               |
            |               |                 \/              \/ 
        ########         ########           ########        ######## 
        #  (1) #-------> #  (3) #           # (11) # -----> # (12) # 
        ########         ########           ########        ######## 
            | (2)           |(2)             (2) |______________| (2)
            |---------------+                   \/    
           \/                                ########
        ########---------------------------> # (13) #
        #  (2) #                             ########
        ########                                 | (2)
                                                \/   
                                             ########
                                             # (14) #
                                             ########
         */

        TTopologyTemplate detector = generateDetectorWithTwoPatternsHostedOnOne();

        // region refinement structure
        TNodeTemplate refinementNode_11 = new TNodeTemplate();
        refinementNode_11.setType("{http://ex.org}nodeType_11");
        refinementNode_11.setId("11");

        TNodeTemplate refinementNode_12 = new TNodeTemplate();
        refinementNode_12.setType("{http://ex.org}nodeType_12");
        refinementNode_12.setId("12");

        TNodeTemplate refinementNode_13 = new TNodeTemplate();
        refinementNode_13.setType("{http://ex.org}nodeType_13");
        refinementNode_13.setId("13");

        TNodeTemplate refinementNode_14 = new TNodeTemplate();
        refinementNode_14.setType("{http://ex.org}nodeType_14");
        refinementNode_14.setId("14");

        TRelationshipTemplate node11_hostedOn_node13 = ModelUtilities.createRelationshipTemplate(
            refinementNode_11, refinementNode_13, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node12_hostedOn_node13 = ModelUtilities.createRelationshipTemplate(
            refinementNode_12, refinementNode_13, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node13_hostedOn_node14 = ModelUtilities.createRelationshipTemplate(
            refinementNode_13, refinementNode_14, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node11_connectsTo_node12 = ModelUtilities.createRelationshipTemplate(
            refinementNode_11, refinementNode_12, QName.valueOf("{http://ex.org}relType_connectsTo"));

        TTopologyTemplate refinementStructure = new TTopologyTemplate();
        refinementStructure.addNodeTemplate(refinementNode_11);
        refinementStructure.addNodeTemplate(refinementNode_12);
        refinementStructure.addNodeTemplate(refinementNode_13);
        refinementStructure.addNodeTemplate(refinementNode_14);
        refinementStructure.addRelationshipTemplate(node11_hostedOn_node13);
        refinementStructure.addRelationshipTemplate(node12_hostedOn_node13);
        refinementStructure.addRelationshipTemplate(node13_hostedOn_node14);
        refinementStructure.addRelationshipTemplate(node11_connectsTo_node12);
        // endregion

        // region mappings
        OTPermutationMapping pattern1_to_node11 = new OTPermutationMapping();
        pattern1_to_node11.setId("1_to_11");
        pattern1_to_node11.setRefinementElement(refinementNode_11);
        pattern1_to_node11.setDetectorElement(detector.getNodeTemplate("1"));

        OTPermutationMapping pattern2_to_node13 = new OTPermutationMapping();
        pattern2_to_node13.setId("2_to_13");
        pattern2_to_node13.setDetectorElement(detector.getNodeTemplate("2"));
        pattern2_to_node13.setRefinementElement(refinementNode_13);

        OTPermutationMapping pattern3_to_node12 = new OTPermutationMapping();
        pattern3_to_node12.setId("3_to_12");
        pattern3_to_node12.setDetectorElement(detector.getNodeTemplate("3"));
        pattern3_to_node12.setRefinementElement(refinementNode_12);
        // endregion

        // region prm
        OTPatternRefinementModel refinementModel = new OTPatternRefinementModel();
        refinementModel.setId("PrmWithComplexRelationMappings");
        refinementModel.setName("PrmWithComplexRelationMappings");
        refinementModel.setTargetNamespace("http://ex.org");
        refinementModel.setDetector(detector);
        refinementModel.setRefinementTopology(refinementStructure);
        List<OTPermutationMapping> list = new ArrayList<>();
        list.add(pattern1_to_node11);
        list.add(pattern2_to_node13);
        list.add(pattern3_to_node12);
        refinementModel.setPermutationMappings(list);
        // endregion

        return refinementModel;
    }

    static OTPatternRefinementModel generatePrmWithComplexRelationMaps2() {
        /*
        ########                 ########        ######## 
        #  (1) #---------------> # (11) # -----> # (12) # 
        ########                 ########        ######## 
            |                    (2) |______________| (2)
            | (2)                   \/    
            |                    ########
            |                    # (13) #
            |                    ########
            |                       | (2)
           \/                      \/   
        ########---------------> ########
        #  (2) #                 # (14) #
        ########                 ########
                                     | (2)
                                    \/   
                                 ########
                                 # (15) #
                                 ########
         */
        TTopologyTemplate detector = generateDetectorWithTwoPatterns();

        // region refinement structure
        TNodeTemplate refinementNode_11 = new TNodeTemplate();
        refinementNode_11.setType("{http://ex.org}nodeType_11");
        refinementNode_11.setId("11");

        TNodeTemplate refinementNode_12 = new TNodeTemplate();
        refinementNode_12.setType("{http://ex.org}nodeType_12");
        refinementNode_12.setId("12");

        TNodeTemplate refinementNode_13 = new TNodeTemplate();
        refinementNode_13.setType("{http://ex.org}nodeType_13");
        refinementNode_13.setId("13");

        TNodeTemplate refinementNode_14 = new TNodeTemplate();
        refinementNode_14.setType("{http://ex.org}nodeType_14");
        refinementNode_14.setId("14");

        TNodeTemplate refinementNode_15 = new TNodeTemplate();
        refinementNode_15.setType("{http://ex.org}nodeType_15");
        refinementNode_15.setId("15");

        TRelationshipTemplate node11_hostedOn_node13 = ModelUtilities.createRelationshipTemplate(
            refinementNode_11, refinementNode_13, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node12_hostedOn_node13 = ModelUtilities.createRelationshipTemplate(
            refinementNode_12, refinementNode_13, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node13_hostedOn_node14 = ModelUtilities.createRelationshipTemplate(
            refinementNode_13, refinementNode_14, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node14_hostedOn_node15 = ModelUtilities.createRelationshipTemplate(
            refinementNode_14, refinementNode_15, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node11_connectsTo_node12 = ModelUtilities.createRelationshipTemplate(
            refinementNode_11, refinementNode_12, QName.valueOf("{http://ex.org}relType_connectsTo"));

        TTopologyTemplate refinementStructure = new TTopologyTemplate();
        refinementStructure.addNodeTemplate(refinementNode_11);
        refinementStructure.addNodeTemplate(refinementNode_12);
        refinementStructure.addNodeTemplate(refinementNode_13);
        refinementStructure.addNodeTemplate(refinementNode_14);
        refinementStructure.addNodeTemplate(refinementNode_15);
        refinementStructure.addRelationshipTemplate(node11_hostedOn_node13);
        refinementStructure.addRelationshipTemplate(node12_hostedOn_node13);
        refinementStructure.addRelationshipTemplate(node13_hostedOn_node14);
        refinementStructure.addRelationshipTemplate(node14_hostedOn_node15);
        refinementStructure.addRelationshipTemplate(node11_connectsTo_node12);
        // endregion

        //region mappings
        OTPermutationMapping pattern1_to_node11 = new OTPermutationMapping();
        pattern1_to_node11.setId("1_to_11");
        pattern1_to_node11.setRefinementElement(refinementNode_11);
        pattern1_to_node11.setDetectorElement(detector.getNodeTemplate("1"));

        OTPermutationMapping pattern2_to_node14 = new OTPermutationMapping();
        pattern2_to_node14.setId("2_to_14");
        pattern2_to_node14.setDetectorElement(detector.getNodeTemplate("2"));
        pattern2_to_node14.setRefinementElement(refinementNode_14);
        // endregion

        // region prm
        OTPatternRefinementModel refinementModel = new OTPatternRefinementModel();
        refinementModel.setId("PrmWithComplexRelationMappings2");
        refinementModel.setName("PrmWithComplexRelationMappings2");
        refinementModel.setTargetNamespace("http://ex.org");
        refinementModel.setDetector(detector);
        refinementModel.setRefinementTopology(refinementStructure);
        List<OTPermutationMapping> list = new ArrayList<>();
        list.add(pattern1_to_node11);
        list.add(pattern2_to_node14);
        refinementModel.setPermutationMappings(list);
        // endregion

        return refinementModel;
    }

    static OTPatternRefinementModel generateComplexPrmWithPatternSet() {
        /*                +------------------------+   
                          |                        \| 
        ########----------+      ########        ######## 
        #  (1) #---------------> # (11) # -----> # (12) # 
        ########                 ########        ######## 
            |                        | (2)           | (2)
            | (2)                   \/              \/    
           \/                    ########        ######## 
        ########---------------> # (13) #-------># (14) # 
        #  (2) #                 ########        ######## 
        ########--------+            | (2)           |
            |           |           \/               |      
            | (2)       +------> ########            |
           \/                    # (15) #        (2) |       
        ########---------------> ########            |
        #  (3) #                     | (2)           |
        ########                    \/               |
                                 ########            |
                                 # (16) #<-----------+ 
                                 ######## 
         */

        TTopologyTemplate detector = generateDetectorWithThreePatterns();

        // region refinement structure
        TNodeTemplate refinementNode_11 = new TNodeTemplate();
        refinementNode_11.setType("{http://ex.org}nodeType_11");
        refinementNode_11.setId("11");

        TNodeTemplate refinementNode_12 = new TNodeTemplate();
        refinementNode_12.setType("{http://ex.org}nodeType_12");
        refinementNode_12.setId("12");

        TNodeTemplate refinementNode_13 = new TNodeTemplate();
        refinementNode_13.setType("{http://ex.org}nodeType_13");
        refinementNode_13.setId("13");

        TNodeTemplate refinementNode_14 = new TNodeTemplate();
        refinementNode_14.setType("{http://ex.org}nodeType_14");
        refinementNode_14.setId("14");

        TNodeTemplate refinementNode_15 = new TNodeTemplate();
        refinementNode_15.setType("{http://ex.org}nodeType_15");
        refinementNode_15.setId("15");

        TNodeTemplate refinementNode_16 = new TNodeTemplate();
        refinementNode_16.setType("{http://ex.org}nodeType_16");
        refinementNode_16.setId("16");

        TRelationshipTemplate node11_connectsTo_node12 = ModelUtilities.createRelationshipTemplate(
            refinementNode_11, refinementNode_12, QName.valueOf("{http://ex.org}relType_connectsTo"));
        TRelationshipTemplate node11_hostedOn_node13 = ModelUtilities.createRelationshipTemplate(
            refinementNode_11, refinementNode_13, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node12_hostedOn_node14 = ModelUtilities.createRelationshipTemplate(
            refinementNode_12, refinementNode_14, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node13_connectsTo_node14 = ModelUtilities.createRelationshipTemplate(
            refinementNode_13, refinementNode_14, QName.valueOf("{http://ex.org}relType_connectsTo"));
        TRelationshipTemplate node13_hostedOn_node15 = ModelUtilities.createRelationshipTemplate(
            refinementNode_13, refinementNode_15, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node14_hostedOn_node16 = ModelUtilities.createRelationshipTemplate(
            refinementNode_14, refinementNode_16, QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate node15_hostedOn_node16 = ModelUtilities.createRelationshipTemplate(
            refinementNode_15, refinementNode_16, QName.valueOf("{http://ex.org}relType_hostedOn"));

        TTopologyTemplate refinementStructure = new TTopologyTemplate();
        refinementStructure.addNodeTemplate(refinementNode_11);
        refinementStructure.addNodeTemplate(refinementNode_12);
        refinementStructure.addNodeTemplate(refinementNode_13);
        refinementStructure.addNodeTemplate(refinementNode_14);
        refinementStructure.addNodeTemplate(refinementNode_15);
        refinementStructure.addNodeTemplate(refinementNode_16);
        refinementStructure.addRelationshipTemplate(node11_connectsTo_node12);
        refinementStructure.addRelationshipTemplate(node11_hostedOn_node13);
        refinementStructure.addRelationshipTemplate(node12_hostedOn_node14);
        refinementStructure.addRelationshipTemplate(node13_connectsTo_node14);
        refinementStructure.addRelationshipTemplate(node13_hostedOn_node15);
        refinementStructure.addRelationshipTemplate(node14_hostedOn_node16);
        refinementStructure.addRelationshipTemplate(node15_hostedOn_node16);
        // endregion

        // region mappings
        OTRelationMapping pattern1_to_node11 = new OTRelationMapping();
        pattern1_to_node11.setId("p1_to_n11");
        pattern1_to_node11.setRelationType(QName.valueOf("{http://ex.org}relType_connectsTo"));
        pattern1_to_node11.setDirection(OTRelationDirection.INGOING);
        pattern1_to_node11.setDetectorElement(detector.getNodeTemplate("1"));
        pattern1_to_node11.setRefinementElement(refinementNode_11);

        OTDeploymentArtifactMapping pattern1_to_node12 = new OTDeploymentArtifactMapping();
        pattern1_to_node11.setId("p1_to_n12");
        pattern1_to_node12.setDetectorElement(detector.getNodeTemplate("1"));
        pattern1_to_node12.setRefinementElement(refinementNode_12);
        pattern1_to_node12.setArtifactType(QName.valueOf("{http://ex.org}artType_war"));

        OTAttributeMapping pattern2_to_node13 = new OTAttributeMapping();
        pattern2_to_node13.setId("p2_to_n13");
        pattern2_to_node13.setType(OTAttributeMappingType.ALL);
        pattern2_to_node13.setDetectorElement(detector.getNodeTemplate("2"));
        pattern2_to_node13.setRefinementElement(refinementNode_13);

        OTRelationMapping pattern2_to_node15 = new OTRelationMapping();
        pattern2_to_node15.setId("p2_to_n15");
        pattern2_to_node15.setRelationType(QName.valueOf("{http://ex.org}relType_connectsTo"));
        pattern2_to_node15.setDirection(OTRelationDirection.INGOING);
        pattern2_to_node15.setDetectorElement(detector.getNodeTemplate("2"));
        pattern2_to_node15.setRefinementElement(refinementNode_15);

        OTRelationMapping pattern3_to_node15 = new OTRelationMapping();
        pattern3_to_node15.setId("p3_to_n15");
        pattern3_to_node15.setRelationType(QName.valueOf("{http://ex.org}relType_connectsTo"));
        pattern3_to_node15.setDirection(OTRelationDirection.INGOING);
        pattern3_to_node15.setDetectorElement(detector.getNodeTemplate("3"));
        pattern3_to_node15.setRefinementElement(refinementNode_15);
        // endregion

        // region prm
        OTPatternRefinementModel refinementModel = new OTPatternRefinementModel();
        refinementModel.setId("ComplexPrmWithPatternSet");
        refinementModel.setName("ComplexPrmWithPatternSet");
        refinementModel.setTargetNamespace("http://ex.org");
        refinementModel.setDetector(detector);
        refinementModel.setRefinementTopology(refinementStructure);
        refinementModel.setAttributeMappings(Collections.singletonList(pattern2_to_node13));
        ArrayList<OTRelationMapping> relationMappings = new ArrayList<>();
        relationMappings.add(pattern1_to_node11);
        relationMappings.add(pattern2_to_node15);
        relationMappings.add(pattern3_to_node15);
        refinementModel.setRelationMappings(relationMappings);
        refinementModel.setDeploymentArtifactMappings(Collections.singletonList(pattern1_to_node12));
        // endregion

        return refinementModel;
    }

    private static TTopologyTemplate generateDetectorWithTwoPatterns() {
        TNodeTemplate pattern_1 = new TNodeTemplate();
        pattern_1.setType("{http://ex.org/patterns}pattern_1");
        pattern_1.setId("1");

        TNodeTemplate pattern_2 = new TNodeTemplate();
        pattern_2.setType("{http://ex.org/patterns}pattern_2");
        pattern_2.setId("2");

        TRelationshipTemplate pattern1_hostedOn_pattern2 = ModelUtilities.createRelationshipTemplate(
            pattern_1, pattern_2, QName.valueOf("{http://ex.org}relType_hostedOn"));

        TTopologyTemplate detector = new TTopologyTemplate();
        detector.addNodeTemplate(pattern_1);
        detector.addNodeTemplate(pattern_2);
        detector.addRelationshipTemplate(pattern1_hostedOn_pattern2);

        return detector;
    }

    private static TTopologyTemplate generateDetectorWithThreePatterns() {
        TTopologyTemplate detector = generateDetectorWithTwoPatterns();
        TNodeTemplate pattern_3 = new TNodeTemplate();
        pattern_3.setType("{http://ex.org/patterns}pattern_3");
        pattern_3.setId("3");

        TRelationshipTemplate pattern2_hostedOn_pattern3 = ModelUtilities.createRelationshipTemplate(
            detector.getNodeTemplate("2"), pattern_3, QName.valueOf("{http://ex.org}relType_hostedOn"));

        detector.addNodeTemplate(pattern_3);
        detector.addRelationshipTemplate(pattern2_hostedOn_pattern3);

        return detector;
    }

    private static TTopologyTemplate generateDetectorWithTwoPatternsHostedOnOne() {
        TTopologyTemplate detector = generateDetectorWithTwoPatterns();
        TNodeTemplate pattern_3 = new TNodeTemplate();
        pattern_3.setType("{http://ex.org/patterns}pattern_3");
        pattern_3.setId("3");

        TRelationshipTemplate pattern3_hostedOn_pattern2 = ModelUtilities.createRelationshipTemplate(
            pattern_3, detector.getNodeTemplate("2"), QName.valueOf("{http://ex.org}relType_hostedOn"));
        TRelationshipTemplate pattern1_connectsTo_pattern3 = ModelUtilities.createRelationshipTemplate(
            detector.getNodeTemplate("1"), pattern_3, QName.valueOf("{http://ex.org}relType_connectsTo"));

        detector.addNodeTemplate(pattern_3);
        detector.addRelationshipTemplate(pattern3_hostedOn_pattern2);
        detector.addRelationshipTemplate(pattern1_connectsTo_pattern3);

        return detector;
    }

    static void addAllPermutationMappings(OTPatternRefinementModel refinementModel) {
        addSomePermutationMappings(refinementModel);

        OTPermutationMapping relation1to2_to_relation12to14 = new OTPermutationMapping();
        relation1to2_to_relation12to14.setDetectorElement(refinementModel.getDetector().getRelationshipTemplate("1--2"));
        relation1to2_to_relation12to14.setRefinementElement(refinementModel.getRefinementStructure().getNodeTemplate("14"));
        relation1to2_to_relation12to14.setId("p1-p2_to_n14");
        refinementModel.getPermutationMappings().add(relation1to2_to_relation12to14);
    }

    static void addSomePermutationMappings(OTPatternRefinementModel refinementModel) {
        OTPermutationMapping pattern2_to_node14 = new OTPermutationMapping();
        pattern2_to_node14.setDetectorElement(refinementModel.getDetector().getNodeTemplate("2"));
        pattern2_to_node14.setRefinementElement(refinementModel.getRefinementStructure().getNodeTemplate("14"));
        pattern2_to_node14.setId("p2_to_n14");

        OTPermutationMapping pattern3_to_node15 = new OTPermutationMapping();
        pattern3_to_node15.setDetectorElement(refinementModel.getDetector().getNodeTemplate("3"));
        pattern3_to_node15.setRefinementElement(refinementModel.getRefinementStructure().getNodeTemplate("15"));
        pattern3_to_node15.setId("p3_to_n15");

        OTPermutationMapping pattern3_to_node16 = new OTPermutationMapping();
        pattern3_to_node16.setDetectorElement(refinementModel.getDetector().getNodeTemplate("3"));
        pattern3_to_node16.setRefinementElement(refinementModel.getRefinementStructure().getNodeTemplate("16"));
        pattern3_to_node16.setId("p3_to_n16");

        ArrayList<OTPermutationMapping> permutationMaps = new ArrayList<>(Arrays.asList(pattern2_to_node14, pattern3_to_node15, pattern3_to_node16));
        refinementModel.setPermutationMappings(permutationMaps);
    }
}
