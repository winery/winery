/*******************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * Copyright (c) 2017 ZTE Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Sebastian Wagner - initial API and implementation
 *     ZTE - support of more gateways
 *******************************************************************************/
package org.eclipse.winery.bpmn2bpel;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.eclipse.winery.bpmn2bpel.parser.ParseException;
import org.eclipse.winery.bpmn2bpel.planwriter.PlanWriterException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class BPMN4Tosca2BpelTest {

	protected static String RESOURCES_DIR = "src/test/resources/bpmn4tosca";


	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTransform() throws ParseException, PlanWriterException, MalformedURLException, URISyntaxException {
		URI srcUri = Paths.get(RESOURCES_DIR, "bpmn4tosca.json").toUri();
		URI targetUri = Paths.get(RESOURCES_DIR, "managementplan.zip").toUri();
		BPMN4Tosca2BpelTest.class.getResource(".");
		Bpmn4Tosca2Bpel transformer = new Bpmn4Tosca2Bpel();
		transformer.transform(srcUri, targetUri);
	}
	
	@Test
	public void testTransformGateway()
			throws ParseException, PlanWriterException, MalformedURLException, URISyntaxException {
		URI srcUri = Paths.get(RESOURCES_DIR, "bpmn4tosca.exclusivegateway.json").toUri();
		URI targetUri = Paths.get(RESOURCES_DIR, "managementplan.exclusivegateway.zip").toUri();
		BPMN4Tosca2BpelTest.class.getResource(".");
		Bpmn4Tosca2Bpel transformer = new Bpmn4Tosca2Bpel();
		transformer.transform(srcUri, targetUri);
	}

}
