/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
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

import java.lang.reflect.Method;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.model.tosca.TBoolean;
import org.eclipse.winery.model.tosca.TEntityType.DerivedFrom;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.apiData.AvailableSuperclassesApiData;
import org.eclipse.winery.repository.resources.apiData.InheritanceResourceApiData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Models a component instance with name, derived from, abstract, and final <br />
 * Tags are provided by AbstractComponentInstanceResource
 * <p>
 * This class mirrors
 * AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinalConfigurationBacked.
 * We did not include interfaces as the getters are currently only called at the jsp.
 */
public abstract class AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal extends AbstractComponentInstanceResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal.class);

	protected AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal(TOSCAComponentId id) {
		super(id);
	}

	/**
	 * @return The associated name of this resource. CSDPR01 foresees a NCName name and no ID for an entity type.
	 * Therefore, we use the ID as unique identification and convert it to a name when a read request is put.
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

	/**
	 * @return resource managing abstract, final, derivedFrom
	 */
	@Path("inheritance/")
	public InheritanceResource getInheritanceManagement() {
		return new InheritanceResource(this);
	}

	@GET
	@Path("getAvailableSuperClasses")
	@Produces(MediaType.APPLICATION_JSON)
	public AvailableSuperclassesApiData getAvailableSuperClasses() {
		return new AvailableSuperclassesApiData(this);
	}

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

	/**
	 * @param methodName the method to call: getAbstract|getFinal
	 * @return {@inheritDoc}
	 */
	public String getTBoolean(String methodName) {
		// see getAvailableSuperClasses for verbose comments
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
	 * @return Response
	 */
	Response putInheritance(InheritanceResourceApiData json) {
		// see getAvailableSuperClasses for verbose comments
		DerivedFrom derivedFrom = null;
		Method method;

		// If (none) is selected, derivedFrom needs to be null in order to have valid XML in ALL cases!
		if (!json.derivedFrom.endsWith("(none)")) {
			QName qname = QName.valueOf(json.derivedFrom);
			derivedFrom = new DerivedFrom();
			derivedFrom.setTypeRef(qname);
		}

		try {
			method = this.getElement().getClass().getMethod("setDerivedFrom", DerivedFrom.class);
			method.invoke(this.getElement(), derivedFrom);
			method = this.getElement().getClass().getMethod("setAbstract", TBoolean.class);
			method.invoke(this.getElement(), TBoolean.fromValue(json.isAbstract));
			method = this.getElement().getClass().getMethod("setFinal", TBoolean.class);
			method.invoke(this.getElement(), TBoolean.fromValue(json.isFinal));
		} catch (ClassCastException e) {
			AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal.LOGGER.error("Seems that *Implementation is now Definitions backed, but not yet fully implemented", e);
			throw new IllegalStateException(e);
		} catch (Exception e) {
			AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal.LOGGER.error("Could not set inheritance resource", e);
			throw new IllegalStateException(e);
		}
		return BackendUtils.persist(this);
	}
}
