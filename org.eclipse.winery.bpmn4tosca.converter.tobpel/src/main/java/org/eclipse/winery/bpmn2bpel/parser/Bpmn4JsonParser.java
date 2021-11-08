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
package org.eclipse.winery.bpmn2bpel.parser;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.bpmn2bpel.model.ConditionBranch;
import org.eclipse.winery.bpmn2bpel.model.EndTask;
import org.eclipse.winery.bpmn2bpel.model.ManagementFlow;
import org.eclipse.winery.bpmn2bpel.model.ManagementTask;
import org.eclipse.winery.bpmn2bpel.model.Node;
import org.eclipse.winery.bpmn2bpel.model.OrGatewayMerge;
import org.eclipse.winery.bpmn2bpel.model.OrGatewaySplit;
import org.eclipse.winery.bpmn2bpel.model.StartTask;
import org.eclipse.winery.bpmn2bpel.model.Task;
import org.eclipse.winery.bpmn2bpel.model.param.ConcatParameter;
import org.eclipse.winery.bpmn2bpel.model.param.DeploymentArtefactParameter;
import org.eclipse.winery.bpmn2bpel.model.param.ImplementationArtefactParameter;
import org.eclipse.winery.bpmn2bpel.model.param.Parameter;
import org.eclipse.winery.bpmn2bpel.model.param.PlanParameter;
import org.eclipse.winery.bpmn2bpel.model.param.StringParameter;
import org.eclipse.winery.bpmn2bpel.model.param.TopologyParameter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO describe expected JSON format here
 */
