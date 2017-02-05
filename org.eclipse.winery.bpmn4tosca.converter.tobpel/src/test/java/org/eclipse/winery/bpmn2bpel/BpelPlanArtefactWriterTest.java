/*******************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Sebastian Wagner - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.bpmn2bpel;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.eclipse.winery.bpmn2bpel.planwriter.BpelPlanArtefactWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.winery.bpmn2bpel.model.ManagementFlow;
import org.eclipse.winery.bpmn2bpel.parser.Bpmn4JsonParser;
import org.eclipse.winery.bpmn2bpel.parser.ParseException;


public class BpelPlanArtefactWriterTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link BpelPlanArtefactWriter#writePlan(ManagementFlow, java.net.URL)}.
	 */
	@Test
	public void testWritePlan() throws MalformedURLException, ParseException, URISyntaxException {
		Bpmn4JsonParser parser = new Bpmn4JsonParser();
		URI uri = Paths.get("src/test/resources/bpmn4tosca/bppmn4tosca.json").toUri();
		//Path testBpmn4JsonFile = Paths.get("C:/temp/bpmn4tosca/bppmn4tosca.json");
		ManagementFlow mngmtFlow = parser.parse(uri);

//		BpelPlanArtefactWriter writer = new BpelPlanArtefactWriter();
//		writer.writePlan(mngmtFlow, null);
	}

}
