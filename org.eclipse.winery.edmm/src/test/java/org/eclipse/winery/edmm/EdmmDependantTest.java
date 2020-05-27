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

package org.eclipse.winery.edmm;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.namespace.QName;

import org.eclipse.winery.edmm.model.EdmmType;
import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TInterfaces;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.extensions.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.extensions.kvproperties.PropertyDefinitionKVList;
import org.eclipse.winery.model.tosca.extensions.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;

import org.junit.jupiter.api.BeforeEach;

public abstract class EdmmDependantTest {

    protected final String NAMESPACE = "https://ex.org/tosca/to/edmm";
    protected final String NAMESPACE_DOUBLE_ENCODED = URLEncoder.encode(URLEncoder.encode(NAMESPACE, "UTF-8"), "UTF-8");
    protected final HashMap<QName, TNodeType> nodeTypes = new HashMap<>();
    protected final HashMap<QName, TRelationshipType> relationshipTypes = new HashMap<>();
    protected final HashMap<String, TNodeTemplate> nodeTemplates = new HashMap<>();
    protected final HashMap<String, TRelationshipTemplate> relationshipTemplates = new HashMap<>();
    protected final HashMap<QName, TNodeTypeImplementation> nodeTypeImplementations = new HashMap<>();
    protected final HashMap<QName, TRelationshipTypeImplementation> relationshipTypeImplementations = new HashMap<>();
    protected final HashMap<QName, TArtifactTemplate> artifactTemplates = new HashMap<>();
    protected final HashMap<QName, EdmmType> edmmTypeExtendsMapping = new HashMap<>();
    protected final HashMap<QName, EdmmType> edmm1to1Mapping = new HashMap<>();

    protected EdmmDependantTest() throws UnsupportedEncodingException {
    }

