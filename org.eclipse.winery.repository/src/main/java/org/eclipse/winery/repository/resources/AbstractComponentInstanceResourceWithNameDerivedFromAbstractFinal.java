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
import java.io.StringWriter;
import java.lang.reflect.Method;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.model.tosca.TBoolean;
import org.eclipse.winery.model.tosca.TEntityType.DerivedFrom;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Models a component instance with name, derived from, abstract, and final <br />
 * Tags are provided by AbstractComponentInstanceResource
 *
 * This class mirrors
 * AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinalConfigurationBacked
 * . We did not include interfaces as the getters are currently only called at
 * the jsp
 */
public abstract class AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal extends AbstractComponentInstanceResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal.class);


	protected AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal(TOSCAComponentId id) {
		super(id);
	}

	/**
	 * @return The associated name of this resource. CSDPR01 foresees a NCName
	 *         name and no ID for an entity type. Therefore, we use the ID as
	 *         unique identification and convert it to a name when a read
	 *         request is put.
	 */
	@GET
	@Path("name")
	public String getName() {
		return ModelUtilities.getName(this.getElement());
	}

	@PUT
	@Path("name")
	public Response putName(String name) {
		ModelUtilities.setName(this.getElement(), name);
		return BackendUtils.persist(this);
	}

	@GET
	@Path("derivedFrom")
	public String getDerivedFrom() {
		// TOSCA does not introduce a type like WithNameDerivedFromAbstractFinal
		// We could enumerate all possible implementing classes
		// Or use java reflection, what we're doing now.
		Method method;
		// We have three different "DerivedFrom", for NodeTypeImplementation and RelationshipTypeImplementation, we have to assign to a different "DerivedFrom"
		// This has to be done in the derived resources
		DerivedFrom derivedFrom;
		try {
			method = this.getElement().getClass().getMethod("getDerivedFrom");
			derivedFrom = (DerivedFrom) method.invoke(this.getElement());
		} catch (ClassCastException e) {
			AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal.LOGGER.error("Seems that *Implementation is now Definitions backed, but not yet fully implented", e);
			throw new IllegalStateException(e);
		} catch (Exception e) {
			AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal.LOGGER.error("Could not get derivedFrom", e);
			throw new IllegalStateException(e);
		}
		if (derivedFrom == null) {
			return null;
		}
		QName typeRef = derivedFrom.getTypeRef();
		if (typeRef == null) {
			return null;
		} else {
			return typeRef.toString();
		}
	}

	@PUT
	@Path("derivedFrom")
	public Response putDerivedFrom(String type) {
		QName qname = QName.valueOf(type);

		// see getDerivedFrom for verbose comments
		Method method;
		DerivedFrom derivedFrom = new DerivedFrom();
		derivedFrom.setTypeRef(qname);
		try {
			method = this.getElement().getClass().getMethod("setDerivedFrom", DerivedFrom.class);
			method.invoke(this.getElement(), derivedFrom);
		} catch (ClassCastException e) {
			AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal.LOGGER.error("Seems that *Implementation is now Definitions backed, but not yet fully implemented", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
		} catch (Exception e) {
			AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal.LOGGER.error("Could not set derivedFrom", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
		}

		return BackendUtils.persist(this);
	}

	/**
	 * @param methodName the method to call: getAbstract|getFinal
	 * @return {@inheritDoc}
	 */
	private String getTBoolean(String methodName) {
		// see getDerivedFrom for verbose comments
		Method method;
		TBoolean tBoolean;
		try {
			method = this.getElement().getClass().getMethod(methodName);
			tBoolean = (TBoolean) method.invoke(this.getElement());
		} catch (Exception e) {
			AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal.LOGGER.error("Could not get boolean " + methodName, e);
			throw new IllegalStateException(e);
		}
		if (tBoolean == null) {
			return null;
		} else {
			return tBoolean.value();
		}
	}

	/**
	 * @param methodName the method to call: setAbstract|setFinal
	 * @return {@inheritDoc}
	 */
	private Response putTBoolean(String tBooleanStr, String methodName) {
		// see getDerivedFrom for verbose comments

		Method method;
		TBoolean tBoolean = TBoolean.fromValue(tBooleanStr);
		try {
			method = this.getElement().getClass().getMethod(methodName, TBoolean.class);
			method.invoke(this.getElement(), tBoolean);
		} catch (Exception e) {
			AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal.LOGGER.error("Could not set tBoolean " + methodName, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
		}

		return BackendUtils.persist(this);
	}

	/**
	 * Method name is not "getAbstract" as ${it.abstract} does not work as
	 * "abstract" is not allowed at that place
	 */
	@GET
	@Path("abstract")
	public String getIsAbstract() {
		return this.getTBoolean("getAbstract");
	}

	@PUT
	@Path("abstract")
	public Response putIsAbstract(String isAbstract) {
		return this.putTBoolean(isAbstract, "setAbstract");
	}

	@GET
	@Path("final")
	public String getIsFinal() {
		return this.getTBoolean("getFinal");
	}

	@PUT
	@Path("final")
	public Response putIsFinal(String isFinal) {
		return this.putTBoolean(isFinal, "setFinal");
	}

	/**
	 * @return resource managing abstract, final, derivedFrom
	 */
	@Path("inheritance/")
	public InheritanceResource getInheritanceManagement() {
		return new InheritanceResource(this);
	}

}
