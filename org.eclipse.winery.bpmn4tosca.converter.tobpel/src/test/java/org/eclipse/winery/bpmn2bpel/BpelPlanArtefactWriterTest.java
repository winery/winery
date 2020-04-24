/*******************************************************************************
 * Copyright (c) 2015-2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.bpmn2bpel;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.eclipse.winery.bpmn2bpel.model.ManagementFlow;
import org.eclipse.winery.bpmn2bpel.parser.Bpmn4JsonParser;
import org.eclipse.winery.bpmn2bpel.parser.ParseException;
import org.eclipse.winery.bpmn2bpel.planwriter.BpelPlanArtefactWriter;

import org.junit.jupiter.api.Test;

public class BpelPlanArtefactWriterTest {

    /**
     * Test method for {@link BpelPlanArtefactWriter#writePlan(ManagementFlow, java.net.URL)}.
     */
    @Test
    public void testWritePlan() throws MalformedURLException, ParseException, URISyntaxException {
        Bpmn4JsonParser parser = new Bpmn4JsonParser();
        URI uri = Paths.get("src/test/resources/bpmn4tosca/bpmn4tosca.json").toUri();
        //Path testBpmn4JsonFile = Paths.get("C:/temp/bpmn4tosca/bpmn4tosca.json");
        ManagementFlow mngmtFlow = parser.parse(uri);

//		BpelPlanArtefactWriter writer = new BpelPlanArtefactWriter();
//		writer.writePlan(mngmtFlow, null);
    }
}
