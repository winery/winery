/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.entitytypes.nodetypes.reqandcapdefs;

import javax.ws.rs.core.MediaType;

import org.eclipse.winery.repository.PrefsTestEnabledGitBackedRepository;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

//@formatter:off

/*
 * import static com.jayway.restassured.RestAssured.*; import static
 * com.jayway.restassured.matcher.RestAssuredMatchers.*; import static
 * org.hamcrest.Matchers.*; import static
 * com.jayway.restassured.path.json.JsonPath.*;
 */


/**
 * REST-based testing of requirement definitions
 *
 * We use a fixed method sort order as we create resources in one test and work
 * with them in the next step
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Ignore("Working on old test repository")
public class RequirementDefinitionsTest {

	@BeforeClass
	public static void init() throws Exception {
		// enable git-backed repository
		new PrefsTestEnabledGitBackedRepository();

		// we use a half-filled repository
		((GitBasedRepository) Repository.INSTANCE).setRevisionTo("97fa997b92965d8bc84e86274b0203f1db7495c5");

		// we test on the Amazon EC2 node type
		// could be any other node type without requirement definitions
		//
		// the following URI is already encoded (copied from the browser URL field)
		RestAssured.urlEncodingEnabled = false;
		RestAssured.basePath = "/org.eclipse.winery.repository/nodetypes/http%253A%252F%252Fwww.example.org%252Ftosca%252Fnodetypes/Amazon_EC2/requirementdefinitions";
	}

	@Test
	public void test01_NoRequirementDefinitions() throws Exception {
		RestAssured.given()
			.header("Accept", MediaType.APPLICATION_JSON)
		.expect()
			.body(Matchers.equalTo("[]"))
		.when()
			.get("");
	}

	@Test
	public void test02_CreateRequirementDefinition() throws Exception {
		RestAssured.given()
			.parameter("name", "test")
		.expect()
			.statusCode(204)
		.when()
			.post("/");
	}

	@Test
	public void test03_NoConstraints() throws Exception {
		RestAssured.given()
			.header("Accept", MediaType.APPLICATION_JSON)
		.expect()
			.body(Matchers.equalTo("[]"))
		.when()
			.get("test/constraints/");
	}

	@Test
	public void test04_CreateConstraint() throws Exception {
		RestAssured.given()
			.body("<tosca:Constraint xmlns:tosca=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\" constraintType=\"http://www.example.org/constrainttype\"/>")
			.contentType(ContentType.XML)
		.expect()
			.statusCode(200)
			.body(Matchers.notNullValue())
		.when()
			.post("test/constraints/");
	}

	@Test
	public void test05_GetConstraint() throws Exception {
		Response response = RestAssured
			.given()
				.header("Accept", MediaType.APPLICATION_JSON)
			.expect()
				.statusCode(200)
			.when()
				.get("test/constraints/");

		// extract answer
		JsonPath jsonPath = JsonPath.from(response.asString());

		Assert.assertEquals("One id", jsonPath.getList("").size(),  1);

		String id = jsonPath.getString("[0]");

		// TODO: check content
		RestAssured
				.given()
					.header("Accept", MediaType.TEXT_XML)
				.expect()
					.statusCode(200)
				.when()
					.get("test/constraints/{id}/", id);

		// we also test the sub resource here
		// otherwise we had to transport the id throught the code via a global variable
		RestAssured
		.expect()
			.statusCode(200)
			.body(Matchers.is("http://www.example.org/constrainttype"))
		.when()
			.get("test/constraints/{id}/type", id);
	}

}
