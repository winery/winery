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
import org.eclipse.winery.bpel2bpmn.model.gen.si.InvokeOperationAsync;
import org.eclipse.winery.bpel2bpmn.utils.ObjectSearcher;
import org.eclipse.winery.bpmn2bpel.model.ManagementTask;
import org.eclipse.winery.bpmn2bpel.model.param.Parameter;
import org.eclipse.winery.bpmn2bpel.model.param.StringParameter;
import org.eclipse.winery.bpmn2bpel.parser.JsonKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SequenceParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceParser.class);
    private static final String CONTEXT = "org.eclipse.winery.bpel2bpmn.model.gen.si";
    private static final String SI_NAMESPACE = "http://siserver.org/schema";


    public List<ManagementTask> parseSequence(TSequence sequence) throws ParseException {
        final List<ManagementTask> result = new ArrayList<>();
        List<Object> activities = sequence.getActivity();
        List<TAssign> assigns = ObjectSearcher.findAny(activities, TAssign.class);

        for (TAssign assign : assigns) {
            List<TExtensibleElements> assignOperation = assign.getCopyOrExtensionAssignOperation();
            List<TCopy> copies = ObjectSearcher.findAny(assignOperation, TCopy.class);
            result.addAll(parseCopy(copies));
        }

        return result;
    }

    protected List<ManagementTask> parseCopy(List<TCopy> copies) throws ParseException {
        final List<ManagementTask> managementTasks = new ArrayList<>();
        for (TCopy copy : copies) {
            TFrom from = copy.getFrom();
            List<Object> content = from.getContent();
            if (content.size() == 3) {
                ObjectSearcher.findFirst(content, JAXBElement.class)
                    .ifPresent(jaxbElement -> {
                        Object value = jaxbElement.getValue();
                        if (value instanceof TLiteral) {
                            TLiteral tLiteral = (TLiteral) value;
                            ObjectSearcher.findFirst(tLiteral.getContent(), Element.class)
                                .ifPresent(element -> {
                                    if (element.getNamespaceURI().equals(SI_NAMESPACE)) {
                                        try {
                                            ManagementTask task = this.parseElement(element);
                                            managementTasks.add(task);
                                        } catch (ParseException e) {
                                            LOGGER.error(e.getMessage(), e);
                                        }
                                    }
                                });
                        }
                    });
            }
        }
        return managementTasks;
    }

    protected ManagementTask parseElement(final Element element) throws ParseException {
        LOGGER.debug("Parsing Element");
        ManagementTask managementTask = new ManagementTask();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(CONTEXT);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<InvokeOperationAsync> jaxbElement = unmarshaller
                .unmarshal(element, InvokeOperationAsync.class);
            InvokeOperationAsync invokeOperationAsync = jaxbElement.getValue();
            List<Parameter> stringParameters = invokeOperationAsync.getParams().getParam().stream().map(paramsMapItemType -> {
                String key = paramsMapItemType.getKey();
                String value = paramsMapItemType.getValue();
                Parameter stringParameter = new StringParameter();
                stringParameter.setName(key);
                stringParameter.setValue(value);
                return stringParameter;
            }).collect(Collectors.toList());

            managementTask.setNodeOperation(invokeOperationAsync.getOperationName());
            managementTask.setInterfaceName(invokeOperationAsync.getInterfaceName());
            managementTask.setNodeTemplateId(new QName(invokeOperationAsync.getNodeTemplateID()));
            managementTask.setType(JsonKeys.NODE_TYPE_MGMT_TASK);
            String name = invokeOperationAsync.getOperationName() + invokeOperationAsync.getNodeTemplateID();
            managementTask.setInputParameters(stringParameters);
            managementTask.setName(name);

        } catch (JAXBException e) {
            throw new ParseException(e);
        }

        return managementTask;
    }
}
