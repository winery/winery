/********************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.yaml.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtil {
    private static Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);

    /**
     * Resolves the field of a Class recursively for all superclasses0
     *
     * @param clazz   the class checked for the field
     * @param keyName the name of the field
     * @return the field from the claZZ with the keyName
     */
    private Field resolveField(Class<?> clazz, String keyName) {
        Map<String, Field> fields = Arrays.stream(clazz.getDeclaredFields())
            .map(this::getFieldName)
            .collect(Collectors.toMap(Pair::getOne, Pair::getTwo));
        if (fields.containsKey(keyName)) {
            return fields.get(keyName);
        } else if (clazz.equals(Object.class)) {
            // No logging here (logging in caller)
            return null;
        } else {
            return resolveField(clazz.getSuperclass(), keyName);
        }
    }

    /**
     * Checks a field for XmlElement or XmlAttribute annotations
     *
     * @return a pair of name and field
     */
    @NonNull
    private Pair<String, Field> getFieldName(Field field) {
        XmlAttribute xmlAttribute = field.getAnnotation(XmlAttribute.class);
        XmlElement xmlElement = field.getAnnotation(XmlElement.class);
        String name = field.getName();
        if (Objects.nonNull(xmlAttribute) && !xmlAttribute.name().equals("##default")) {
            name = xmlAttribute.name();
        } else if (Objects.nonNull(xmlElement) && !xmlElement.name().equals("##default")) {
            name = xmlElement.name();
        }
        return Tuples.pair(name, field);
    }

    /**
     * Resolves the smallest dot separated substring that represents a key in the map
     *
     * @param keyName dot separated substring that contain the key
     * @param nth     the number of first dot separated substrings the map is checked
     * @return Object that resolves the key (could be a sub field of a value map by a key which is a substring of the
     * keyName)
     */
    private Object resolveMap(@NonNull String keyName, @NonNull Map<String, Object> map, int nth) {
        String part = getKeyNamePart(keyName, nth);
        Object result;
        if (map.containsKey(part)) {
            result = map.get(part);
            if (!part.equals(keyName)) {
                result = resolve(keyName.substring(part.length() + 1), result);
            }
            return result;
        } else if (part.equals(keyName)) {
            result = null;
        } else {
            result = resolveMap(keyName, map, nth + 1);
        }

        if (Objects.isNull(result) && !mapContainsKeyname(keyName, map)) {
            logger.error("The map {} does not contain a key with name {}", map, part);
        }
        return result;
    }

    private Boolean mapContainsKeyname(@NonNull String keyName, @NonNull Map<String, Object> map) {
        int nth = 1;
        String part;
        do {
            part = getKeyNamePart(keyName, nth);
            nth += 1;
            if (map.containsKey(part)) {
                return true;
            }
        } while (!part.equals(keyName));
        return false;
    }

    /**
     * KeyNames are strings containing substrings separated by dots. This function returns the combination of the first
     * n substrings
     */
    private String getKeyNamePart(@NonNull String keyName, int nth) {
        int index = keyName.indexOf(".");
        int number = nth;
        while (--number > 0 && index != -1) {
            index = keyName.indexOf(".", index + 1);
        }
        if (index < 0) index = keyName.length();
        return keyName.substring(0, index);
    }

    /**
     * Invokes the getter function of a field in an object. Recursively checks all super classes until Object.class is
     * reached
     */
    private Object invokeGetter(Class<?> clazz, String name, Object object) {
        return Arrays.stream(clazz.getDeclaredMethods())
            .filter(method -> method.getName().startsWith("get") && method.getName().length() == (name.length() + 3))
            .filter(method -> method.getName().toLowerCase().endsWith(name.toLowerCase()))
            .findAny().map(method -> {
                try {
                    return method.invoke(object);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    return null;
                }
            }).orElseGet(() -> {
                if (clazz.equals(Object.class)) return null;
                return invokeGetter(clazz.getSuperclass(), name, object);
            });
    }

    /**
     * Resolves the value of a field or a sub field specified by a keyname and an object
     */
    public Object resolve(@NonNull String keyName, Object object) {
        if (Objects.isNull(object)) {
            logger.error("Resolving keyName failed: Object is null!");
            return null;
        }

        String part = getKeyNamePart(keyName, 1);

        Object result;
        if (object instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) object;
            return resolveMap(keyName, map, 1);
        } else if (object instanceof List) {
            try {
                int i = Integer.parseInt(part);
                result = ((List) object).get(i);
            } catch (NumberFormatException e) {
                logger.error("Resolving keyName {} for Object {} failed: Expected integer but received {}",
                    keyName, object, part);
                return null;
            }
        } else {
            Field field = resolveField(object.getClass(), part);
            if (Objects.isNull(field)) {
                logger.error("Resolving field name '{}' for Object '{}' failed; keyname='{}'", part, object, keyName);
                return null;
            } else {
                result = invokeGetter(object.getClass(), field.getName(), object);
                if (Objects.isNull(result)) {
                    // Test if the a getter for the XmlElement or XmlAttribute annotation name exists
                    result = invokeGetter(object.getClass(), getFieldName(field).getOne(), object);
                }
            }
        }

        if (part.equals(keyName)) {
            return result;
        } else {
            return resolve(keyName.substring(part.length() + 1), result);
        }
    }
}
