/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.tosca.xml.utils;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.xml.XHasId;
import org.eclipse.winery.model.tosca.xml.XRelationshipSourceOrTarget;
import org.eclipse.winery.model.tosca.xml.XTBoundaryDefinitions;
import org.eclipse.winery.model.tosca.xml.XTCapability;
import org.eclipse.winery.model.tosca.xml.XTEntityTemplate;
import org.eclipse.winery.model.tosca.xml.XTEntityType;
import org.eclipse.winery.model.tosca.xml.XTExtensibleElements;
import org.eclipse.winery.model.tosca.xml.XTNodeTemplate;
import org.eclipse.winery.model.tosca.xml.XTNodeType;
import org.eclipse.winery.model.tosca.xml.XTPlan;
import org.eclipse.winery.model.tosca.xml.XTPlans;
import org.eclipse.winery.model.tosca.xml.XTPolicies;
import org.eclipse.winery.model.tosca.xml.XTPolicy;
import org.eclipse.winery.model.tosca.xml.XTRelationshipTemplate;
import org.eclipse.winery.model.tosca.xml.XTRelationshipType;
import org.eclipse.winery.model.tosca.xml.XTRequirementDefinition;
import org.eclipse.winery.model.tosca.xml.XTServiceTemplate;
import org.eclipse.winery.model.tosca.xml.XTTag;
import org.eclipse.winery.model.tosca.xml.constants.Namespaces;
import org.eclipse.winery.model.tosca.xml.constants.QNames;
import org.eclipse.winery.model.tosca.xml.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.xml.XTCapabilityDefinition;
import org.eclipse.winery.model.tosca.xml.XTRequirement;
import org.eclipse.winery.model.tosca.xml.XTTopologyTemplate;

import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ModelUtilities {

    public static final QName QNAME_LOCATION = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "location");
    public static final QName NODE_TEMPLATE_REGION = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "region");
    public static final QName NODE_TEMPLATE_PROVIDER = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE,
        "provider");
    public static final QName RELATIONSHIP_TEMPLATE_TRANSFER_TYPE =
        new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "dataTransferType");

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ModelUtilities.class);

//    /**
//     * @param et the entity type to query
//     * @return null if et == null
//     * @deprecated Use {@link TEntityType#getWinerysPropertiesDefinition()}
//     */
//    @Deprecated
//    public static WinerysPropertiesDefinition getWinerysPropertiesDefinition(TEntityType et) {
//        if (et != null) {
//            return et.getWinerysPropertiesDefinition();
//        }
//        return null;
//    }

