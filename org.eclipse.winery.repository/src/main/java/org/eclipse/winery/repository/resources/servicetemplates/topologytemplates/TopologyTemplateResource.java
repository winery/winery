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
 *     Karoline Saatkamp - add split API
 *     Philipp Meyer - support for source directory
 *******************************************************************************/
package org.eclipse.winery.repository.resources.servicetemplates.topologytemplates;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.Prefs;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.client.IWineryRepositoryClient;
import org.eclipse.winery.repository.client.WineryRepositoryClientFactory;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplatesResource;
import org.eclipse.winery.repository.splitting.Splitting;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.view.Viewable;
import org.restdoc.annotations.RestDoc;
import org.restdoc.annotations.RestDocParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopologyTemplateResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(TopologyTemplateResource.class);

	private final TTopologyTemplate topologyTemplate;

	private final ServiceTemplateResource serviceTemplateRes;


	/**
	 * A topology template is always nested in a service template
	 */
	public TopologyTemplateResource(ServiceTemplateResource parent) {
		this.topologyTemplate = parent.getServiceTemplate().getTopologyTemplate();
		this.serviceTemplateRes = parent;
	}

	public static class DataForJSP {

		private String location;
		private TTopologyTemplate topologyTemplate;
		private URI repositoryURI;
		private String additonalCSS;
		private Boolean autoLayoutOnLoad;
		private String additionalScript;


		public DataForJSP(String location, URI repositoryURI, TTopologyTemplate topologyTemplate, String additonalCSS, String additionalScript, Boolean autoLayoutOnLoad) {
			this.location = location;
			this.repositoryURI = repositoryURI;
			this.topologyTemplate = topologyTemplate;
			this.additonalCSS = additonalCSS;
			this.additionalScript = additionalScript;
			this.autoLayoutOnLoad = autoLayoutOnLoad;
		}

		public String getLocation() {
			return this.location;
		}

		public TTopologyTemplate getTopologyTemplate() {
			return this.topologyTemplate;
		}

		public String getAdditonalCSS() {
			return this.additonalCSS;
		}

		public String getAdditionalScript() {
			return this.additionalScript;
		}

		public Boolean getAutoLayoutOnLoad() {
			return this.autoLayoutOnLoad;
		}

		public IWineryRepositoryClient getClient() {
			// Quick hack
			// IWineryRepository is not implemented by Prefs.INSTANCE.getRepository()
			// Therefore, we have to generate a real WineryRepositoryClient even if that causes more http load
			IWineryRepositoryClient client = WineryRepositoryClientFactory.getWineryRepositoryClient();
			client.addRepository(this.repositoryURI.toString());
			return client;
		}
	}

	@GET
	@RestDoc(methodDescription = "?edit is used in the URL to get the jsPlumb-based editor")
	@Produces(MediaType.TEXT_HTML)
	// @formatter:off
	public Response getHTML(
			@QueryParam(value = "edit") String edit,
			@QueryParam(value = "script") @RestDocParam(description = "the script to include in a <script> tag. The function wineryViewExternalScriptOnLoad if it is defined. Only available if 'view' is also set") String script,
			@QueryParam(value = "view") String view,
			@QueryParam(value = "autoLayoutOnLoad") String autoLayoutOnLoad,
			@Context UriInfo uriInfo) {
		// @formatter:on
		Response res;
		String JSPName;
		String location = Prefs.INSTANCE.getWineryTopologyModelerPath();
		location = uriInfo.getBaseUri().resolve(location).toString();
		// at the topology modeler, jersey needs to have an absolute path
		URI repositoryURI = uriInfo.getBaseUri();
		location = location + "/?repositoryURL=";
		location = location + Util.URLencode(repositoryURI.toString());
		ServiceTemplateId serviceTemplate = (ServiceTemplateId) this.serviceTemplateRes.getId();
		location = location + "&ns=";
		location = location + serviceTemplate.getNamespace().getEncoded();
		location = location + "&id=";
		location = location + serviceTemplate.getXmlId().getEncoded();
		if (edit == null) {
			String additionalCSS = null;
			Boolean autoLayoutOnLoadBoolean = false;
			if (view == null) {
				// integration in Winery
				// currently not maintained: Winery includes ?view as iframe
				JSPName = "/jsp/servicetemplates/topologytemplates/topologytemplate.jsp";
			} else {
				// view only mode
				// fullscreen: additionalCSS and script possible
				if (!"".equals(view)) {
					// view with additional CSS
					URI cssURI = URI.create(view);
					if (cssURI.isAbsolute()) {
						additionalCSS = view;
					} else {
						// relative URLs starts at "/css/topologyrendering/"
						additionalCSS = uriInfo.getBaseUri().resolve("css/topologytemplaterendering/").resolve(view).toString();
						if (!additionalCSS.endsWith(".css")) {
							additionalCSS += ".css";
						}
					}
				}
				if (autoLayoutOnLoad != null) {
					autoLayoutOnLoadBoolean = true;
				}
				JSPName = "/jsp/servicetemplates/topologytemplates/topologytemplateview.jsp";
			}
			Viewable viewable = new Viewable(JSPName, new DataForJSP(location, repositoryURI, this.topologyTemplate, additionalCSS, script, autoLayoutOnLoadBoolean));
			res = Response.ok().header(HttpHeaders.VARY, HttpHeaders.ACCEPT).entity(viewable).build();
		} else {
			// edit mode
			URI uri = Utils.createURI(location);
			res = Response.seeOther(uri).build();
		}
		return res;
	}

	/**
	 * @param uriInfo the URI ending with "topologytemplate/" of a service template
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response triggerGenerateBuildPlan(@Context UriInfo uriInfo) {
		String plansURI = uriInfo.getAbsolutePath().resolve("../plans/").toString();
		String csarURI = uriInfo.getAbsolutePath().resolve("../?csar").toString();

		String request = "<generatePlanForTopology><CSARURL>";
		request += csarURI;
		request += "</CSARURL><PLANPOSTURL>";
		request += plansURI;
		request += "</PLANPOSTURL></generatePlanForTopology>";

		Client client = Client.create();

		Builder wr = null;
		if (Utils.isResourceAvailable("http://localhost:1339/planbuilder")) {
			wr = client.resource("http://localhost:1339/planbuilder/sync").type(MediaType.APPLICATION_XML);
		} else if (Utils.isResourceAvailable("http://localhost:1337/containerapi/planbuilder")) {
			wr = client.resource("http://localhost:1337/containerapi/planbuilder/sync").type(MediaType.APPLICATION_XML);
		}

		try {
			wr.post(String.class, request);
		} catch (com.sun.jersey.api.client.UniformInterfaceException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}

		return Response.ok().build();
	}

	// @formatter:off
	@GET
	@RestDoc(methodDescription = "Returns a JSON representation of the topology template. <br />" +
			"X and Y coordinates are embedded as attributes. QName string with Namespace: <br />" +
			"{@link org.eclipse.winery.repository.common.constants.Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE} <br />" +
			"@return The JSON representation of the topology template <em>without</em> associated artifacts and without the parent service template")
	@Produces(MediaType.APPLICATION_JSON)
	// @formatter:on
	public Response getComponentInstanceJSON() {
		return Response.ok(this.topologyTemplate).build();
	}

	/**
	 * Only works for relationship templates connecting node templates; and not capabilities
	 */
	@Path("merge/")
	@Consumes(MediaType.TEXT_PLAIN)
	@POST
	public Response mergeWithOtherTopologyTemplate(String strOtherServiceTemplateQName) {
		QName otherServiceTemplateQName = QName.valueOf(strOtherServiceTemplateQName);
		ServiceTemplateId otherServiceTemplateId = new ServiceTemplateId(otherServiceTemplateQName);
		ServiceTemplateResource otherServiceTemplateResource = (ServiceTemplateResource) ServiceTemplatesResource.getComponentInstaceResource(otherServiceTemplateId);
		Optional<Integer> shiftLeft = this.topologyTemplate.getNodeTemplateOrRelationshipTemplate().stream()
				.filter(x -> x instanceof TNodeTemplate)
				.map(x -> (TNodeTemplate) x)
				.max(Comparator.comparingInt(n -> Integer.valueOf(ModelUtilities.getLeft(n))))
				.map(n -> Integer.valueOf(ModelUtilities.getLeft(n)));
		if (!shiftLeft.isPresent()) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No node templates in existing in topology to merge").build();
		} else {
			Map<String, String> idMapping = new HashMap<>();

			// collect existing node template ids
			this.topologyTemplate.getNodeTemplateOrRelationshipTemplate().stream()
					.filter(x -> x instanceof TNodeTemplate)
					.map(x -> (TNodeTemplate) x)
					// the existing ids are left unchanged
					.forEach(x -> idMapping.put(x.getId(), x.getId()));

			// collect existing node template ids
			this.topologyTemplate.getNodeTemplateOrRelationshipTemplate().stream()
					.filter(x -> x instanceof TRelationshipTemplate)
					.map(x -> (TRelationshipTemplate) x)
					// the existing ids are left unchanged
					.forEach(x -> idMapping.put(x.getId(), x.getId()));

			// patch the ids of node templates and add them
			otherServiceTemplateResource.getServiceTemplate().getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().stream()
					.filter(x -> x instanceof TNodeTemplate)
					.map(x -> (TNodeTemplate) x)
					.forEach(nt -> {
						String newId = nt.getId();
						while (idMapping.containsKey(newId)) {
							newId = newId + "-new";
						}
						idMapping.put(nt.getId(), newId);
						nt.setId(newId);
						int newLeft = Integer.valueOf(ModelUtilities.getLeft((TNodeTemplate) nt)) + shiftLeft.get();
						ModelUtilities.setLeft((TNodeTemplate) nt, Integer.toString(newLeft));
						this.topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(nt);
					});

			// patch the ids of relationship templates and add them
			otherServiceTemplateResource.getServiceTemplate().getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().stream()
					.filter(x -> x instanceof TRelationshipTemplate)
					.map(x -> (TRelationshipTemplate) x)
					.forEach(rt -> {
						String newId = rt.getId();
						while (idMapping.containsKey(newId)) {
							newId = newId + "-new";
						}
						idMapping.put(rt.getId(), newId);
						rt.setId(newId);
						this.topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(rt);
					});

			try {
				this.serviceTemplateRes.persist();
				return Response.noContent().build();
			} catch (IOException e) {
				LOGGER.error("Could not persist", e);
				return Response.serverError().entity(e.getMessage()).build();
			}
		}
	}

	@Path("nodetemplates/")
	public NodeTemplatesResource getNodeTemplatesResource() {
		// FIXME: onDelete will not work as we have a copy of the original list. We have to add a "listener" to remove at the list and route that remove to the original list
		List<TNodeTemplate> l = BackendUtils.getAllNestedNodeTemplates(this.serviceTemplateRes.getServiceTemplate());
		return new NodeTemplatesResource(l, this.serviceTemplateRes);
	}

	@Path("relationshiptemplates/")
	public RelationshipTemplatesResource getRelationshipTemplatesResource() {
		// FIXME: onDelete will not work. See getNodeTemplatesResource
		List<TRelationshipTemplate> l = new ArrayList<>();
		for (TEntityTemplate t : this.topologyTemplate.getNodeTemplateOrRelationshipTemplate()) {
			if (t instanceof TRelationshipTemplate) {
				l.add((TRelationshipTemplate) t);
			}
		}
		return new RelationshipTemplatesResource(l, this.serviceTemplateRes);
	}

	@PUT
	@RestDoc(methodDescription = "Replaces the topology by the information given in the XML")
	@Consumes(MediaType.TEXT_XML)
	public Response setModel(TTopologyTemplate topologyTemplate) {
		this.serviceTemplateRes.getServiceTemplate().setTopologyTemplate(topologyTemplate);
		return BackendUtils.persist(this.serviceTemplateRes);
	}

	@PUT
	@RestDoc(methodDescription = "Replaces the topology by the information given in the XML")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setModelJson(TTopologyTemplate topologyTemplate) throws Exception {
		ModelUtilities.patchAnyAttributes(topologyTemplate.getNodeTemplates());
		this.serviceTemplateRes.getServiceTemplate().setTopologyTemplate(topologyTemplate);
		return BackendUtils.persist(this.serviceTemplateRes);
	}

	// @formatter:off
	@GET
	@RestDoc(methodDescription = "<p>Returns an XML representation of the topology template." +
			" X and Y coordinates are embedded as attributes. Namespace:" +
			"{@link org.eclipse.winery.repository.common.constants.Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE} </p>" +
			"<p>{@link org.eclipse.winery.repository.client.WineryRepositoryClient." +
			"getTopologyTemplate(QName)} consumes this template</p>" +
			"<p>@return The XML representation of the topology template <em>without</em>" +
			"associated artifacts and without the parent service template </p>")
	@Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
	// @formatter:on
	public Response getComponentInstanceXML() {
		return Utils.getXML(TTopologyTemplate.class, this.topologyTemplate);
	}

	@Path("split/")
	@Produces(MediaType.TEXT_PLAIN)
	@POST
	public Response split(@Context UriInfo uriInfo) {
		Splitting splitting = new Splitting();
		ServiceTemplateId splitServiceTemplateId;
		try {
			splitServiceTemplateId = splitting.splitTopologyOfServiceTemplate((ServiceTemplateId) this.serviceTemplateRes.getId());
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not split. " + e.getMessage()).build();
		}
		URI url = uriInfo.getBaseUri().resolve(Utils.getAbsoluteURL(splitServiceTemplateId));
		return Response.created(url).build();
	}

	@Path("match/")
	@Produces(MediaType.TEXT_PLAIN)
	@POST
	public Response match(@Context UriInfo uriInfo) {
		Splitting splitting = new Splitting();
		ServiceTemplateId matchedServiceTemplateId;
		try {
			matchedServiceTemplateId = splitting.matchTopologyOfServiceTemplate((ServiceTemplateId) this.serviceTemplateRes.getId());
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not match. " + e.getMessage()).build();
		}
		URI url = uriInfo.getBaseUri().resolve(Utils.getAbsoluteURL(matchedServiceTemplateId));
		return Response.created(url).build();
	}

}
