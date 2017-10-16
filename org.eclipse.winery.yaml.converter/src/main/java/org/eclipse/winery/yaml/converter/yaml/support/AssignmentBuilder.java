/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.converter.yaml.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.eclipse.winery.yaml.common.Namespaces;
import org.eclipse.winery.yaml.common.writer.xml.support.AnonymousPropertiesList;
import org.eclipse.winery.yaml.common.writer.xml.support.PropertiesList;

public class AssignmentBuilder {
	private TypeConverter typeConverter;
	private Map<QName, Map<String, QName>> propertyAssignmentBuildPlan;

	public AssignmentBuilder(Map<QName, Map<String, QName>> buildPlan) {
		this.propertyAssignmentBuildPlan = buildPlan;
		reset();
	}

	public void reset() {
		this.typeConverter = new TypeConverter();
	}

	public PropertiesList getAssignment(Map<String, TPropertyAssignment> assignmentMap, QName type) {
		Map<String, Object> assignments = assignmentMap.entrySet().stream()
			.filter(e -> e.getValue() != null)
			.collect(Collectors.toMap(
				Map.Entry::getKey, e -> e.getValue().getValue())
			);

		List<JAXBElement> elements = convertAssignment(assignments, propertyAssignmentBuildPlan.get(type));
		return new PropertiesList().setEntries(elements).setNamespace(type.getNamespaceURI());
	}

	private List<JAXBElement> convertAssignment(Map<String, Object> yamlAssignment, Map<String, QName> buildPlan) {
		List<JAXBElement> result = new ArrayList<>();
		for (Map.Entry<String, Object> entry : yamlAssignment.entrySet()) {
			if (entry.getValue() == null) {
				continue;
			}

			QName assignmentType = buildPlan.get(entry.getKey());
			// Add primitive yaml types as JAXBElements with their java class
			if (assignmentType.getNamespaceURI().equals(Namespaces.YAML_NS)) {
				Class assignmentClass = typeConverter.convertToJavaType(assignmentType);
				// Handle TOSCA YAML functions (example: {get_input: DockerEngineURL})
				if (assignmentClass == String.class && !(entry.getValue() instanceof String)) {
					String value = entry.getValue().toString();
					// Filter for function else use Object.toString()
					if (entry.getValue() instanceof Map && ((Map) entry.getValue()).size() == 1) {
						for (Map.Entry<String, Object> fentry : ((Map<String, Object>) entry.getValue()).entrySet())
							value = fentry.getKey() + ": " + fentry.getValue().toString();
					}

					@SuppressWarnings("unchecked")
					JAXBElement element = new JAXBElement(new QName(entry.getKey()), assignmentClass, value);
					result.add(element);
				} else {
					@SuppressWarnings("unchecked")
					JAXBElement element = new JAXBElement(new QName(entry.getKey()), assignmentClass, entry.getValue());
					result.add(element);
				}
			}
			// Add complex (data_types) as AnonymousPropertyLists
			else if (entry.getValue() instanceof Map) {
				@SuppressWarnings({"unchecked"})
				Map<String, Object> tmp = (Map<String, Object>) entry.getValue();
				List<JAXBElement> elements = convertAssignment(tmp, this.propertyAssignmentBuildPlan.get(assignmentType));
				AnonymousPropertiesList list = new AnonymousPropertiesList().setEntries(elements);
				@SuppressWarnings("unchecked")
				JAXBElement element = new JAXBElement(new QName(entry.getKey()), AnonymousPropertiesList.class, list);
				result.add(element);
			} else if (entry.getValue() instanceof List) {
				// TODO check what types are possible for assignments and how to convert them
				assert false;
			} else {
				assert false;
			}
		}
		return result;
	}
}
