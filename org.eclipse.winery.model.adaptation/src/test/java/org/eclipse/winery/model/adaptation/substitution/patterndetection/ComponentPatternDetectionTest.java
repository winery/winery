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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.substitution.refinement.DefaultRefinementChooser;
import org.eclipse.winery.model.tosca.HasPolicies;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTAttributeMapping;
import org.eclipse.winery.model.tosca.extensions.OTAttributeMappingType;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTPermutationMapping;
import org.eclipse.winery.model.tosca.extensions.OTRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTRelationDirection;
import org.eclipse.winery.model.tosca.extensions.OTRelationMapping;
import org.eclipse.winery.model.tosca.extensions.OTStayMapping;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.topologygraph.matching.ToscaIsomorphismMatcher;
import org.eclipse.winery.topologygraph.matching.patterndetection.PatternDetectionUtils;
import org.eclipse.winery.topologygraph.matching.patterndetection.ToscaComponentPatternMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

import org.jgrapht.GraphMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComponentPatternDetectionTest {

    OTPatternRefinementModel prm1;
    OTPatternRefinementModel prm2;
    TTopologyTemplate topology;

    /**
     * Based on example from thesis
     */
    @BeforeEach
    public void setUp() {
        // needs to be swapped manually as only PRMs retrieved from repo are swapped automatically
        prm1 = PatternDetectionUtils.swapDetectorWithRefinement(prm1());
        prm2 = PatternDetectionUtils.swapDetectorWithRefinement(prm2());
        topology = topology();
    }

    private boolean isCompatible(OTPatternRefinementModel prm, List<OTRefinementModel> refinementModels) {
        ToscaComponentPatternMatcher matcher = new ToscaComponentPatternMatcher(prm, null, refinementModels, new HashMap<>());
        ToscaIsomorphismMatcher isomorphismMatcher = new ToscaIsomorphismMatcher();
        ToscaGraph detectorGraph = ToscaTransformer.createTOSCAGraph(prm.getDetector());
        ToscaGraph topologyGraph = ToscaTransformer.createTOSCAGraph(topology);
        Iterator<GraphMapping<ToscaNode, ToscaEdge>> matches = isomorphismMatcher
            .findMatches(detectorGraph, topologyGraph, matcher);
        return matches.hasNext();
    }

    @Test
    public void isCompatiblePrm1() {
        assertTrue(isCompatible(prm1, Collections.singletonList(prm1)));
        assertTrue(isCompatible(prm1, Arrays.asList(prm1, prm2)));
    }

    @Test
    public void isCompatiblePrm2WithoutPrm1() {
        assertFalse(isCompatible(prm2, Collections.singletonList(prm2)));
    }

    @Test
    public void isCompatiblePrm2() {
        assertTrue(isCompatible(prm2, Arrays.asList(prm1, prm2)));
        assertTrue(isCompatible(prm2, Arrays.asList(prm2, prm1)));
    }

    @Test
    public void refineTopology() {
        ComponentPatternDetection detection = new ComponentPatternDetection(
            new DefaultRefinementChooser(),
            Arrays.asList(prm2, prm1)
        );
        detection.refineTopology(topology);

        // check inserted elements
        assertNotNull(topology.getNodeTemplate("relationalDb-d2"));
        assertNotNull(topology.getRelationshipTemplate("relationalDb-privateCloud-d2"));
        assertNotNull(topology.getNodeTemplate("privateCloud-d2"));
        assertNotNull(topology.getRelationshipTemplate("processor-db"));
        assertNotNull(topology.getNodeTemplate("processor"));
        assertNotNull(topology.getRelationshipTemplate("processor-webserver"));
        assertNotNull(topology.getNodeTemplate("webserver"));
        assertNotNull(topology.getRelationshipTemplate("webserver-vm"));

        // check removed elements
        assertNull(topology.getNodeTemplate("database"));
        assertNull(topology.getRelationshipTemplate("db-dbms"));
        assertNull(topology.getNodeTemplate("dbms"));
        assertNull(topology.getRelationshipTemplate("dbms-vm"));
        assertNull(topology.getNodeTemplate("vm"));
        assertNull(topology.getRelationshipTemplate("vm-openstack"));
        assertNull(topology.getNodeTemplate("openstack"));

        // redirected relations
        TRelationshipTemplate processorToDb = topology.getRelationshipTemplate("processor-db");
        assertNotNull(processorToDb);
        assertEquals(processorToDb.getSourceElement().getRef().getId(), "processor");
        assertEquals(processorToDb.getTargetElement().getRef().getId(), "relationalDb-d2");
        TRelationshipTemplate webserverToVm = topology.getRelationshipTemplate("webserver-vm");
        assertNotNull(webserverToVm);
        assertEquals(webserverToVm.getSourceElement().getRef().getId(), "webserver");
        assertEquals(webserverToVm.getTargetElement().getRef().getId(), "privateCloud-d2");

        // attributes should not have been mapped
        TNodeTemplate relationalDb = topology.getNodeTemplate("relationalDb-d2");
        assertNotNull(relationalDb);
        LinkedHashMap<String, String> relationDbProps = ModelUtilities.getPropertiesKV(relationalDb);
        assertNotNull(relationDbProps);
        assertTrue(relationDbProps.isEmpty());
        TNodeTemplate privateCloud = topology.getNodeTemplate("privateCloud-d2");
        assertNotNull(privateCloud);
        LinkedHashMap<String, String> privateCloudProps = ModelUtilities.getPropertiesKV(privateCloud);
        assertNotNull(privateCloudProps);
        assertTrue(privateCloudProps.isEmpty());
    }

    @Test
    public void refineTopologyWithBehaviorPatterns() {
        TPolicy behaviorPattern1 = new TPolicy(new TPolicy.Builder(QName.valueOf("behaviorPattern1")).setName("behaviorPattern1"));
        TPolicy behaviorPattern2 = new TPolicy(new TPolicy.Builder(QName.valueOf("behaviorPattern2")).setName("behaviorPattern2"));

        List<TPolicy> topologyPolicies = new ArrayList<>();
        topologyPolicies.add(behaviorPattern1);
        ((HasPolicies) topology.getNodeTemplateOrRelationshipTemplate("database"))
            .setPolicies(topologyPolicies);

        List<TPolicy> detectorPolicies = new ArrayList<>();
        detectorPolicies.add(behaviorPattern1);
        ((HasPolicies) prm2.getDetector().getNodeTemplateOrRelationshipTemplate("database-rs2"))
            .setPolicies(detectorPolicies);

        List<TPolicy> refinementPolicies = new ArrayList<>();
        refinementPolicies.add(behaviorPattern1);
        refinementPolicies.add(behaviorPattern2);
        ((HasPolicies) prm2.getRefinementStructure().getNodeTemplateOrRelationshipTemplate("relationalDb-d2"))
            .setPolicies(refinementPolicies);

        ComponentPatternDetection detection = new ComponentPatternDetection(
            new DefaultRefinementChooser(),
            Arrays.asList(prm2, prm1)
        );
        detection.refineTopology(topology);

        // behaviorPattern2 must have been removed as it cannot be guaranteed to be implemented
        HasPolicies db = ((HasPolicies) topology.getNodeTemplateOrRelationshipTemplate("relationalDb-d2"));
        assertEquals(db.getPolicies().size(), 1);
        assertTrue(db.getPolicies().contains(behaviorPattern1));
    }

    @Test
    public void testRemoveBehaviorPatterns() {
        // region: detector
        TNodeTemplate detectorElement = new TNodeTemplate(new TNodeTemplate.Builder("detectorElement", QName.valueOf("{ns}type1"))
            .setX("1").setY("1")
        );
        TNodeTemplate detectorElement2 = new TNodeTemplate(new TNodeTemplate.Builder("detectorElement2", QName.valueOf("{ns}pattern1"))
            .setX("1").setY("1")
        );
        List<TPolicy> detectorPolicies = new ArrayList<>();
        detectorPolicies.add(new TPolicy(new TPolicy.Builder(QName.valueOf("{patternNs}one")).setName("one")));
        detectorPolicies.add(new TPolicy(new TPolicy.Builder(QName.valueOf("{patternNs}two")).setName("two")));
        detectorElement.setPolicies(detectorPolicies);
        TTopologyTemplate detector = new TTopologyTemplate(new TTopologyTemplate.Builder()
            .addNodeTemplates(Arrays.asList(detectorElement, detectorElement2)));
        // endregion
        // region: refinement
        TNodeTemplate refinementElement = new TNodeTemplate(new TNodeTemplate.Builder("refinementElement", QName.valueOf("{ns}type1"))
            .setX("1").setY("1")
        );
        TNodeTemplate refinementElement2 = new TNodeTemplate(new TNodeTemplate.Builder("refinementElement2", QName.valueOf("{ns}type2"))
            .setX("1").setY("1")
        );
        List<TPolicy> refinementPolicies = new ArrayList<>();
        refinementPolicies.add(new TPolicy(new TPolicy.Builder(QName.valueOf("{patternNs}one")).setName("one")));
        refinementElement.setPolicies(refinementPolicies);
        TTopologyTemplate refinement = new TTopologyTemplate(new TTopologyTemplate.Builder()
            .addNodeTemplates(Arrays.asList(refinementElement, refinementElement2)));
        // endregion
        OTStayMapping stayMapping = new OTStayMapping(new OTStayMapping.Builder()
            .setDetectorElement(detectorElement)
            .setRefinementElement(refinementElement));
        OTPatternRefinementModel prm = new OTPatternRefinementModel(new OTPatternRefinementModel.Builder()
            .setDetector(detector)
            .setRefinementStructure(refinement)
            .setStayMappings(Arrays.asList(stayMapping))
        );
        PatternDetectionUtils.swapDetectorWithRefinement(prm);

        TNodeTemplate nodeTemplate = new TNodeTemplate(new TNodeTemplate.Builder("nodeTemplate", QName.valueOf("{ns}type1"))
            .setX("1").setY("1")
        );
        TNodeTemplate nodeTemplate2 = new TNodeTemplate(new TNodeTemplate.Builder("nodeTemplate2", QName.valueOf("{ns}type2"))
            .setX("1").setY("1")
        );
        nodeTemplate.setPolicies(new ArrayList<>(refinementPolicies));
        TTopologyTemplate topology = new TTopologyTemplate(new TTopologyTemplate.Builder()
            .addNodeTemplates(Arrays.asList(nodeTemplate, nodeTemplate2)));

        ComponentPatternDetection detection = new ComponentPatternDetection(new DefaultRefinementChooser(), Arrays.asList(prm));
        detection.refineTopology(topology);
        assertEquals(topology.getNodeTemplates().size(), 2);
        Set<QName> types = topology.getNodeTemplates().stream()
            .map(TNodeTemplate::getType)
            .collect(Collectors.toSet());
        assertTrue(types.contains(QName.valueOf("{ns}type1")));
        assertTrue(types.contains(QName.valueOf("{ns}pattern1")));
        TNodeTemplate nodeTemplate1 = topology.getNodeTemplate("nodeTemplate");
        assertNotNull(nodeTemplate1);
        assertNotNull(nodeTemplate1.getPolicies());
        List<TPolicy> policies = nodeTemplate1.getPolicies();
        assertEquals(policies.size(), 1);
        assertTrue(policies.stream().anyMatch(policy -> policy.getPolicyType().equals(QName.valueOf("{patternNs}one"))));
    }

    private OTPatternRefinementModel prm1() {
        // region: detector
        TNodeTemplate processorD = new TNodeTemplate(new TNodeTemplate.Builder("processor-d1", QName.valueOf("{ns}java-8-app"))
            .setX("1").setY("1")
        );
        LinkedHashMap<String, String> processorDProps = new LinkedHashMap<>();
        processorDProps.put("port", "8081");
        ModelUtilities.setPropertiesKV(processorD, processorDProps);
        TNodeTemplate executionEnv = new TNodeTemplate(new TNodeTemplate.Builder("executionEnv-d1", QName.valueOf("{ns}execution-env"))
            .setX("1").setY("1")
        );
        TNodeTemplate privateCloud = new TNodeTemplate(new TNodeTemplate.Builder("privateCloud-d1", QName.valueOf("{ns}private-cloud"))
            .setX("1").setY("1")
        );

        TRelationshipTemplate processorDToExecutionEnv = new TRelationshipTemplate(new TRelationshipTemplate.Builder(
            "processorD-executionEnv-d1",
            QName.valueOf("{ns}hostedOn"),
            processorD,
            executionEnv
        ));
        TRelationshipTemplate executionEnvToPrivateCloud = new TRelationshipTemplate(new TRelationshipTemplate.Builder(
            "executionEnv-privateCloud-d1",
            QName.valueOf("{ns}hostedOn"),
            executionEnv,
            privateCloud
        ));

        TTopologyTemplate detector = new TTopologyTemplate(new TTopologyTemplate.Builder()
            .addNodeTemplates(Arrays.asList(processorD, executionEnv, privateCloud))
            .addRelationshipTemplates(Arrays.asList(processorDToExecutionEnv, executionEnvToPrivateCloud))
        );
        // endregion

        // region: refinement
        TNodeTemplate processorRs = new TNodeTemplate(new TNodeTemplate.Builder("processor-rs1", QName.valueOf("{ns}java-8-app"))
            .setX("1").setY("1")
        );
        TNodeTemplate webserver = new TNodeTemplate(new TNodeTemplate.Builder("webserver-rs1", QName.valueOf("{ns}tomcat-9"))
            .setX("1").setY("1")
        );
        TNodeTemplate vm = new TNodeTemplate(new TNodeTemplate.Builder("ubuntu-2004-rs1", QName.valueOf("{ns}vm"))
            .setX("1").setY("1")
        );
        TNodeTemplate openstack = new TNodeTemplate(new TNodeTemplate.Builder("openstack-rs1", QName.valueOf("{ns}openstack-victoria"))
            .setX("1").setY("1")
        );

        LinkedHashMap<String, String> processorRsProps = new LinkedHashMap<>();
        processorRsProps.put("port", "8081");
        ModelUtilities.setPropertiesKV(processorRs, processorRsProps);
        LinkedHashMap<String, String> webserverProps = new LinkedHashMap<>();
        webserverProps.put("jmxPort", "9091");
        ModelUtilities.setPropertiesKV(webserver, webserverProps);
        LinkedHashMap<String, String> vmProps = new LinkedHashMap<>();
        vmProps.put("ram", "128GB");
        ModelUtilities.setPropertiesKV(vm, vmProps);
        LinkedHashMap<String, String> openstackProps = new LinkedHashMap<>();
        openstackProps.put("api", "openstack.uni-stuttgart");
        ModelUtilities.setPropertiesKV(openstack, openstackProps);

        TRelationshipTemplate processorToWebserver = new TRelationshipTemplate(new TRelationshipTemplate.Builder(
            "processor-webserver-rs1",
            QName.valueOf("{ns}hostedOn"),
            processorRs,
            webserver
        ));
        TRelationshipTemplate webserverToVm = new TRelationshipTemplate(new TRelationshipTemplate.Builder(
            "webserver-vm-rs1",
            QName.valueOf("{ns}hostedOn"),
            webserver,
            vm
        ));
        TRelationshipTemplate vmToOpenstack = new TRelationshipTemplate(new TRelationshipTemplate.Builder(
            "vm-openstack-rs1",
            QName.valueOf("{ns}hostedOn"),
            vm,
            openstack
        ));

        TTopologyTemplate refinement = new TTopologyTemplate(new TTopologyTemplate.Builder()
            .addNodeTemplates(Arrays.asList(processorRs, webserver, vm, openstack))
            .addRelationshipTemplates(Arrays.asList(processorToWebserver, webserverToVm, vmToOpenstack))
        );
        // endregion

        OTStayMapping stayMapping = new OTStayMapping(new OTStayMapping.Builder()
            .setDetectorElement(processorD)
            .setRefinementElement(processorRs)
        );

        List<OTPermutationMapping> permutationMappings = new ArrayList<>();
        permutationMappings.add(new OTPermutationMapping(new OTPermutationMapping.Builder()
            .setDetectorElement(executionEnv)
            .setRefinementElement(webserver)
        ));
        permutationMappings.add(new OTPermutationMapping(new OTPermutationMapping.Builder()
            .setDetectorElement(privateCloud)
            .setRefinementElement(openstack)
        ));

        List<OTAttributeMapping> attributeMappings = new ArrayList<>();
        attributeMappings.add(new OTAttributeMapping(new OTAttributeMapping.Builder()
            .setDetectorElement(executionEnv)
            .setDetectorProperty("port")
            .setRefinementElement(webserver)
            .setRefinementProperty("jmxPort")
            .setType(OTAttributeMappingType.SELECTIVE)
        ));
        attributeMappings.add(new OTAttributeMapping(new OTAttributeMapping.Builder()
            .setDetectorElement(privateCloud)
            .setDetectorProperty("interface")
            .setRefinementElement(openstack)
            .setRefinementProperty("api")
            .setType(OTAttributeMappingType.SELECTIVE)
        ));

        List<OTRelationMapping> relationMappings = new ArrayList<>();
        relationMappings.add(new OTRelationMapping(new OTRelationMapping.Builder()
            .setDetectorElement(privateCloud)
            .setRefinementElement(vm)
            .setRelationType("{ns}hostedOn")
            .setValidSourceOrTarget(null)
            .setDirection(OTRelationDirection.INGOING)
        ));

        return new OTPatternRefinementModel(new OTPatternRefinementModel.Builder()
            .setDetector(detector)
            .setRefinementStructure(refinement)
            .setStayMappings(Arrays.asList(stayMapping))
            .setPermutationMappings(permutationMappings)
            .setAttributeMappings(attributeMappings)
            .setRelationMappings(relationMappings)
        );
    }

    private OTPatternRefinementModel prm2() {
        // region: detector
        TNodeTemplate relationalDb = new TNodeTemplate(new TNodeTemplate.Builder("relationalDb-d2", QName.valueOf("{ns}relational-db"))
            .setX("1").setY("1")
        );
        ModelUtilities.setPropertiesKV(relationalDb, new LinkedHashMap<>());
        TNodeTemplate privateCloud = new TNodeTemplate(new TNodeTemplate.Builder("privateCloud-d2", QName.valueOf("{ns}private-cloud"))
            .setX("1").setY("1")
        );
        ModelUtilities.setPropertiesKV(privateCloud, new LinkedHashMap<>());

        TRelationshipTemplate relationalDbToPrivateCloud = new TRelationshipTemplate(new TRelationshipTemplate.Builder(
            "relationalDb-privateCloud-d2",
            QName.valueOf("{ns}hostedOn"),
            relationalDb,
            privateCloud
        ));

        TTopologyTemplate detector = new TTopologyTemplate(new TTopologyTemplate.Builder()
            .addNodeTemplates(Arrays.asList(relationalDb, privateCloud))
            .addRelationshipTemplates(Arrays.asList(relationalDbToPrivateCloud))
        );
        // endregion

        // region: refinement
        TNodeTemplate database = new TNodeTemplate(new TNodeTemplate.Builder("database-rs2", QName.valueOf("{ns}mysql-db-5.7"))
            .setX("1").setY("1")
        );
        TNodeTemplate dbms = new TNodeTemplate(new TNodeTemplate.Builder("dbms-rs2", QName.valueOf("{ns}mysql-dbms-5.7"))
            .setX("1").setY("1")
        );
        TNodeTemplate vm = new TNodeTemplate(new TNodeTemplate.Builder("windows-server-2019-rs2", QName.valueOf("{ns}vm"))
            .setX("1").setY("1")
        );
        TNodeTemplate vsphere = new TNodeTemplate(new TNodeTemplate.Builder("vsphere-rs2", QName.valueOf("{ns}vsphere-7"))
            .setX("1").setY("1")
        );

        LinkedHashMap<String, String> dbProps = new LinkedHashMap<>();
        dbProps.put("dbName", "database");
        ModelUtilities.setPropertiesKV(database, dbProps);
        LinkedHashMap<String, String> dbmsProps = new LinkedHashMap<>();
        dbmsProps.put("adminPort", "33062");
        ModelUtilities.setPropertiesKV(dbms, dbmsProps);
        LinkedHashMap<String, String> vmProps = new LinkedHashMap<>();
        vmProps.put("ram", "64GB");
        ModelUtilities.setPropertiesKV(vm, vmProps);
        LinkedHashMap<String, String> vsphereProps = new LinkedHashMap<>();
        vsphereProps.put("api", "vsphere.uni-stuttgart");
        ModelUtilities.setPropertiesKV(vsphere, vsphereProps);

        TRelationshipTemplate dbToDbms = new TRelationshipTemplate(new TRelationshipTemplate.Builder(
            "db-dbms-rs2",
            QName.valueOf("{ns}hostedOn"),
            database,
            dbms
        ));
        TRelationshipTemplate dbmsToVm = new TRelationshipTemplate(new TRelationshipTemplate.Builder(
            "dbms-vm-rs2",
            QName.valueOf("{ns}hostedOn"),
            dbms,
            vm
        ));
        TRelationshipTemplate vmToVsphere = new TRelationshipTemplate(new TRelationshipTemplate.Builder(
            "vm-vsphere-rs2",
            QName.valueOf("{ns}hostedOn"),
            vm,
            vsphere
        ));

        TTopologyTemplate refinement = new TTopologyTemplate(new TTopologyTemplate.Builder()
            .addNodeTemplates(Arrays.asList(database, dbms, vm, vsphere))
            .addRelationshipTemplates(Arrays.asList(dbToDbms, dbmsToVm, vmToVsphere))
        );
        // endregion

        List<OTPermutationMapping> permutationMappings = new ArrayList<>();
        permutationMappings.add(new OTPermutationMapping(new OTPermutationMapping.Builder()
            .setDetectorElement(relationalDb)
            .setRefinementElement(database)
        ));
        permutationMappings.add(new OTPermutationMapping(new OTPermutationMapping.Builder()
            .setDetectorElement(privateCloud)
            .setRefinementElement(vsphere)
        ));

        List<OTAttributeMapping> attributeMappings = new ArrayList<>();
        attributeMappings.add(new OTAttributeMapping(new OTAttributeMapping.Builder()
            .setDetectorElement(relationalDb)
            .setDetectorProperty("name")
            .setRefinementElement(database)
            .setRefinementProperty("dbName")
            .setType(OTAttributeMappingType.SELECTIVE)
        ));
        attributeMappings.add(new OTAttributeMapping(new OTAttributeMapping.Builder()
            .setDetectorElement(privateCloud)
            .setDetectorProperty("interface")
            .setRefinementElement(vsphere)
            .setRefinementProperty("api")
            .setType(OTAttributeMappingType.SELECTIVE)
        ));

        List<OTRelationMapping> relationMappings = new ArrayList<>();
        relationMappings.add(new OTRelationMapping(new OTRelationMapping.Builder()
            .setDetectorElement(relationalDb)
            .setRefinementElement(database)
            .setRelationType("{ns}secureSqlConnection")
            .setValidSourceOrTarget(null)
            .setDirection(OTRelationDirection.INGOING)
        ));
        relationMappings.add(new OTRelationMapping(new OTRelationMapping.Builder()
            .setDetectorElement(privateCloud)
            .setRefinementElement(vm)
            .setRelationType("{ns}hostedOn")
            .setValidSourceOrTarget(null)
            .setDirection(OTRelationDirection.INGOING)
        ));

        return new OTPatternRefinementModel(new OTPatternRefinementModel.Builder()
            .setDetector(detector)
            .setRefinementStructure(refinement)
            .setPermutationMappings(permutationMappings)
            .setAttributeMappings(attributeMappings)
            .setRelationMappings(relationMappings)
        );
    }

    private TTopologyTemplate topology() {
        TNodeTemplate processor = new TNodeTemplate(new TNodeTemplate.Builder("processor", QName.valueOf("{ns}java-8-app"))
            .setX("1").setY("1")
        );
        TNodeTemplate webserver = new TNodeTemplate(new TNodeTemplate.Builder("webserver", QName.valueOf("{ns}tomcat-9"))
            .setX("1").setY("1")
        );
        TNodeTemplate database = new TNodeTemplate(new TNodeTemplate.Builder("database", QName.valueOf("{ns}mysql-db-5.7"))
            .setX("1").setY("1")
        );
        TNodeTemplate dbms = new TNodeTemplate(new TNodeTemplate.Builder("dbms", QName.valueOf("{ns}mysql-dbms-5.7"))
            .setX("1").setY("1")
        );
        TNodeTemplate vm = new TNodeTemplate(new TNodeTemplate.Builder("ubuntu-2004", QName.valueOf("{ns}vm"))
            .setX("1").setY("1")
        );
        TNodeTemplate openstack = new TNodeTemplate(new TNodeTemplate.Builder("openstack", QName.valueOf("{ns}openstack-victoria"))
            .setX("1").setY("1")
        );

        LinkedHashMap<String, String> processorProps = new LinkedHashMap<>();
        processorProps.put("port", "8081");
        ModelUtilities.setPropertiesKV(processor, processorProps);
        LinkedHashMap<String, String> webserverProps = new LinkedHashMap<>();
        webserverProps.put("jmxPort", "9091");
        ModelUtilities.setPropertiesKV(webserver, webserverProps);
        LinkedHashMap<String, String> dbProps = new LinkedHashMap<>();
        dbProps.put("dbName", "database");
        ModelUtilities.setPropertiesKV(database, dbProps);
        LinkedHashMap<String, String> dbmsProps = new LinkedHashMap<>();
        dbmsProps.put("adminPort", "33062");
        ModelUtilities.setPropertiesKV(dbms, dbmsProps);
        LinkedHashMap<String, String> vmProps = new LinkedHashMap<>();
        vmProps.put("ram", "128GB");
        ModelUtilities.setPropertiesKV(vm, vmProps);
        LinkedHashMap<String, String> openstackProps = new LinkedHashMap<>();
        openstackProps.put("api", "openstack.uni-stuttgart");
        ModelUtilities.setPropertiesKV(openstack, openstackProps);

        TRelationshipTemplate processorToDb = new TRelationshipTemplate(new TRelationshipTemplate.Builder(
            "processor-db",
            QName.valueOf("{ns}secureSqlConnection"),
            processor,
            database
        ));
        TRelationshipTemplate processorToWebserver = new TRelationshipTemplate(new TRelationshipTemplate.Builder(
            "processor-webserver",
            QName.valueOf("{ns}hostedOn"),
            processor,
            webserver
        ));
        TRelationshipTemplate webserverToVm = new TRelationshipTemplate(new TRelationshipTemplate.Builder(
            "webserver-vm",
            QName.valueOf("{ns}hostedOn"),
            webserver,
            vm
        ));
        TRelationshipTemplate dbToDbms = new TRelationshipTemplate(new TRelationshipTemplate.Builder(
            "db-dbms",
            QName.valueOf("{ns}hostedOn"),
            database,
            dbms
        ));
        TRelationshipTemplate dbmsToVm = new TRelationshipTemplate(new TRelationshipTemplate.Builder(
            "dbms-vm",
            QName.valueOf("{ns}hostedOn"),
            dbms,
            vm
        ));
        TRelationshipTemplate vmToOpenstack = new TRelationshipTemplate(new TRelationshipTemplate.Builder(
            "vm-openstack",
            QName.valueOf("{ns}hostedOn"),
            vm,
            openstack
        ));

        return new TTopologyTemplate(new TTopologyTemplate.Builder()
            .addNodeTemplates(Arrays.asList(processor, webserver, database, dbms, vm, openstack))
            .addRelationshipTemplates(Arrays.asList(processorToDb, processorToWebserver,
                webserverToVm, dbToDbms, dbmsToVm, vmToOpenstack))
        );
    }
}
