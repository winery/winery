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
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.bpmn2bpel.model.EndTask;
import org.eclipse.winery.bpmn2bpel.model.Link;
import org.eclipse.winery.bpmn2bpel.model.ManagementFlow;
import org.eclipse.winery.bpmn2bpel.model.ManagementTask;
import org.eclipse.winery.bpmn2bpel.model.Node;
import org.eclipse.winery.bpmn2bpel.model.StartTask;
import org.eclipse.winery.bpmn2bpel.model.Task;
import org.eclipse.winery.bpmn2bpel.model.param.ConcatParameter;
import org.eclipse.winery.bpmn2bpel.model.param.DeploymentArtefactParameter;
import org.eclipse.winery.bpmn2bpel.model.param.ImplementationArtefactParameter;
import org.eclipse.winery.bpmn2bpel.model.param.ParamType;
import org.eclipse.winery.bpmn2bpel.model.param.Parameter;
import org.eclipse.winery.bpmn2bpel.model.param.PlanParameter;
import org.eclipse.winery.bpmn2bpel.model.param.StringParameter;
import org.eclipse.winery.bpmn2bpel.model.param.TopologyParameter;
import org.eclipse.winery.bpmn2bpel.parser.Bpmn4JsonParser;
import org.eclipse.winery.bpmn2bpel.parser.ParseException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Disabled
public class JsonParserTest {

    protected static String RESOURCES_DIR = "src/test/resources/bpmn4tosca";

    @Test
    public void testParse() throws MalformedURLException, ParseException, URISyntaxException {
        Bpmn4JsonParser parser = new Bpmn4JsonParser();
        URI srcUri = Paths.get("src/test/resources/bpmn4tosca/bpmn4tosca.json").toUri();
        ManagementFlow actualFlow = parser.parse(srcUri);
        ManagementFlow expectedFlow = createReferenceFlow();

        assertNodeSets(expectedFlow.vertexSet(), actualFlow.vertexSet());
        assertLinkSets(expectedFlow.edgeSet(), actualFlow.edgeSet());
    }

    public static void assertNodeSets(Set<Node> expectedNodes, Set<Node> actualNodes) {
        assertEquals(expectedNodes.size(), actualNodes.size());

        for (Node expectedNode : expectedNodes) {
            Node actualNode = getNodeById(expectedNode.getId(), actualNodes);
            if (actualNode != null) {
                assertNodes(expectedNode, actualNode);
            } else {
                fail("Node with id " + expectedNode.getId() + " could not be found");
            }
        }
    }

    public static void assertLinkSets(Set<Link> expectedLinks, Set<Link> actualLinks) {
        //assertEquals(expectedNodes.size(), actualNodes.size());
        assertEquals(expectedLinks.size(), actualLinks.size());
    }

    public static void assertLink(Link expectedLink, Link actualLink) {
        assertEquals("Link source: id", expectedLink.getSource().getId(), actualLink.getSource().getId());
        assertEquals("Link target :id", expectedLink.getTarget().getId(), actualLink.getTarget().getId());
    }

