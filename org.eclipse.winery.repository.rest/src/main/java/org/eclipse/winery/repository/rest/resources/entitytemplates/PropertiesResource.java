/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
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

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.propertydefinitionkv.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.AbstractComponentInstanceResource;

public class PropertiesResource {

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

	/**
	 * Currently, properties can only be updated as a whole XML fragment
	 *
	 * Getting/setting a fragment of properties is not possible yet
	 */
	@PUT
	@Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
	public Response setProperties(TEntityTemplate.Properties properties) {
		this.template.setProperties(properties);
		return RestUtils.persist(this.res);
	}

	/**
	 * @return Key/Value map in the case of Winery WPD mode - else instance of XML Element in case of non-key/value properties
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Object getJson() {
		TEntityType tempType = RestUtils.getTypeForTemplate(this.template);
		WinerysPropertiesDefinition wpd = ModelUtilities.getWinerysPropertiesDefinition(tempType);
		TEntityTemplate.Properties props = this.template.getProperties();
		if (wpd == null) {
			// no Winery special treatment, just return the XML properties
			return props;
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
			return properties;
		}

	}
}
