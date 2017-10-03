/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.admin;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.repository.configuration.Environment;
import org.eclipse.winery.repository.configuration.GitHubConfiguration;
import org.eclipse.winery.repository.rest.resources.admin.types.ConstraintTypesManager;
import org.eclipse.winery.repository.rest.resources.admin.types.PlanLanguagesManager;
import org.eclipse.winery.repository.rest.resources.admin.types.PlanTypesManager;
import org.eclipse.winery.repository.rest.resources.apiData.OAuthStateAndCodeApiData;

import io.swagger.annotations.Api;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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

	@POST
	@Path("githubaccesstoken")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGitHubAccessToken(OAuthStateAndCodeApiData codeApiData) throws Exception {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("https://github.com/login/oauth/access_token");
		httppost.setHeader("Accept", "application/json");

		List<NameValuePair> params = new ArrayList<NameValuePair>(4);

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
