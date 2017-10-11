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

import org.eclipse.winery.bpel2bpmn.exception.ParseException;
import org.eclipse.winery.bpel2bpmn.model.gen.*;
import org.eclipse.winery.bpel2bpmn.utils.ObjectSearcher;
import org.eclipse.winery.bpel2bpmn.utils.ParameterFilter;
import org.eclipse.winery.bpel2bpmn.utils.TypeMapper;
import org.eclipse.winery.bpmn2bpel.model.param.StringParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for the input parameters
 */
public class InputParameterParser {
    /**
     * The Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(InputParameterParser.class);
    /**
     * The pattern of the parameters
     */
    private static final Pattern PATTERN_PARAMETER = Pattern.compile("^prop_(?<name>[A-Z].*)_(?<variable>.*)$");
    /**
     * The name of the nodetemplate
     */
    private static final String NODE_TEMPLATE_NAME = "name";
    /**
     * The name of the parameter
     */
    private static final String PARAMETER_NAME = "variable";

    /**
     * Reads all parameters of a {@link TProcess}
     *
     * @param tProcess The {@link TProcess}
     * @return A list of {@link StringParameter} mapped to a Scope name
     * @throws ParseException If something happens during parsing
     */
    public Map<String, List<StringParameter>> parse(final TProcess tProcess) throws ParseException {
        Map<String, List<StringParameter>> parameters = new HashMap<>();
        TAssign firstAssign = this.findFirstAssign(tProcess);
        firstAssign.getCopyOrExtensionAssignOperation()
            .stream()
            .filter(new ParameterFilter())
            .map(new TypeMapper<TExtensibleElements, TCopy>())
            .forEach(tCopy -> {
                try {
                    extractParameter(tCopy, (nodeName, parameter) -> {
                        LOGGER.info("Adding Parameter ({},{}) to {}", parameter.getName(), parameter.getValue(),
                            nodeName);
                        parameters.computeIfAbsent(nodeName, key -> new ArrayList<>()).add(parameter);
                    });
                } catch (ParseException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            });

        return parameters;

    }

    /**
     * Parameters are stored in the first <assign></assign> node of the BPEL file
     *
     * @param tProcess The {@link TProcess}
     * @return The first {@link TAssign}
     * @throws ParseException If a {@link TAssign} could not be found
     */
    protected TAssign findFirstAssign(TProcess tProcess) throws ParseException {
        TSequence tSequence = tProcess.getSequence();
        List<Object> activities = tSequence.getActivity();

        return ObjectSearcher
            .findFirst(activities, TAssign.class)
            .orElseThrow(() -> new ParseException("Could not find first <assign>"));
    }

    /**
     * Extracts the parameter from a {@link TCopy} element
     *
     * @param tCopy             The {@link TCopy}
     * @param parameterConsumer A consumer which will be called after the parameter have been parsed
     */
    protected void extractParameter(TCopy tCopy, BiConsumer<String, StringParameter> parameterConsumer) throws ParseException {
        TFrom from = tCopy.getFrom();
        TTo to = tCopy.getTo();
        ParameterName parameterName = extractParameterName(to);
        String nodeName = parameterName.nodeName;
        StringParameter stringParameter = new StringParameter();
        stringParameter.setName(parameterName.parameterName);

        List<Object> content = from.getContent();
        for (Object o : content) {
            if (o instanceof JAXBElement) {
                JAXBElement jaxbElement = (JAXBElement) o;
                if (jaxbElement.getValue() instanceof TLiteral) {
                    TLiteral tLiteral = (TLiteral) jaxbElement.getValue();
                    String value = extractLiteralContent(tLiteral);
                    stringParameter.setValue(value);
                }
            }
        }

        parameterConsumer.accept(nodeName, stringParameter);
    }

    /**
     * Extracts the Parameter name which is inside a {@link TTo} element
     *
     * @param to The {@link TTo}
     * @return The name of the parameter which consists of the Node(scope) name and the actual parametername
     * @throws ParseException If the paramenter name could not be extracted from the {@link TTo}
     */
    protected ParameterName extractParameterName(TTo to) throws ParseException {
        String parameterFullName = to.getVariable();
        Matcher matcher = PATTERN_PARAMETER.matcher(parameterFullName);
        if (matcher.find()) {
            throw new ParseException(parameterFullName + " could not be matched");
        }
        String nodeName = matcher.group(NODE_TEMPLATE_NAME);
        String parameter = matcher.group(PARAMETER_NAME);
        return new ParameterName(nodeName, parameter);
    }

    /**
     * Extracts the content of the {@link TLiteral} which is the value of the parameter
     *
     * @param tLiteral The {@link TLiteral}
     * @return The value of the parameter
     */
    protected String extractLiteralContent(TLiteral tLiteral) {
        List<Object> content = tLiteral.getContent();
        String value = "";
        if (content.size() == 1) {
            value = content.get(0).toString();
        }

        return value;
    }

    /**
     * Simple class which holds the parameter name because the parameter name consists of
     * a node name (scope name) and a parameter name
     */
    private class ParameterName {
        String nodeName;
        String parameterName;

        ParameterName(String nodeName, String parameterName) {
            this.nodeName = nodeName;
            this.parameterName = parameterName;
        }
    }
}
