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
 *******************************************************************************/
package org.eclipse.winery.repository.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.repository.backend.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericKeyValueResource {
	
	private static final Logger logger = LoggerFactory.getLogger(GenericKeyValueResource.class);
	
	protected final Configuration configuration;
	
	
	public GenericKeyValueResource(RepositoryFileReference ref) {
		this.configuration = Repository.INSTANCE.getConfiguration(ref);
	}
	
	@GET
	@Path("{name}/")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getValue(@PathParam("name") String name) {
		name = Util.URLdecode(name);
		if (this.configuration.containsKey(name)) {
			String value = this.configuration.getString(name);
			return Response.ok(value).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	@DELETE
	@Path("{name}/")
	public Response deleteValue(@PathParam("name") String name) {
		name = Util.URLdecode(name);
		if (this.configuration.containsKey(name)) {
			this.configuration.clearProperty(name);
			// according to http://stackoverflow.com/a/2342589/873282, HTTP 204
			// NO CONTENT should be sent instead of HTTP 200 OK if no entity is
			// included in the answer
			return Response.noContent().build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	/**
	 * @param name the key to set
	 * @param value the value to put. Contained in the body of the message.
	 * @return
	 */
	@PUT
	@Path("{name}/")
	public Response createOrUpdateValue(@PathParam("name") String name, String value) {
		this.configuration.setProperty(name, value);
		return Response.noContent().build();
	}
	
	/**
	 * Note that this is implemented differently in PropertiesDefinition. There,
	 * a JSON object is returned instead of an XML document
	 * 
	 * @return an XML following http://java.sun.com/dtd/properties.dtd
	 */
	@GET
	@Produces(MediaType.TEXT_XML)
	public Response getAllProperties() {
		final XMLPropertiesConfiguration xmlConf = new XMLPropertiesConfiguration();
		Iterator<String> keys = this.configuration.getKeys();
		// copy over the whole configuration to the xml configuration
		while (keys.hasNext()) {
			String key = keys.next();
			xmlConf.addProperty(key, this.configuration.getProperty(key));
		}
		StreamingOutput so = new StreamingOutput() {
			
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				try (Writer w = new OutputStreamWriter(output, "UTF-8");) {
					xmlConf.save(w);
				} catch (ConfigurationException e) {
					GenericKeyValueResource.logger.error(e.getMessage(), e);
				}
			}
		};
		return Response.ok().entity(so).build();
		
	}
	
	/**
	 * Returns a reference to the internal data structure. Used by the exporter
	 * for serialization
	 * 
	 * @return reference to internal data structure
	 */
	public Configuration getConfiguration() {
		return this.configuration;
	}
	
	/**
	 * Replaces the configuration values by the configuration values provided
	 * 
	 * @param configuration the configuration values that should replace the
	 *            current configuration
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration.clear();
		// we have to copy the configuration manually as Configuration does not offer copying a complete configuration
		Iterator<String> keys = configuration.getKeys();
		while (keys.hasNext()) {
			String key = keys.next();
			this.configuration.setProperty(key, configuration.getProperty(key));
		}
	}
	
}