public class Bpmn4JsonParser extends Parser {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bpmn4JsonParser.class);

    @Override
    public ManagementFlow parse(URI jsonFileUrl) throws ParseException {

        try {
            // general method, same as with data binding
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            // (note: can also use more specific type, like ArrayNode or
            // ObjectNode!)
            JsonNode rootNode = mapper.readValue(jsonFileUrl.toURL(), JsonNode.class);

            String prettyPrintedJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
            LOGGER.debug("Creating management flow from following Json model:" + prettyPrintedJson);

            Map<String, Node> nodeMap = new HashMap<>();
            /* Contains the ids (values) of the target nodes of a certain node
             * (key is node id of this node) */
            Map<String, Set<String>> nodeWithTargetsMap = new HashMap<>();

            /* Create model objects from Json nodes */
            LOGGER.debug("Creating node models...");
            for (JsonNode jsonNode : rootNode) {
                /*
                 * As top level elements just start events, end events, gateway and
                 * management tasks expected which are transformed to tasks in
                 * our management model
                 */
                Node node = createNodeFromJson(jsonNode);
                /*
                 * Node may be null if it could not be created due to missing or
                 * incorrect fields/values in the Json node
                 */
                if (node != null) {
                    nodeMap.put(node.getId(), node);
                    nodeWithTargetsMap.put(node.getId(), extractNodeTargetIds(jsonNode));
                } else {
                    String ignoredJsonNode = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
                    LOGGER.warn("No model element could be created from following node due to missing or invalid keys/values :" + ignoredJsonNode);
                }
            }

            /*
             * Now since all node models are created they can be linked with each other in the management flow
             */
            return new SortParser(nodeMap, nodeWithTargetsMap).buildManagementFlow();
        } catch (Exception e) {
            LOGGER.error("Error while creating management flow : " + e.getMessage());
            throw new ParseException(e);
        }
    }

    protected Node createNodeFromJson(JsonNode jsonNode) {
        // TODO check if type attributes are set and are correct

        if (!hasRequiredFields(jsonNode, Arrays.asList(JsonKeys.TYPE, JsonKeys.NAME, JsonKeys.ID))) {
            LOGGER.warn("Ignoring gateway/task/event node: One of the fields '" + JsonKeys.TYPE + "', '" + JsonKeys.NAME + "' or '"
                + JsonKeys.ID + "' is missing");
            return null;
        }

        Node node = null;
        String nodeType = jsonNode.get(JsonKeys.TYPE).asText();
        String nodeName = jsonNode.get(JsonKeys.NAME).asText();
        String nodeId = jsonNode.get(JsonKeys.ID).asText();

        LOGGER.debug("Parsing JSON task or event node with id '" + nodeId + "', name '" + nodeName + "', type '" + nodeType
            + "'");

        switch (nodeType) {
            case JsonKeys.NODE_TYPE_START_EVENT:
                node = createStartTaskFromJson(jsonNode);
                break;
            case JsonKeys.NODE_TYPE_MGMT_TASK:
                node = createManagementTaskFromJson(jsonNode);
                break;
            case JsonKeys.NODE_TYPE_END_EVENT:
                node = createEndTaskFromJson(jsonNode);
                break;
            case JsonKeys.NODE_TYPE_GATEWAY_EXCLUSIVE:
                node = createOrGatewaySplitFromJson(jsonNode);
                break;
            case JsonKeys.NODE_TYPE_GATEWAY_EXCLUSIVE_END:
                node = createOrGatewayMergeFromJson(jsonNode);
                break;
            default:
                LOGGER.warn("Ignoring node: type '" + nodeType + "' is unkown");
                return null;
        }

        /* Set generic node attributes */
        node.setId(nodeId);
        node.setName(nodeName);
        node.setType(nodeType);
        if (node instanceof Task) {
            loadParameter4Task((Task) node, jsonNode);
        }

        return node;
    }

    private void loadParameter4Task(Task task, JsonNode jsonNode) {
        /* Add input parameters to task */
        JsonNode inputParams = jsonNode.get(JsonKeys.INPUT);
        String taskId = jsonNode.get(JsonKeys.ID).asText();

        if (inputParams != null) {
            /*
             * Iterator map required to retrieve the name of the parameter node
             *
             * @see
             * http://stackoverflow.com/questions/7653813/jackson-json-get-node-
             * name-from-json-tree
             */
            Iterator<Map.Entry<String, JsonNode>> inputParamIter = inputParams.fields();
            while (inputParamIter.hasNext()) {
                Map.Entry<String, JsonNode> inputParamEntry = (Map.Entry<String, JsonNode>) inputParamIter.next();
                Parameter inputParam = createParameterFromJson(inputParamEntry.getKey(), inputParamEntry.getValue());
                task.addInputParameter(inputParam);
            }
        } else {
            LOGGER.debug("No input parameters found for node with id '" + taskId + "'");
        }

        /* Add output Parameters to task */
        JsonNode outputParams = jsonNode.get(JsonKeys.OUTPUT);
        if (outputParams != null) {
            Iterator<Map.Entry<String, JsonNode>> outputParamIter = outputParams.fields();
            while (outputParamIter.hasNext()) {
                Map.Entry<String, JsonNode> outputParamEntry = (Map.Entry<String, JsonNode>) outputParamIter.next();
                Parameter outputParam = createParameterFromJson(outputParamEntry.getKey(), outputParamEntry.getValue());
                task.addOutputParameter(outputParam);
            }
        } else {
            LOGGER.debug("No output parameters found for node with id '" + taskId + "'");
        }
    }

    protected StartTask createStartTaskFromJson(JsonNode startTaskNode) {
        return new StartTask();
    }

    protected EndTask createEndTaskFromJson(JsonNode endTaskNode) {
        return new EndTask();
    }

    protected OrGatewaySplit createOrGatewaySplitFromJson(JsonNode jsonNode) {
        OrGatewaySplit gatewaySplit = new OrGatewaySplit();
        JsonNode conditionsNode = jsonNode.findValue(JsonKeys.CONDITIONS);

        ConditionBranch defaultBranch = null;

        if (conditionsNode != null && conditionsNode.isArray()) {
            for (JsonNode entry : conditionsNode) {
                if (hasRequiredFields(entry, Collections.singletonList(JsonKeys.ID))) {

                    String id = entry.get(JsonKeys.ID).asText();

                    String condition = "";
                    if (entry.has(JsonKeys.CONDITION)) {
                        condition = entry.get(JsonKeys.CONDITION).asText();
                    }

                    boolean isDefault = false;
                    if (entry.has(JsonKeys.DEFAULT)) {
                        isDefault = entry.get(JsonKeys.DEFAULT).asBoolean();
                    }

                    ConditionBranch branch = new ConditionBranch(id, condition, isDefault);

                    if (isDefault) { // default branch
                        defaultBranch = branch;
                    } else {
                        gatewaySplit.getBranchList().add(branch);
                    }
                }
            }
        }

        if (defaultBranch != null) {
            gatewaySplit.getBranchList().add(defaultBranch);
        }
        return gatewaySplit;
    }

    protected OrGatewayMerge createOrGatewayMergeFromJson(JsonNode jsonNode) {
        return new OrGatewayMerge();
    }

    protected ManagementTask createManagementTaskFromJson(JsonNode managementTaskNode) {

        if (!hasRequiredFields(managementTaskNode, Arrays.asList(JsonKeys.NODE_TEMPLATE, JsonKeys.NODE_OPERATION))) {
            LOGGER.warn("Ignoring mangement node: One of the fields '" + JsonKeys.NODE_TEMPLATE + "' or '"
                + JsonKeys.NODE_OPERATION + "' is missing");
            return null;
        }
        String nodeTemplate = managementTaskNode.get(JsonKeys.NODE_TEMPLATE).asText();
        String nodeInterfaceName = managementTaskNode.get(JsonKeys.NODE_INTERFACE_NAME).asText();
        String nodeOperation = managementTaskNode.get(JsonKeys.NODE_OPERATION).asText();

        LOGGER.debug("Creating management task with id '" + managementTaskNode.get(JsonKeys.ID) + "', name '" + managementTaskNode.get(JsonKeys.NAME)
            + "', node template '" + nodeTemplate + "', node operation '" + "', node operation '" + nodeOperation + "'");

        ManagementTask task = new ManagementTask();
        task.setNodeTemplateId(QName.valueOf(nodeTemplate));
        task.setNodeOperation(nodeOperation);
        task.setInterfaceName(nodeInterfaceName);

        return task;
    }

    protected Parameter createParameterFromJson(String paramName, JsonNode paramNode) {

        if (!hasRequiredFields(paramNode, Arrays.asList(JsonKeys.TYPE, JsonKeys.VALUE))) {
            LOGGER.warn("Ignoring parameter node: One of the fields '" + JsonKeys.TYPE + "' or '"
                + JsonKeys.VALUE + "' is missing");
            return null;
        }
        String paramType = paramNode.get(JsonKeys.TYPE).asText();
        String paramValue = paramNode.get(JsonKeys.VALUE).asText();

        LOGGER.debug("Parsing JSON parameter node with name '" + paramName + "', type '" + paramType + "' and value '" + paramValue + "'");

        Parameter param = null;
        switch (paramType) {
            case JsonKeys.PARAM_TYPE_VALUE_CONCAT:
                param = new ConcatParameter(); // TODO add concat operands
                break;
            case JsonKeys.PARAM_TYPE_VALUE_DA:
                param = new DeploymentArtefactParameter();
                break;
            case JsonKeys.PARAM_TYPE_VALUE_IA:
                param = new ImplementationArtefactParameter();
                break;
            case JsonKeys.PARAM_TYPE_VALUE_PLAN:
                param = new PlanParameter(); // TODO add task name
                break;
            case JsonKeys.PARAM_TYPE_VALUE_STRING:
                param = new StringParameter();
                break;
            case JsonKeys.PARAM_TYPE_VALUE_TOPOLOGY:
                param = new TopologyParameter();
                break;
            default:
                LOGGER.warn("JSON parameter type '" + paramType + "' unknown");
                return null;
        }

        /* Set generic parameter attributes */
        param.setName(paramName);
        param.setValue(paramValue);

        return param;
    }

    protected Set<String> extractNodeTargetIds(JsonNode node) {
        Set<String> linkTargetIds = new HashSet<String>();
        /* Look for the 'connections' element within the node or its children */
        JsonNode connectionsNode = node.findValue(JsonKeys.CONNECTIONS);
        /*
         * The connection node hosts an array of all outgoing connections to
         * other nodes
         */
        if (connectionsNode != null && connectionsNode.isArray()) {
            for (JsonNode connectionEntry : connectionsNode) {
                /*
                 * Should always be true as the connection entry is the id of
                 * the target node
                 */
                if (connectionEntry.isTextual()) {
                    linkTargetIds.add(connectionEntry.asText());
                } else {
                    // TODO warn
                }
            }
        } else {
            LOGGER.debug("Node with id '" + node.get(JsonKeys.ID) + "' has no connections to other nodes");
            return null;
        }
        return linkTargetIds;
    }

    protected boolean hasRequiredFields(JsonNode jsonNode, List<String> reqFieldNames) {

        /* Returns false if one of the field names is missing */
        for (String reqField : reqFieldNames) {
            if (jsonNode.get(reqField) == null) {
                return false;
            }
        }
        return true;
    }
}
