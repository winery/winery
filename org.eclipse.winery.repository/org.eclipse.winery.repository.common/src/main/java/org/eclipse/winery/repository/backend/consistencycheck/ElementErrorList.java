/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
package org.eclipse.winery.repository.backend.consistencycheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

public class ElementErrorList {

    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private String toscaType;

    public ElementErrorList(@NonNull String toscaType) {
        this.toscaType = toscaType;
    }

    public void addError(@NonNull String message) {
        this.errors.add(message);
    }

    public void addWarning(@NonNull String message) {
        this.warnings.add(message);
    }

    public @NonNull List<String> getErrors() {
        return errors;
    }

    public @NonNull List<String> getWarnings() {
        return warnings;
    }

    public @NonNull String getToscaType() {
        return toscaType;
    }

    @Override
    public @NonNull String toString() {
        return "ElementErrorList{" +
            "toscaType='" + toscaType + '\'' +
            ", errors=" + errors +
            ", warnings=" + warnings +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElementErrorList)) return false;
        ElementErrorList that = (ElementErrorList) o;
        return Objects.equals(errors, that.errors) &&
            Objects.equals(warnings, that.warnings) &&
            Objects.equals(toscaType, that.toscaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(errors, warnings, toscaType);
    }
}
