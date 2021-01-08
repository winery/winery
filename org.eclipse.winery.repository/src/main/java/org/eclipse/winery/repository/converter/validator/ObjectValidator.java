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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.winery.model.converter.support.exception.InvalidToscaSyntax;

public class ObjectValidator {
    public void validateObject(Object object) throws InvalidToscaSyntax {
        if (object instanceof LinkedHashMap) {
            LinkedHashMap map = (LinkedHashMap) object;
            Set<Map.Entry> entries = map.entrySet();
            if (entries.size() == 0 || !entries.iterator().next().getKey().equals("tosca_definitions_version")) {
                for (Map.Entry entry : entries) {
                    if (entry.getKey().equals("tosca_definitions_version")) {
                        throw new InvalidToscaSyntax(
                            "The field tosca_definitions_version MUST be defined before all other YAML elements"
                        );
                    }
                }
            }
        }
    }
}
