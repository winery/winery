/*******************************************************************************
 * Copyright (c) 2012-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.entitytemplates;

import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.jaxbsupport.map.PropertiesAdapter;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.extensions.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.extensions.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResource;

import io.github.adr.embedded.ADR;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesResource.class);

    private AbstractComponentInstanceResource res;
    private TEntityTemplate template;
    private IRepository requestRepository;

    /**
     * @param template the template to store the definitions at
     * @param res      the resource to save after modifications
     */
    public PropertiesResource(TEntityTemplate template, AbstractComponentInstanceResource res) {
        this.template = template;
        this.res = res;
        this.requestRepository = RepositoryFactory.getRepository();
    }

    @PUT
    @Consumes( {MediaType.APPLICATION_XML, MediaType.TEXT_XML})
//    public Response setProperties(@ADR(6) @XmlJavaTypeAdapter(PropertiesAdapter.class) TEntityTemplate.Properties properties) {
    public Response setProperties(@ADR(6) PropertiesAdapter.Union xml) {
        TEntityTemplate.Properties properties = new PropertiesAdapter().unmarshal(xml);
        this.template.setProperties(properties);
        return RestUtils.persist(this.res);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setProperties(LinkedHashMap<String, String> properties) {
        ModelUtilities.setPropertiesKV(template, properties);
        return RestUtils.persist(this.res);
    }

    /**
     * Get effective properties.
     * 
     * An effective property is either a defined property or a default.
     * Inheritance is considered.
     * Only winery properties definitions are considered.
     */
    @GET
    @Path("effective")
    @Produces(MediaType.APPLICATION_JSON)
    public LinkedHashMap<String, String> getEffectiveProperties() {
        // Get entity type of current template
        TEntityType entityType = RepositoryFactory.getRepository().getTypeForTemplate(this.template);

        // Get complete inheritance hierarchy
        List<TEntityType> hierarchy = RepositoryFactory.getRepository().getParentsAndChild(entityType);

        // Merge properties definitions
        List<PropertyDefinitionKV> propertiesDefinitions = ModelUtilities.mergePropertiesDefinitions(hierarchy);

        // Construct effective properties by assigning only defined properties or defaults
        LinkedHashMap<String, String> assignedProperties = ModelUtilities.getPropertiesKV(this.template);
        LinkedHashMap<String, String> effectiveProperties = new LinkedHashMap<>();
        propertiesDefinitions.forEach(propDef -> {
            String effectiveValue;
            if (assignedProperties != null) {
                 effectiveValue = assignedProperties.getOrDefault(propDef.getKey(), propDef.getDefaultValue());
            } else {
                effectiveValue = propDef.getDefaultValue();
            }
            
            if (effectiveValue != null) {
                effectiveProperties.put(propDef.getKey(), effectiveValue);
            }
        });

        return effectiveProperties;
    }

    /**
     * Gets the defined properties.
     * Inheritance is not considered, see {@link #getEffectiveProperties()} instead.
     *
     * If no properties are defined, an empty JSON object is returned.
     * If k/v properties are defined, then a JSON object is returned.
     * If xml properties are defined, then an XML object is returned.
     * Otherwise, an empty JSON is returned.
     */
    @GET
    @Produces( {MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public @NonNull Response getProperties() {
        TEntityTemplate.Properties properties = this.template.getProperties();
        WinerysPropertiesDefinition propertiesDefinition = RepositoryFactory.getRepository()
            .getTypeForTemplate(this.template)
            .getWinerysPropertiesDefinition();

        // CASE "WKV": Return WineryKV properties as JSON if existed.
        if (properties instanceof TEntityTemplate.WineryKVProperties || (properties == null && propertiesDefinition != null)) {
            return Response.ok()
                .entity(ModelUtilities.getPropertiesKV(template))
                .type(MediaType.APPLICATION_JSON)
                .build();
        }

        // CASE "YAML": Return YAML properties as JSON if existed.
        if (properties instanceof TEntityTemplate.YamlProperties) {
            // hurrah for yaml properties
            return Response.ok()
                .entity(((TEntityTemplate.YamlProperties) properties).getProperties())
                .type(MediaType.APPLICATION_JSON)
                .build();
        }

        // CASE "XML": Return XML properties as XML if existed.
        if (properties instanceof TEntityTemplate.XmlProperties) {
            @Nullable final Object any = ((TEntityTemplate.XmlProperties) properties).getAny();
            if (any == null) {
                LOGGER.debug("XML properties expected, but none found. Returning empty JSON.");
                return Response.ok().entity("{}").type(MediaType.APPLICATION_JSON).build();
            }
            try {
                @ADR(6)
//                String xmlAsString = BackendUtils.getXMLAsString(TEntityTemplate.XmlProperties.class, (TEntityTemplate.XmlProperties)props, true, requestRepository);
                String xmlAsString = BackendUtils.getXMLAsString(properties, requestRepository);
                return Response
                    .ok()
                    .entity(xmlAsString)
                    .type(MediaType.TEXT_XML)
                    .build();
            } catch (Exception e) {
                throw new WebApplicationException(e);
            }
        }

        // CASE "NULL": Return empty JSON if no properties existed.
        if (properties == null) {
            return Response.ok().entity("{}").type(MediaType.APPLICATION_JSON).build();
        }

        // OTHERWISE: Throw error if properties could not be handled. Should never happen.
        LOGGER.error("Property definition for Entity Template {} was not handled", template.getId());
        return Response.serverError().build();
    }
}
