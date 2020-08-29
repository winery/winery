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

package org.eclipse.winery.model.tosca.dependencyresolver;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.winery.model.tosca.dependencyresolver.xml_definitions.RR_DependsOn;
import org.eclipse.winery.model.tosca.dependencyresolver.xml_definitions.RR_NodeType;
import org.eclipse.winery.model.tosca.dependencyresolver.xml_definitions.RR_PackageArtifactTemplate;
import org.eclipse.winery.model.tosca.dependencyresolver.xml_definitions.RR_PackageArtifactType;
import org.eclipse.winery.model.tosca.dependencyresolver.xml_definitions.RR_PreDependsOn;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author jery Service Template Handler
 */
public class Topology_Handler {

    public static final String Tosca = ".tosca";
    public static final String Definitions = "Definitions/";
    private static final String ToscaNS = "xmlns:RR_tosca_ns";
    private static final String myPrefix = "RR_tosca_ns:";
    private static final String Type_glue = "_update_RR_";

    // Reference from NodeType to files with Service Templates
    HashMap<String, List<String>> NodeTypeToServiceTemplate;

    // Reference from Script position to ArtifactID
    HashMap<String, List<String>> RefToArtID;

    // Reference from Script position to Node Type
    HashMap<String, List<String>> RefToNodeType;
    CSAR_handler ch;

    /**
     * Constructor with initialization
     */
    public Topology_Handler(final CSAR_handler new_ch) {
        this.NodeTypeToServiceTemplate = new HashMap<>();
        this.RefToArtID = new HashMap<>();
        this.RefToNodeType = new HashMap<>();
        init(new_ch);
    }

    /**
     * simple Constructor
     */

    public static String encode(final String packet) throws UnsupportedEncodingException {
        return java.net.URLEncoder.encode(packet, "UTF-8");// +
        // "_template";
    }

    public HashMap<String, List<String>> getRefToNodeType() {
        return this.RefToNodeType;
    }

    /**
     * Init all local references, search for script positions and dependent Node Types
     */
    public void init(final CSAR_handler new_ch) {

        this.ch = new_ch;

        this.NodeTypeToServiceTemplate.clear();
        this.RefToArtID.clear();
        this.RefToNodeType.clear();

        final File folder = new File(this.ch.getFolder() + Definitions);
        if (!folder.exists()) {
            return;
        }
        System.out.println("Parse Artifacts");
        for (final File entry : folder.listFiles()) {
            parseArtifacts(entry);
        }

        System.out.println("Parse Implementations");
        for (final File entry : folder.listFiles()) {
            parseImplementations(entry);
        }

        System.out.println("Parse ServiceTemplates");
        for (final File entry : folder.listFiles()) {
            parseServiceTemplates(entry);
        }

        System.out.println("RefToNodeType");
        for (final String key : this.RefToNodeType.keySet()) {
            System.out.println(key + " : " + this.RefToNodeType.get(key));
        }
        System.out.println("NodeTypeToServiceTemplate");
        for (final String key : this.NodeTypeToServiceTemplate.keySet()) {
            System.out.println(key + " : " + this.NodeTypeToServiceTemplate.get(key));
        }
    }

