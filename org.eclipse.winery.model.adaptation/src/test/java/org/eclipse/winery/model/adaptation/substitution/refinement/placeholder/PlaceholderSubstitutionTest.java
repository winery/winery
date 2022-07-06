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

package org.eclipse.winery.model.adaptation.substitution.refinement.placeholder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.jupiter.api.BeforeAll;

class PlaceholderSubstitutionTest extends TestWithGitBackedRepository {

    protected static TServiceTemplate originServiceTemplate;
    protected static TServiceTemplate candidate1;
    protected static TServiceTemplate candidate2;
    protected static TTopologyTemplate originTopology;
    protected static TTopologyTemplate candidate1Topology;
    protected static TTopologyTemplate candidate2Topology;
    protected static TTopologyTemplate detector;

    @BeforeAll
    protected static void setUp() {

        //RelationshipTypes

        // region *** origin topology ***
        TNodeTemplate nt1 = new TNodeTemplate.Builder("1", QName.valueOf("{http://ex.org}nodeType_1"))
            .setX("10")
            .setY("10")
            .build();

        TNodeTemplate nt2 = new TNodeTemplate.Builder("2", QName.valueOf("{http://ex.org}nodeType_2"))
            .setX("110")
            .setY("10")
            .build();

        TNodeTemplate nt3 = new TNodeTemplate.Builder("3", QName.valueOf("{http://ex.org}nodeType_3"))
            .setX("210")
            .setY("10")
            .build();

        //placeholder Components
        TNodeTemplate nt4 = new TNodeTemplate.Builder("4", QName.valueOf("{http://opentosca/multiparticipant/placeholdertypes}placeholdernt2"))
            .setX("110")
            .setY("110")
            .build();

        TNodeTemplate nt5 = new TNodeTemplate.Builder("5", QName.valueOf("{http://opentosca/multiparticipant/placeholdertypes}placeholder1"))
            .setX("110")
            .setY("110")
            .build();

        //Properties for NT1
        LinkedHashMap<String, String> requiredPropertiesnt1 = new LinkedHashMap<>();
        requiredPropertiesnt1.put("Property", "");
        ModelUtilities.setPropertiesKV(nt1, requiredPropertiesnt1);

        //Properties for placeholder nt4
        LinkedHashMap<String, String> requiredPropertiesnt4 = new LinkedHashMap<>();
        requiredPropertiesnt4.put("oneProp", "");
        requiredPropertiesnt4.put("twoProp", "");
        ModelUtilities.setPropertiesKV(nt4, requiredPropertiesnt4);

        //Properties for placeholder nt5
        LinkedHashMap<String, String> requiredPropertiesnt5 = new LinkedHashMap<>();
        requiredPropertiesnt5.put("threeProp", "");
        requiredPropertiesnt5.put("fourProp", "");
        ModelUtilities.setPropertiesKV(nt5, requiredPropertiesnt5);

        //Capabilities fpr NT4 and NT5
        List<TCapability> capNT4 = new ArrayList<>();
        capNT4.add(new TCapability.Builder("CapNT4", new QName("https://capabilities", "Type1"), "CapNT4").build());
        List<TCapability> capNT5 = new ArrayList<>();
        capNT5.add(new TCapability.Builder("CapNT5", new QName("https://capabilities", "Type2"), "CapNT5").build());
        nt4.setCapabilities(capNT4);
        nt5.setCapabilities(capNT5);

        TRelationshipTemplate rt21 = new TRelationshipTemplate.Builder(
            "21",
            QName.valueOf("{http://ex.org}relType_1"),
            nt2,
            nt1
        ).build();

        TRelationshipTemplate rt32 = new TRelationshipTemplate.Builder(
            "32",
            QName.valueOf("{http://ex.org}relType_1"),
            nt3,
            nt2
        ).build();

        TRelationshipTemplate rt24 = new TRelationshipTemplate.Builder(
            "24",
            QName.valueOf("{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}HostedOn"),
            nt2,
            nt4
        ).build();

        TRelationshipTemplate rt15 = new TRelationshipTemplate.Builder(
            "15",
            QName.valueOf("{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}HostedOn"),
            nt1,
            nt5
        ).build();
        
        /*
        #######   (1)  #######   (1)  #######
        # (1) # <----- # (2) # <----- # (3) #
        #######        #######        #######
           | (15)         | (24)
          \/             \/
        #######        #######
        # (5) #        # (4) #
        #######        #######
         */
        originTopology = new TTopologyTemplate.Builder()
            .addNodeTemplate(nt1)
            .addNodeTemplate(nt2)
            .addNodeTemplate(nt3)
            .addNodeTemplate(nt4)
            .addNodeTemplate(nt5)
            .addRelationshipTemplate(rt21)
            .addRelationshipTemplate(rt32)
            .addRelationshipTemplate(rt24)
            .addRelationshipTemplate(rt15)
            .build();

        originServiceTemplate.setTopologyTemplate(originTopology);
        originServiceTemplate.setId("originServiceTemplate");
        // endregion

        // region *** candidate1Topology ***
        /*
        #######   (1)  #######  
        # (1X)# <----- # (2X)#
        #######        #######       
           | (15X)        | (24X)
          \/             \/
        #######        #######
        # (5X)#        # (4X)#
        #######        #######
           | (56X)         | (46X)        
           ---->#######<----
                # (6X)#
                #######
         
         */

        // region *** origin topology ***
        TNodeTemplate nt1X = new TNodeTemplate.Builder("1X", QName.valueOf("{http://ex.org}nodeType_1"))
            .setX("10")
            .setY("10")
            .build();

        TNodeTemplate nt2X = new TNodeTemplate.Builder("2X", QName.valueOf("{http://ex.org}nodeType_2"))
            .setX("110")
            .setY("10")
            .build();

        TNodeTemplate nt4X = new TNodeTemplate.Builder("4X", QName.valueOf("{http://ex.org}nodeType_4"))
            .setX("110")
            .setY("110")
            .build();

        TNodeTemplate nt5X = new TNodeTemplate.Builder("5X", QName.valueOf("{http://ex.org}nodeType_5"))
            .setX("110")
            .setY("110")
            .build();

        TNodeTemplate nt6X = new TNodeTemplate.Builder("6X", QName.valueOf("{http://ex.org}nodeType_6"))
            .setX("110")
            .setY("110")
            .build();

        //Properties for NT1X
        LinkedHashMap<String, String> requiredPropertiesnt1X = new LinkedHashMap<>();
        requiredPropertiesnt1.put("Property", "");
        ModelUtilities.setPropertiesKV(nt1X, requiredPropertiesnt1);

        //Properties for nt4X
        LinkedHashMap<String, String> propertiesnt4X = new LinkedHashMap<>();
        propertiesnt4X.put("oneProp", "");
        ModelUtilities.setPropertiesKV(nt4X, propertiesnt4X);

        //Properties for nt5X
        LinkedHashMap<String, String> propertiesnt5X = new LinkedHashMap<>();
        propertiesnt5X.put("threeProp", "");
        ModelUtilities.setPropertiesKV(nt5X, propertiesnt5X);

        //Properties for nt6X
        LinkedHashMap<String, String> propertiesnt6X = new LinkedHashMap<>();
        propertiesnt6X.put("twoProp", "test3");
        propertiesnt6X.put("fourProp", "test4");
        ModelUtilities.setPropertiesKV(nt6X, propertiesnt6X);

        //Capabilities for NT4X and NT5X
        List<TCapability> capNT4X = new ArrayList<>();
        capNT4X.add(new TCapability.Builder("CapNT4", new QName("https://capabilities", "Type1"), "CapNT4").build());
        List<TCapability> capNT5X = new ArrayList<>();
        capNT5X.add(new TCapability.Builder("CapNT5", new QName("https://capabilities", "Type2"), "CapNT5").build());
        nt4X.setCapabilities(capNT4X);
        nt5X.setCapabilities(capNT5X);

        TRelationshipTemplate rt21X = new TRelationshipTemplate.Builder(
            "21X",
            QName.valueOf("{http://ex.org}relType_1"),
            nt2X,
            nt1X
        ).build();

        TRelationshipTemplate rt24X = new TRelationshipTemplate.Builder(
            "24X",
            QName.valueOf("{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}HostedOn"),
            nt2X,
            nt4X
        ).build();

        TRelationshipTemplate rt15X = new TRelationshipTemplate.Builder(
            "15X",
            QName.valueOf("{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}HostedOn"),
            nt1X,
            nt5X
        ).build();

        TRelationshipTemplate rt56X = new TRelationshipTemplate.Builder(
            "56X",
            QName.valueOf("{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}HostedOn"),
            nt5X,
            nt6X
        ).build();

        TRelationshipTemplate rt46X = new TRelationshipTemplate.Builder(
            "46X",
            QName.valueOf("{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}HostedOn"),
            nt4X,
            nt6X
        ).build();

        candidate1Topology = new TTopologyTemplate.Builder()
            .addNodeTemplate(nt1X)
            .addNodeTemplate(nt2X)
            .addNodeTemplate(nt4X)
            .addNodeTemplate(nt5X)
            .addNodeTemplate(nt6X)
            .addRelationshipTemplate(rt21X)
            .addRelationshipTemplate(rt24X)
            .addRelationshipTemplate(rt46X)
            .addRelationshipTemplate(rt56X)
            .addRelationshipTemplate(rt15X)
            .build();
        // endregion

    }
}
