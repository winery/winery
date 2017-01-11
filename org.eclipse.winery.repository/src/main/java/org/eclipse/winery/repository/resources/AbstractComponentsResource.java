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
 *     Lukas Harzenetter - added show all items query argument
 *     Nicole Keppler - Bugfixes
 *******************************************************************************/
package org.eclipse.winery.repository.resources;

import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.backend.ResourceCreationResult;
import org.eclipse.winery.repository.resources.entitytemplates.artifacttemplates.ArtifactTemplatesResource;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.view.Viewable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource handling of a set of components. Each component has to provide a
 * class to handle the set. This is required to provide the correct instances of
 * TOSCAcomponentIds.
 *
 * TODO: Add generics here!
 * {@link Utils.getComponentIdClassForComponentContainer} is then obsolete
 */
public abstract class AbstractComponentsResource<R extends AbstractComponentInstanceResource> {

	protected static final Logger logger = LoggerFactory.getLogger(AbstractComponentsResource.class);

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getHTML(@DefaultValue("false") @QueryParam("full") boolean full) {
		return Response.ok().entity(new Viewable("/jsp/genericcomponentpage.jsp", new GenericComponentPageData(this.getClass(), full))).build();
	}

	@Path("{namespace}/")
	public ComponentsOfOneNamespaceResource getAllResourcesInNamespaceResource(@PathParam("namespace") String namespace) {
		return new ComponentsOfOneNamespaceResource(this.getClass(), namespace);
	}

	/**
	 * Creates a new component instance in the given namespace
	 *
	 * @param namespace plain namespace
	 * @param id plain id
	 */
	protected ResourceCreationResult onPost(String namespace, String name) {
		ResourceCreationResult res;
		if (StringUtils.isEmpty(namespace) || StringUtils.isEmpty(name)) {
			res = new ResourceCreationResult(Status.BAD_REQUEST);
		} else {
			String id = Utils.createXMLidAsString(name);
			TOSCAComponentId tcId;
			try {
				tcId = this.getTOSCAcomponentId(namespace, id, false);
				res = this.createComponentInstance(tcId);
				// in case the resource additionally supports a name attribute, we set the original name
				if (res.getStatus().equals(Status.CREATED)) {
					if ((tcId instanceof ServiceTemplateId) || (tcId instanceof ArtifactTemplateId) || (tcId instanceof PolicyTemplateId)) {
						// these three types have an additional name (instead of a pure id)
						// we store the name
						IHasName resource = (IHasName) AbstractComponentsResource.getComponentInstaceResource(tcId);
						resource.setName(name);
					}
				}
			} catch (Exception e) {
				AbstractComponentsResource.logger.debug("Could not create id instance", e);
				res = new ResourceCreationResult(Status.INTERNAL_SERVER_ERROR);
			}
		}
		return res;
	}

	/**
	 * Creates a new component instance in the given namespace
	 *
	 * @param namespace plain namespace
	 * @param id plain id
	 * @param ignored this parameter is ignored, but necessary for
	 *            {@link ArtifactTemplatesResource} to be able to accept the
	 *            artifact type at a post
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public Response onPost(@FormParam("namespace") String namespace, @FormParam("name") String name, String ignored) {
		ResourceCreationResult res = this.onPost(namespace, name);
		return res.getResponse();
	}

	/**
	 * Creates a TOSCAcomponentId for the given namespace / id combination
	 *
	 * Uses reflection to create a new instance
	 */
	protected TOSCAComponentId getTOSCAcomponentId(String namespace, String id, boolean URLencoded) throws Exception {
		Class<? extends TOSCAComponentId> idClass = Utils.getComponentIdClassForComponentContainer(this.getClass());
		return BackendUtils.getTOSCAcomponentId(idClass, namespace, id, URLencoded);
	}