    /**
     * Add Node Template for new packet, and depends it to packet created by me
     *
     * @param source_packet packet already in Service Template
     * @param target_packet packet to be created
     */
    public void addDependencyToPacket(String source_packet, String target_packet,
                                      final String dependencyType) throws UnsupportedEncodingException {
        source_packet = encode(source_packet);
        target_packet = encode(target_packet);
        for (final String filename : this.NodeTypeToServiceTemplate.get(source_packet)) {
            try {
                final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder;
                documentBuilder = documentBuilderFactory.newDocumentBuilder();
                final Document document =
                    documentBuilder.parse(this.ch.getFolder() + CSAR_handler.Definitions + filename);
                final NodeList nodes = document.getElementsByTagName("*");
                for (int i = 0; i < nodes.getLength(); i++) {
                    if (nodes.item(i).getNodeName().endsWith(":NodeTemplate")
                        || nodes.item(i).getNodeName().equals("NodeTemplate")) {
                        final String type = ((Element) nodes.item(i)).getAttribute("type");
                        final String sourceID = ((Element) nodes.item(i)).getAttribute("id");
                        if (type.equals("RRnt:" + RR_NodeType.getTypeName(source_packet))
                            && sourceID.startsWith(getID(source_packet) + Type_glue)) {
                            // right NodeTemplate found
                            // need to create new Node Template
                            // and reference
                            final Node topology = nodes.item(i).getParentNode();
                            updateTopology(document, topology, filename, sourceID, target_packet, dependencyType);
                        }
                    }
                }
                addRRImport_NT(document, target_packet);
                final Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                final Result output =
                    new StreamResult(new File(this.ch.getFolder() + CSAR_handler.Definitions + filename));
                final Source input = new DOMSource(document);
                transformer.transform(input, output);
            } catch (ParserConfigurationException | SAXException | IOException | TransformerFactoryConfigurationError
                | TransformerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void updateTopology(final Document document, final Node topology, final String filename,
                                final String sourceID, final String target_packet,
                                final String dependencyType) throws UnsupportedEncodingException {
        createPacketTemplate(document, topology, target_packet, sourceID);
        createPacketDependency(document, topology, sourceID, getID(target_packet), dependencyType);

        if (!this.NodeTypeToServiceTemplate.containsKey(target_packet)) {
            this.NodeTypeToServiceTemplate.put(target_packet, new LinkedList<String>());
        }
        if (!this.NodeTypeToServiceTemplate.get(target_packet).contains(filename)) {
            this.NodeTypeToServiceTemplate.get(target_packet).add(filename);
        }
    }

    /**
     * Generates and return all files, which use give script
     *
     * @param reference script position
     * @return list with each files containing service templates for given script position
     */
    private List<String> getServiceTemplatesFromRef(final String reference) {
        final List<String> serviceTemplates = new LinkedList<>();

        for (final String nodeType : this.RefToNodeType.get(reference)) {
            for (final String serviceTemplate : this.NodeTypeToServiceTemplate.get(nodeType)) {
                if (!serviceTemplates.contains(serviceTemplate)) {
                    serviceTemplates.add(serviceTemplate);
                }
            }
        }
        return serviceTemplates;
    }

    /**
     * Add new NodeTemplate and dependency to existing NodeTemplate by given script position
     *
     * @param script_filename script position
     * @param target_packet   packet to be added
     */
    public void addDependencyToScript(final String script_filename,
                                      String target_packet) throws UnsupportedEncodingException {
        final List<String> files = getServiceTemplatesFromRef(script_filename);
        target_packet = encode(target_packet);
        for (final String filename : files) {
            try {
                final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder;
                documentBuilder = documentBuilderFactory.newDocumentBuilder();
                final Document document =
                    documentBuilder.parse(this.ch.getFolder() + CSAR_handler.Definitions + filename);
                final NodeList nodes = document.getElementsByTagName("*");
                for (int i = 0; i < nodes.getLength(); i++) {
                    if (nodes.item(i).getNodeName().endsWith(":NodeTemplate")
                        || nodes.item(i).getNodeName().equals("NodeTemplate")) {
                        final String type = ((Element) nodes.item(i)).getAttribute("type");
                        for (final String nodeType : this.RefToNodeType.get(script_filename)) {
                            if (type.endsWith(":" + nodeType) || type.equals(nodeType)) {
                                final String sourceID = ((Element) nodes.item(i)).getAttribute("id");
                                // right NodeTemplate found
                                // need to create new Node Template
                                // and reference
                                final Node topology = nodes.item(i).getParentNode();
                                updateTopology(document, topology, filename, sourceID, target_packet,
                                    RR_PreDependsOn.Name);
                            }
                        }
                    }
                }
                addRRImport_NT(document, target_packet);
                final Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                final Result output =
                    new StreamResult(new File(this.ch.getFolder() + CSAR_handler.Definitions + filename));
                final Source input = new DOMSource(document);
                transformer.transform(input, output);
            } catch (ParserConfigurationException | SAXException | IOException | TransformerFactoryConfigurationError
                | TransformerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generate NodeTemplate ID for given packet
     *
     * @param packet packet name
     * @return ID
     */
    private String getID(final String packet) {
        return RR_NodeType.getTypeName(packet);// +
        // "_template";
    }

    /**
     * Creates NodeTemplate
     *
     * @param document to be proceed
     * @param topology Node Containing Topology of Service Template
     * @param packet   packet name
     */
    private void createPacketTemplate(final Document document, final Node topology, final String packet,
                                      final String source) throws UnsupportedEncodingException {
        final NodeList nodes = document.getElementsByTagName("*");
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeName().endsWith(":NodeTemplate")
                || nodes.item(i).getNodeName().equals("NodeTemplate")) {
                if (((Element) nodes.item(i)).getAttribute("id").equals(getID(packet))) {
                    return;
                }
            }
        }
        System.out.println("Add template for " + packet);
        final Element template = document.createElement(myPrefix + "NodeTemplate");
        template.setAttribute("xmlns:RRnt", RR_NodeType.Definitions.NodeType.targetNamespace);
        template.setAttribute("id", getID(packet + Type_glue + source));
        template.setAttribute("name", packet);
        template.setAttribute("type", "RRnt:" + RR_NodeType.getTypeName(packet));
        topology.appendChild(template);
    }

    /**
     * Creates dependency between sourceID and targetID
     *
     * @param topology node containing topology
     * @param sourceID packet which needs targetID
     * @param targetID is needed by sourceID
     */
    private void createPacketDependency(final Document document, final Node topology, final String sourceID,
                                        final String targetID, final String type) {
        if (type == null) {
            throw new NullPointerException();
        }
        System.out.println("Add relation from " + sourceID + " to " + targetID);
        final NodeList nodes = document.getElementsByTagName("*");
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeName().endsWith(":RelationshipTemplate")
                || nodes.item(i).getNodeName().equals("RelationshipTemplate")) {
                if (((Element) nodes.item(i)).getAttribute("id").equals(sourceID + "_" + targetID)) {
                    return;
                }
            }
        }
        final Element relation = document.createElement(myPrefix + "RelationshipTemplate");
        relation.setAttribute("id", sourceID + "_" + targetID);
        relation.setAttribute("name", sourceID + "_needs_" + targetID);
        if (type.equals(RR_DependsOn.Name)) {
            relation.setAttribute("xmlns:RRrt", RR_DependsOn.Definitions.RelationshipType.targetNamespace);
            relation.setAttribute("type", "RRrt:" + RR_DependsOn.Definitions.RelationshipType.name);
        } else if (type.equals(RR_PreDependsOn.Name)) {
            relation.setAttribute("xmlns:RRrt", RR_PreDependsOn.Definitions.RelationshipType.targetNamespace);
            relation.setAttribute("type", "RRrt:" + RR_PreDependsOn.Definitions.RelationshipType.name);
        }
        topology.appendChild(relation);
        final Element sourceElement = document.createElement(myPrefix + "SourceElement");
        sourceElement.setAttribute("ref", sourceID);
        relation.appendChild(sourceElement);
        final Element targetElement = document.createElement(myPrefix + "TargetElement");
        targetElement.setAttribute("ref", targetID + Type_glue + sourceID);
        relation.appendChild(targetElement);
    }

    /**
     * Add my imports to document
     */
    private void addRRImport_Base(final Document document, final String packet) { // TODO
        Element tImport;
        final Node definitions = document.getFirstChild();
        if (definitions.getAttributes().getNamedItem(ToscaNS) == null) {
            ((Element) definitions).setAttribute(ToscaNS, "http://docs.oasis-open.org/tosca/ns/2011/12");

            tImport = document.createElement("RR_tosca_ns:Import");
            tImport.setAttribute("importType", "http://docs.oasis-open.org/tosca/ns/2011/12");
            tImport.setAttribute("location", RR_DependsOn.filename);
            tImport.setAttribute("namespace", RR_DependsOn.Definitions.RelationshipType.targetNamespace);
            definitions.insertBefore(tImport, definitions.getFirstChild());

            tImport = document.createElement("RR_tosca_ns:Import");
            tImport.setAttribute("importType", "http://docs.oasis-open.org/tosca/ns/2011/12");
            tImport.setAttribute("location", RR_PreDependsOn.filename);
            tImport.setAttribute("namespace", RR_PreDependsOn.Definitions.RelationshipType.targetNamespace);
            definitions.insertBefore(tImport, definitions.getFirstChild());

            tImport = document.createElement("RR_tosca_ns:Import");
            tImport.setAttribute("importType", "http://docs.oasis-open.org/tosca/ns/2011/12");
            tImport.setAttribute("location", RR_PackageArtifactType.filename);
            tImport.setAttribute("namespace", RR_PackageArtifactType.Definitions.ArtifactType.targetNamespace);
            definitions.insertBefore(tImport, definitions.getFirstChild());
        }
    }

    private void addRRImport_NT(final Document document, final String packet) { // TODO
        Element tImport;
        final Node definitions = document.getFirstChild();
        addRRImport_Base(document, packet);
        final NodeList nodes = document.getElementsByTagName("RR_tosca_ns:Import");
        for (int i = 0; i < nodes.getLength(); i++) {
            if (((Element) nodes.item(i)).getAttribute("location").equals(RR_NodeType.getFileName(packet))) {
                return;
            }
        }
        tImport = document.createElement("RR_tosca_ns:Import");
        tImport.setAttribute("importType", "http://docs.oasis-open.org/tosca/ns/2011/12");
        tImport.setAttribute("location", RR_NodeType.getFileName(packet));
        tImport.setAttribute("namespace", RR_NodeType.Definitions.NodeType.targetNamespace);
        definitions.insertBefore(tImport, definitions.getFirstChild());
    }

    private void addRRImport_DA(final Document document, final String packet) { // TODO
        Element tImport;
        final Node definitions = document.getFirstChild();
        addRRImport_Base(document, packet);
        final NodeList nodes = document.getElementsByTagName("RR_tosca_ns:Import");
        for (int i = 0; i < nodes.getLength(); i++) {
            if (((Element) nodes.item(i)).getAttribute("location")
                .equals(RR_PackageArtifactTemplate.getFileName(packet))) {
                return;
            }
        }
        tImport = document.createElement("RR_tosca_ns:Import");
        tImport.setAttribute("importType", "http://docs.oasis-open.org/tosca/ns/2011/12");
        tImport.setAttribute("location", RR_PackageArtifactTemplate.getFileName(packet));
        tImport.setAttribute("namespace", RR_PackageArtifactTemplate.Definitions.targetNamespace);
        definitions.insertBefore(tImport, definitions.getFirstChild());
    }

    /**
     * Parse Artifact Templates for creating script position -> ArtifactID reference
     */
    private void parseArtifacts(final File file) {
        // System.out.println("Parse " + file.getName());
        if (!file.getName().toLowerCase().endsWith(Tosca)) {
            return;
        }
        try {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder;
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            final Document document = documentBuilder.parse(file);
            final NodeList nodes = document.getElementsByTagName("*");
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i).getNodeName().endsWith(":ArtifactTemplate")
                    || nodes.item(i).getNodeName().equals("ArtifactTemplate")) {
                    final Element e = (Element) nodes.item(i);
                    final String ID = e.getAttribute("id");
                    final NodeList ArtifactReferences = e.getChildNodes();
                    for (int k = 0; k < ArtifactReferences.getLength(); k++) {
                        if (ArtifactReferences.item(k).getNodeName().endsWith(":ArtifactReferences")
                            || ArtifactReferences.item(k).getNodeName().equals("ArtifactReferences")) {
                            if (ArtifactReferences.item(k).getNodeType() == Node.ELEMENT_NODE) {
                                final Element elem = (Element) ArtifactReferences.item(k);
                                final NodeList ArtifactReferenceList = elem.getChildNodes();
                                for (int j = 0; j < ArtifactReferenceList.getLength(); j++) {
                                    if (ArtifactReferenceList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                                        final Element ref = (Element) ArtifactReferenceList.item(j);
                                        final String REF =
                                            Utils.correctName(java.net.URLDecoder.decode(ref.getAttribute("reference"),
                                                "UTF-8"));
                                        if (!this.RefToArtID.containsKey(REF)) {
                                            this.RefToArtID.put(REF, new LinkedList<String>());
                                        }
                                        this.RefToArtID.get(REF).add(Utils.correctName(ID));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Parse node Containing Implementation Artifact, to create script position -> NodeType reference
     *
     * @param artifact node
     */
    private void parseImplementationsArtifact(final Node artifact, final String nodeType) {
        if (artifact.getNodeName().endsWith(":ImplementationArtifacts")
            || artifact.getNodeName().equals("ImplementationArtifacts")
            || artifact.getNodeName().endsWith(":DeploymentArtifacts")
            || artifact.getNodeName().equals("DeploymentArtifacts")) {
            if (artifact.getNodeType() == Node.ELEMENT_NODE) {
                final Element elem = (Element) artifact;
                final NodeList artifacts = elem.getChildNodes();
                for (int j = 0; j < artifacts.getLength(); j++) {
                    if (artifacts.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        final Element artifactImplementation = (Element) artifacts.item(j);
                        String artifactRef = artifactImplementation.getAttribute("artifactRef");
                        if (artifactRef.contains(":")) {
                            artifactRef = artifactRef.substring(artifactRef.indexOf(':') + 1, artifactRef.length());
                        }
                        addNodeTypeRef(nodeType, Utils.correctName(artifactRef));
                    }
                }
            }
        }
    }

    public void expandTOSCA_Nodes(final List<String> packages, String source) throws IOException, JAXBException {
        source = encode(source);
        if (this.RefToNodeType.get(source) == null) {
            System.out.println("not found");
            return;
        }

        for (final String nodeType : this.RefToNodeType.get(source)) {
            System.out.println("Found Node Type: " + nodeType);
            for (final String serviceTemplate : this.NodeTypeToServiceTemplate.get(nodeType)) {
                System.out.println("Found Service Template: " + serviceTemplate);
                expandTOSCA_Node(packages, nodeType, serviceTemplate);
            }
        }
    }

    public void expandTOSCA_Node(final List<String> packages, final String nodeType,
                                 final String serviceTemplate) throws IOException, JAXBException {
        Element deploymentArtifacts = null;
        try {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder;
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            final Document document =
                documentBuilder.parse(this.ch.getFolder() + CSAR_handler.Definitions + serviceTemplate);
            final NodeList nodes = document.getElementsByTagName("*");
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i).getNodeName().endsWith(":NodeTemplate")
                    || nodes.item(i).getNodeName().equals("NodeTemplate")) {
                    final String type = ((Element) nodes.item(i)).getAttribute("type");
                    if (type.endsWith(":" + nodeType) || type.equals(nodeType)) {
                        // right NodeTemplate found
                        // need to add deployment artifacts
                        final Element e = (Element) nodes.item(i);
                        final NodeList nodeTypeChildren = e.getChildNodes();
                        for (int j = 0; j < nodeTypeChildren.getLength(); j++) {
                            if (nodeTypeChildren.item(j).getNodeType() == Node.ELEMENT_NODE) {
                                if (nodeTypeChildren.item(j).getNodeName().endsWith(":DeploymentArtifacts")
                                    || nodeTypeChildren.item(j).getNodeName().equals("DeploymentArtifacts")) {

                                    deploymentArtifacts = (Element) nodeTypeChildren.item(j);
                                    final NodeList deploymentArtifactsList = deploymentArtifacts.getChildNodes();
                                    for (int d = 0; d < deploymentArtifactsList.getLength(); d++) {

                                        if (deploymentArtifactsList.item(d).getNodeType() == Node.ELEMENT_NODE) {
                                            if (deploymentArtifactsList.item(d).getNodeName()
                                                .endsWith(":DeploymentArtifact")
                                                || deploymentArtifactsList.item(d).getNodeName()
                                                .equals("DeploymentArtifact")) {
                                                final String depArtID =
                                                    ((Element) deploymentArtifactsList.item(d)).getAttribute("artifactRef");
                                                if (packages.contains(depArtID)) {
                                                    System.out.println("artifact exists: " + depArtID);
                                                    packages.remove(depArtID);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (deploymentArtifacts == null) {
                            deploymentArtifacts = document.createElement(myPrefix + "DeploymentArtifacts");
                            e.appendChild(deploymentArtifacts);
                        }
                        for (final String packet : packages) {
                            final Element deploymentArtifact = document.createElement(myPrefix + "DeploymentArtifact");
                            deploymentArtifact.setAttribute("xmlns:tbt",
                                RR_PackageArtifactType.Definitions.ArtifactType.targetNamespace);
                            deploymentArtifact.setAttribute("xmlns:art",
                                RR_PackageArtifactTemplate.Definitions.targetNamespace);
                            deploymentArtifact.setAttribute("name", packet);
                            deploymentArtifact.setAttribute("artifactType", "tbt:"
                                + RR_PackageArtifactType.Definitions.ArtifactType.name);
                            deploymentArtifact.setAttribute("artifactRef",
                                "art:" + RR_PackageArtifactTemplate.getID(packet));
                            deploymentArtifacts.appendChild(deploymentArtifact);
                        }
                    }
                }
            }
            for (final String packet : packages) {
                addRRImport_DA(document, packet);
            }
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            final Result output =
                new StreamResult(new File(this.ch.getFolder() + CSAR_handler.Definitions + serviceTemplate));
            final Source input = new DOMSource(document);
            transformer.transform(input, output);
        } catch (ParserConfigurationException | SAXException | IOException | TransformerFactoryConfigurationError
            | TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Parsing Implementation nodes, looking for NodeType
     */
    private void parseImplementationsNodes(final Node node) {
        if (node.getNodeName().endsWith(":NodeTypeImplementation")
            || node.getNodeName().equals("NodeTypeImplementation")) {
            final Element e = (Element) node;
            String nodeType = e.getAttribute("nodeType");
            if (nodeType.contains(":")) {
                nodeType = nodeType.substring(nodeType.indexOf(':') + 1, nodeType.length());
            }
            final NodeList artifactLists = e.getChildNodes();
            for (int k = 0; k < artifactLists.getLength(); k++) {
                parseImplementationsArtifact(artifactLists.item(k), nodeType);
            }
        }
    }

    /**
     * Parse Implementations
     */
    private void parseImplementations(final File file) {

        if (!file.getName().toLowerCase().endsWith(Tosca)) {
            return;
        }
        // System.out.println("Parse " + file.getName());
        if (!file.getName().toLowerCase().endsWith(Tosca)) {
            return;
        }
        try {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder;
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            final Document document = documentBuilder.parse(file);
            final NodeList nodes = document.getElementsByTagName("*");
            for (int i = 0; i < nodes.getLength(); i++) {
                parseImplementationsNodes(nodes.item(i));
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * creates script position -> Node Type reference, by given Script position -> ArtifactID and ArtifactID ->
     * NodeType
     */
    private void addNodeTypeRef(final String nodeType, final String artifactID) {
        for (final String key : this.RefToArtID.keySet()) {
            if (this.RefToArtID.get(key).contains(artifactID)) {
                if (!this.RefToNodeType.containsKey(key)) {
                    this.RefToNodeType.put(key, new LinkedList<String>());
                }
                this.RefToNodeType.get(key).add(nodeType);
            }
        }
    }

    /**
     * Parse Service templates, looking for right Node Types
     */
    private void parseServiceTemplates(final File file) {
        if (!file.getName().toLowerCase().endsWith(Tosca)) {
            return;
        }
        // System.out.println("Parse " + file.getName());
        if (!file.getName().toLowerCase().endsWith(Tosca)) {
            return;
        }
        try {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder;
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            final Document document = documentBuilder.parse(file);
            final NodeList nodes = document.getElementsByTagName("*");
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i).getNodeName().endsWith(":NodeTemplate")
                    || nodes.item(i).getNodeName().equals("NodeTemplate")) {
                    final Element e = (Element) nodes.item(i);
                    String nodeType = e.getAttribute("type");
                    if (nodeType.contains(":")) {
                        nodeType = nodeType.substring(nodeType.indexOf(':') + 1, nodeType.length());
                    }
                    if (!this.NodeTypeToServiceTemplate.containsKey(nodeType)) {
                        this.NodeTypeToServiceTemplate.put(nodeType, new LinkedList<String>());
                    }
                    if (!this.NodeTypeToServiceTemplate.get(nodeType).contains(file.getName())) {
                        this.NodeTypeToServiceTemplate.get(nodeType).add(file.getName());
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
