/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Lukas Harzenetter - JSON
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytemplates;

import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.propertydefinitionkv.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.GetType;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.AbstractComponentInstanceResource;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class PropertiesResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesResource.class);

	private AbstractComponentInstanceResource res;
	private TEntityTemplate template;


	/**
	 * @param template the template to store the definitions at
	 * @param res      the resource to save after modifications
	 */
	public PropertiesResource(TEntityTemplate template, AbstractComponentInstanceResource res) {
		this.template = template;
		this.res = res;
	}

	@PUT
	@Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
	public Response setProperties(TEntityTemplate.Properties properties) {
		this.template.setProperties(properties);
		return RestUtils.persist(this.res);
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setProperties(Properties properties) {
		TEntityType type = GetType.getType(RepositoryFactory.getRepository(), this.template);
		WinerysPropertiesDefinition wpd = ModelUtilities.getWinerysPropertiesDefinition(type);
		ModelUtilities.setPropertiesKV(wpd, this.template, properties);
		return RestUtils.persist(this.res);
	}

	/**
	 * Gets the defined properties. If no properties are defined, an empty JSON object is returned. If k/v properties
	 * are defined, then a JSON is returned. Otherwise an XML is returned.
	 *
	 * @return Key/Value map in the case of Winery WPD mode - else instance of XML Element in case of non-key/value
	 * properties
	 */
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
	public @NonNull Response getProperties() {
		TEntityType tempType = RepositoryFactory.getRepository().getTypeForTemplate(this.template);
		WinerysPropertiesDefinition wpd = tempType.getWinerysPropertiesDefinition();
		TEntityTemplate.Properties props = this.template.getProperties();
		if (wpd == null) {
			// no Winery special treatment, just return the XML properties
			// These can be null resulting in 200 No Content at the caller
			if (props == null) {
				return Response.ok().entity("{}").type(MediaType.APPLICATION_JSON).build();
			} else {
				@Nullable final Object any = props.getAny();
				if (any == null) {
					LOGGER.debug("XML properties expected, but none found. Returning empty JSON.");
					return Response.ok().entity("{}").type(MediaType.APPLICATION_JSON).build();
				}
				return Response.ok().entity(Util.getXMLAsString((Element) any)).type(MediaType.TEXT_XML).build();
			}
		} else {
			Properties properties;
			if (props == null) {
				// ensure that always empty data is returned
				properties = new Properties();
			} else {
				properties = props.getKVProperties();
			}
			// iterate on all defined properties and add them if necessary
			for (PropertyDefinitionKV propdef : wpd.getPropertyDefinitionKVList()) {
				String key = propdef.getKey();
				String value = properties.getProperty(key);
				if (value == null) {
					// render null as ""
					properties.put(key, "");
				} else {
					properties.put(key, value);
				}
			}
			return Response.ok().entity(properties).type(MediaType.APPLICATION_JSON).build();
		}
	}
}
