/*************************************************************************
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
 **************************************************************************/
package org.eclipse.winery.bpmn2bpel;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.winery.bpmn2bpel.parser.ParseException;
import org.eclipse.winery.bpmn2bpel.planwriter.PlanWriterException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * BPMN4Tosca2BpelTest Class for Testing.
 */
public class BPMN4Tosca2BpelTest {
    /**
     * Resources Dir for Test Files.
     */
    private static final String RESOURCESDIR =
            "src/test/resources/bpmn4tosca";

    /**
     * Timestamp for defining actual Test Results.
     */
    private String timeStamp;

    /**
     * Setup everything needed.
     *
     * @throws Exception
     *         If the Pattern is not correct.
     */
    @Before
    public void setUp() throws Exception {
        timeStamp = new SimpleDateFormat(
                "yyyy.MM.dd.HH.mm.ss")
                .format(new Date());
    }

    /**
     * Teardown after all Tests.
     *
     * @throws Exception
     *         If something goes wrong.
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Tests the Tranform  Json  to Zip Functionality.
     *
     * @throws ParseException
     *         If json contains errors.
     * @throws PlanWriterException
     *         if Plan writer has problems.
     * @throws MalformedURLException
     *         If file paths are not defined correctly.
     * @throws URISyntaxException
     *         If the Syntax of the URI contains an Error.
     */
    @Test
    public void testTransform() throws ParseException,
            PlanWriterException, MalformedURLException,
            URISyntaxException {
        URI srcUri = Paths.get(RESOURCESDIR,
                "bpmn4tosca.json").toUri();
        URI targetUri = Paths.get(RESOURCESDIR,
                timeStamp + "managementplan.zip").toUri();
        BPMN4Tosca2BpelTest.class.getResource(".");
        Bpmn4Tosca2Bpel transformer = new Bpmn4Tosca2Bpel();
        transformer.transform(srcUri, targetUri);
    }

    /**
     * Tests the Tranform  Json  to Zip Functionality with a Gateway.
     *
     * @throws ParseException
     *         If json contains errors.
     * @throws PlanWriterException
     *         if Plan writer has problems.
     * @throws MalformedURLException
     *         If file paths are not defined correctly.
     * @throws URISyntaxException
     *         If the Syntax of the URI contains an Error.
     */
    @Test
    public void testTransformGateway()
            throws ParseException, PlanWriterException,
            MalformedURLException, URISyntaxException {
        URI srcUri = Paths.get(RESOURCESDIR,
                "bpmn4tosca.exclusivegateway.json")
                .toUri();
        URI targetUri = Paths.get(RESOURCESDIR,
                timeStamp
                        + "managementplan"
                        + ".exclusivegateway.zip")
                .toUri();
        BPMN4Tosca2BpelTest.class.getResource(".");
        Bpmn4Tosca2Bpel transformer = new Bpmn4Tosca2Bpel();
        transformer.transform(srcUri, targetUri);
    }


    /**
     * Tests the Tranform  Json to Zip Functionality with a missing Node.
     *
     * @throws ParseException
     *         If json contains errors.
     * @throws PlanWriterException
     *         if Plan writer has problems.
     * @throws MalformedURLException
     *         If file paths are not defined correctly.
     * @throws URISyntaxException
     *         If the Syntax of the URI contains an Error.
     */
    @Test
    public void testTransformExceptionMissingNode()
            throws ParseException, PlanWriterException,
            MalformedURLException, URISyntaxException {
        URI srcUri = Paths.get(RESOURCESDIR,
                "bpmn2tosca_exceptionNode.json")
                .toUri();
        URI targetUri = Paths.get(RESOURCESDIR,
                timeStamp
                        + "managementplan_"
                        + "exceptionNode.zip")
                .toUri();
        BPMN4Tosca2BpelTest.class.getResource(".");
        Bpmn4Tosca2Bpel transformer = new Bpmn4Tosca2Bpel();
        try {
            transformer.transform(srcUri, targetUri);
        } catch (ParseException e) {
            return;
        }
        fail();
    }

    /**
     * Tests the Tranform  Json  to
     * Zip Functionality with a missing connection.
     *
     * @throws ParseException
     *         If json contains errors.
     * @throws PlanWriterException
     *         if Plan writer has problems.
     * @throws MalformedURLException
     *         If file paths are not defined correctly.
     * @throws URISyntaxException
     *         If the Syntax of the URI contains an Error.
     */
    @Test
    public void testTransformExceptionMissingConnection()
            throws ParseException, PlanWriterException,
            MalformedURLException, URISyntaxException {
        URI srcUri = Paths.get(RESOURCESDIR,
                "bpmn2tosca_exceptionConnection.json")
                .toUri();
        URI targetUri = Paths.get(RESOURCESDIR,
                timeStamp
                        + "managementplan_"
                        + "exceptionConnection.zip")
                .toUri();
        BPMN4Tosca2BpelTest.class.getResource(".");
        Bpmn4Tosca2Bpel transformer = new Bpmn4Tosca2Bpel();
        try {
            transformer.transform(srcUri, targetUri);
            fail();
        } catch (ParseException e) {
            return;
        }
    }

    /**
     * Tests the Tranform  Json  to Zip Functionality
     * with output without Destination.
     *
     * @throws ParseException
     *         If json contains errors.
     * @throws PlanWriterException
     *         if Plan writer has problems.
     * @throws MalformedURLException
     *         If file paths are not defined correctly.
     * @throws URISyntaxException
     *         If the Syntax of the URI contains an Error.
     */
    @Test
    public void testTransformGatewayOneWayOut()
            throws ParseException, PlanWriterException,
            MalformedURLException, URISyntaxException {
        URI srcUri = Paths.get(RESOURCESDIR,
                "bpmn4tosca.exclusivegatewayOneWayOut.json")
                .toUri();
        URI targetUri = Paths.get(RESOURCESDIR,
                timeStamp
                        + "managementplan."
                        + "exclusivegateway.zip")
                .toUri();
        BPMN4Tosca2BpelTest.class.getResource(".");
        Bpmn4Tosca2Bpel transformer = new Bpmn4Tosca2Bpel();
        try {
            transformer.transform(srcUri, targetUri);
            fail();
        } catch (Exception e) {
            return;
        }
    }

    /**
     * Tests the Tranform  Json  to Zip Functionality with
     * output two connections to the same Destination.
     *
     * @throws ParseException
     *         If json contains errors.
     * @throws PlanWriterException
     *         if Plan writer has problems.
     * @throws MalformedURLException
     *         If file paths are not defined correctly.
     * @throws URISyntaxException
     *         If the Syntax of the URI contains an Error.
     */
    @Test
    public void testTransformExceptionTwoConnections()
            throws ParseException, PlanWriterException,
            MalformedURLException, URISyntaxException {
        URI srcUri = Paths.get(RESOURCESDIR,
                "bpmn2tosca_exceptionTwoConnections.json")
                .toUri();
        URI targetUri = Paths.get(RESOURCESDIR,
                timeStamp
                        + "managementplan"
                        + "_exceptionConnection.zip")
                .toUri();
        BPMN4Tosca2BpelTest.class.getResource(".");
        Bpmn4Tosca2Bpel transformer = new Bpmn4Tosca2Bpel();
        try {
            transformer.transform(srcUri, targetUri);
            fail();
        } catch (ParseException e) {
            return;
        }

    }

}
