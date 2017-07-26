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
 *****************************************************************************/
package org.eclipse.winery.bpmn2bpel;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.winery.bpmn2bpel.parser.Bpmn4JsonParser;
import org.eclipse.winery.bpmn2bpel.parser.ParseException;

/**
 * Class BpelPlanArtefactWriterTest.
 */
public class BpelPlanArtefactWriterTest {

    /**
     * Setup Method - runs before tests.
     * @throws Exception if Errors occur
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * tearDown Method - runs after Tests.
     * @throws Exception if Errors occur
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link BpelPlanArtefactWriter#writePlan(ManagementFlow, java.net.URL)}.
     *      *
     * @throws ParseException
     *         If json contains errors.
     * @throws MalformedURLException
     *         If file paths are not defined correctly.
     * @throws URISyntaxException
     *         If the Syntax of the URI contains an Error.

     */
    @Test
    public void testWritePlan() throws MalformedURLException,
            ParseException, URISyntaxException {
        Bpmn4JsonParser parser = new Bpmn4JsonParser();
        URI uri = Paths.get("src/test/resources/bpmn4tosca/bpmn4tosca.json")
                .toUri();
        parser.parse(uri);

    }

}
