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
 *     Lukas Harzenetter - add JSON implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources;

import java.io.IOException;
import java.io.StringWriter;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.repository.backend.Repository;

import com.sun.jersey.api.view.Viewable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for managing inheritance properties: abstract, final, derivedFromn
 *
 * The linking in the resources tree is different than the others. Here, there
 * is no additional Id generated.
 *
 * We separated the code here to have the collection of valid super types in a
 * separate class. We think, this is less confusing than including this
 * functionality in
 * AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinalDefinitionsBacked
 */
public class InheritanceResource {

	private AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal managedResource;
	private static final Logger LOGGER = LoggerFactory.getLogger(InheritanceResource.class);

	public InheritanceResource(AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal res) {
		this.managedResource = res;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable getHTML() {
		return new Viewable("/jsp/inheritance.jsp", this);
	}

	public String getIsAbstract() {
		return this.managedResource.getIsAbstract();
	}

	public String getIsFinal() {
		return this.managedResource.getIsAbstract();
	}

	public String getDerivedFrom() {
		return this.managedResource.getDerivedFrom();
	}

	/** JSP Data **/

	public SortedSet<? extends TOSCAComponentId> getPossibleSuperTypes() {
		// sorted by Name, not by namespace
		SortedSet<? extends TOSCAComponentId> allTOSCAcomponentIds = Repository.INSTANCE.getAllTOSCAComponentIds(this.managedResource.getId().getClass());
		SortedSet<? extends TOSCAComponentId> res = new TreeSet<>(allTOSCAcomponentIds);
		res.remove(this.managedResource.getId());
		// FEATURE: Possibly exclude all subtypes to avoid circles. However, this could be disappointing for users who know what they are doing
		return res;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getInheritanceManagementJSON() {
		JsonFactory jsonFactory = new JsonFactory();
		StringWriter sw = new StringWriter();
		try {
			JsonGenerator jg = jsonFactory.createGenerator(sw);
			jg.writeStartObject();
			jg.writeStringField("abstract", this.managedResource.getIsAbstract());
			jg.writeStringField("final", this.managedResource.getIsFinal());
			jg.writeFieldName("derivedFrom");
			jg.writeStartArray();
			for (TOSCAComponentId type : this.getPossibleSuperTypes()) {
				jg.writeStartObject();
				jg.writeStringField("name", type.getXmlId().toString());
				jg.writeStringField("QName", type.getQName().toString());
				if (this.managedResource.getDerivedFrom() != null) {
					jg.writeBooleanField("selected", this.managedResource.getDerivedFrom().contains(type.getQName().toString()));
				}
				jg.writeEndObject();
			}
			jg.writeEndArray();
			jg.writeEndObject();
			jg.close();
		} catch (IOException e) {
			this.LOGGER.error(e.getMessage(), e);
			return "[]";
		}

		return sw.toString();
	}
}
