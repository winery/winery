/*******************************************************************************
 * Copyright (c) 2013 Pascal Hirmer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Pascal Hirmer - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.topologymodeler.addons.topologycompleter.helper;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.LoggerFactory;

/**
 * This class contains methods for marshalling and unmarshalling a topology XML string via JAXB.
 *
 */
public class JAXBHelper {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JAXBHelper.class.getName());

	/**
	 * This constant is used in the buildXML method which add coordinates to Node Templates so they
	 * are arranged properly in the Winery topology modeler.
	 *
	 * The x coordinate is constant because it is assumed that a stack of NodeTemplates is displayed.
	 */
	private static final String NODETEMPLATE_X_COORDINATE = "500";

	/**
	 * This method creates an JAXB Unmarshaller used by the methods contained in this class.
	 *
	 * @return the JAXB unmarshaller object
	 *
	 * @throws JAXBException
	 *             this exception can occur when the JAXBContext is created
	 */
	private static Unmarshaller createUnmarshaller() throws JAXBException {
		// initiate JaxB context
		JAXBContext context;
		context = JAXBContext.newInstance(Definitions.class);

		return context.createUnmarshaller();
	}

	/**
	 * This method returns a {@link TTopologyTemplate} given as XML string as JaxBObject.
	 *
	 * @param xmlString
	 *            the {@link TTopologyTemplate} to be unmarshalled
	 *
	 * @return the unmarshalled {@link TTopologyTemplate}
	 */
	public static TTopologyTemplate getTopologyAsJaxBObject(String xmlString) {
		try {

			logger.info("Getting Definitions Document...");

			StringReader reader = new StringReader(xmlString);

			// unmarshall the XML string
			Definitions jaxBDefinitions = (Definitions) createUnmarshaller().unmarshal(reader);
			TServiceTemplate serviceTemplate = (TServiceTemplate) jaxBDefinitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().get(0);

			logger.info("Unmarshalling successful! ");

			return serviceTemplate.getTopologyTemplate();

		} catch (JAXBException e) {
			logger.error(e.getLocalizedMessage());
		}
		return null;
	}

	/**
	 * This method returns {@link TRelationshipTemplate}s as a JaxBObject.
	 *
	 * @param xmlString
	 *            the {@link TRelationshipTemplate} to be unmarshalled
	 *
	 * @return the unmarshalled {@link TRelationshipTemplate}
	 */
	public static List<TRelationshipTemplate> getRelationshipTemplatesAsJaxBObject(String xmlString) {
		try {
			StringReader reader = new StringReader(xmlString);

			// unmarshall
			Definitions jaxBDefinitions = (Definitions) createUnmarshaller().unmarshal(reader);
			TServiceTemplate serviceTemplate = (TServiceTemplate) jaxBDefinitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().get(0);

			List<TRelationshipTemplate> foundRTs = new ArrayList<>();
			for (TEntityTemplate entity : serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate()) {
				if (entity instanceof TRelationshipTemplate) {
					foundRTs.add((TRelationshipTemplate) entity);
				}
			}

			return foundRTs;

		} catch (JAXBException e) {
			logger.error(e.getLocalizedMessage());
		}
		return null;

	}

	/**
	 * Turns XML Strings into {@link TEntityTemplate} objects using JaxB.
	 *
	 * @param xmlString
	 *            the XMLString to be parsed
	 * @return the parsed XMLString as {@link TEntityTemplate}
	 */
	public static List<TEntityTemplate> getEntityTemplatesAsJaxBObject(String xmlString) {
		try {
			StringReader reader = new StringReader(xmlString);

			Definitions jaxBDefinitions = (Definitions) createUnmarshaller().unmarshal(reader);
			TServiceTemplate serviceTemplate = (TServiceTemplate) jaxBDefinitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().get(0);

			return serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();

		} catch (JAXBException e) {
			logger.error(e.getLocalizedMessage());
		}
		return null;

	}

	/**
	 * Converts any object of the TOSCA data model to a JaxBObject.
	 *
	 * @param xmlString
	 *            the {@link Definitions} object to be converted
	 *
	 * @return the unmarshalled {@link Definitions} object
	 */
	public static Definitions getXasJaxBObject(String xmlString) {
		try {
			StringReader reader = new StringReader(xmlString);
			Definitions jaxBDefinitions = (Definitions) createUnmarshaller().unmarshal(reader);

			return jaxBDefinitions;

		} catch (JAXBException e) {
			logger.error(e.getLocalizedMessage());
		}
		return null;

	}

	/**
	 * This method adds a selection of {@link TNodeTemplate}- and {@link TRelationshipTemplate}-XML-Strings to a {@link TTopologyTemplate}-XML-String using JAXB.
	 * After the templates have been added, the {@link TTopologyTemplate} object is re-marshalled to an XML-String.
	 *
	 * This method is called by the selectionHandler.jsp after several Node or RelationshipTemplates have been chosen in a dialog.
	 *
	 * @param topology
	 *            the topology as XML string
	 * @param allTemplateChoicesAsXML
	 *            all possible template choices as TOSCA-XML strings containing the complete templates
	 * @param selectedNodeTemplatesAsJSON
	 *            the names of the selected NodeTemplates as JSONArray
	 * @param selectedRelationshipTemplatesAsJSON
	 *            the names of the selected RelationshipTemplates as JSONArray
	 *
	 * @return the complete topology XML string
	 */
	public static String addTemplatesToTopology(String topology, String allTemplateChoicesAsXML, String selectedNodeTemplatesAsJSON, String selectedRelationshipTemplatesAsJSON) {
		try {

			// initialization code for the jackson types used to convert JSON string arrays to a java.util.List
			ObjectMapper mapper = new ObjectMapper();
			TypeFactory factory = mapper.getTypeFactory();

			// convert the JSON array containing the names of the selected RelationshipTemplates to a java.util.List
			List<String> selectedRelationshipTemplates = mapper.readValue(selectedRelationshipTemplatesAsJSON, factory.constructCollectionType(List.class, String.class));

			// convert the topology and the choices to objects using JAXB
			TTopologyTemplate topologyTemplate = getTopologyAsJaxBObject(topology);
			List<TEntityTemplate> allTemplateChoices = getEntityTemplatesAsJaxBObject(allTemplateChoicesAsXML);

			// this distinction of cases is necessary because it is possible that only RelationshipTemplates have been selected
			if (selectedNodeTemplatesAsJSON != null) {

				// convert the JSON string array containing the names of the selected NodeTemplates to a java.util.List
				List<String> selectedNodeTemplates = mapper.readValue(selectedNodeTemplatesAsJSON, factory.constructCollectionType(List.class, String.class));

				// search the selected NodeTemplate in the List of all choices by its name to receive its object which will ne added to the topology
				for (String nodeTemplateName : selectedNodeTemplates) {
					for (TEntityTemplate choice : allTemplateChoices) {
						if (choice instanceof TNodeTemplate) {
							TNodeTemplate nodeTemplate = (TNodeTemplate) choice;
							// matching a name is usually unsafe because the uniqueness cannot be assured,
							// however similar names are not possible at this location due to the implementation of the selection dialogs
							if (nodeTemplateName.equals(nodeTemplate.getName())) {
								// add the selected NodeTemplate to the topology
								topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(nodeTemplate);

								// due to the mapping of IDs in the selection dialog, the corresponding Relationship Template of the inserted Node Template misses its SourceElement.
								// Re-add it to avoid errors.
								for (TEntityTemplate entity: topologyTemplate.getNodeTemplateOrRelationshipTemplate()) {
									if (entity instanceof TRelationshipTemplate) {
										TRelationshipTemplate relationshipTemplate = (TRelationshipTemplate) entity;
										if (relationshipTemplate.getSourceElement().getRef() == null) {
											// connect to the added NodeTemplate
											TRelationshipTemplate.SourceOrTargetElement sourceElement = new TRelationshipTemplate.SourceOrTargetElement();
											sourceElement.setRef(nodeTemplate);
											relationshipTemplate.setSourceElement(sourceElement);
										}
									}
								}
							}
						}
					}
				}

				// now search and add the selected RelationshipTemplate object connecting to the inserted NodeTemplate
				for (String relationshipTemplateName : selectedRelationshipTemplates) {
					for (TEntityTemplate toBeAdded : allTemplateChoices) {
						if (toBeAdded instanceof TRelationshipTemplate) {
							TRelationshipTemplate relationshipTemplate = (TRelationshipTemplate) toBeAdded;
							if (relationshipTemplateName.equals(relationshipTemplate.getName())) {
								topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(relationshipTemplate);
							}
						}
					}
				}

			} else {
				// in this case only Relationship Templates have been selected
				List<TRelationshipTemplate> allRelationshipTemplateChoices = JAXBHelper.getRelationshipTemplatesAsJaxBObject(allTemplateChoicesAsXML);

				// add the target Node Template to the topology which is unique due to the implementation of the selection dialog
				topologyTemplate.getNodeTemplateOrRelationshipTemplate().add((TNodeTemplate) ((TRelationshipTemplate) allRelationshipTemplateChoices.get(0)).getTargetElement().getRef());

				// search the JAXB object of the selected RelationshipTemplate and add it to the topology
				for (String relationshipTemplateName : selectedRelationshipTemplates) {
					for (TRelationshipTemplate choice : allRelationshipTemplateChoices) {
						if (relationshipTemplateName.equals(choice.getName())) {
							topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(choice);
						}
					}
				}

				for (TEntityTemplate entityTemplate : topologyTemplate.getNodeTemplateOrRelationshipTemplate()) {
					if (entityTemplate instanceof TRelationshipTemplate) {
						TRelationshipTemplate relationship = (TRelationshipTemplate) entityTemplate;

						// due to the mapping of IDs in the selection dialog, the corresponding Relationship Template of the inserted Node Template misses its SourceElement.
						// Re-add it to avoid errors.
						if (relationship.getSourceElement().getRef() == null) {
							relationship.getSourceElement().setRef((TNodeTemplate) ((TRelationshipTemplate) allRelationshipTemplateChoices.get(0)).getTargetElement().getRef());
						}
					}
				}
			}

			// re-convert the topology from a JAXB object to an XML string and return it
			Definitions definitions = new Definitions();
			TServiceTemplate st = new TServiceTemplate();
			st.setTopologyTemplate(topologyTemplate);
			definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(st);
			JAXBContext context = JAXBContext.newInstance(Definitions.class);
			Marshaller m = context.createMarshaller();
			StringWriter stringWriter = new StringWriter();

			m.marshal(definitions, stringWriter);

			return stringWriter.toString();

		} catch (JAXBException | IOException e) {
			logger.error(e.getLocalizedMessage());
		}

		return null;
	}


	/**
	 * Marshalls a JAXB object of the TOSCA model to an XML string.
	 *
	 * @param clazz
	 * 			 the class of the object
	 * @param obj
	 * 			 the object to be marshalled
	 *
	 * @return
	 */
	public static String getXMLAsString(@SuppressWarnings("rawtypes") Class clazz, Object obj) {
		try {
			@SuppressWarnings("rawtypes")
			JAXBElement rootElement = Util.getJAXBElement(clazz, obj);
			JAXBContext context = JAXBContext.newInstance(TDefinitions.class);
			Marshaller m;

			m = context.createMarshaller();

			StringWriter w = new StringWriter();
			m.marshal(rootElement, w);
			String res = w.toString();

			return res;
		} catch (JAXBException e) {
			logger.error(e.getLocalizedMessage());
		}
		return null;
	}

	/**
	 * This methods alters the XML with JAXB so it can be imported in Winery. This is necessary because Winery needs additional information for the position of the templates in the
	 * Winery-Modeler-UI.
	 *
	 * This code is adapted from the org.eclipse.winery.repository.Utils.getXMLAsString() method.
	 *
	 * @param topology
	 *            the {@link TTopologyTemplate} to be altered
	 *
	 * @return the altered {@link TTopologyTemplate}
	 */
	public static TTopologyTemplate buildXML(TTopologyTemplate topology) {

		// the coordinate of the NodeTemplate in Winery. Begin 100 pixel from the top to improve arrangement.
		int yCoordinates = 100;

		for (TEntityTemplate template : topology.getNodeTemplateOrRelationshipTemplate()) {
			// add node templates
			if (template instanceof TNodeTemplate) {

				TNodeTemplate nodeTemplate = (TNodeTemplate) template;

				// remove the Requirements tag if necessary
				if (nodeTemplate.getRequirements() != null && nodeTemplate.getRequirements().getRequirement() == null) {
					nodeTemplate.setRequirements(null);
				}

				ModelUtilities.setLeft(nodeTemplate, NODETEMPLATE_X_COORDINATE);
				ModelUtilities.setTop(nodeTemplate, Integer.toString(yCoordinates));

				yCoordinates += 150;
			}
		}

		return topology;
	}
}
