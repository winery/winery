/********************************************************************************
⅛ * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.converter.support.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MultiException extends Exception {
    private List<Exception> exceptions;
    private List<String> context;

    public MultiException() {
        this.exceptions = new ArrayList<>();
        this.context = new ArrayList<>();
    }

    public String getMessage() {
        return "\n".concat(this.exceptions.stream()
            .filter(Objects::nonNull)
            .map(exception -> exception.getClass().getSimpleName().concat(":\n   ")
                .concat(exception.getMessage().replaceAll("\n(?!\n)", "\n   "))
            )
            .collect(Collectors.joining("\n")))
            .concat("\nMultiException Context = ")
            .concat(context.stream()
                .collect(Collectors.joining("\n   "))
            );
    }

    public MultiException add(Exception exception) {
        exceptions.add(exception);
        return this;
    }

    public MultiException add(List<Exception> exceptions) {
        this.exceptions.addAll(exceptions);
        return this;
    }

    public MultiException add(String context) {
        this.context.add(context);
        return this;
    }

    public Exception getException() {
        if (exceptions.size() == 1) {
            if (exceptions.get(0) instanceof MultiException) {
                return ((MultiException) exceptions.get(0)).getException();
            } else {
                return exceptions.get(0);
            }
        } else {
            return this;
        }
    }

    public boolean hasException() {
        return !isEmpty();
    }

    public boolean isEmpty() {
        return exceptions.isEmpty();
    }
} 