    private static Node getNodeById(String id, Set<Node> nodeSet) {
        for (Node node : nodeSet) {
            if (node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }

    public static void assertNodes(Node expected, Node actual) {
        //assertEquals(expected.getId(), actual.getId());
        assertEquals("node: id ", expected.getId(), actual.getId());

        /* Just tasks contain further properties that have to be tested */
        if (expected instanceof Task) {
            assertTasks((Task) expected, (Task) actual);
        }
    }

    public static void assertTasks(Task expected, Task actual) {
        assertEquals("Management node: id ", expected.getId(), actual.getId());
        assertEquals("Management node: name", expected.getName(), actual.getName());

        if (expected instanceof StartTask) {
            assertStartTask((StartTask) expected, (StartTask) actual);
        } else if (expected instanceof EndTask) {
            assertEndTask((EndTask) expected, (EndTask) actual);
        } else if (expected instanceof ManagementTask) {
            assertMngmtTasks((ManagementTask) expected, (ManagementTask) actual);
        } else {
            fail("Task of type " + actual.getName() + " unknown");
        }
    }

    public static void assertMngmtTasks(ManagementTask expected, ManagementTask actual) {
        assertEquals(expected.getNodeTemplateId(), actual.getNodeTemplateId(), "Management node: topology template");
        assertEquals(expected.getNodeOperation(), actual.getNodeOperation(), "Management node: topology operation");
        assertParameters(expected.getInputParameters(), actual.getInputParameters());
        assertParameters(expected.getOutputParameters(), actual.getOutputParameters());
    }

    public static void assertStartTask(StartTask expected, StartTask actual) {
        assertParameters(expected.getOutputParameters(), actual.getOutputParameters());
    }

    public static void assertEndTask(EndTask expected, EndTask actual) {
        assertParameters(expected.getInputParameters(), actual.getInputParameters());
    }

    public static void assertParameters(List<Parameter> expected, List<Parameter> actual) {
        assertEquals(expected.size(), actual.size(), "Number of parameters");

        for (Parameter expectedParam : actual) {
            Parameter actualParam = getParameterByName(expectedParam.getName(), actual);
            if (actualParam != null) {
                assertParameter(expectedParam, actualParam);
            } else {
                fail("Parameter with name " + expectedParam.getName() + " could not be found");
            }
        }
    }

    public static void assertParameter(Parameter expected, Parameter actual) {
        assertEquals(expected.getName(), actual.getName(), "Parameter: name");
        assertEquals(expected.getType(), actual.getType(), "Parameter: type");
        assertEquals(expected.getValue(), actual.getValue(), "Parameter: value");
    }

    private static Parameter getParameterByName(String name, List<Parameter> parameters) {
        for (Parameter param : parameters) {
            if (param.getName().equals(name)) {
                return param;
            }
        }
        return null;
    }

    private static ManagementFlow createReferenceFlow() {
        ManagementFlow flow = new ManagementFlow();

        StartTask startTask = new StartTask();
        startTask.setId("element6");
        startTask.setName("StartEvent");
        startTask.addOutputParameter(createParameter("SSHUserInput", ParamType.TOPOLOGY, "StartEvent.SSHUserInput"));
        flow.addVertex(startTask);

        ManagementTask createEC2Task = new ManagementTask();
        createEC2Task.setId("element10");
        createEC2Task.setName("CreateAmazonEC2Task");
        createEC2Task.setNodeTemplateId(QName.valueOf("AmazonEC2NodeTemplate"));
        createEC2Task.setInterfaceName("ec2Interface");
        createEC2Task.setNodeOperation("CreateVM");
        createEC2Task.addInputParameter(createParameter("Size", ParamType.STRING, "t1.medium"));
        createEC2Task.addInputParameter(createParameter("SSHUser", ParamType.PLAN, "StartEvent.SSHUserInput"));
        createEC2Task.addInputParameter(createParameter("SSHKey", ParamType.STRING, "myKey"));
        createEC2Task.addInputParameter(createParameter("ImageID", ParamType.TOPOLOGY, "UbuntuVM.ImageID"));
        createEC2Task.addInputParameter(createParameter("AccountUser", ParamType.STRING, ""));
        createEC2Task.addInputParameter(createParameter("AccountPassword", ParamType.STRING, ""));
        createEC2Task.addOutputParameter(createParameter("IPAddress", ParamType.TOPOLOGY, "UbuntuVM.IPAddress"));
        flow.addVertex(createEC2Task);

        ManagementTask runUbuntuTask = new ManagementTask();
        runUbuntuTask.setId("element38");
        runUbuntuTask.setName("InstallUbuntutTask");
        runUbuntuTask.setNodeTemplateId(QName.valueOf("UbuntuNodeTemplate"));
        runUbuntuTask.setInterfaceName("ubunutuInterface");
        runUbuntuTask.setNodeOperation("installUbuntu");
        runUbuntuTask.addInputParameter(createParameter("script", ParamType.IA, "{http://www.opentosca.org}ApacheWebserverInstallImplementation"));
        runUbuntuTask.addOutputParameter(createParameter("result", ParamType.STRING, ""));
        flow.addVertex(runUbuntuTask);

        EndTask endTask = new EndTask();
        endTask.setId("element45");
        endTask.setName("EndEvent");
        endTask.addInputParameter(createParameter("AppURL", ParamType.CONCAT, "http://,UbuntuVM.IPAddress,:8080/,PHPApplication.ID"));
        flow.addVertex(endTask);

        flow.addEdge(startTask, createEC2Task);
        flow.addEdge(createEC2Task, runUbuntuTask);
        flow.addEdge(runUbuntuTask, endTask);

        return flow;
    }

    private static Parameter createParameter(String name, ParamType type, String value) {

        Parameter param = null;
        switch (type) {
            case CONCAT:
                param = new ConcatParameter(); // TODO add concat operands
                break;
            case DA:
                param = new DeploymentArtefactParameter();
                break;
            case IA:
                param = new ImplementationArtefactParameter();
                break;
            case PLAN:
                param = new PlanParameter(); // TODO add task name
                break;
            case STRING:
                param = new StringParameter();
                break;
            case TOPOLOGY:
                param = new TopologyParameter();
                break;
            default:
                fail("Invalid paramet type: " + type);
        }

        param.setName(name);
        param.setValue(value);

        return param;
    }
}
