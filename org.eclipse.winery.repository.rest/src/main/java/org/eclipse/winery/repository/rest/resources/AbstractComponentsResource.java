/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation, minor improvements
 *     Lukas Harzenetter - added show all items query argument
 *     Nicole Keppler - Bugfixes
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.ResourceCreationResult;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.sun.jersey.api.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource handling of a set of components. Each component has to provide a class to handle the set. This is required
 * to provide the correct instances of DefinitionsChildIds.
 *
 * TODO: Add generics here! {@link RestUtils#getComponentIdClassForComponentContainer(java.lang.Class)} is then
 * obsolete
 *
 * TODO: Rename to "AbstractDefinitionsChildResource
 */
public abstract class AbstractComponentsResource<R extends AbstractComponentInstanceResource> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractComponentsResource.class);

	@Path("{namespace}/")
	public ComponentsOfOneNamespaceResource getAllResourcesInNamespaceResource(@PathParam("namespace") String namespace) {
		return new ComponentsOfOneNamespaceResource(this.getClass(), namespace);
	}

	/**
	 * Creates a new component instance in the given namespace
	 *
	 * @param namespace plain namespace
	 * @param name      the name; used as id
	 */
	protected ResourceCreationResult onPost(String namespace, String name) {
		ResourceCreationResult res;
		if (StringUtils.isEmpty(namespace) || StringUtils.isEmpty(name)) {
			res = new ResourceCreationResult(Status.BAD_REQUEST);
		} else {
			String id = RestUtils.createXMLidAsString(name);
			DefinitionsChildId tcId;
			try {
				tcId = this.getDefinitionsChildId(namespace, id, false);
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
				AbstractComponentsResource.LOGGER.debug("Could not create id instance", e);
				res = new ResourceCreationResult(Status.INTERNAL_SERVER_ERROR);
			}
		}
		return res;
	}

	/**
	 * Creates a DefinitionsChildId for the given namespace / id combination
	 *
	 * Uses reflection to create a new instance
	 */
	protected DefinitionsChildId getDefinitionsChildId(String namespace, String id, boolean URLencoded) {
		Class<? extends DefinitionsChildId> idClass = RestUtils.getComponentIdClassForComponentContainer(this.getClass());
		return BackendUtils.getDefinitionsChildId(idClass, namespace, id, URLencoded);
	}

	/**
	 * Creates a new instance of the current component
	 *
	 * @return <ul> <li>Status.CREATED (201) if the resource has been created,</li> <li>Status.CONFLICT if the resource
	 * already exists,</li> <li>Status.INTERNAL_SERVER_ERROR (500) if something went wrong</li> </ul>
	 */
	protected ResourceCreationResult createComponentInstance(DefinitionsChildId tcId) {
		return RestUtils.create(tcId);
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends AbstractComponentInstanceResource> getComponentInstanceResourceClassForType(String type) {
		// Guess the package
		String pkg = "org.eclipse.winery.repository.rest.resources.";

		pkg += RestUtils.getIntermediateLocationStringForType(type, ".");

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
	 * Returns the requested resource. It has to be abstract to enable the SWAGGER tooling finding all resources.
	 *
	 * @param namespace encoded namespace
	 * @param id        encoded id
	 * @return an instance of the requested resource
	 */
	public abstract R getComponentInstaceResource(String namespace, String id);

	/**
	 * @param encoded specifies whether namespace and id are encoded
	 * @return an instance of the requested resource
	 */
	@SuppressWarnings("unchecked")
	protected R getComponentInstaceResource(String namespace, String id, boolean encoded) {
		DefinitionsChildId tcId;
		try {
			tcId = this.getDefinitionsChildId(namespace, id, encoded);
		} catch (Exception e) {
			throw new IllegalStateException("Could not create id instance", e);
		}
		return (R) AbstractComponentsResource.getComponentInstaceResource(tcId);
	}

	/**
	 * @return an instance of the requested resource
	 */
	public R getComponentInstaceResource(QName qname) {
		return this.getComponentInstaceResource(qname.getNamespaceURI(), qname.getLocalPart(), false);
	}

	/**
	 * @return an instance of the requested resource
	 * @throws NotFoundException if resource doesn't exist.
	 */
	public static AbstractComponentInstanceResource getComponentInstaceResource(DefinitionsChildId tcId) {
		String type = Util.getTypeForComponentId(tcId.getClass());
		if (!RepositoryFactory.getRepository().exists(tcId)) {
			AbstractComponentsResource.LOGGER.debug("Definition child id " + tcId.toString() + " not found");
			throw new NotFoundException("Definition child id " + tcId.toString() + " not found");
		}
		Class<? extends AbstractComponentInstanceResource> newResource = AbstractComponentsResource.getComponentInstanceResourceClassForType(type);
		Constructor<?>[] constructors = newResource.getConstructors();
		assert (constructors.length == 1);
		AbstractComponentInstanceResource newInstance;
		try {
			newInstance = (AbstractComponentInstanceResource) constructors[0].newInstance(tcId);
		} catch (InstantiationException | IllegalAccessException
			| IllegalArgumentException | InvocationTargetException e) {
			AbstractComponentsResource.LOGGER.error("Could not instantiate sub resource " + tcId);
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
		Class<? extends DefinitionsChildId> idClass = RestUtils.getComponentIdClassForComponentContainer(this.getClass());
		SortedSet<? extends DefinitionsChildId> allDefinitionsChildIds = RepositoryFactory.getRepository().getAllDefinitionsChildIds(idClass);
		ArrayList<AbstractComponentInstanceResource> res = new ArrayList<>(allDefinitionsChildIds.size());
		for (DefinitionsChildId id : allDefinitionsChildIds) {
			AbstractComponentInstanceResource r = AbstractComponentsResource.getComponentInstaceResource(id);
			res.add(r);
		}
		return res;
	}

	/**
	 * Used by org.eclipse.winery.repository.repository.client and by the artifactcreationdialog.tag. Especially the
	 * "name" field is used there at the UI
	 *
	 * @param grouped if given, the JSON output is grouped by namespace
	 * @return A list of all ids of all instances of this component type. <br /> Format: <code>[({"namespace":
	 * "[namespace]", "id": "[id]"},)* ]</code>. <br /><br /> If grouped is set, the list will be grouped by namespace.
	 * <br /> <code>[{"id": "[namsepace encoded]", "test": "[namespace decoded]", "children":[{"id": "[qName]", "text":
	 * "[id]"}]}]</code>
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getListOfAllIds(@QueryParam("grouped") String grouped) {
		Class<? extends DefinitionsChildId> idClass = RestUtils.getComponentIdClassForComponentContainer(this.getClass());
		boolean supportsNameAttribute = Util.instanceSupportsNameAttribute(idClass);
		SortedSet<? extends DefinitionsChildId> allDefinitionsChildIds = RepositoryFactory.getRepository().getAllDefinitionsChildIds(idClass);
		JsonFactory jsonFactory = new JsonFactory();
		StringWriter sw = new StringWriter();
		try {
			JsonGenerator jg = jsonFactory.createGenerator(sw);
			// We produce org.eclipse.winery.repository.client.WineryRepositoryClient.NamespaceAndId by hand here
			// Refactoring could move this class to common and fill it here
			if (grouped == null) {
				jg.writeStartArray();
				for (DefinitionsChildId id : allDefinitionsChildIds) {
					jg.writeStartObject();
					jg.writeStringField("namespace", id.getNamespace().getDecoded());
					jg.writeStringField("id", id.getXmlId().getDecoded());
					if (supportsNameAttribute) {
						AbstractComponentInstanceResource componentInstaceResource = AbstractComponentsResource.getComponentInstaceResource(id);
						String name = ((IHasName) componentInstaceResource).getName();
						jg.writeStringField("name", name);
					} else {
						// used for winery-qNameSelector to avoid an if there
						jg.writeStringField("name", id.getXmlId().getDecoded());
					}
					jg.writeStringField("qName", id.getQName().toString());
					jg.writeEndObject();
				}
				jg.writeEndArray();
			} else {
				jg.writeStartArray();
				Map<Namespace, ? extends List<? extends DefinitionsChildId>> groupedIds = allDefinitionsChildIds.stream().collect(Collectors.groupingBy(id -> id.getNamespace()));
				groupedIds.keySet().stream().sorted().forEach(namespace -> {
					try {
						jg.writeStartObject();
						jg.writeStringField("id", namespace.getEncoded());
						jg.writeStringField("text", namespace.getDecoded());
						jg.writeFieldName("children");
						jg.writeStartArray();
						groupedIds.get(namespace).forEach(id -> {
							try {
								jg.writeStartObject();
								String text;
								if (supportsNameAttribute) {
									AbstractComponentInstanceResource componentInstaceResource = AbstractComponentsResource.getComponentInstaceResource(id);
									text = ((IHasName) componentInstaceResource).getName();
								} else {
									text = id.getXmlId().getDecoded();
								}
								jg.writeStringField("id", id.getQName().toString());
								jg.writeStringField("text", text);
								jg.writeEndObject();
							} catch (IOException e) {
								AbstractComponentsResource.LOGGER.error("Could not create JSON", e);
							}
						});
						jg.writeEndArray();
						jg.writeEndObject();
					} catch (IOException e) {
						AbstractComponentsResource.LOGGER.error("Could not create JSON", e);
					}
				});
				jg.writeEndArray();
			}
			jg.close();
		} catch (Exception e) {
			AbstractComponentsResource.LOGGER.error(e.getMessage(), e);
			return "[]";
		}
		return sw.toString();
	}
}