//    /**
//     * Generates a XSD when Winery's K/V properties are used. This method is put here instead of
//     * WinerysPropertiesDefinitionResource to avoid generating the subresource
//     * <p>
//     * public because of the usage by TOSCAEXportUtil
//     *
//     * @return empty Document, if Winery's Properties Definition is not fully filled (e.g., no wrapping element defined)
//     */
//    public static Document getWinerysPropertiesDefinitionXsdAsDocument(WinerysPropertiesDefinition wpd) {
//        /*
//         * This is a quick hack: an XML schema container is created for each
//         * element. Smarter solution: create a hash from namespace to XML schema
//         * element and re-use that for each new element
//         * Drawback of "smarter" solution: not a single XSD file any more
//         */
//        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder docBuilder;
//        try {
//            docBuilder = docFactory.newDocumentBuilder();
//        } catch (ParserConfigurationException e) {
//            ModelUtilities.LOGGER.debug(e.getMessage(), e);
//            throw new IllegalStateException("Could not instantiate document builder", e);
//        }
//        Document doc = docBuilder.newDocument();
//
//        if (!ModelUtilities.allRequiredFieldsNonNull(wpd)) {
//            // wpd not fully filled -> valid XSD cannot be provided
//            // fallback: add comment and return "empty" document
//            Comment comment = doc.createComment("Required fields are missing in Winery's key/value properties definition.");
//            doc.appendChild(comment);
//            return doc;
//        }
//
//        // create XSD schema container
//        Element schemaElement = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "schema");
//        doc.appendChild(schemaElement);
//        schemaElement.setAttribute("elementFormDefault", "qualified");
//        schemaElement.setAttribute("attributeFormDefault", "unqualified");
//        schemaElement.setAttribute("targetNamespace", wpd.getNamespace());
//
//        // create XSD element itself
//        Element el = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
//        schemaElement.appendChild(el);
//        el.setAttribute("name", wpd.getElementName());
//        Element el2 = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "complexType");
//        el.appendChild(el2);
//        el = el2;
//        el2 = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "sequence");
//        el.appendChild(el2);
//        el = el2;
//
//        // currently, "xsd" is a hardcoded prefix in the type definition
//        el.setAttribute("xmlns:xsd", XMLConstants.W3C_XML_SCHEMA_NS_URI);
//
//        for (PropertyDefinitionKV prop : wpd.getPropertyDefinitionKVList()) {
//            el2 = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
//            el.appendChild(el2);
//            el2.setAttribute("name", prop.getKey());
//            // prop.getType has the prefix included
//            el2.setAttribute("type", prop.getType());
//        }
//
//        return doc;
//    }
//
//    /**
//     * Removes an existing Winery's Properties definition. If no such definition exists, the TEntityType is not
//     * modified
//     */
//    public static void removeWinerysPropertiesDefinition(TEntityType et) {
//        for (Iterator<Object> iterator = et.getAny().iterator(); iterator.hasNext(); ) {
//            Object o = iterator.next();
//            if (o instanceof WinerysPropertiesDefinition) {
//                iterator.remove();
//                break;
//            }
//        }
//    }
//
//    public static void replaceWinerysPropertiesDefinition(TEntityType et, WinerysPropertiesDefinition wpd) {
//        ModelUtilities.removeWinerysPropertiesDefinition(et);
//        et.getAny().add(wpd);
//    }

    /**
     * Determines a color belonging to the given name
     */
    public static String getColor(String name) {
        int hash = name.hashCode();
        // trim to 3*8=24 bits
        hash = hash & 0xFFFFFF;
        // check if color is more than #F0F0F0, i.e., too light
        if (((hash & 0xF00000) >= 0xF00000) && (((hash & 0x00F000) >= 0x00F000) && ((hash & 0x0000F0) >= 0x0000F0))) {
            // set one high bit to zero for each channel. That makes the overall color darker
            hash = hash & 0xEFEFEF;
        }
        return String.format("#%06x", hash);
    }

    public static String getBorderColor(XTNodeType nt) {
        String borderColor = nt.getOtherAttributes().get(QNames.QNAME_BORDER_COLOR);
        if (borderColor == null) {
            borderColor = getColor(nt.getName());
        }
        return borderColor;
    }

    public static String getColor(XTRelationshipType rt) {
        String color = rt.getOtherAttributes().get(QNames.QNAME_COLOR);
        if (color == null) {
            color = getColor(rt.getName());
        }
        return color;
    }

    /**
     * Returns the Properties. If no properties exist, the element is created
     */
    public static XTBoundaryDefinitions.Properties getProperties(XTBoundaryDefinitions defs) {
        XTBoundaryDefinitions.Properties properties = defs.getProperties();
        if (properties == null) {
            properties = new XTBoundaryDefinitions.Properties();
            defs.setProperties(properties);
        }
        return properties;
    }

    /**
     * Special method to get the name of an extensible element as the TOSCA specification does not have a separate super
     * type for elements with a name
     * <p>
     *
     * @param e the extensible element offering a name attribute (besides an id attribute)
     * @return the name of the extensible element
     * @throws IllegalStateException if e does not offer the method "getName"
     */
    public static String getName(XTExtensibleElements e) {
        Method method;
        Object res;
        try {
            method = e.getClass().getMethod("getName");
            res = method.invoke(e);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return (String) res;
    }

    /**
     * Returns the name of the given element. If the name does not exist or is empty, the id is returned
     * <p>
     * {@link #getName}
     *
     * @return the name if there is a name field, if not, the id is returned. In case there is a Name field,
     */
    public static String getNameWithIdFallBack(XTExtensibleElements ci) {
        Method method;
        String res = null;
        //noinspection EmptyCatchBlock
        try {
            method = ci.getClass().getMethod("getName");
            res = (String) method.invoke(ci);
        } catch (Exception e) {
        }
        if (res == null) {
            try {
                method = ci.getClass().getMethod("getId");
                res = (String) method.invoke(ci);
            } catch (Exception e2) {
                throw new IllegalStateException(e2);
            }
        }
        return res;
    }

    /**
     * Special method to set the name of an extensible element as the TOSCA specification does not have a separate super
     * type for elements with a name
     *
     * @param e    the extensible element offering a name attribute (besides an id attribute)
     * @param name the new name
     * @throws IllegalStateException if e does not offer the method "getName"
     */
    public static void setName(XTExtensibleElements e, String name) {
        Method method;
        try {
            method = e.getClass().getMethod("setName", String.class);
            method.invoke(e, name);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

//    public static boolean allRequiredFieldsNonNull(WinerysPropertiesDefinition wpd) {
//        boolean valid = wpd.getNamespace() != null;
//        valid = valid && (wpd.getElementName() != null);
//        if (valid) {
//            PropertyDefinitionKVList propertyDefinitionKVList = wpd.getPropertyDefinitionKVList();
//            valid = (propertyDefinitionKVList != null);
//            if (valid) {
//                for (PropertyDefinitionKV def : propertyDefinitionKVList) {
//                    valid = valid && (def.getKey() != null);
//                    valid = valid && (def.getType() != null);
//                }
//            }
//        }
//        return valid;
//    }

    public static Optional<Integer> getLeft(XTNodeTemplate nodeTemplate) {
        Map<QName, String> otherAttributes = nodeTemplate.getOtherAttributes();
        String x = otherAttributes.get(new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "x"));
        if (x == null) {
            return Optional.empty();
        }
        Float floatValue;
        try {
            floatValue = Float.parseFloat(x);
        } catch (NumberFormatException e) {
            LOGGER.debug("Could not parse x value", e);
            return Optional.empty();
        }
        return Optional.of(floatValue.intValue());
    }

    /**
     * @return null if no explicit left is set
     */
    public static String getTop(XTNodeTemplate nodeTemplate) {
        Map<QName, String> otherAttributes = nodeTemplate.getOtherAttributes();
        return otherAttributes.get(new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "y"));
    }

    /**
     * locates targetObjectRef inside a topology template
     *
     * @param topologyTemplate the topology template to search in
     * @param targetObjectRef  the object ref as String
     * @return null if not found, otherwise the entity template in the topology
     */
    public static XTEntityTemplate findNodeTemplateOrRequirementOfNodeTemplateOrCapabilityOfNodeTemplateOrRelationshipTemplate(XTTopologyTemplate topologyTemplate, String targetObjectRef) {
        // We cannot use XMLs id pointing capabilities as we work on the Java model
        // Other option: modify the stored XML directly. This is more error prune than walking through the whole topology
        for (XTEntityTemplate t : topologyTemplate.getNodeTemplateOrRelationshipTemplate()) {
            if (t instanceof XTNodeTemplate) {
                if (t.getId().equals(targetObjectRef)) {
                    return t;
                }
                XTNodeTemplate nt = (XTNodeTemplate) t;

                XTNodeTemplate.Requirements requirements = nt.getRequirements();
                if (requirements != null) {
                    for (XTRequirement req : requirements.getRequirement()) {
                        if (req.getId().equals(targetObjectRef)) {
                            return req;
                        }
                    }
                }

                XTNodeTemplate.Capabilities capabilities = nt.getCapabilities();
                if (capabilities != null) {
                    for (XTCapability cap : capabilities.getCapability()) {
                        if (cap.getId().equals(targetObjectRef)) {
                            return cap;
                        }
                    }
                }
            } else {
                assert (t instanceof XTRelationshipTemplate);
                if (t.getId().equals(targetObjectRef)) {
                    return t;
                }
            }
        }

        // no return hit inside the loop: nothing was found
        return null;
    }

    /**
     * Returns the id of the given element
     * <p>
     * The TOSCA specification does NOT always put an id field. In the case of EntityTypes and
     * EntityTypeImplementations, there is no id, but a name field
     * <p>
     * This method abstracts from that fact.
     */
    public static String getId(XTExtensibleElements ci) {
        Method method;
        Object res;
        try {
            method = ci.getClass().getMethod("getId");
            res = method.invoke(ci);
        } catch (Exception e) {
            // If no "getId" method is there, we try "getName"
            try {
                method = ci.getClass().getMethod("getName");
                res = method.invoke(ci);
            } catch (Exception e2) {
                throw new IllegalStateException(e2);
            }
        }
        return (String) res;
    }

    /**
     * Resolves a given id as requirement in the given ServiceTemplate
     *
     * @return null if not found
     */
    public static XTRequirement resolveRequirement(XTServiceTemplate serviceTemplate, String reference) {
        XTRequirement resolved = null;
        for (XTEntityTemplate tmpl : serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate()) {
            if (tmpl instanceof XTNodeTemplate) {
                XTNodeTemplate n = (XTNodeTemplate) tmpl;
                XTNodeTemplate.Requirements requirements = n.getRequirements();
                if (requirements != null) {
                    for (XTRequirement req : n.getRequirements().getRequirement()) {
                        if (req.getId().equals(reference)) {
                            resolved = req;
                        }
                    }
                }
            }
        }
        return resolved;
    }

    public static XTCapability resolveCapability(XTServiceTemplate serviceTemplate, String reference) {
        XTCapability resolved = null;
        for (XTEntityTemplate tmpl : serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate()) {
            if (tmpl instanceof XTNodeTemplate) {
                XTNodeTemplate n = (XTNodeTemplate) tmpl;
                XTNodeTemplate.Capabilities capabilities = n.getCapabilities();
                if (capabilities != null) {
                    for (XTCapability cap : n.getCapabilities().getCapability()) {
                        if (cap.getId().equals(reference)) {
                            resolved = cap;
                        }
                    }
                }
            }
        }
        return resolved;
    }

    public static XTNodeTemplate resolveNodeTemplate(XTServiceTemplate serviceTemplate, String reference) {
        XTNodeTemplate resolved = null;
        for (XTEntityTemplate tmpl : serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate()) {
            if (tmpl instanceof XTNodeTemplate) {
                XTNodeTemplate n = (XTNodeTemplate) tmpl;
                if (n.getId().equals(reference)) {
                    resolved = n;
                }
            }
        }
        return resolved;
    }

    public static XTRelationshipTemplate resolveRelationshipTemplate(XTServiceTemplate serviceTemplate, String reference) {
        XTRelationshipTemplate resolved = null;
        for (XTEntityTemplate tmpl : serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate()) {
            if (tmpl instanceof XTRelationshipTemplate) {
                XTRelationshipTemplate n = (XTRelationshipTemplate) tmpl;
                if (n.getId().equals(reference)) {
                    resolved = n;
                }
            }
        }
        return resolved;
    }

    public static XTPlan resolvePlan(XTServiceTemplate serviceTemplate, String reference) {
        XTPlan resolved = null;
        XTPlans plans = serviceTemplate.getPlans();
        if (plans == null) {
            return null;
        }
        for (XTPlan p : plans.getPlan()) {
            if (p.getId().equals(reference)) {
                resolved = p;
            }
        }
        return resolved;
    }

    /**
     * This method instantiates a {@link XTNodeTemplate} for a given {@link XTNodeType}.
     *
     * @param nodeType the {@link XTNodeType} used for the {@link XTNodeTemplate} instantiation.
     * @return the instantiated {@link XTNodeTemplate}
     */
    public static XTNodeTemplate instantiateNodeTemplate(XTNodeType nodeType) {

        XTNodeTemplate.Builder nodeTemplate = new XTNodeTemplate.Builder(UUID.randomUUID().toString(), new QName(nodeType.getTargetNamespace(), nodeType.getName()));

        nodeTemplate.setName(nodeType.getName());

        // add capabilities to the NodeTemplate
        if (nodeType.getCapabilityDefinitions() != null) {
            for (XTCapabilityDefinition cd : nodeType.getCapabilityDefinitions().getCapabilityDefinition()) {
                XTCapability.Builder capa = new XTCapability.Builder(UUID.randomUUID().toString(),
                    new QName(cd.getCapabilityType().getNamespaceURI(), cd.getCapabilityType().getLocalPart()), cd.getCapabilityType().getLocalPart());
                nodeTemplate.setCapabilities(new XTNodeTemplate.Capabilities());
                nodeTemplate.addCapabilities(capa.build());
            }
        }

        // add requirements
        if (nodeType.getRequirementDefinitions() != null && nodeType.getRequirementDefinitions().getRequirementDefinition() != null) {
            XTNodeTemplate.Requirements requirementsNode = new XTNodeTemplate.Requirements();
            nodeTemplate.setRequirements(requirementsNode);
            for (XTRequirementDefinition definition : nodeType.getRequirementDefinitions().getRequirementDefinition()) {
                XTRequirement.Builder newRequirement = new XTRequirement.Builder(definition.getName(), definition.getRequirementType());
                nodeTemplate.addRequirements(newRequirement.build());
            }
        }

        return nodeTemplate.build();
    }

    /**
     * This method instantiates a {@link XTRelationshipTemplate} for a given {@link XTRelationshipType}.
     *
     * @param relationshipType   the {@link XTRelationshipType} used for the {@link XTRelationshipTemplate}
     *                           instantiation.
     * @param sourceNodeTemplate the source {@link XTNodeTemplate} of the connection
     * @param targetNodeTemplate the target {@link XTNodeTemplate} of the connection
     * @return the instantiated {@link XTRelationshipTemplate}
     */
    public static XTRelationshipTemplate instantiateRelationshipTemplate(XTRelationshipType relationshipType, XTNodeTemplate sourceNodeTemplate, XTNodeTemplate targetNodeTemplate) {

        // connect the NodeTemplates
        XTRelationshipTemplate.SourceOrTargetElement source = new XTRelationshipTemplate.SourceOrTargetElement();
        source.setRef(sourceNodeTemplate);
        XTRelationshipTemplate.SourceOrTargetElement target = new XTRelationshipTemplate.SourceOrTargetElement();
        target.setRef(targetNodeTemplate);

        XTRelationshipTemplate relationshipTemplate = new XTRelationshipTemplate.Builder(UUID.randomUUID().toString(), new QName(relationshipType.getTargetNamespace(), relationshipType.getName()), source, target).build();
        return relationshipTemplate;
    }

    /**
     * Target label is not present if - empty string - undefined - null Target Label is not case sensitive -> always
     * lower case.
     */
    public static Optional<String> getTargetLabel(XTNodeTemplate nodeTemplate) {
        if (nodeTemplate == null) {
            return Optional.empty();
        }
        Map<QName, String> otherAttributes = nodeTemplate.getOtherAttributes();
        String targetLabel = otherAttributes.get(QNAME_LOCATION);
        if (targetLabel != null && (targetLabel.equals("undefined") || targetLabel.equals(""))) {
            return Optional.empty();
        }
        return Optional.ofNullable(targetLabel).map(String::toLowerCase);
    }

    /**
     * Target Label is not case sensitive -> set to lowercase.
     */
    public static void setTargetLabel(XTNodeTemplate nodeTemplate, String targetLabel) {
        Objects.requireNonNull(nodeTemplate);
        Objects.requireNonNull(targetLabel);
        Map<QName, String> otherAttributes = nodeTemplate.getOtherAttributes();
        otherAttributes.put(QNAME_LOCATION, targetLabel.toLowerCase());
    }

    public static XTNodeTemplate getSourceNodeTemplateOfRelationshipTemplate(XTTopologyTemplate topologyTemplate, XTRelationshipTemplate relationshipTemplate) {
        if (relationshipTemplate.getSourceElement().getRef() instanceof XTRequirement) {
            XTRequirement requirement = (XTRequirement) relationshipTemplate.getSourceElement().getRef();
            return topologyTemplate.getNodeTemplates().stream()
                .filter(nt -> nt.getRequirements().getRequirement() != null)
                .filter(nt -> nt.getRequirements().getRequirement().contains(requirement))
                .findAny().get();
        } else {
            XTNodeTemplate sourceNodeTemplate = (XTNodeTemplate) relationshipTemplate.getSourceElement().getRef();
            return sourceNodeTemplate;
        }
    }

    public static XTNodeTemplate getTargetNodeTemplateOfRelationshipTemplate(XTTopologyTemplate topologyTemplate, XTRelationshipTemplate relationshipTemplate) {
        if (relationshipTemplate.getTargetElement().getRef() instanceof XTCapability) {
            XTCapability capability = (XTCapability) relationshipTemplate.getTargetElement().getRef();
            return topologyTemplate.getNodeTemplates().stream()
                .filter(nt -> nt.getRequirements().getRequirement() != null)
                .filter(nt -> nt.getRequirements().getRequirement().contains(capability))
                .findAny().get();
        } else {
            return (XTNodeTemplate) relationshipTemplate.getTargetElement().getRef();
        }
    }

    /**
     * @return incoming relation ship templates <em>pointing to node templates</em>
     */
    public static List<XTRelationshipTemplate> getIncomingRelationshipTemplates(XTTopologyTemplate topologyTemplate, XTNodeTemplate nodeTemplate) {
        Objects.requireNonNull(topologyTemplate);
        Objects.requireNonNull(nodeTemplate);
        List<XTRelationshipTemplate> incomingRelationshipTemplates = topologyTemplate.getRelationshipTemplates()
            .stream()
            .filter(rt -> getTargetNodeTemplateOfRelationshipTemplate(topologyTemplate, rt).equals(nodeTemplate))
            .collect(Collectors.toList());

        return incomingRelationshipTemplates;
    }

    /**
     * @return outgoing relation ship templates <em>pointing to node templates</em>
     */
    public static List<XTRelationshipTemplate> getOutgoingRelationshipTemplates(XTTopologyTemplate topologyTemplate, XTNodeTemplate nodeTemplate) {
        Objects.requireNonNull(topologyTemplate);
        Objects.requireNonNull(nodeTemplate);
        List<XTRelationshipTemplate> outgoingRelationshipTemplates = topologyTemplate.getRelationshipTemplates()
            .stream()
            .filter(rt -> getSourceNodeTemplateOfRelationshipTemplate(topologyTemplate, rt).equals(nodeTemplate))
            .collect(Collectors.toList());

        return outgoingRelationshipTemplates;
    }

    /**
     * @return all nodes templates of the topologyTemplate
     * @deprecated Use {@link XTTopologyTemplate#getNodeTemplates()}
     */
    @Deprecated
    public static List<XTNodeTemplate> getAllNodeTemplates(XTTopologyTemplate topologyTemplate) {
        Objects.requireNonNull(topologyTemplate);

        return topologyTemplate.getNodeTemplateOrRelationshipTemplate()
            .stream()
            .filter(x -> x instanceof XTNodeTemplate)
            .map(XTNodeTemplate.class::cast)
            .collect(Collectors.toList());
    }

    /**
     * @deprecated Use {@link XTTopologyTemplate#getRelationshipTemplates()}
     */
    @Deprecated
    public static List<XTRelationshipTemplate> getAllRelationshipTemplates(XTTopologyTemplate topologyTemplate) {
        Objects.requireNonNull(topologyTemplate);

        return topologyTemplate.getNodeTemplateOrRelationshipTemplate()
            .stream()
            .filter(x -> x instanceof XTRelationshipTemplate)
            .map(XTRelationshipTemplate.class::cast)
            .collect(Collectors.toList());
    }

    /**
     * When sending JSON to the server, the content of "any" is a String and not some JSON data structure. To be able to
     * save it as XML, we have to "objectize" the content of Any
     *
     * @param templates The templates (node, relationship) to update. The content of the given collection is modified.
     * @throws IllegalStateException if DocumentBuilder could not iniitialized
     * @throws IOException           if something goes wrong during parsing
     */
    public static void patchAnyAttributes(Collection<? extends XTEntityTemplate> templates) throws IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOGGER.error("Could not initialize document builder", e);
            throw new IllegalStateException("Could not initialize document builder", e);
        }

        Map<QName, String> tempConvertedOtherAttributes = new HashMap<>();

        for (XTEntityTemplate template : templates) {

            //Convert the wrong QName created by the JSON serialization back to a right QName
            for (Map.Entry<QName, String> otherAttribute : template.getOtherAttributes().entrySet()) {
                QName qName;
                String localPart = otherAttribute.getKey().getLocalPart();
                if (localPart.startsWith("{")) {
                    // QName is stored as plain string - this is the case when nested in "any"
                    qName = QName.valueOf(localPart);
                } else {
                    // sometimes, the QName is retrieved properly. So, we just keep it. This is the case when directly nested in nodetemplate's JSON
                    qName = new QName(otherAttribute.getKey().getNamespaceURI(), localPart);
                }
                tempConvertedOtherAttributes.put(qName, otherAttribute.getValue());
            }
            template.getOtherAttributes().clear();
            template.getOtherAttributes().putAll(tempConvertedOtherAttributes);
            tempConvertedOtherAttributes.clear();

            // Convert the String created by the JSON serialization back to a XML dom document
            XTEntityTemplate.Properties properties = template.getProperties();
            if (properties != null) {
                Object any = properties.getAny();
                if (any instanceof String) {
                    Document doc = null;
                    try {
                        doc = documentBuilder.parse(new InputSource(new StringReader((String) any)));
                    } catch (SAXException e) {
                        LOGGER.error("Could not parse", e);
                        throw new IOException("Could not parse", e);
                    } catch (IOException e) {
                        LOGGER.error("Could not parse", e);
                        throw e;
                    }
                    template.getProperties().setAny(doc.getDocumentElement());
                }
            }
        }
    }

    public static boolean isOfType(QName requiredType, QName givenType, Map<QName, ? extends XTEntityType> elements) {
        if (!givenType.equals(requiredType)) {
            XTEntityType entityType = elements.get(givenType);
            if (Objects.isNull(entityType) || Objects.isNull(entityType.getDerivedFrom())) {
                return false;
            } else {
                return isOfType(requiredType, entityType.getDerivedFrom().getTypeAsQName(), elements);
            }
        }
        return true;
    }

    public static <T extends XTEntityType> Map<QName, T> getChildrenOf(QName givenType, Map<QName, T> elements) {
        HashMap<QName, T> children = new HashMap<>();
        XTEntityType entityType = elements.get(givenType);
        if (Objects.nonNull(entityType)) {
            elements.forEach((qName, type) -> {
                if (!qName.equals(givenType) && isOfType(givenType, qName, elements)) {
                    children.put(qName, type);
                }
            });
        }
        return children;
    }

    public static <T extends XTEntityType> Map<T, String> getAvailableFeaturesOfType(QName givenType, Map<QName, T> elements) {
        HashMap<T, String> features = new HashMap<>();
        getChildrenOf(givenType, elements).forEach((qName, t) -> {
            if (Objects.nonNull(t.getTags())) {
                List<XTTag> list = t.getTags().getTag();
                list.stream()
                    .filter(tag -> "feature".equals(tag.getName()))
                    .findFirst()
                    .ifPresent(tTag -> features.put(elements.get(qName), tTag.getValue()));
            }
        });
        return features;
    }

    public static <T extends XTEntityType> boolean isFeatureType(QName givenType, Map<QName, T> elements) {
        return Objects.nonNull(elements.get(givenType))
            && Objects.nonNull(elements.get(givenType).getTags())
            && elements.get(givenType).getTags().getTag().stream()
            .anyMatch(tag -> "feature".equals(tag.getName()));
    }

    public static void updateNodeTemplate(XTTopologyTemplate topology, String oldComponentId, QName newType, XTNodeType newComponentType) {
        XTNodeTemplate nodeTemplate = topology.getNodeTemplate(oldComponentId);
        nodeTemplate.setType(newType);
        nodeTemplate.setName(newType.getLocalPart());
        // TODO: also make some more adjustments etc.
    }

    /**
     * This is specific to the TOSCA hostedOn relationship type.
     */
    public static ArrayList<XTNodeTemplate> getHostedOnSuccessors(XTTopologyTemplate topologyTemplate, String nodeTemplate) {
        return getHostedOnSuccessors(topologyTemplate, topologyTemplate.getNodeTemplate(nodeTemplate));
    }

    public static ArrayList<XTNodeTemplate> getHostedOnSuccessors(XTTopologyTemplate topologyTemplate, XTNodeTemplate nodeTemplate) {
        ArrayList<XTNodeTemplate> hostedOnSuccessors = new ArrayList<>();

        Optional<XTRelationshipTemplate> hostedOn;

        do {
            List<XTRelationshipTemplate> outgoingRelationshipTemplates = getOutgoingRelationshipTemplates(topologyTemplate, nodeTemplate);

            hostedOn = outgoingRelationshipTemplates.stream()
                .filter(relation -> relation.getType().equals(ToscaBaseTypes.hostedOnRelationshipType))
                .findFirst();
            if (hostedOn.isPresent()) {
                nodeTemplate = getNodeTemplateFromRelationshipSourceOrTarget(topologyTemplate, hostedOn.get().getTargetElement().getRef());
                hostedOnSuccessors.add(nodeTemplate);
            }
        } while (hostedOn.isPresent());

        return hostedOnSuccessors;
    }

    /**
     * Returns the referenced TNodeTemplate of a TRelationshipTemplate which internally uses a
     * RelationshipSourceOrTarget element. to point to the respective TNodeTemplate. If the referenced TNodeTemplate
     * cannot be found, a NullPointerException is thrown.
     *
     * @param topologyTemplate           the TTopologyTemplate the TNodeTemplate and TRelationshipTemplate are contained
     *                                   in.
     * @param relationshipSourceOrTarget the source or target element the relationship points to.
     * @return the actual TNodeTemplate the TRelationshipTemplate is referring to.
     */
    public static XTNodeTemplate getNodeTemplateFromRelationshipSourceOrTarget(XTTopologyTemplate topologyTemplate, XRelationshipSourceOrTarget relationshipSourceOrTarget) {
        Optional<XTNodeTemplate> nodeTemplate = Optional.empty();

        if (relationshipSourceOrTarget instanceof XTNodeTemplate) {
            nodeTemplate = Optional.of((XTNodeTemplate) relationshipSourceOrTarget);
        } else if (relationshipSourceOrTarget instanceof XTCapability) {
            nodeTemplate = topologyTemplate.getNodeTemplates().stream()
                .filter(node -> Objects.nonNull(node.getCapabilities()))
                .filter(node ->
                    node.getCapabilities()
                        .getCapability().stream().anyMatch(capability -> capability.getId().equals(relationshipSourceOrTarget.getId())
                    )
                ).findFirst();
        } else if (relationshipSourceOrTarget instanceof XTRequirement) {
            nodeTemplate = topologyTemplate.getNodeTemplates().stream()
                .filter(node -> Objects.nonNull(node.getRequirements()))
                .filter(node ->
                    node.getRequirements()
                        .getRequirement().stream().anyMatch(requirement -> requirement.getId().equals(relationshipSourceOrTarget.getId())
                    )
                ).findFirst();
        }

        return nodeTemplate.orElseThrow(NullPointerException::new);
    }

    public static void collectIdsOfExistingTopologyElements(XTTopologyTemplate topologyTemplateB, Map<String, String> idMapping) {
        // collect existing node & relationship template ids
        topologyTemplateB.getNodeTemplateOrRelationshipTemplate()
            // the existing ids are left unchanged
            .forEach(x -> idMapping.put(x.getId(), x.getId()));

        // collect existing requirement ids
        topologyTemplateB.getNodeTemplates().stream()
            .filter(nt -> nt.getRequirements() != null)
            .forEach(nt -> nt.getRequirements().getRequirement()
                // the existing ids are left unchanged
                .forEach(x -> idMapping.put(x.getId(), x.getId())));

        //collect existing capability ids
        topologyTemplateB.getNodeTemplates().stream()
            .filter(nt -> nt.getCapabilities() != null)
            .forEach(nt -> nt.getCapabilities().getCapability()
                // the existing ids are left unchanged
                .forEach(x -> idMapping.put(x.getId(), x.getId())));
    }

    public static void generateNewIdOfTemplate(XHasId element, XTTopologyTemplate topologyTemplate) {
        HashMap<String, String> map = new HashMap<>();
        collectIdsOfExistingTopologyElements(topologyTemplate, map);
        generateNewIdOfTemplate(element, map);
    }

    public static void generateNewIdOfTemplate(XHasId element, Map<String, String> idMapping) {
        String newId = element.getId();
        while (idMapping.containsKey(newId)) {
            newId = newId + "-new";
        }
        idMapping.put(element.getId(), newId);
        element.setId(newId);
    }

    public static XTRelationshipTemplate createRelationshipTemplate(XTNodeTemplate sourceNode, XTNodeTemplate targetNode, QName type) {
        return createRelationshipTemplate(sourceNode, targetNode, type, "");
    }

    public static XTRelationshipTemplate createRelationshipTemplate(XTNodeTemplate sourceNode, XTNodeTemplate targetNode, QName type, String connectionDescription) {
        XTRelationshipTemplate.SourceOrTargetElement sourceRef = new XTRelationshipTemplate.SourceOrTargetElement();
        sourceRef.setRef(sourceNode);
        XTRelationshipTemplate.SourceOrTargetElement targetRef = new XTRelationshipTemplate.SourceOrTargetElement();
        targetRef.setRef(targetNode);

        XTRelationshipTemplate.Builder rel = new XTRelationshipTemplate.Builder(sourceNode.getId() + "-" + connectionDescription + "-" + targetNode.getId(), type, sourceRef, targetRef);
        rel.setName(type.getLocalPart());
        return rel.build();
    }

    public static XTRelationshipTemplate createRelationshipTemplateAndAddToTopology(XTNodeTemplate sourceNode, XTNodeTemplate targetNode, QName type,
                                                                                    XTTopologyTemplate topology) {
        return createRelationshipTemplateAndAddToTopology(sourceNode, targetNode, type, "", topology);
    }

    public static XTRelationshipTemplate createRelationshipTemplateAndAddToTopology(XTNodeTemplate sourceNode, XTNodeTemplate targetNode, QName type,
                                                                                    String connectionDescription, XTTopologyTemplate topology) {
        XTRelationshipTemplate relationshipTemplate = createRelationshipTemplate(sourceNode, targetNode, type, connectionDescription);
        generateNewIdOfTemplate(relationshipTemplate, topology);
        topology.addRelationshipTemplate(relationshipTemplate);
        return relationshipTemplate;
    }

    public static boolean nodeTypeHasInterface(XTNodeType nodeType, String interfaceName) {
        return Objects.nonNull(nodeType.getInterfaces()) && nodeType.getInterfaces().getInterface().stream()
            .anyMatch(nodeInterface -> interfaceName.equals(nodeInterface.getName()));
    }

    public static void addPolicy(XTNodeTemplate node, QName policyType, String name) {
        XTPolicies policies = node.getPolicies();
        if (Objects.isNull(policies)) {
            policies = new XTPolicies();
            node.setPolicies(policies);
        }

        XTPolicy.Builder policy = new XTPolicy.Builder(policyType);
        policy.setName(name);
        policies.getPolicy()
            .add(policy.build());
    }

    public static boolean containsPolicyType(XTNodeTemplate node, QName policyType) {
        return Objects.nonNull(node.getPolicies()) &&
            node.getPolicies().getPolicy().stream()
                .anyMatch(policy -> policy.getPolicyType()
                    .equals(policyType)
                );
    }
}
