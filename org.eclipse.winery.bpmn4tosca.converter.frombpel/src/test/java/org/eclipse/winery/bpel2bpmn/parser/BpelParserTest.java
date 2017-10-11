/*
 * *****************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Alex Frank - initial API and implementation
 * *****************************************************************************
 *
 */

package org.eclipse.winery.bpel2bpmn.parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.winery.bpel2bpmn.exception.ParseException;
import org.eclipse.winery.bpel2bpmn.model.Scope;
import org.eclipse.winery.bpel2bpmn.model.gen.TProcess;
import org.eclipse.winery.bpmn2bpel.model.EndTask;
import org.eclipse.winery.bpmn2bpel.model.ManagementTask;
import org.eclipse.winery.bpmn2bpel.model.StartTask;
import org.eclipse.winery.bpmn2bpel.model.param.StringParameter;
import org.eclipse.winery.bpmn2bpel.parser.JsonKeys;
import org.junit.Before;
import org.junit.Test;

import javax.xml.namespace.QName;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BpelParserTest {
    private static final URI TEST_FILE = getTestFile();
    private BpelParser bpelParser;

    /**
     * Returns the test file
     *
     * @return The BPEL test file
     */
    private static URI getTestFile() {
        URL resource = BpelParserTest.class.getClassLoader().getResource("MyTinyToDo_Bare_Docker_buildPlan.bpel");
        try {
            return resource.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Before
    public void beforeTest() {
        bpelParser = new BpelParser();
    }

    /**
     * Tests a successful parsing
     */
    @Test
    public void parse_Successful() throws Exception {
        JsonNode jsonNode = this.bpelParser.parse(TEST_FILE);
        assertEquals(3, jsonNode.size());
    }

    /**
     * Tests if a file not found exception
     */
    @Test(expected = ParseException.class)
    public void parse_fileNotExisting() throws Exception {
        JsonNode imaginaryFile = this.bpelParser
            .parse(new File("imaginaryFile").toURI());
        fail();
    }

    /**
     * Test createStartTask
     */
    @Test
    public void createStartTask() throws Exception {
        StartTask startTask = this.bpelParser.createStartTask(1);
        assertEquals(startTask.getId(), "element1");
        assertEquals(startTask.getType(), JsonKeys.NODE_TYPE_START_EVENT);
        assertEquals(startTask.getName(), JsonKeys.NODE_TYPE_START_EVENT);
    }

    /**
     * Test createEndTask
     */
    @Test
    public void createEndTask() throws Exception {
        EndTask endTask = this.bpelParser.createEndTask(1);
        assertEquals(endTask.getId(), "element1");
        assertEquals(endTask.getType(), JsonKeys.NODE_TYPE_END_EVENT);
        assertEquals(endTask.getName(), JsonKeys.NODE_TYPE_END_EVENT);
    }

    /**
     * Tests the assignParameters
     */
    @Test
    public void assignParameters() throws Exception {
        final Map<String, List<StringParameter>> scopeParameters = new HashMap<>();
        final List<Scope> scopes = new ArrayList<>();
        final StringParameter parameter1 = new StringParameter();
        parameter1.setName("Port");
        parameter1.setValue("80");
        final StringParameter parameter2 = new StringParameter();
        parameter2.setName("Host");
        parameter2.setValue("www.opentosca.org");

        final Scope testScope = new Scope();
        testScope.setScopeName("Test-Scope");
        ManagementTask managementTask = new ManagementTask();
        managementTask.setNodeTemplateId(new QName("Test-Scope"));
        StringParameter p1 = new StringParameter();
        p1.setValue("Value");
        p1.setName("Host");

        StringParameter p2 = new StringParameter();
        p2.setValue("Value");
        p2.setName("Port");
        managementTask.setInputParameters(Arrays.asList(p1, p2));
        testScope.setManagementTasks(Collections.singletonList(managementTask));

        scopes.add(testScope);
        scopeParameters.put("Test-Scope", Arrays.asList(parameter1, parameter2));

        this.bpelParser.assignParameters(scopeParameters, scopes);

        List<StringParameter> stringParameters = scopeParameters.get("Test-Scope");
        for (StringParameter stringParameter : stringParameters) {
            String name = stringParameter.getName();
            String value = stringParameter.getValue();
            switch (name) {
                case "Port":
                    assertEquals("80", value);
                    break;
                case "Host":
                    assertEquals("www.opentosca.org", value);
                    break;
                default:
                    fail(name + ": " + value);
                    break;
            }
        }
    }

    /**
     * Test unsuccessful unmarshalling
     */
    @Test(expected = ParseException.class)
    public void read_fileNotFound() throws Exception {
        bpelParser.read(new File("imaginaryfile").toURI());
        fail("This should fail");
    }

    /**
     * Test successful marshalling
     */
    @Test
    public void read_Successful() throws Exception {
        TProcess tProcess = bpelParser.read(TEST_FILE);
        assertEquals(tProcess.getName(), "MyTinyToDo_Bare_Docker_buildPlan");
    }

}
