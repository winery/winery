/*******************************************************************************
 * Copyright (c) 2012-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources._support;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.datatypes.LocalNameForAngular;
import org.eclipse.winery.repository.rest.datatypes.NamespaceAndDefinedLocalNamesForAngular;

import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource handling of a set of components. Each component has to provide a class to handle the set. This is required
 * to provide the correct instances of DefinitionsChildIds.
 * <p>
 * TODO: Add generics here! {@link RestUtils#getComponentIdClassForComponentContainer(java.lang.Class)} is then
 * obsolete
 * <p>
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
    protected ResourceResult onPost(String namespace, String name) {
        ResourceResult res;
        if (StringUtils.isEmpty(namespace) || StringUtils.isEmpty(name)) {
            res = new ResourceResult(Status.BAD_REQUEST);
        } else {
            String id = RestUtils.createXmlIdAsString(name);
            DefinitionsChildId tcId;
            try {
                tcId = this.getDefinitionsChildId(namespace, id, false);
                res = RestUtils.create(tcId, name);
                res.setMessage(getComponentInstanceResource(tcId).getElement());
            } catch (Exception e) {
                AbstractComponentsResource.LOGGER.debug("Could not create id instance", e);
                res = new ResourceResult(Status.INTERNAL_SERVER_ERROR);
            }
        }
        return res;
    }

    /**
     * Creates a DefinitionsChildId for the given namespace / id combination
     */
    protected DefinitionsChildId getDefinitionsChildId(String namespace, String id, boolean URLencoded) {
        Class<? extends DefinitionsChildId> idClass = RestUtils.getComponentIdClassForComponentContainer(this.getClass());
        return BackendUtils.getDefinitionsChildId(idClass, namespace, id, URLencoded);
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
    public abstract R getComponentInstanceResource(String namespace, String id);

    /**
     * @param encoded specifies whether namespace and id are encoded
     * @return an instance of the requested resource
     */
    @SuppressWarnings("unchecked")
    protected R getComponentInstanceResource(String namespace, String id, boolean encoded) {
        DefinitionsChildId tcId;
        try {
            tcId = this.getDefinitionsChildId(namespace, id, encoded);
        } catch (Exception e) {
            throw new IllegalStateException("Could not create id instance", e);
        }
        return (R) AbstractComponentsResource.getComponentInstanceResource(tcId);
    }

    /**
     * @return an instance of the requested resource
     */
    public R getComponentInstanceResource(QName qname) {
        return this.getComponentInstanceResource(qname.getNamespaceURI(), qname.getLocalPart(), false);
    }

    /**
     * @return an instance of the requested resource
     * @throws NotFoundException if resource doesn't exist.
     */
    public static AbstractComponentInstanceResource getComponentInstanceResource(DefinitionsChildId tcId) {
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
     * <p>
     * Required by XaaSPackager logic
     * <p>
     * TODO: remove that method and refactor callers
     */
    public Collection<AbstractComponentInstanceResource> getAll() {
        Class<? extends DefinitionsChildId> idClass = RestUtils.getComponentIdClassForComponentContainer(this.getClass());
        SortedSet<? extends DefinitionsChildId> allDefinitionsChildIds = RepositoryFactory.getRepository().getAllDefinitionsChildIds(idClass);
        ArrayList<AbstractComponentInstanceResource> res = new ArrayList<>(allDefinitionsChildIds.size());
        for (DefinitionsChildId id : allDefinitionsChildIds) {
            AbstractComponentInstanceResource r = AbstractComponentsResource.getComponentInstanceResource(id);
            res.add(r);
        }
        return res;
    }

    /**
     * Used by org.eclipse.winery.repository.repository.client and by the artifactcreationdialog.tag. Especially the
     * "name" field is used there at the UI
     *
     * @param grouped if given, the JSON output is grouped by namespace
     * @return A list of all ids of all instances of this component type. Format: <code>[({"namespace": "[namespace]",
     * "id": "[id]"},)* ]</code>.
     * <p>
     * If grouped is set, the list will be grouped by namespace.
     * <code>[{"id": "[namsepace encoded]", "test": "[namespace decoded]", "children":[{"id": "[qName]", "text":
     * "[id]"}]}]</code>
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Object getListOfAllIds(
        @QueryParam("grouped") String grouped,
        @QueryParam("includeVersions") String includeVersions,
        @QueryParam("full") @ApiParam("If set, the full information of the definition's child is returned. E.g., in the case of node types, the same result as a GET on {ns}/{id] is returned. Works only in the case of grouped.") String full) {
        Class<? extends DefinitionsChildId> idClass = RestUtils.getComponentIdClassForComponentContainer(this.getClass());
        final IRepository repository = RepositoryFactory.getRepository();
        SortedSet<? extends DefinitionsChildId> allDefinitionsChildIds = repository.getAllDefinitionsChildIds(idClass);

        if (Objects.nonNull(grouped)) {
            return getGroupedListOfIds(allDefinitionsChildIds, full, includeVersions);
        } else {
            return RestUtils.getListOfIds(allDefinitionsChildIds, Objects.nonNull(full), Objects.nonNull(includeVersions));
        }
    }

    private List<NamespaceAndDefinedLocalNamesForAngular> getGroupedListOfIds(SortedSet<? extends DefinitionsChildId> allDefinitionsChildIds, String full, String includeVersions) {
        Map<Namespace, ? extends List<? extends DefinitionsChildId>> groupedIds = allDefinitionsChildIds.stream().collect(Collectors.groupingBy(DefinitionsChildId::getNamespace));
        return groupedIds.keySet().stream()
            .sorted()
            .map(namespace -> {
                List<LocalNameForAngular> names = groupedIds.get(namespace).stream()
                    .map(definition -> {
                        Definitions fullDefinition = null;
                        if (Objects.nonNull(full)) {
                            fullDefinition = RestUtils.getFullComponentData(definition);
                        }

                        String qName = definition.getQName().toString();
                        String id = definition.getXmlId().toString();

                        if ("componentVersionOnly".equals(includeVersions)) {
                            qName = VersionUtils.getQNameWithComponentVersionOnly(definition);
                            id = qName.split("}")[1];
                        }

                        return new LocalNameForAngular(qName, id, fullDefinition);
                    })
                    .distinct()
                    .collect(Collectors.toList());

                return new NamespaceAndDefinedLocalNamesForAngular(namespace, names);
            })
            .collect(Collectors.toList());
    }
}
