/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.converter.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.eclipse.winery.model.converter.support.exception.InvalidToscaSyntax;
import org.eclipse.winery.repository.converter.reader.YamlBuilder;
import org.eclipse.winery.model.tosca.yaml.TArtifactDefinition;

public class FieldValidator {
    private Map<Class, Set<String>> declaredFields;

    public FieldValidator() {
        this.declaredFields = new LinkedHashMap<>();
    }

    private void setDeclaredFields(Class base, Class parent) {
        if (!this.declaredFields.containsKey(base)) {
            this.declaredFields.put(base, new HashSet<>());
        }

        if (parent.equals(TArtifactDefinition.class)) {
            this.declaredFields.get(base).add("file");
        }

        if (!parent.equals(Object.class)) {
            this.declaredFields.get(base)
                .addAll(Arrays.stream(parent.getDeclaredFields()).map(field -> {
                        XmlAttribute xmlAttribute = field.getAnnotation(XmlAttribute.class);
                        XmlElement xmlElement = field.getAnnotation(XmlElement.class);
                        if (Objects.nonNull(xmlAttribute) && !xmlAttribute.name().equals("##default")) {
                            return xmlAttribute.name();
                        } else if (Objects.nonNull(xmlElement) && !xmlElement.name().equals("##default")) {
                            return xmlElement.name();
                        } else {
                            return field.getName();
                        }
                    }
                ).collect(Collectors.toList()));
            setDeclaredFields(base, parent.getSuperclass());
        }
    }

    public <T, K> List<Exception> validate(Class<T> t, Map<String, Object> fields, YamlBuilder.Parameter<K> parameter) {
        List<Exception> exceptions = new ArrayList<>();

        if (!fields.isEmpty() && !this.declaredFields.containsKey(t)) {
            setDeclaredFields(t, t);
        }

        Set<String> declaredFields = this.declaredFields.get(t);
        fields.forEach((key, value) -> {
            if (!declaredFields.contains(key)) {
                exceptions.add(new InvalidToscaSyntax(
                        "Class '{}' has no field with name '{}'\n Possible fields are '{}'",
                        t.getName(),
                        key,
                        declaredFields
                    ).setContext(new ArrayList<>(parameter.copy().addContext(key).getContext()))
                );
            }
        });
        return exceptions;
    }
}
