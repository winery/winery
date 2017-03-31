/*******************************************************************************
 * Copyright (c) 2012-2016 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Lukas Harzenetter - return namespaces sorted
 *     Nicole Keppler - return filtered namespace with number of containing components
 *     Niko Stadelmaier - return namespaces with prefix
 *******************************************************************************/
package org.eclipse.winery.repository.resources.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.datatypes.NamespaceAndCountOfComponentInstances;
import org.eclipse.winery.repository.datatypes.ids.admin.NamespacesId;
import org.eclipse.winery.repository.resources.apiData.NamespaceWithPrefix;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages prefixes for the namespaces
 */
public class NamespacesResource extends AbstractAdminResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(NamespacesResource.class);

	private Integer nsCount = 0;

	private NamespacesResource() {
		super(new NamespacesId());

		// globally set prefixes
		// if that behavior is not desired, the code has to be moved to "generatePrefix" which checks for existence, ...
		this.configuration.setProperty("http://www.w3.org/2001/XMLSchema", "xsd");
		this.configuration.setProperty("http://www.w3.org/XML/1998/namespace", "xmlns");
		this.configuration.setProperty(org.eclipse.winery.common.constants.Namespaces.TOSCA_NAMESPACE, "tosca");
		this.configuration.setProperty(org.eclipse.winery.common.constants.Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "winery");
	}

	/**
	 * SIDEFFECT: URI is added to list of known namespaces if it did not exist
	 * before
	 */
	public static String getPrefix(Namespace namespace) {
		String ns = namespace.getDecoded();
		return NamespacesResource.getPrefix(ns);
	}

	/**
	 * SIDEFFECT: URI is added to list of known namespaces if it did not exist
	 * before
	 */
	public static String getPrefix(String namespace) {
		if (namespace == null) {
			throw new IllegalArgumentException("Namespace must not be null");
		}
		String prefix = NamespacesResource.getInstance().configuration.getString(namespace);
		if (prefix == null) {
			prefix = NamespacesResource.generatePrefix(namespace);
			NamespacesResource.getInstance().configuration.setProperty(namespace, prefix);
		}
		return prefix;
	}

	private static String generatePrefix(String namespace) {
		String prefix;
		final NamespacesResource resource = NamespacesResource.getInstance();
		Collection<String> allPrefixes = resource.getAllPrefixes();

		// TODO: generate prefix using URI (and not "arbitrary" prefix)
		do {
			prefix = String.format("ns%d", resource.nsCount);
			resource.nsCount++;
		} while (allPrefixes.contains(prefix));
		return prefix;
	}

	/**
	 * Returns the list of all namespaces registered with his manager and used
	 * at component instances.
	 */
	public static Collection<Namespace> getNamespaces() {
		HashSet<Namespace> res = NamespacesResource.getInstance().getRegisteredNamespaces();
		res.addAll(Repository.INSTANCE.getUsedNamespaces());
		ArrayList<Namespace> list = new ArrayList<>(res);
		Collections.sort(list);
		return list;
	}

	/**
	 * Returns the list of all namespaces registered with his manager and their number
	 * of containing components of requested type
	 */
	public static <I extends TOSCAComponentId> Collection<NamespaceAndCountOfComponentInstances> getCountOfInstancesInEachNamespace(Class<I> id) {
		Objects.requireNonNull(id);

		// set with all components of requested Type (e.g. NodeType)
		// to only show components for the requested type of namespace
		SortedSet<I> setOfAllTOSCAComponentIds = Repository.INSTANCE.getAllTOSCAComponentIds(id);

		// convert sortedset to arraylist
		// and group list by namespace string and count components
		List<String> listOfAllTOSCAComponentIds = new ArrayList<>();
		for (TOSCAComponentId toscaComponentId : setOfAllTOSCAComponentIds) {
			listOfAllTOSCAComponentIds.add(toscaComponentId.getNamespace().toString());
		}
		Map<String, Long> mapOfGroupedComponents =
				listOfAllTOSCAComponentIds.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		// create collection to return and fill with grouped components by namespace and number of containing components
		Collection<NamespaceAndCountOfComponentInstances> namespacesAndCount = new ArrayList<>();
		for (Map.Entry<String, Long> groupOfComponents : mapOfGroupedComponents.entrySet()) {
			for (TOSCAComponentId toscaComponentId : setOfAllTOSCAComponentIds) {
				if (toscaComponentId.getNamespace().toString().equals(groupOfComponents.getKey())) {
					namespacesAndCount.add(
							new NamespaceAndCountOfComponentInstances(
									toscaComponentId.getNamespace(), Objects.requireNonNull(groupOfComponents.getValue()).intValue()));
					break;
				}
			}
		}
		return namespacesAndCount;
	}

	/**
	 * Returns the list of all namespaces in the given TOSCA component.
	 *
	 * @param clazz the TOSCA component class which namespaces' should be returned.
	 */
	public static Collection<Namespace> getComponentsNamespaces(Class<? extends TOSCAComponentId> clazz) {
		HashSet<Namespace> res = NamespacesResource.getInstance().getRegisteredNamespaces();
		res.addAll(Repository.INSTANCE.getComponentsNamespaces(clazz));
		ArrayList<Namespace> list = new ArrayList<>(res);
		Collections.sort(list);
		return list;
	}

	public static NamespacesResource getInstance() {
		return new NamespacesResource();
	}

	private Collection<String> getAllPrefixes() {
		Iterator<String> keys = this.configuration.getKeys();
		HashSet<String> res = new HashSet<>();
		while (keys.hasNext()) {
			String key = keys.next();
			String prefix = this.configuration.getString(key);
			res.add(prefix);
		}
		return res;
	}

	/**
	 * Sets / overwrites prefix/namespace mapping
	 * <p>
	 * In case the prefix is already bound to another namespace, BAD_REQUEST is
	 * returned.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addNamespace(@FormParam("namespace") String namespace, @FormParam("nsPrefix") String prefix) {
		if (StringUtils.isEmpty(namespace)) {
			return Response.status(Status.BAD_REQUEST).entity("namespace must be given.").build();
		}
		if (StringUtils.isEmpty(prefix)) {
			return Response.status(Status.BAD_REQUEST).entity("prefix must be given.").build();
		}
		namespace = Util.URLdecode(namespace);
		prefix = Util.URLdecode(prefix);
		Collection<String> allPrefixes = this.getAllPrefixes();
		if (allPrefixes.contains(prefix)) {
			if (NamespacesResource.getPrefix(namespace).equals(prefix)) {
				return Response.notModified().build();
			} else {
				// the requested prefix is already bound to a different namespace
				return Response.status(Status.BAD_REQUEST).entity("prefix already bound to a different namespace.").build();
			}
		}
		this.configuration.setProperty(namespace, prefix);
		return Response.noContent().build();
	}

	public void addNamespace(String namespace) {
		String prefix = generatePrefix(namespace);
		// directly store it in the internal data structure
		this.configuration.setProperty(namespace, prefix);
	}

	/**
	 * Sets / overwrites prefix/namespace mapping
	 * <p>
	 * In case the prefix is already bound to another namespace, BAD_REQUEST is
	 * returned.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addNamespace(ArrayList<NamespaceWithPrefix> namespacesList) {
		if (namespacesList == null) {
			return Response.status(Status.BAD_REQUEST).entity("namespace list must be given.").build();
		}
		//delete all namespaces
		this.configuration.clear();
		//set all namespaces
		for (NamespaceWithPrefix nsp : namespacesList) {
			this.addNamespace(nsp.namespace, nsp.prefix);
		}
		return Response.noContent().build();
	}

	/**
	 * Deletes given namespace from the repository
	 *
	 * @param URI to delete. The namespace is URLencoded.
	 */
	@DELETE
	@Path("{namespace}")
	public Response onDelete(@PathParam("namespace") String URI) {
		Response res;
		URI = Util.URLdecode(URI);
		if (this.configuration.containsKey(URI)) {
			this.configuration.clearProperty(URI);
			res = Response.noContent().build();
		} else {
			res = Response.status(Status.NOT_FOUND).build();
		}
		return res;
	}

	@Path("{namespace}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getPrefixForEncodedNamespace(@PathParam("namespace") String URI) {
		URI = Util.URLdecode(URI);
		return NamespacesResource.getPrefix(URI);
	}

	/**
	 * Returns the list of all namespaces registered with his manager. It could
	 * be incomplete, if entries have been added manually to the repository
	 *
	 * @return all namespaces registered with this manager.
	 */
	private HashSet<Namespace> getRegisteredNamespaces() {
		HashSet<Namespace> res = new HashSet<>();
		Iterator<String> keys = this.configuration.getKeys();
		while (keys.hasNext()) {
			String key = keys.next();
			Namespace ns = new Namespace(key, false);
			res.add(ns);
		}
		return res;
	}

	/**
	 * This method is required because static methods cannot be accessed by EL
	 *
	 * @return see getNamespaces()
	 */
	public Collection<Namespace> getNamespacesForJSP() {
		return NamespacesResource.getNamespaces();
	}

	/**
	 * Returns the list of all namespaces registered with his manager and used
	 * at component instances.
	 *
	 * @return a JSON list containing the non-encoded URIs of each known
	 * namespace
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<NamespaceWithPrefix> getNamespacesAsJSONlist() {
		Collection<Namespace> namespaces = NamespacesResource.getNamespaces();

		// We now have all namespaces
		// We need to convert from Namespace to String

		ArrayList<NamespaceWithPrefix> namespacesList = new ArrayList<>();
		for (Namespace ns : namespaces) {
			namespacesList.add(new NamespaceWithPrefix(ns));
		}
		Collections.sort(namespacesList);
		return namespacesList;
	}

	/**
	 * Checks whether a prefix is registered for a namespace
	 * <p>
	 * Used at CSARImporter
	 */
	public boolean getIsPrefixKnownForNamespace(String namespace) {
		return this.configuration.containsKey(namespace);
	}

}