    @BeforeEach
    void setup() {
        // region *** NodeType setup ***
        QName nodeType1QName = QName.valueOf("{" + NAMESPACE + "}" + "test_node_type");
        TNodeType nodeType1 = new TNodeType();
        nodeType1.setName(nodeType1QName.getLocalPart());
        nodeType1.setTargetNamespace(nodeType1QName.getNamespaceURI());
        nodeTypes.put(nodeType1QName, nodeType1);

        QName nodeType2QName = QName.valueOf("{" + NAMESPACE + "}" + "test_node_type_2");
        TNodeType nodeType2 = new TNodeType();
        nodeType2.setName(nodeType2QName.getLocalPart());
        nodeType2.setTargetNamespace(nodeType2QName.getNamespaceURI());
        TEntityType.DerivedFrom derivedFrom = new TNodeType.DerivedFrom();
        derivedFrom.setTypeRef(nodeType1QName);
        nodeType2.setDerivedFrom(derivedFrom);
        nodeTypes.put(nodeType2QName, nodeType2);

        QName nodeType3QName = QName.valueOf("{" + NAMESPACE + "}" + "test_node_type_3");
        TNodeType nodeType3 = new TNodeType();
        nodeType3.setName(nodeType3QName.getLocalPart());
        nodeType3.setTargetNamespace(nodeType3QName.getNamespaceURI());
        PropertyDefinitionKVList kvList = new PropertyDefinitionKVList();
        kvList.add(new PropertyDefinitionKV("os_family", "xsd:string"));
        kvList.add(new PropertyDefinitionKV("public_key", "xsd:string"));
        kvList.add(new PropertyDefinitionKV("ssh_port", "number"));
        WinerysPropertiesDefinition wpd = new WinerysPropertiesDefinition();
        wpd.setPropertyDefinitionKVList(kvList);
        ModelUtilities.replaceWinerysPropertiesDefinition(nodeType3, wpd);
        nodeTypes.put(nodeType3QName, nodeType3);

        QName nodeType4QName = QName.valueOf("{" + NAMESPACE + "}" + "test_node_type_4");
        TNodeType nodeType4 = new TNodeType();
        nodeType4.setName(nodeType4QName.getLocalPart());
        nodeType4.setTargetNamespace(nodeType4QName.getNamespaceURI());
        TOperation start = new TOperation();
        start.setName("start");
        TOperation stop = new TOperation();
        stop.setName("stop");
        TInterface lifecycle = new TInterface();
        lifecycle.setName("lifecycle_interface");
        lifecycle.getOperation().add(start);
        lifecycle.getOperation().add(stop);
        TInterfaces tInterfaces = new TInterfaces();
        tInterfaces.getInterface().add(lifecycle);
        nodeType4.setInterfaces(tInterfaces);
        nodeTypes.put(nodeType4QName, nodeType4);
        // endregion

        // region *** ArtifactTemplates setup ***
        TArtifactReference startArtifactReference = new TArtifactReference();
        startArtifactReference.setReference("/artifacttemplates/" + NAMESPACE_DOUBLE_ENCODED + "/startTestNode4/files/script.sh");
        TArtifactTemplate.ArtifactReferences startArtifactReferences = new TArtifactTemplate.ArtifactReferences();
        startArtifactReferences.getArtifactReference().add(startArtifactReference);
        TArtifactTemplate startArtifactIA = new TArtifactTemplate();
        QName startArtifactIAQName = QName.valueOf("{" + NAMESPACE + "}" + "Start_IA");
        startArtifactIA.setName(startArtifactIAQName.getLocalPart());
        startArtifactIA.setArtifactReferences(startArtifactReferences);
        artifactTemplates.put(startArtifactIAQName, startArtifactIA);

        TArtifactReference stopArtifactReference = new TArtifactReference();
        stopArtifactReference.setReference("/artifacttemplates/" + NAMESPACE_DOUBLE_ENCODED + "/stopTestNode4/files/script.sh");
        TArtifactTemplate.ArtifactReferences stopArtifactReferences = new TArtifactTemplate.ArtifactReferences();
        stopArtifactReferences.getArtifactReference().add(startArtifactReference);
        TArtifactTemplate stopArtifactIA = new TArtifactTemplate();
        QName stopArtifactIAQName = QName.valueOf("{" + NAMESPACE + "}" + "Stop_IA");
        stopArtifactIA.setName(stopArtifactIAQName.getLocalPart());
        stopArtifactIA.setArtifactReferences(stopArtifactReferences);
        artifactTemplates.put(stopArtifactIAQName, stopArtifactIA);

        TArtifactReference deploymentArtifactArtifactReference = new TArtifactReference();
        deploymentArtifactArtifactReference.setReference("/artifacttemplates/" + NAMESPACE_DOUBLE_ENCODED + "/testNode1-DA/files/da.war");
        TArtifactTemplate deploymentArtifactTemplate = new TArtifactTemplate();
        TArtifactTemplate.ArtifactReferences dploymentArtifactArtifactReferences = new TArtifactTemplate.ArtifactReferences();
        dploymentArtifactArtifactReferences.getArtifactReference().add(startArtifactReference);
        deploymentArtifactTemplate.setArtifactReferences(dploymentArtifactArtifactReferences);
        QName deploymentArtifactIAQName = QName.valueOf("{" + NAMESPACE + "}" + "TestNode1-DA");
        deploymentArtifactTemplate.setName(deploymentArtifactIAQName.getLocalPart());
        deploymentArtifactTemplate.setArtifactReferences(dploymentArtifactArtifactReferences);
        artifactTemplates.put(deploymentArtifactIAQName, deploymentArtifactTemplate);
        // endregion

        // region *** NodeTypeImplementations setup ***
        TImplementationArtifacts artifacts = new TImplementationArtifacts();
        QName nodeTypeImpl4QName = QName.valueOf("{" + NAMESPACE + "}" + "test_node_type_Impl_4");
        TNodeTypeImplementation nodeTypeImpl4 = new TNodeTypeImplementation();
        nodeTypeImpl4.setNodeType(nodeType4QName);
        nodeTypeImpl4.setName(nodeTypeImpl4QName.getLocalPart());
        TImplementationArtifacts.ImplementationArtifact startArtifact = new TImplementationArtifacts.ImplementationArtifact();
        startArtifact.setArtifactRef(startArtifactIAQName);
        startArtifact.setInterfaceName("lifecycle_interface");
        startArtifact.setOperationName("start");
        TImplementationArtifacts.ImplementationArtifact stopArtifact = new TImplementationArtifacts.ImplementationArtifact();
        stopArtifact.setArtifactRef(deploymentArtifactIAQName);
        stopArtifact.setInterfaceName("lifecycle_interface");
        stopArtifact.setOperationName("stop");
        artifacts.getImplementationArtifact().add(startArtifact);
        artifacts.getImplementationArtifact().add(stopArtifact);
        nodeTypeImpl4.setImplementationArtifacts(artifacts);
        nodeTypeImplementations.put(nodeTypeImpl4QName, nodeTypeImpl4);

        // endregion

        // region *** RelationType setup ***
        QName hostedOnQName = QName.valueOf("{" + NAMESPACE + "}" + "hostedOn");
        TRelationshipType hostedOnType = new TRelationshipType();
        hostedOnType.setName(hostedOnQName.getLocalPart());
        hostedOnType.setTargetNamespace(hostedOnQName.getNamespaceURI());
        relationshipTypes.put(hostedOnQName, hostedOnType);

        QName connectsToQName = QName.valueOf("{" + NAMESPACE + "}" + "connectsTo");
        TRelationshipType connectsToType = new TRelationshipType();
        connectsToType.setName(connectsToQName.getLocalPart());
        connectsToType.setTargetNamespace(connectsToQName.getNamespaceURI());
        relationshipTypes.put(connectsToQName, connectsToType);
        // endregion

        // region *** create NodeTemplates ***
        TNodeTemplate nt1 = new TNodeTemplate();
        nt1.setType(nodeType1QName);
        nt1.setId("test_node_1");
        nt1.setName("test_node_1");
        TDeploymentArtifacts deploymentArtifacts = new TDeploymentArtifacts();
        TDeploymentArtifact artifact = new TDeploymentArtifact();
        artifact.setArtifactRef(deploymentArtifactIAQName);
        artifact.setArtifactType(QName.valueOf("{" + NAMESPACE + "}" + "WAR"));
        deploymentArtifacts.getDeploymentArtifact().add(artifact);
        nt1.setDeploymentArtifacts(deploymentArtifacts);
        nodeTemplates.put(nt1.getId(), nt1);

        TNodeTemplate nt2 = new TNodeTemplate();
        nt2.setType(nodeType2QName);
        nt2.setId("test_node_2");
        nt2.setName("test_node_2");
        nodeTemplates.put(nt2.getId(), nt2);

        TNodeTemplate nt3 = new TNodeTemplate();
        nt3.setType(nodeType3QName);
        nt3.setId("test_node_3");
        nt3.setName("test_node_3");
        TEntityTemplate.WineryKVProperties properties = new TEntityTemplate.WineryKVProperties();
        LinkedHashMap<String, String> nt3Properties = new LinkedHashMap<>();
        nt3Properties.put("os_family", "ubuntu");
        nt3Properties.put("public_key", "-----BEGIN PUBLIC KEY----- ... -----END PUBLIC KEY-----");
        nt3Properties.put("ssh_port", "22");
        properties.setKVProperties(nt3Properties);
        nt3.setProperties(properties);
        nodeTemplates.put(nt3.getId(), nt3);

        TNodeTemplate nt4 = new TNodeTemplate();
        nt4.setType(nodeType4QName);
        nt4.setId("test_node_4");
        nt4.setName("test_node_4");
        nodeTemplates.put(nt4.getId(), nt4);
        // endregion 

        // region *** create RelationshipTemplate ***
        TRelationshipTemplate rt13 = new TRelationshipTemplate();
        rt13.setType(hostedOnQName);
        rt13.setId("1_hosted_on_3");
        rt13.setName("1_hosted_on_3");
        rt13.setSourceNodeTemplate(nt1);
        rt13.setTargetNodeTemplate(nt3);
        relationshipTemplates.put(rt13.getId(), rt13);

        TRelationshipTemplate rt23 = new TRelationshipTemplate();
        rt23.setType(hostedOnQName);
        rt23.setId("2_hosted_on_3");
        rt23.setName("2_hosted_on_3");
        rt23.setSourceNodeTemplate(nt2);
        rt23.setTargetNodeTemplate(nt3);
        relationshipTemplates.put(rt23.getId(), rt23);

        TRelationshipTemplate rt41 = new TRelationshipTemplate();
        rt41.setType(hostedOnQName);
        rt41.setId("4_hosted_on_1");
        rt41.setName("4_hosted_on_1");
        rt41.setSourceNodeTemplate(nt4);
        rt41.setTargetNodeTemplate(nt1);
        relationshipTemplates.put(rt41.getId(), rt41);

        TRelationshipTemplate rt12 = new TRelationshipTemplate();
        rt12.setType(connectsToQName);
        rt12.setId("1_connects_to_2");
        rt12.setName("1_connects_to_2");
        rt12.setSourceNodeTemplate(nt1);
        rt12.setTargetNodeTemplate(nt2);
        relationshipTemplates.put(rt12.getId(), rt12);
        // endregion

        // region *** create edmm type mapping ***
        edmm1to1Mapping.put(nodeType1QName, EdmmType.SOFTWARE_COMPONENT);
        // edmmTypeMapping.put(nodeType2QName, EdmmType.SOFTWARE_COMPONENT);
        edmmTypeExtendsMapping.put(nodeType3QName, EdmmType.COMPUTE);
        edmmTypeExtendsMapping.put(nodeType4QName, EdmmType.WEB_APPLICATION);
        edmm1to1Mapping.put(hostedOnQName, EdmmType.HOSTED_ON);
        edmm1to1Mapping.put(connectsToQName, EdmmType.CONNECTS_TO);
        // endregion
    }
}
