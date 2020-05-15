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

package org.eclipse.winery.model.tosca.utils;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.HasId;
import org.eclipse.winery.model.tosca.RelationshipSourceOrTarget;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate.Capabilities;
import org.eclipse.winery.model.tosca.TNodeTemplate.Requirements;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlans;
import org.eclipse.winery.model.tosca.TPolicies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate.SourceOrTargetElement;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.model.tosca.constants.QNames;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKVList;
import org.eclipse.winery.model.tosca.kvproperties.WinerysPropertiesDefinition;

import org.slf4j.LoggerFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

    /**
     * @param et the entity type to query
     * @return null if et == null
     * @deprecated Use {@link TEntityType#getWinerysPropertiesDefinition()}
     */
    @Deprecated
    public static WinerysPropertiesDefinition getWinerysPropertiesDefinition(TEntityType et) {
        if (et != null) {
            return et.getWinerysPropertiesDefinition();
        }
        return null;
    }

    /**
     * This is a special method for Winery. Winery allows to define a property by specifying name/value values. Instead
     * of parsing the XML contained in TNodeType, this method is a convenience method to access this information
     * <p>
     * The return type "Properties" is used because of the key/value properties.
     *
     * @param template the node template to get the associated properties
     */
    public static LinkedHashMap<String, String> getPropertiesKV(TEntityTemplate template) {
        if (template.getProperties() instanceof TEntityTemplate.WineryKVProperties) {
            return ((TEntityTemplate.WineryKVProperties) template.getProperties()).getKvProperties();
        } else {
            // FIXME is this appropriate for YAML properties??
            return null;
        }
    }
    
    public static void setPropertiesKV(TEntityTemplate template, LinkedHashMap<String, String> properties) {
        if (template.getProperties() instanceof TEntityTemplate.WineryKVProperties) {
            ((TEntityTemplate.WineryKVProperties) template.getProperties()).setKvProperties(properties);
        }
    }

    /**
     * Generates a XSD when Winery's K/V properties are used. This method is put here instead of
     * WinerysPropertiesDefinitionResource to avoid generating the subresource
     * <p>
     * public because of the usage by TOSCAExportUtil
     *
     * @return empty Document, if Winery's Properties Definition is not fully filled (e.g., no wrapping element defined)
     */
    public static Document getWinerysPropertiesDefinitionXsdAsDocument(WinerysPropertiesDefinition wpd) {
        /*
         * This is a quick hack: an XML schema container is created for each
         * element. Smarter solution: create a hash from namespace to XML schema
         * element and re-use that for each new element
         * Drawback of "smarter" solution: not a single XSD file any more
         */
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            ModelUtilities.LOGGER.debug(e.getMessage(), e);
            throw new IllegalStateException("Could not instantiate document builder", e);
        }
        Document doc = docBuilder.newDocument();

        if (!ModelUtilities.allRequiredFieldsNonNull(wpd)) {
            // wpd not fully filled -> valid XSD cannot be provided
            // fallback: add comment and return "empty" document
            Comment comment = doc.createComment("Required fields are missing in Winery's key/value properties definition.");
            doc.appendChild(comment);
            return doc;
        }

        // create XSD schema container
        Element schemaElement = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "schema");
        doc.appendChild(schemaElement);
        schemaElement.setAttribute("elementFormDefault", "qualified");
        schemaElement.setAttribute("attributeFormDefault", "unqualified");
        schemaElement.setAttribute("targetNamespace", wpd.getNamespace());

        // create XSD element itself
        Element el = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
        schemaElement.appendChild(el);
        el.setAttribute("name", wpd.getElementName());
        Element el2 = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "complexType");
        el.appendChild(el2);
        el = el2;
        el2 = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "sequence");
        el.appendChild(el2);
        el = el2;

        // currently, "xsd" is a hardcoded prefix in the type definition
        el.setAttribute("xmlns:xsd", XMLConstants.W3C_XML_SCHEMA_NS_URI);

        for (PropertyDefinitionKV prop : wpd.getPropertyDefinitionKVList()) {
            el2 = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
            el.appendChild(el2);
            el2.setAttribute("name", prop.getKey());
            // prop.getType has the prefix included
            el2.setAttribute("type", prop.getType());
        }

        return doc;
    }

    /**
     * Removes an existing Winery's Properties definition. If no such definition exists, the TEntityType is not
     * modified
     */
    public static void removeWinerysPropertiesDefinition(TEntityType et) {
        for (Iterator<Object> iterator = et.getAny().iterator(); iterator.hasNext(); ) {
            Object o = iterator.next();
            if (o instanceof WinerysPropertiesDefinition) {
                iterator.remove();
                break;
            }
        }
    }

    public static void replaceWinerysPropertiesDefinition(TEntityType et, WinerysPropertiesDefinition wpd) {
        ModelUtilities.removeWinerysPropertiesDefinition(et);
        et.getAny().add(wpd);
    }

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

    public static String getBorderColor(TNodeType nt) {
        String borderColor = nt.getOtherAttributes().get(QNames.QNAME_BORDER_COLOR);
        if (borderColor == null) {
            borderColor = getColor(nt.getName());
        }
        return borderColor;
    }

    public static String getColor(TRelationshipType rt) {
        String color = rt.getOtherAttributes().get(QNames.QNAME_COLOR);
        if (color == null) {
            color = getColor(rt.getName());
        }
        return color;
    }

    /**
     * Returns the Properties. If no properties exist, the element is created
     */
    public static org.eclipse.winery.model.tosca.TBoundaryDefinitions.Properties getProperties(TBoundaryDefinitions defs) {
        org.eclipse.winery.model.tosca.TBoundaryDefinitions.Properties properties = defs.getProperties();
        if (properties == null) {
            properties = new org.eclipse.winery.model.tosca.TBoundaryDefinitions.Properties();
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
    public static String getName(TExtensibleElements e) {
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
    public static String getNameWithIdFallBack(TExtensibleElements ci) {
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
    public static void setName(TExtensibleElements e, String name) {
        Method method;
        try {
            method = e.getClass().getMethod("setName", String.class);
            method.invoke(e, name);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static boolean allRequiredFieldsNonNull(WinerysPropertiesDefinition wpd) {
        boolean valid = wpd.getNamespace() != null;
        valid = valid && (wpd.getElementName() != null);
        if (valid) {
            PropertyDefinitionKVList propertyDefinitionKVList = wpd.getPropertyDefinitionKVList();
            valid = (propertyDefinitionKVList != null);
            if (valid) {
                for (PropertyDefinitionKV def : propertyDefinitionKVList) {
                    valid = valid && (def.getKey() != null);
                    valid = valid && (def.getType() != null);
                }
            }
        }
        return valid;
    }

    public static Optional<Integer> getLeft(TNodeTemplate nodeTemplate) {
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
    public static String getTop(TNodeTemplate nodeTemplate) {
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
    public static TEntityTemplate findNodeTemplateOrRequirementOfNodeTemplateOrCapabilityOfNodeTemplateOrRelationshipTemplate(TTopologyTemplate topologyTemplate, String targetObjectRef) {
        // We cannot use XMLs id pointing capabilities as we work on the Java model
        // Other option: modify the stored XML directly. This is more error prune than walking through the whole topology
        for (TEntityTemplate t : topologyTemplate.getNodeTemplateOrRelationshipTemplate()) {
            if (t instanceof TNodeTemplate) {
                if (t.getId().equals(targetObjectRef)) {
                    return t;
                }
                TNodeTemplate nt = (TNodeTemplate) t;

                Requirements requirements = nt.getRequirements();
                if (requirements != null) {
                    for (TRequirement req : requirements.getRequirement()) {
                        if (req.getId().equals(targetObjectRef)) {
                            return req;
                        }
                    }
                }

                Capabilities capabilities = nt.getCapabilities();
                if (capabilities != null) {
                    for (TCapability cap : capabilities.getCapability()) {
                        if (cap.getId().equals(targetObjectRef)) {
                            return cap;
                        }
                    }
                }
            } else {
                assert (t instanceof TRelationshipTemplate);
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
    public static String getId(TExtensibleElements ci) {
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
    public static TRequirement resolveRequirement(TServiceTemplate serviceTemplate, String reference) {
        TRequirement resolved = null;
        for (TEntityTemplate tmpl : serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate()) {
            if (tmpl instanceof TNodeTemplate) {
                TNodeTemplate n = (TNodeTemplate) tmpl;
                Requirements requirements = n.getRequirements();
                if (requirements != null) {
                    for (TRequirement req : n.getRequirements().getRequirement()) {
                        if (req.getId().equals(reference)) {
                            resolved = req;
                        }
                    }
                }
            }
        }
        return resolved;
    }

    public static TCapability resolveCapability(TServiceTemplate serviceTemplate, String reference) {
        TCapability resolved = null;
        for (TEntityTemplate tmpl : serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate()) {
            if (tmpl instanceof TNodeTemplate) {
                TNodeTemplate n = (TNodeTemplate) tmpl;
                Capabilities capabilities = n.getCapabilities();
                if (capabilities != null) {
                    for (TCapability cap : n.getCapabilities().getCapability()) {
                        if (cap.getId().equals(reference)) {
                            resolved = cap;
                        }
                    }
                }
            }
        }
        return resolved;
    }

    public static TNodeTemplate resolveNodeTemplate(TServiceTemplate serviceTemplate, String reference) {
        TNodeTemplate resolved = null;
        for (TEntityTemplate tmpl : serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate()) {
            if (tmpl instanceof TNodeTemplate) {
                TNodeTemplate n = (TNodeTemplate) tmpl;
                if (n.getId().equals(reference)) {
                    resolved = n;
                }
            }
        }
        return resolved;
    }

    public static TRelationshipTemplate resolveRelationshipTemplate(TServiceTemplate serviceTemplate, String reference) {
        TRelationshipTemplate resolved = null;
        for (TEntityTemplate tmpl : serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate()) {
            if (tmpl instanceof TRelationshipTemplate) {
                TRelationshipTemplate n = (TRelationshipTemplate) tmpl;
                if (n.getId().equals(reference)) {
                    resolved = n;
                }
            }
        }
        return resolved;
    }

    public static TPlan resolvePlan(TServiceTemplate serviceTemplate, String reference) {
        TPlan resolved = null;
        TPlans plans = serviceTemplate.getPlans();
        if (plans == null) {
            return null;
        }
        for (TPlan p : plans.getPlan()) {
            if (p.getId().equals(reference)) {
                resolved = p;
            }
        }
        return resolved;
    }

    /**
     * This method instantiates a {@link TNodeTemplate} for a given {@link TNodeType}.
     *
     * @param nodeType the {@link TNodeType} used for the {@link TNodeTemplate} instantiation.
     * @return the instantiated {@link TNodeTemplate}
     */
    public static TNodeTemplate instantiateNodeTemplate(TNodeType nodeType) {

        TNodeTemplate nodeTemplate = new TNodeTemplate();

        nodeTemplate.setId(UUID.randomUUID().toString());
        nodeTemplate.setName(nodeType.getName());
        nodeTemplate.setType(new QName(nodeType.getTargetNamespace(), nodeType.getName()));

        // add capabilities to the NodeTemplate
        if (nodeType.getCapabilityDefinitions() != null) {
            for (TCapabilityDefinition cd : nodeType.getCapabilityDefinitions().getCapabilityDefinition()) {
                TCapability capa = new TCapability();
                capa.setId(UUID.randomUUID().toString());
                capa.setName(cd.getCapabilityType().getLocalPart());
                capa.setType(new QName(cd.getCapabilityType().getNamespaceURI(), cd.getCapabilityType().getLocalPart()));
                nodeTemplate.setCapabilities(new Capabilities());
                nodeTemplate.getCapabilities().getCapability().add(capa);
            }
        }

        // add requirements
        if (nodeType.getRequirementDefinitions() != null && nodeType.getRequirementDefinitions().getRequirementDefinition() != null) {
            Requirements requirementsNode = new Requirements();
            nodeTemplate.setRequirements(requirementsNode);
            for (TRequirementDefinition definition : nodeType.getRequirementDefinitions().getRequirementDefinition()) {
                TRequirement newRequirement = new TRequirement();
                newRequirement.setName(definition.getName());
                newRequirement.setId(definition.getName());
                newRequirement.setType(definition.getRequirementType());
                nodeTemplate.getRequirements().getRequirement().add(newRequirement);
            }
        }

        return nodeTemplate;
    }

    /**
     * This method instantiates a {@link TRelationshipTemplate} for a given {@link TRelationshipType}.
     *
     * @param relationshipType   the {@link TRelationshipType} used for the {@link TRelationshipTemplate}
     *                           instantiation.
     * @param sourceNodeTemplate the source {@link TNodeTemplate} of the connection
     * @param targetNodeTemplate the target {@link TNodeTemplate} of the connection
     * @return the instantiated {@link TRelationshipTemplate}
     */
    public static TRelationshipTemplate instantiateRelationshipTemplate(TRelationshipType relationshipType, TNodeTemplate sourceNodeTemplate, TNodeTemplate targetNodeTemplate) {

        TRelationshipTemplate relationshipTemplate = new TRelationshipTemplate();
        relationshipTemplate.setId(UUID.randomUUID().toString());
        relationshipTemplate.setName(relationshipType.getName());
        relationshipTemplate.setType(new QName(relationshipType.getTargetNamespace(), relationshipType.getName()));

        // connect the NodeTemplates
        SourceOrTargetElement source = new SourceOrTargetElement();
        source.setRef(sourceNodeTemplate);
        relationshipTemplate.setSourceElement(source);
        SourceOrTargetElement target = new SourceOrTargetElement();
        target.setRef(targetNodeTemplate);
        relationshipTemplate.setTargetElement(target);

        return relationshipTemplate;
    }

    /**
     * Target label is not present if - empty string - undefined - null Target Label is not case sensitive -> always
     * lower case.
     */
    public static Optional<String> getTargetLabel(TNodeTemplate nodeTemplate) {
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
    public static void setTargetLabel(TNodeTemplate nodeTemplate, String targetLabel) {
        Objects.requireNonNull(nodeTemplate);
        Objects.requireNonNull(targetLabel);
        Map<QName, String> otherAttributes = nodeTemplate.getOtherAttributes();
        otherAttributes.put(QNAME_LOCATION, targetLabel.toLowerCase());
    }

    public static TNodeTemplate getSourceNodeTemplateOfRelationshipTemplate(TTopologyTemplate topologyTemplate, TRelationshipTemplate relationshipTemplate) {
        if (relationshipTemplate.getSourceElement().getRef() instanceof TRequirement) {
            TRequirement requirement = (TRequirement) relationshipTemplate.getSourceElement().getRef();
            return topologyTemplate.getNodeTemplates().stream()
                .filter(nt -> nt.getRequirements().getRequirement() != null)
                .filter(nt -> nt.getRequirements().getRequirement().contains(requirement))
                .findAny().get();
        } else {
            TNodeTemplate sourceNodeTemplate = (TNodeTemplate) relationshipTemplate.getSourceElement().getRef();
            return sourceNodeTemplate;
        }
    }

    public static TNodeTemplate getTargetNodeTemplateOfRelationshipTemplate(TTopologyTemplate topologyTemplate, TRelationshipTemplate relationshipTemplate) {
        if (relationshipTemplate.getTargetElement().getRef() instanceof TCapability) {
            TCapability capability = (TCapability) relationshipTemplate.getTargetElement().getRef();
            return topologyTemplate.getNodeTemplates().stream()
                .filter(nt -> nt.getRequirements().getRequirement() != null)
                .filter(nt -> nt.getRequirements().getRequirement().contains(capability))
                .findAny().get();
        } else {
            return (TNodeTemplate) relationshipTemplate.getTargetElement().getRef();
        }
    }

    /**
     * @return incoming relation ship templates <em>pointing to node templates</em>
     */
    public static List<TRelationshipTemplate> getIncomingRelationshipTemplates(TTopologyTemplate topologyTemplate, TNodeTemplate nodeTemplate) {
        Objects.requireNonNull(topologyTemplate);
        Objects.requireNonNull(nodeTemplate);
        List<TRelationshipTemplate> incomingRelationshipTemplates = topologyTemplate.getRelationshipTemplates()
            .stream()
            .filter(rt -> getTargetNodeTemplateOfRelationshipTemplate(topologyTemplate, rt).equals(nodeTemplate))
            .collect(Collectors.toList());

        return incomingRelationshipTemplates;
    }

    /**
     * @return outgoing relation ship templates <em>pointing to node templates</em>
     */
    public static List<TRelationshipTemplate> getOutgoingRelationshipTemplates(TTopologyTemplate topologyTemplate, TNodeTemplate nodeTemplate) {
        Objects.requireNonNull(topologyTemplate);
        Objects.requireNonNull(nodeTemplate);
        List<TRelationshipTemplate> outgoingRelationshipTemplates = topologyTemplate.getRelationshipTemplates()
            .stream()
            .filter(rt -> getSourceNodeTemplateOfRelationshipTemplate(topologyTemplate, rt).equals(nodeTemplate))
            .collect(Collectors.toList());

        return outgoingRelationshipTemplates;
    }

    /**
     * @return all nodes templates of the topologyTemplate
     * @deprecated Use {@link TTopologyTemplate#getNodeTemplates()}
     */
    @Deprecated
    public static List<TNodeTemplate> getAllNodeTemplates(TTopologyTemplate topologyTemplate) {
        Objects.requireNonNull(topologyTemplate);

        return topologyTemplate.getNodeTemplateOrRelationshipTemplate()
            .stream()
            .filter(x -> x instanceof TNodeTemplate)
            .map(TNodeTemplate.class::cast)
            .collect(Collectors.toList());
    }

    /**
     * @deprecated Use {@link TTopologyTemplate#getRelationshipTemplates()}
     */
    @Deprecated
    public static List<TRelationshipTemplate> getAllRelationshipTemplates(TTopologyTemplate topologyTemplate) {
        Objects.requireNonNull(topologyTemplate);

        return topologyTemplate.getNodeTemplateOrRelationshipTemplate()
            .stream()
            .filter(x -> x instanceof TRelationshipTemplate)
            .map(TRelationshipTemplate.class::cast)
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
    public static void patchAnyAttributes(Collection<? extends TEntityTemplate> templates) throws IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOGGER.error("Could not initialize document builder", e);
            throw new IllegalStateException("Could not initialize document builder", e);
        }

        Map<QName, String> tempConvertedOtherAttributes = new HashMap<>();

        for (TEntityTemplate template : templates) {

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
            TEntityTemplate.Properties properties = template.getProperties();
            // FIXME deal with serialization madness?
//            if (properties != null) {
//                Object any = properties.getInternalAny();
//                if (any instanceof String) {
//                    Document doc = null;
//                    try {
//                        doc = documentBuilder.parse(new InputSource(new StringReader((String) any)));
//                    } catch (SAXException e) {
//                        LOGGER.error("Could not parse", e);
//                        throw new IOException("Could not parse", e);
//                    } catch (IOException e) {
//                        LOGGER.error("Could not parse", e);
//                        throw e;
//                    }
//                    template.getProperties().setAny(doc.getDocumentElement());
//                }
//            }
        }
    }

    public static boolean isOfType(QName requiredType, QName givenType, Map<QName, ? extends TEntityType> elements) {
        if (!givenType.equals(requiredType)) {
            TEntityType entityType = elements.get(givenType);
            if (Objects.isNull(entityType) || Objects.isNull(entityType.getDerivedFrom())) {
                return false;
            } else {
                return isOfType(requiredType, entityType.getDerivedFrom().getTypeAsQName(), elements);
            }
        }
        return true;
    }

    public static <T extends TEntityType> Map<QName, T> getChildrenOf(QName givenType, Map<QName, T> elements) {
        HashMap<QName, T> children = new HashMap<>();
        TEntityType entityType = elements.get(givenType);
        if (Objects.nonNull(entityType)) {
            elements.forEach((qName, type) -> {
                if (!qName.equals(givenType) && isOfType(givenType, qName, elements)) {
                    children.put(qName, type);
                }
            });
        }
        return children;
    }

    public static <T extends TEntityType> Map<T, String> getAvailableFeaturesOfType(QName givenType, Map<QName, T> elements) {
        HashMap<T, String> features = new HashMap<>();
        getChildrenOf(givenType, elements).forEach((qName, t) -> {
            if (Objects.nonNull(t.getTags())) {
                List<TTag> list = t.getTags().getTag();
                list.stream()
                    .filter(tag -> "feature".equals(tag.getName()))
                    .findFirst()
                    .ifPresent(tTag -> features.put(elements.get(qName), tTag.getValue()));
            }
        });
        return features;
    }

    public static <T extends TEntityType> boolean isFeatureType(QName givenType, Map<QName, T> elements) {
        return Objects.nonNull(elements.get(givenType))
            && Objects.nonNull(elements.get(givenType).getTags())
            && elements.get(givenType).getTags().getTag().stream()
            .anyMatch(tag -> "feature".equals(tag.getName()));
    }

    public static void updateNodeTemplate(TTopologyTemplate topology, String oldComponentId, QName newType, TNodeType newComponentType) {
        TNodeTemplate nodeTemplate = topology.getNodeTemplate(oldComponentId);
        nodeTemplate.setType(newType);
        nodeTemplate.setName(newType.getLocalPart());
        // TODO: also make some more adjustments etc.
    }

    /**
     * This is specific to the TOSCA hostedOn relationship type.
     */
    public static ArrayList<TNodeTemplate> getHostedOnSuccessors(TTopologyTemplate topologyTemplate, String nodeTemplate) {
        return getHostedOnSuccessors(topologyTemplate, topologyTemplate.getNodeTemplate(nodeTemplate));
    }

    public static ArrayList<TNodeTemplate> getHostedOnSuccessors(TTopologyTemplate topologyTemplate, TNodeTemplate nodeTemplate) {
        ArrayList<TNodeTemplate> hostedOnSuccessors = new ArrayList<>();

        Optional<TRelationshipTemplate> hostedOn;

        do {
            List<TRelationshipTemplate> outgoingRelationshipTemplates = getOutgoingRelationshipTemplates(topologyTemplate, nodeTemplate);

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
    public static TNodeTemplate getNodeTemplateFromRelationshipSourceOrTarget(TTopologyTemplate topologyTemplate, RelationshipSourceOrTarget relationshipSourceOrTarget) {
        Optional<TNodeTemplate> nodeTemplate = Optional.empty();

        if (relationshipSourceOrTarget instanceof TNodeTemplate) {
            nodeTemplate = Optional.of((TNodeTemplate) relationshipSourceOrTarget);
        } else if (relationshipSourceOrTarget instanceof TCapability) {
            nodeTemplate = topologyTemplate.getNodeTemplates().stream()
                .filter(node -> Objects.nonNull(node.getCapabilities()))
                .filter(node ->
                    node.getCapabilities()
                        .getCapability().stream().anyMatch(capability -> capability.getId().equals(relationshipSourceOrTarget.getId())
                    )
                ).findFirst();
        } else if (relationshipSourceOrTarget instanceof TRequirement) {
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

    public static void collectIdsOfExistingTopologyElements(TTopologyTemplate topologyTemplateB, Map<String, String> idMapping) {
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

    public static void generateNewIdOfTemplate(HasId element, TTopologyTemplate topologyTemplate) {
        HashMap<String, String> map = new HashMap<>();
        collectIdsOfExistingTopologyElements(topologyTemplate, map);
        generateNewIdOfTemplate(element, map);
    }

    public static void generateNewIdOfTemplate(HasId element, Map<String, String> idMapping) {
        String newId = element.getId();
        while (idMapping.containsKey(newId)) {
            newId = newId + "-new";
        }
        idMapping.put(element.getId(), newId);
        element.setId(newId);
    }

    public static TRelationshipTemplate createRelationshipTemplate(TNodeTemplate sourceNode, TNodeTemplate targetNode, QName type) {
        return createRelationshipTemplate(sourceNode, targetNode, type, "");
    }

    public static TRelationshipTemplate createRelationshipTemplate(TNodeTemplate sourceNode, TNodeTemplate targetNode, QName type, String connectionDescription) {
        TRelationshipTemplate rel = new TRelationshipTemplate();
        rel.setType(type);
        rel.setName(type.getLocalPart());
        rel.setId(sourceNode.getId() + "-" + connectionDescription + "-" + targetNode.getId());
        rel.setSourceNodeTemplate(sourceNode);
        rel.setTargetNodeTemplate(targetNode);
        return rel;
    }

    public static TRelationshipTemplate createRelationshipTemplateAndAddToTopology(TNodeTemplate sourceNode, TNodeTemplate targetNode, QName type,
                                                                                   TTopologyTemplate topology) {
        return createRelationshipTemplateAndAddToTopology(sourceNode, targetNode, type, "", topology);
    }

    public static TRelationshipTemplate createRelationshipTemplateAndAddToTopology(TNodeTemplate sourceNode, TNodeTemplate targetNode, QName type,
                                                                                   String connectionDescription, TTopologyTemplate topology) {
        TRelationshipTemplate relationshipTemplate = createRelationshipTemplate(sourceNode, targetNode, type, connectionDescription);
        generateNewIdOfTemplate(relationshipTemplate, topology);
        topology.addRelationshipTemplate(relationshipTemplate);
        return relationshipTemplate;
    }

    public static boolean nodeTypeHasInterface(TNodeType nodeType, String interfaceName) {
        return Objects.nonNull(nodeType.getInterfaces()) && nodeType.getInterfaces().getInterface().stream()
            .anyMatch(nodeInterface -> interfaceName.equals(nodeInterface.getName()));
    }

    public static void addPolicy(TNodeTemplate node, QName policyType, String name) {
        TPolicies policies = node.getPolicies();
        if (Objects.isNull(policies)) {
            policies = new TPolicies();
            node.setPolicies(policies);
        }

        TPolicy policy = new TPolicy();
        policy.setPolicyType(policyType);
        policy.setName(name);
        policies.getPolicy()
            .add(policy);
    }

    public static boolean containsPolicyType(TNodeTemplate node, QName policyType) {
        return Objects.nonNull(node.getPolicies()) &&
            node.getPolicies().getPolicy().stream()
                .anyMatch(policy -> policy.getPolicyType()
                    .equals(policyType)
                );
    }
}
