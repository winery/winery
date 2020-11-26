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
package org.eclipse.winery.repository.rest.resources.admin;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.common.configuration.UiConfigurationObject;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.consistencycheck.ConsistencyChecker;
import org.eclipse.winery.repository.backend.consistencycheck.ConsistencyCheckerConfiguration;
import org.eclipse.winery.repository.backend.consistencycheck.ConsistencyCheckerVerbosity;
import org.eclipse.winery.repository.backend.consistencycheck.ConsistencyErrorCollector;
import org.eclipse.winery.repository.rest.resources.admin.types.ConstraintTypesManager;
import org.eclipse.winery.repository.rest.resources.admin.types.PlanLanguagesManager;
import org.eclipse.winery.repository.rest.resources.admin.types.PlanTypesManager;
import org.eclipse.winery.repository.rest.resources.admin.types.RepositoryConfigurationResponse;
import org.eclipse.winery.repository.rest.resources.admin.types.che.CheResponse;
import org.eclipse.winery.repository.rest.resources.admin.types.che.Machine;
import org.eclipse.winery.repository.rest.resources.admin.types.che.Server;
import org.eclipse.winery.repository.rest.resources.admin.types.che.WorkspaceResponse;
import org.eclipse.winery.repository.rest.resources.apiData.OAuthStateAndCodeApiData;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

@Api(tags = "Admin")
public class AdminTopResource {

    @Path("namespaces/")
    public NamespacesResource getNamespacesResource() {
        return new NamespacesResource();
    }

    @Path("repository/")
    public RepositoryAdminResource getRepositoryAdminResource() {
        return new RepositoryAdminResource();
    }

    @Path("planlanguages/")
    public PlanLanguagesManager getPlanLanguagesResource() {
        return PlanLanguagesManager.INSTANCE;
    }

    @Path("plantypes/")
    public PlanTypesManager getPlanTypesResource() {
        return PlanTypesManager.INSTANCE;
    }

    @Path("constrainttypes/")
    public ConstraintTypesManager getConstraintTypesManager() {
        return ConstraintTypesManager.INSTANCE;
    }

    @GET
    @Path("consistencycheck")
    @Produces(MediaType.APPLICATION_JSON)
    public ConsistencyErrorCollector checkConsistency(@QueryParam("serviceTemplatesOnly") boolean serviceTemplatesOnly, @QueryParam("checkDocumentation") boolean checkDocumentation) {
        IRepository repo = RepositoryFactory.getRepository();
        EnumSet<ConsistencyCheckerVerbosity> verbosity = EnumSet.of(ConsistencyCheckerVerbosity.NONE);
        ConsistencyCheckerConfiguration config = new ConsistencyCheckerConfiguration(serviceTemplatesOnly, checkDocumentation, verbosity, repo);
        final ConsistencyChecker consistencyChecker = new ConsistencyChecker(config);
        consistencyChecker.checkCorruption();
        return consistencyChecker.getErrorCollector();
    }

    /**
     * This method answers a get-request by the WineryRepositoryConfigurationService
     *
     * @return the winery config file in json format.
     */
    @GET
    @Path("config")
    @Produces(MediaType.APPLICATION_JSON)
    public UiConfigurationObject getConfig() {
        return Environments.getInstance().getUiConfig();
    }

    @GET
    @Path("repository-config")
    @Produces(MediaType.APPLICATION_JSON)
    public RepositoryConfigurationResponse getRepositoryConfig() {
        RepositoryConfigurationObject config = Environments.getInstance().getRepositoryConfig();
        return new RepositoryConfigurationResponse(config.getRepositoryRoot());
    }

    @GET
    @Path("che")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCheUrl() throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        String cheApiEndpoint = System.getenv("CHE_API");
        String cheMachineToken = System.getenv("CHE_MACHINE_TOKEN");
        String cheWorkspaceId = System.getenv("CHE_WORKSPACE_ID");
        if (cheApiEndpoint == null || cheMachineToken == null || cheWorkspaceId == null) {
            return Response.status(500, "Server environment is not correctly configured").build();
        }
        HttpGet httpGet = new HttpGet(cheApiEndpoint + "/workspace/" + cheWorkspaceId);
        httpGet.setHeader("Authorization", "Bearer " + cheMachineToken);

        HttpResponse response = httpclient.execute(httpGet);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        WorkspaceResponse workspace = mapper.readValue(response.getEntity().getContent(), WorkspaceResponse.class);
        Optional<Machine> machine = workspace.runtime.machines.values().stream().filter(x -> "theia-editor".equals(x.attributes.get("component"))).findFirst();
        if (!machine.isPresent()) {
            return Response.status(500, "Eclipse Che is not correctly configured").build();
        }

        Optional<Server> runningServer = machine.get().servers.values().stream().filter(x -> x.status.equals("RUNNING")).findFirst();
        if (!runningServer.isPresent()) {
            return Response.status(500, "Eclipse Che is not correctly configured").build();
        }

        return Response
            .status(response.getStatusLine().getStatusCode())
            .entity(new CheResponse(runningServer.get().url))
            .build();
    }

    @PUT
    @Path("config")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UiConfigurationObject setConfig(UiConfigurationObject changedConfiguration) {
        Environments.save(changedConfiguration);
        Environments.save(Environments.getInstance().getRepositoryConfig());
        return Environments.getInstance().getUiConfig();
    }

    @POST
    @Path("githubaccesstoken")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGitHubAccessToken(OAuthStateAndCodeApiData codeApiData) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://github.com/login/oauth/access_token");
        httppost.setHeader("Accept", "application/json");

        List<NameValuePair> params = new ArrayList<>(4);

        params.add(new BasicNameValuePair("client_id", Environments.getInstance().getGitConfig().getClientID()));
        params.add(new BasicNameValuePair("client_secret", Environments.getInstance().getGitConfig().getClientSecret()));
        params.add(new BasicNameValuePair("code", codeApiData.code));
        params.add(new BasicNameValuePair("state", codeApiData.state));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        HttpResponse response = httpclient.execute(httppost);

        return Response
            .status(response.getStatusLine().getStatusCode())
            .entity(response.getEntity().getContent())
            .build();
    }

    @Path("1to1edmmmappings")
    public EdmmMappingsResource get1to1EdmmMappingsResource() {
        return new EdmmMappingsResource(EdmmMappingsResource.Type.ONE_TO_ONE);
    }

    @Path("edmmtypemappings")
    public EdmmMappingsResource getEdmmTypeMappingsResource() {
        return new EdmmMappingsResource(EdmmMappingsResource.Type.EXTENDS);
    }
}
