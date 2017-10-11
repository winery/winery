/******************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Alex Frank - initial API and implementation
 ******************************************************************************/
package org.eclipse.winery.bpel2bpmn.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.eclipse.winery.bpel2bpmn.exception.ParseException;
import org.eclipse.winery.bpel2bpmn.model.Scope;
import org.eclipse.winery.bpel2bpmn.model.gen.TProcess;
import org.eclipse.winery.bpel2bpmn.utils.NullChecker;
import org.eclipse.winery.bpmn2bpel.model.EndTask;
import org.eclipse.winery.bpmn2bpel.model.ManagementTask;
import org.eclipse.winery.bpmn2bpel.model.StartTask;
import org.eclipse.winery.bpmn2bpel.model.param.StringParameter;
import org.eclipse.winery.bpmn2bpel.parser.JsonKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * The main entry point for the BPEL parsing
 */
public class BpelParser {
    /**
     * The Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BpelParser.class);
    /**
     * The JAXB Context (package name of the model)
     */
    private static final String CONTEXT = "org.eclipse.winery.bpel2bpmn.model.gen";
    /**
     * Parser for the input parameters
     */
    private final InputParameterParser inputParameterParser = new InputParameterParser();
    /**
     * Parser for the scopes
     */
    private final ScopeParser flowParser = new ScopeParser();

    /**
     * Parses a BPEL file and returns a {@link JsonNode}
     *
     * @param uri The {@link URI} to the file
     * @return The parsed BPEL as {@link JsonNode}
     * @throws ParseException If something happens during the parsing
     */
    public JsonNode parse(URI uri) throws ParseException {
        TProcess process = read(uri);
        Map<String, List<StringParameter>> inputParameters = inputParameterParser.parse(process);
        List<Scope> linkedManagementNodes = this.flowParser.parseScopes(process);
        this.assignParameters(inputParameters, linkedManagementNodes);
        return this.sort(linkedManagementNodes);
    }

    /**
     * Parses the raw BPEL content and returns a raw JSON String
     *
     * @param input The raw BPEL content
     * @return The raw JSON String
     * @throws ParseException If something happens during the parsing
     */
    public String parse(String input) throws ParseException {
        TProcess process = read(input);
        Map<String, List<StringParameter>> inputParameters = inputParameterParser.parse(process);
        List<Scope> linkedManagementNodes = this.flowParser.parseScopes(process);
        this.assignParameters(inputParameters, linkedManagementNodes);
        try {
            return new ObjectMapper().writer(SerializationFeature.INDENT_OUTPUT).writeValueAsString(
                this.sort(linkedManagementNodes));
        } catch (JsonProcessingException e) {
            throw new ParseException(e);
        }
    }

    /**
     * Sorts the scopes and puts them into the right order
     * (e.g. Ubuntu -> Apache -> PHPApp)
     *
     * @param scopes A list of scopes that shall be sorted
     * @return Sorts the list and puts them into a {@link JsonNode}
     */
    protected JsonNode sort(List<Scope> scopes) {
        ArrayNode nodes = JsonNodeFactory.instance.arrayNode();
        int elementId = 0;
        StartTask startTask;
        EndTask endTask;
        List<ManagementTask> tasks = scopes.stream()
            .map(Scope::getManagementTasks)
            .collect(LinkedList::new, LinkedList::addAll, LinkedList::addAll);

        ListIterator<ManagementTask> iterator = tasks.listIterator();
        ManagementTask managementTask = null;

        for (int i = 0; i < tasks.size(); i++) {
            managementTask = tasks.get(i);
            String elementName = "element" + (++elementId);
            managementTask.setId(elementName);
            if (i == 0) {
                startTask = createStartTask(0);
                startTask.getConnections().add(managementTask.getId());
                nodes.addPOJO(startTask);
            } else {
                ManagementTask prevTask = tasks.get(i - 1);
                prevTask.getConnections().add(managementTask.getId());
            }
            nodes.addPOJO(managementTask);
        }

        if (managementTask != null) {
            endTask = createEndTask(++elementId);
            managementTask.getConnections().add(endTask.getId());
            nodes.addPOJO(endTask);
        }

        return nodes;
    }

    /**
     * Creates a {@link StartTask}
     *
     * @param elementId The elementId of the {@link StartTask#id}
     * @return A new {@link StartTask}
     */
    protected StartTask createStartTask(int elementId) {
        StartTask startTask = new StartTask();
        startTask.setId("element" + elementId);
        startTask.setType(JsonKeys.NODE_TYPE_START_EVENT);
        startTask.setName(JsonKeys.NODE_TYPE_START_EVENT);
        return startTask;
    }

    /**
     * Creates a {@link EndTask}
     *
     * @param elementId The elementId of the {@link EndTask#id}
     * @return A new {@link EndTask}
     */
    protected EndTask createEndTask(int elementId) {
        EndTask endTask = new EndTask();
        endTask.setId("element" + elementId);
        endTask.setType(JsonKeys.NODE_TYPE_END_EVENT);
        endTask.setName(JsonKeys.NODE_TYPE_END_EVENT);
        return endTask;
    }

    /**
     * Assigns the parameters to the correct scopes
     *
     * @param scopeParameters The parameters of the different scopes
     * @param scopes          The scopes
     */
    protected void assignParameters(Map<String, List<StringParameter>> scopeParameters, List<Scope> scopes) {
        //Get parameters for scope
        for (Scope scope : scopes) {
            String scopeName = scope.getScopeName();
            List<StringParameter> parameters = scopeParameters.getOrDefault(scopeName, Collections.emptyList());

            scope.getManagementTasks().forEach(mgmnt -> {
                String templateName = mgmnt.getNodeTemplateId().getLocalPart();
                //Set Scope parameter values
                parameters.forEach(stringParameter -> NullChecker.check(mgmnt
                    .getInputParameter(stringParameter.getName()))
                    .ifPresent(parameter -> parameter.setValue(parameter.getValue())));

                //Set template parameter values
                List<StringParameter> templateParameters = scopeParameters
                    .getOrDefault(templateName, Collections.emptyList());

                templateParameters.forEach(stringParameter -> NullChecker.check(mgmnt
                    .getInputParameter(stringParameter.getName()))
                    .ifPresent(parameter -> parameter.setValue(stringParameter.getValue())));
            });

        }
    }

    /**
     * Unmarshals the BPEL file and returns the root element {@link TProcess}
     *
     * @param uri The {@link URI} to the file
     * @return The {@link TProcess}
     * @throws ParseException If something happens during unmarshalling
     */
    protected TProcess read(URI uri) throws ParseException {
        LOGGER.debug("Unmarshalling '{}'", uri);
        try {
            final List<String> lines = Files.readAllLines(Paths.get(uri));
            final String bpelContent = String.join("", lines);
            return this.read(bpelContent);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    /**
     * Unmarshals the raw BPEL content and returns the root element {@link TProcess}
     *
     * @param content The raw BPEL content
     * @return The {@link TProcess}
     * @throws ParseException If something happens during unmarshalling
     */
    protected TProcess read(String content) throws ParseException {
        LOGGER.debug("Unmarshalling '{}'", content);
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(CONTEXT);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<TProcess> jaxbElement = (JAXBElement<TProcess>) unmarshaller.unmarshal(
                new InputSource(new StringReader(content))
            );

            return jaxbElement.getValue();
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }
}
