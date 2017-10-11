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

import java.util.Optional;

/**
 * Utils class which will operate on {@code null} values
 */
public final class NullChecker {

    /**
     * Pure utils class, therefore we dont need a constructor
     */
    private NullChecker() {
        //Pure Utils class
    }

    /**
     * Checks if an Object is {@code null}
     *
     * @param object The object to check
     * @param <T>    The type of the object
     * @return An {@link Optional} which can be {@link Optional#empty()}
     */
    public static <T> Optional<T> check(T object) {
        return object == null ? Optional.empty() : Optional.of(object);
    }

    public static <T, E extends Exception> Optional<T> orThrow(T object, E e) throws E {
        if (object == null) {
            throw e;
        }

        return Optional.of(object);
    }
}
