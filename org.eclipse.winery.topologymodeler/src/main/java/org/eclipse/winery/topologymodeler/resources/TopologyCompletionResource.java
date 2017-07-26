/*******************************************************************************
 * Copyright (c) 2013 Pascal Hirmer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Pascal Hirmer - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.topologymodeler.resources;

import java.io.StringReader;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.JAXBHelper;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.RESTHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains resources used for the topology completion.
 *
 */
@Path("/")
public class TopologyCompletionResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopologyCompletionResource.class);

    /**
     * Adds selected {@link TNodeTemplate}s and {@link TRelationshipTemplate}s
     * to a topology.
     *
     * @param topology
     *            the {@link TTopologyTemplate} as XML string
     * @param allChoices
     *            all possible choices as XML
     * @param selectedNodeTemplates
     *            the selected {@link TNodeTemplate}s as JSON array
     * @param selectedRelationshipTemplates
     *            the selected {@link TRelationshipTemplate}s as JSON array
     * @return the enhanced {@link TTopologyTemplate}
     */
    @Path("selectionhandler/")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response handleSelection(
            @QueryParam(value = "topology") String topology,
            @QueryParam(value = "allChoices") String allChoices,
            @QueryParam(value = "selectedNodeTemplates") String selectedNodeTemplates,
            @QueryParam(value = "selectedRelationshipTemplates") String selectedRelationshipTemplates) {
        return Response
                .ok()
                .entity(JAXBHelper.addTemplatesToTopology(topology, allChoices,
                        selectedNodeTemplates, selectedRelationshipTemplates))
                .build();
    }

    /**
     * This resource is used to save a {@link TTopologyTemplate} to the repository.
     *
     * @param topology
     *            the topology to be saved
     * @param templateURL
     *               the URL the {@link TTopologyTemplate} of the topology template
     * @param repositoryURL
     *               the URL of the repository
     * @param topologyName
     *               the name of the saved {@link TTopologyTemplate}
     * @param topologyNamespace
     *               the namespace of the saved {@link TTopologyTemplate}
     * @param overwriteTopology
     *               whether the {@link TTopologyTemplate} should be overwritten or not
     *
     * @return whether the save operation has been successful or not
     */
    @Path("topologysaver/")
    @POST
    public Response saveTopology(@FormParam("topology") String topology,
            @FormParam(value = "templateURL") String templateURL,
            @FormParam(value = "repositoryURL") String repositoryURL,
            @FormParam(value = "topologyName") String topologyName,
            @FormParam(value = "topologyNamespace") String topologyNamespace,
            @FormParam(value = "overwriteTopology") String overwriteTopology) {
        try {

            boolean overwrite = Boolean.parseBoolean(overwriteTopology);

            // initiate JaxB context
            JAXBContext context;
            context = JAXBContext.newInstance(Definitions.class);
            StringReader reader = new StringReader(topology);

            // unmarshall the topology XML string
            Unmarshaller um;

            um = context.createUnmarshaller();

            Definitions jaxBDefinitions = (Definitions) um.unmarshal(reader);
            TServiceTemplate st = (TServiceTemplate) jaxBDefinitions
                    .getServiceTemplateOrNodeTypeOrNodeTypeImplementation()
                    .get(0);
            TTopologyTemplate toBeSaved = st.getTopologyTemplate();

            // depending on the selected save method (overwrite or create new)
            // the save method is called
            if (overwrite) {
                RESTHelper.saveCompleteTopology(toBeSaved, templateURL, true,
                        "", "", repositoryURL);
            } else {
                RESTHelper.saveCompleteTopology(toBeSaved, templateURL, false,
                        topologyName, topologyNamespace, repositoryURL);
            }

            return Response.ok().build();

        } catch (JAXBException e) {
            LOGGER.error("Could not save topology", e);
            return Response.serverError().build();
        }
    }
}
