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

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTAttributeMapping;
import org.eclipse.winery.model.tosca.extensions.OTAttributeMappingType;
import org.eclipse.winery.model.tosca.extensions.OTDeploymentArtifactMapping;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTPermutationMapping;
import org.eclipse.winery.model.tosca.extensions.OTRelationDirection;
import org.eclipse.winery.model.tosca.extensions.OTRelationMapping;
import org.eclipse.winery.model.tosca.extensions.OTStayMapping;
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

        TTopologyTemplate refinementStructure = new TTopologyTemplate.Builder()
            .addNodeTemplates(refinementNode_11)
            .addNodeTemplates(refinementNode_13)
            .addNodeTemplates(refinementNode_15)
            .addNodeTemplates(refinementNode_16)
            .addRelationshipTemplate(node11_hostedOn_node13)
            .addRelationshipTemplate(node13_hostedOn_node15)
            .addRelationshipTemplate(node15_hostedOn_node16)
            .build();
        // endregion

        // region mappings
        OTStayMapping pattern1_to_node11 = new OTStayMapping.Builder("sm-1--11")
            .setDetectorElement(detector.getNodeTemplate("1"))
            .setRefinementElement(refinementNode_11)
            .build();

        OTRelationMapping pattern2_to_node13 = new OTRelationMapping.Builder("pm-2--13")
            .setRelationType("{http://ex.org}relType_hostedOn")
            .setDirection(OTRelationDirection.INGOING)
            .setDetectorElement(detector.getNodeTemplate("2"))
            .setRefinementElement(refinementNode_13)
            .build();

        OTPermutationMapping pattern2_to_node15 = new OTPermutationMapping.Builder("pm-2--15")
            .setDetectorElement(detector.getNodeTemplate("2"))
            .setRefinementElement(refinementNode_15)
            .build();

        OTRelationMapping pattern3_to_node16 = new OTRelationMapping.Builder("pm-3--16")
            .setRelationType(QName.valueOf("{http://ex.org}relType_connectsTo"))
            .setDirection(OTRelationDirection.INGOING)
            .setDetectorElement(detector.getNodeTemplate("3"))
            .setRefinementElement(refinementNode_16)
            .build();
        // endregion

        // region prm
        ArrayList<OTPermutationMapping> mappings = new ArrayList<>();
        mappings.add(pattern2_to_node15);
        // endregion

        return new OTPatternRefinementModel.Builder()
            .setName("PrmWithStaying")
            .setTargetNamespace("http://ex.org")
            .setDetector(detector)
            .setRefinementStructure(refinementStructure)
            .setRelationMappings(Arrays.asList(pattern2_to_node13, pattern3_to_node16))
            .setStayMappings(Collections.singletonList(pattern1_to_node11))
            .setPermutationMappings(mappings)
            .build();
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

        TTopologyTemplate refinementStructure = new TTopologyTemplate.Builder()
            .addNodeTemplates(refinementNode_11)
            .addNodeTemplates(refinementNode_12)
            .addNodeTemplates(refinementNode_13)
            .addNodeTemplates(refinementNode_14)
            .addNodeTemplates(refinementNode_15)
            .addRelationshipTemplate(node11_hostedOn_node13)
            .addRelationshipTemplate(node12_hostedOn_node13)
            .addRelationshipTemplate(node13_hostedOn_node14)
            .addRelationshipTemplate(node14_hostedOn_node15)
            .addRelationshipTemplate(node11_connectsTo_node12)
            .build();
        // endregion

        // region mappings
        OTRelationMapping pattern1_to_node11 = new OTRelationMapping.Builder("rm-1--11")
            .setRelationType(QName.valueOf("{http://ex.org}relType_connectsTo"))
            .setDirection(OTRelationDirection.INGOING)
            .setDetectorElement(detector.getNodeTemplate("1"))
            .setRefinementElement(refinementNode_11)
            .build();

        OTRelationMapping pattern2_to_node13 = new OTRelationMapping.Builder("rm-2--13")
            .setRelationType(QName.valueOf("{http://ex.org}relType_hostedOn"))
            .setDirection(OTRelationDirection.INGOING)
            .setDetectorElement(detector.getNodeTemplate("2"))
            .setRefinementElement(refinementNode_13)
            .build();

        OTRelationMapping pattern3_to_node14 = new OTRelationMapping.Builder(
            "rm-3-14")
            .setRelationType(QName.valueOf("{http://ex.org}relType_connectsTo"))
            .setDirection(OTRelationDirection.INGOING)
            .setDetectorElement(detector.getNodeTemplate("3"))
            .setRefinementElement(refinementNode_14)
            .build();
        // endregion

        return new OTPatternRefinementModel.Builder()
            .setName("SimplePrm")
            .setTargetNamespace("http://ex.org")
            .setDetector(detector)
            .setRefinementStructure(refinementStructure)
            .setRelationMappings(Arrays.asList(pattern1_to_node11, pattern2_to_node13, pattern3_to_node14))
            .build();
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

        TTopologyTemplate refinementStructure = new TTopologyTemplate.Builder()
            .addNodeTemplates(refinementNode_11)
            .addNodeTemplates(refinementNode_12)
            .addNodeTemplates(refinementNode_13)
            .addNodeTemplates(refinementNode_14)
            .addRelationshipTemplate(node11_hostedOn_node13)
            .addRelationshipTemplate(node12_hostedOn_node13)
            .addRelationshipTemplate(node13_hostedOn_node14)
            .addRelationshipTemplate(node11_connectsTo_node12)
            .build();
        // endregion

        // region mappings
        OTPermutationMapping pattern1_to_node11 = new OTPermutationMapping.Builder("pm-1--11")
            .setRefinementElement(refinementNode_11)
            .setDetectorElement(detector.getNodeTemplate("1"))
            .build();

        OTPermutationMapping pattern2_to_node13 = new OTPermutationMapping.Builder("pm-2--13")
            .setDetectorElement(detector.getNodeTemplate("2"))
            .setRefinementElement(refinementNode_13)
            .build();
        // endregion

        // region prm
        List<OTPermutationMapping> list = new ArrayList<>();
        list.add(pattern1_to_node11);
        list.add(pattern2_to_node13);
        // endregion

        return new OTPatternRefinementModel.Builder()
            .setName("PrmWithComplexRelationMappings")
            .setTargetNamespace("http://ex.org")
            .setDetector(detector)
            .setRefinementStructure(refinementStructure)
            .setPermutationMappings(list)
            .build();
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

        TTopologyTemplate refinementStructure = new TTopologyTemplate.Builder()
            .addNodeTemplates(refinementNode_11)
            .addNodeTemplates(refinementNode_12)
            .addNodeTemplates(refinementNode_13)
            .addNodeTemplates(refinementNode_14)
            .addRelationshipTemplate(node11_hostedOn_node13)
            .addRelationshipTemplate(node12_hostedOn_node13)
            .addRelationshipTemplate(node13_hostedOn_node14)
            .addRelationshipTemplate(node11_connectsTo_node12)
            .build();
        // endregion

        // region mappings
        OTPermutationMapping pattern1_to_node11 = new OTPermutationMapping.Builder("pm-1--11")
            .setRefinementElement(refinementNode_11)
            .setDetectorElement(detector.getNodeTemplate("1"))
            .build();

        OTPermutationMapping pattern2_to_node13 = new OTPermutationMapping.Builder("pm-2--13")
            .setDetectorElement(detector.getNodeTemplate("2"))
            .setRefinementElement(refinementNode_13)
            .build();

        OTPermutationMapping pattern3_to_node12 = new OTPermutationMapping.Builder("pm-3--12")
            .setDetectorElement(detector.getNodeTemplate("3"))
            .setRefinementElement(refinementNode_12)
            .build();
        // endregion

        // region prm
        List<OTPermutationMapping> list = new ArrayList<>();
        list.add(pattern1_to_node11);
        list.add(pattern2_to_node13);
        list.add(pattern3_to_node12);
        // endregion

        return new OTPatternRefinementModel.Builder()
            .setName("PrmWithComplexRelationMappings")
            .setTargetNamespace("http://ex.org")
            .setDetector(detector)
            .setRefinementStructure(refinementStructure)
            .setPermutationMappings(list)
            .build();
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

        TTopologyTemplate refinementStructure = new TTopologyTemplate.Builder()
            .addNodeTemplates(refinementNode_11)
            .addNodeTemplates(refinementNode_12)
            .addNodeTemplates(refinementNode_13)
            .addNodeTemplates(refinementNode_14)
            .addNodeTemplates(refinementNode_15)
            .addRelationshipTemplate(node11_hostedOn_node13)
            .addRelationshipTemplate(node12_hostedOn_node13)
            .addRelationshipTemplate(node13_hostedOn_node14)
            .addRelationshipTemplate(node14_hostedOn_node15)
            .addRelationshipTemplate(node11_connectsTo_node12)
            .build();
        // endregion

        //region mappings
        OTPermutationMapping pattern1_to_node11 = new OTPermutationMapping.Builder("pm-1--11")
            .setRefinementElement(refinementNode_11)
            .setDetectorElement(detector.getNodeTemplate("1"))
            .build();

        OTPermutationMapping pattern2_to_node14 = new OTPermutationMapping.Builder("pm-2--14")
            .setDetectorElement(detector.getNodeTemplate("2"))
            .setRefinementElement(refinementNode_14)
            .build();
        // endregion

        // region permutation mapping
        List<OTPermutationMapping> list = new ArrayList<>();
        list.add(pattern1_to_node11);
        list.add(pattern2_to_node14);
        // endregion

        return new OTPatternRefinementModel.Builder()
            .setName("PrmWithComplexRelationMappings2")
            .setTargetNamespace("http://ex.org")
            .setDetector(detector)
            .setRefinementStructure(refinementStructure)
            .setPermutationMappings(list)
            .build();
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

        TTopologyTemplate refinementStructure = new TTopologyTemplate.Builder()
            .addNodeTemplates(refinementNode_11)
            .addNodeTemplates(refinementNode_12)
            .addNodeTemplates(refinementNode_13)
            .addNodeTemplates(refinementNode_14)
            .addNodeTemplates(refinementNode_15)
            .addNodeTemplates(refinementNode_16)
            .addRelationshipTemplate(node11_connectsTo_node12)
            .addRelationshipTemplate(node11_hostedOn_node13)
            .addRelationshipTemplate(node12_hostedOn_node14)
            .addRelationshipTemplate(node13_connectsTo_node14)
            .addRelationshipTemplate(node13_hostedOn_node15)
            .addRelationshipTemplate(node14_hostedOn_node16)
            .addRelationshipTemplate(node15_hostedOn_node16)
            .build();
        // endregion

        // region mappings
        OTRelationMapping pattern1_to_node11 = new OTRelationMapping.Builder("rm-1--11")
            .setRelationType("{http://ex.org}relType_connectsTo")
            .setDirection(OTRelationDirection.INGOING)
            .setDetectorElement(detector.getNodeTemplate("1"))
            .setRefinementElement(refinementNode_11)
            .build();

        OTDeploymentArtifactMapping pattern1_to_node12 = new OTDeploymentArtifactMapping.Builder("dam-1--12")
            .setDetectorElement(detector.getNodeTemplate("1"))
            .setRefinementElement(refinementNode_12)
            .setArtifactType(QName.valueOf("{http://ex.org}artType_war"))
            .build();

        OTAttributeMapping pattern2_to_node13 = new OTAttributeMapping.Builder("am-2--13")
            .setType(OTAttributeMappingType.ALL)
            .setDetectorElement(detector.getNodeTemplate("2"))
            .setRefinementElement(refinementNode_13)
            .build();

        OTRelationMapping pattern2_to_node15 = new OTRelationMapping.Builder("rm-2--15")
            .setRelationType("{http://ex.org}relType_connectsTo")
            .setDirection(OTRelationDirection.INGOING)
            .setDetectorElement(detector.getNodeTemplate("2"))
            .setRefinementElement(refinementNode_15)
            .build();

        OTRelationMapping pattern3_to_node15 = new OTRelationMapping.Builder("rm-3--15")
            .setRelationType(QName.valueOf("{http://ex.org}relType_connectsTo"))
            .setDirection(OTRelationDirection.INGOING)
            .setDetectorElement(detector.getNodeTemplate("3"))
            .setRefinementElement(refinementNode_15)
            .build();
        // endregion

        // region relation mappings
        ArrayList<OTRelationMapping> relationMappings = new ArrayList<>();
        relationMappings.add(pattern1_to_node11);
        relationMappings.add(pattern2_to_node15);
        relationMappings.add(pattern3_to_node15);
        // endregion

        return new OTPatternRefinementModel.Builder()
            .setName("ComplexPrmWithPatternSet")
            .setTargetNamespace("http://ex.org")
            .setDetector(detector)
            .setRefinementStructure(refinementStructure)
            .setAttributeMappings(Collections.singletonList(pattern2_to_node13))
            .setRelationMappings(relationMappings)
            .setDeploymentArtifactMappings(Collections.singletonList(pattern1_to_node12))
            .build();
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

        TTopologyTemplate detector = new TTopologyTemplate.Builder()
            .addNodeTemplates(pattern_1)
            .addNodeTemplates(pattern_2)
            .addRelationshipTemplate(pattern1_hostedOn_pattern2)
            .build();

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

        OTPermutationMapping relation1to2_to_relation12to14 = new OTPermutationMapping.Builder("pm-1-2--14")
            .setDetectorElement(refinementModel.getDetector().getRelationshipTemplate("1--2"))
            .setRefinementElement(refinementModel.getRefinementStructure().getNodeTemplate("14"))
            .build();
        refinementModel.getPermutationMappings().add(relation1to2_to_relation12to14);
    }

    static void addSomePermutationMappings(OTPatternRefinementModel refinementModel) {
        OTPermutationMapping pattern2_to_node14 = new OTPermutationMapping.Builder("pm-2--14")
            .setDetectorElement(refinementModel.getDetector().getNodeTemplate("2"))
            .setRefinementElement(refinementModel.getRefinementStructure().getNodeTemplate("14"))
            .build();

        OTPermutationMapping pattern3_to_node15 = new OTPermutationMapping.Builder("pm-3--15")
            .setDetectorElement(refinementModel.getDetector().getNodeTemplate("3"))
            .setRefinementElement(refinementModel.getRefinementStructure().getNodeTemplate("15"))
            .build();

        OTPermutationMapping pattern3_to_node16 = new OTPermutationMapping.Builder("pm-3--16")
            .setDetectorElement(refinementModel.getDetector().getNodeTemplate("3"))
            .setRefinementElement(refinementModel.getRefinementStructure().getNodeTemplate("16"))
            .build();

        ArrayList<OTPermutationMapping> permutationMaps = new ArrayList<>(Arrays.asList(pattern2_to_node14, pattern3_to_node15, pattern3_to_node16));
        refinementModel.setPermutationMappings(permutationMaps);
    }
}
