/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Christoph Kleine - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.model.tosca.yaml.support;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNull;

public class Util {

    /**
     * Converts a string representation of an array (for example "[ a1, a2, a3, a4 ]" ) to a list of strings
     */
    @NonNull
    public static List<String> convertStringArray(String array) {
        if (array == null || array.isEmpty()) {
            return new ArrayList<>();
        }

        return Stream.of(array.replace("[", "")
            .replace("]", "")
            .replaceAll("\\s+", "")
            .split(",")).collect(Collectors.toList());
    }
}
