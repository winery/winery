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

package org.eclipse.winery.bpel2bpmn.utils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A simple utils class.
 * This class is used for the searching of objects in a list because the BPEL elements
 * are very generic (every BPEL element extends {@link org.eclipse.winery.bpel2bpmn.model.gen.TExtensibleElements}) and therefore JAXB returns a list of generic objects
 */
public final class ObjectSearcher {

    private ObjectSearcher() {
        //Pure utils class
    }

    /**
     * Searches for the very first object which matches the given class
     *
     * @param objects A List of objects
     * @param target  The target class to search for
     * @param <T>     The type of the object
     * @return The casted object in an optional
     */
    public static <T> Optional<T> findFirst(List<? extends Object> objects, Class<T> target) {
        return objects.stream()
            .filter(object -> target.isAssignableFrom(object.getClass()))
            .map(new TypeMapper<Object, T>())
            .findFirst();
    }

    /**
     * Searches for any objects which match the given class
     *
     * @param objects A List of objects
     * @param target  The target class to search for
     * @param <T>     The type of the object
     * @return A list of all objects that matches the given class
     */
    public static <T> List<T> findAny(List<? extends Object> objects, Class<T> target) {
        return objects.stream()
            .filter(object -> target.isAssignableFrom(object.getClass()))
            .map(new TypeMapper<Object, T>())
            .collect(Collectors.toList());
    }
}
