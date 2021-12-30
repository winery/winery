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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.jaxbsupport.map.PropertiesAdapter;
import org.eclipse.winery.model.tosca.TEntityTemplate;
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

    // TODO: update description

    /**
     * Gets the defined properties.
     *
     * If no properties are defined, an empty JSON object is returned. If k/v properties are defined, then a JSON is
     * returned. Otherwise, an empty JSON is returned.
     *
     * @return Key/Value map in the case of Winery WPD mode - else instance of XML Element in case of non-key/value
     * properties
     */
    @GET
    @Produces( {MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public @NonNull Response getProperties() {
        TEntityTemplate.Properties properties = this.template.getProperties();
        WinerysPropertiesDefinition propertiesDefinition = RepositoryFactory.getRepository()
            .getTypeForTemplate(this.template)
            .getWinerysPropertiesDefinition();

        // TODO: apply merge logic to all props types

        // CASE "WKV": Return XML properties as XML if existed.
        if (properties instanceof TEntityTemplate.WineryKVProperties || (properties == null && propertiesDefinition != null)) {
            // TODO: handle inherited properties?
            LinkedHashMap<String, String> currentKVProperties = ModelUtilities.getPropertiesKV(template);
            LinkedHashMap<String, String> newKVProperties = new LinkedHashMap<>();

            // Fill new map only with defined properties
            propertiesDefinition.getPropertyDefinitions().forEach(propDef -> {
                if (currentKVProperties != null) {
                    newKVProperties.put(propDef.getKey(), currentKVProperties.getOrDefault(propDef.getKey(), ""));
                } else {
                    newKVProperties.put(propDef.getKey(), "");
                }
            });

            // TODO: save only if new map is not equal to old map
            TEntityTemplate.WineryKVProperties update = new TEntityTemplate.WineryKVProperties();
            update.setKVProperties(newKVProperties);
            this.template.setProperties(update);
            RestUtils.persist(this.res);

            return Response.ok().entity(newKVProperties).type(MediaType.APPLICATION_JSON).build();
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

        // CASE "YAML": Return YAML properties as JSON if existed.
        if (properties instanceof TEntityTemplate.YamlProperties) {
            // hurrah for yaml properties
            return Response.ok()
                .entity(((TEntityTemplate.YamlProperties) properties).getProperties())
                .type(MediaType.APPLICATION_JSON)
                .build();
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