	/**
	 * Creates a new instance of the current component
	 *
	 * @return <ul>
	 *         <li>Status.CREATED (201) if the resource has been created,</li>
	 *         <li>Status.CONFLICT if the resource already exists,</li>
	 *         <li>Status.INTERNAL_SERVER_ERROR (500) if something went wrong</li>
	 *         </ul>
	 */
	protected ResourceCreationResult createComponentInstance(TOSCAComponentId tcId) {
		return BackendUtils.create(tcId);
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends AbstractComponentInstanceResource> getComponentInstanceResourceClassForType(String type) {
		// Guess the package
		String pkg = "org.eclipse.winery.repository.resources.";

		pkg += Utils.getIntermediateLocationStringForType(type, ".");

		// naming convention: Instance is named after container, but without the
		// plural s
		String className = pkg + "." + type + "Resource";
		try {
			return (Class<? extends AbstractComponentInstanceResource>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Could not find id class for component instance", e);
		}
	}

	/**
	 *
	 * @param namespace encoded namespace
	 * @param id encoded id
	 * @return an instance of the requested resource
	 */
	@Path("{namespace}/{id}/")
	public R getComponentInstaceResource(@PathParam("namespace") String namespace, @PathParam("id") String id) {
		return this.getComponentInstaceResource(namespace, id, true);
	}

	/**
	 * @param encoded specifies whether namespace and id are encoded
	 * @return an instance of the requested resource
	 */
	@SuppressWarnings("unchecked")
	public R getComponentInstaceResource(String namespace, String id, boolean encoded) {
		TOSCAComponentId tcId;
		try {
			tcId = this.getTOSCAcomponentId(namespace, id, encoded);
		} catch (Exception e) {
			throw new IllegalStateException("Could not create id instance", e);
		}
		return (R) AbstractComponentsResource.getComponentInstaceResource(tcId);
	}

	/**
	 * @return an instance of the requested resource
	 */
	public AbstractComponentInstanceResource getComponentInstaceResource(QName qname) {
		return this.getComponentInstaceResource(qname.getNamespaceURI(), qname.getLocalPart(), false);
	}

	/**
	 * @return an instance of the requested resource
	 * @throws NotFoundException if resource doesn't exist.
	 */
	public static AbstractComponentInstanceResource getComponentInstaceResource(TOSCAComponentId tcId) {
		String type = Util.getTypeForComponentId(tcId.getClass());
		if (!Repository.INSTANCE.exists(tcId)) {
			AbstractComponentsResource.logger.debug("TOSCA component id " + tcId.toString() + " not found");
			throw new NotFoundException("TOSCA component id " + tcId.toString() + " not found");
		}
		Class<? extends AbstractComponentInstanceResource> newResource = AbstractComponentsResource.getComponentInstanceResourceClassForType(type);
		Constructor<?>[] constructors = newResource.getConstructors();
		assert (constructors.length == 1);
		AbstractComponentInstanceResource newInstance;
		try {
			newInstance = (AbstractComponentInstanceResource) constructors[0].newInstance(tcId);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			AbstractComponentsResource.logger.error("Could not instantiate sub resource " + tcId);
			throw new IllegalStateException("Could not instantiate sub resource", e);
		}
		return newInstance;
	}

	/**
	 * Returns resources for all known component instances
	 *
	 * Required by topologytemplateedit.jsp
	 */
	public Collection<AbstractComponentInstanceResource> getAll() {
		Class<? extends TOSCAComponentId> idClass = Utils.getComponentIdClassForComponentContainer(this.getClass());
		SortedSet<? extends TOSCAComponentId> allTOSCAcomponentIds = Repository.INSTANCE.getAllTOSCAComponentIds(idClass);
		ArrayList<AbstractComponentInstanceResource> res = new ArrayList<AbstractComponentInstanceResource>(allTOSCAcomponentIds.size());
		for (TOSCAComponentId id : allTOSCAcomponentIds) {
			AbstractComponentInstanceResource r = AbstractComponentsResource.getComponentInstaceResource(id);
			res.add(r);
		}
		return res;
	}

	/**
	 * Used by org.eclipse.winery.repository.repository.client and by the
	 * artifactcreationdialog.tag. Especially the "name" field is used there at
	 * the UI
	 *
	 * @return A list of all ids of all instances of this component type. If the
	 *         "name" attribute is required, that name is used as id <br />
	 *         Format:
	 *         <code>[({"namespace": "<namespace>", "id": "<id>"},)* ]</code>. A
	 *         <code>name<code> field is added if the model allows an additional name attribute
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getListOfAllIds() {
		Class<? extends TOSCAComponentId> idClass = Utils.getComponentIdClassForComponentContainer(this.getClass());
		boolean supportsNameAttribute = Util.instanceSupportsNameAttribute(idClass);
		SortedSet<? extends TOSCAComponentId> allTOSCAcomponentIds = Repository.INSTANCE.getAllTOSCAComponentIds(idClass);
		JsonFactory jsonFactory = new JsonFactory();
		StringWriter sw = new StringWriter();
		try {
			JsonGenerator jg = jsonFactory.createGenerator(sw);
			// We produce org.eclipse.winery.repository.client.WineryRepositoryClient.NamespaceAndId by hand here
			// Refactoring could move this class to common and fill it here
			jg.writeStartArray();
			for (TOSCAComponentId id : allTOSCAcomponentIds) {
				jg.writeStartObject();
				jg.writeStringField("namespace", id.getNamespace().getDecoded());
				jg.writeStringField("id", id.getXmlId().getDecoded());
				if (supportsNameAttribute) {
					AbstractComponentInstanceResource componentInstaceResource = AbstractComponentsResource.getComponentInstaceResource(id);
					String name = ((IHasName) componentInstaceResource).getName();
					jg.writeStringField("name", name);
				}
				jg.writeEndObject();
			}
			jg.writeEndArray();
			jg.close();
		} catch (Exception e) {
			AbstractComponentsResource.logger.error(e.getMessage(), e);
			return "[]";
		}
		return sw.toString();
	}
}
