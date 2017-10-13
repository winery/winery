/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.common.validator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.winery.yaml.common.exception.TOSCAVersionMustBeFirstLine;

public class ObjectValidator {
    public void validateObject(Object object) throws TOSCAVersionMustBeFirstLine {
        if (object instanceof LinkedHashMap) {
            LinkedHashMap map = (LinkedHashMap) object;
            Set<Map.Entry> entries = map.entrySet();
            if (entries.size() == 0 || !entries.iterator().next().getKey().equals("tosca_definitions_version")) {
                for (Map.Entry entry : entries) {
                    if (entry.getKey().equals("tosca_definitions_version")) {
                        String msg = "tosca_definitions_version must be defined before all other YAML elements!";
                        throw new TOSCAVersionMustBeFirstLine(msg);
                    }
                }
            }
        }
    }
}
