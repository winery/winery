/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
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

import io.swagger.annotations.Api;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.consistencycheck.ConsistencyChecker;
import org.eclipse.winery.repository.backend.consistencycheck.ConsistencyCheckerConfiguration;
import org.eclipse.winery.repository.backend.consistencycheck.ConsistencyCheckerVerbosity;
import org.eclipse.winery.repository.backend.consistencycheck.ConsistencyErrorLogger;
import org.eclipse.winery.repository.configuration.Environment;
import org.eclipse.winery.repository.configuration.GitHubConfiguration;
import org.eclipse.winery.repository.rest.resources.admin.types.ConstraintTypesManager;
import org.eclipse.winery.repository.rest.resources.admin.types.PlanLanguagesManager;
import org.eclipse.winery.repository.rest.resources.admin.types.PlanTypesManager;
import org.eclipse.winery.repository.rest.resources.apiData.OAuthStateAndCodeApiData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

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
    public ConsistencyErrorLogger checkConsistency(@QueryParam("serviceTemplatesOnly") boolean serviceTemplatesOnly, @QueryParam("checkDocumentation") boolean checkDocumentation) {
        IRepository repo = RepositoryFactory.getRepository();
        EnumSet<ConsistencyCheckerVerbosity> verbosity = EnumSet.of(ConsistencyCheckerVerbosity.NONE);
        ConsistencyCheckerConfiguration config = new ConsistencyCheckerConfiguration(serviceTemplatesOnly, checkDocumentation, verbosity, repo);
        return ConsistencyChecker.checkCorruption(config);
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

        // get configuration and fill with default values if no configuration exists
        final GitHubConfiguration gitHubConfiguration = Environment.getGitHubConfiguration().orElse(new GitHubConfiguration("id", "secreat"));

        params.add(new BasicNameValuePair("client_id", gitHubConfiguration.getGitHubClientId()));
        params.add(new BasicNameValuePair("client_secret", gitHubConfiguration.getGitHubClientSecret()));
        params.add(new BasicNameValuePair("code", codeApiData.code));
        params.add(new BasicNameValuePair("state", codeApiData.state));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        HttpResponse response = httpclient.execute(httppost);

        return Response
            .status(response.getStatusLine().getStatusCode())
            .entity(response.getEntity().getContent())
            .build();
    }
}
