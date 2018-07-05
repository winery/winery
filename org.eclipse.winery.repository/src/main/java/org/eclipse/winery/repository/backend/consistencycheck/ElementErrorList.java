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
 ********************************************************************************/
package org.eclipse.winery.repository.backend.consistencycheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ElementErrorList {

    private List<String> errors;
    private List<String> warnings;
    private String toscaType;

    public ElementErrorList(String toscaType) {
        this.toscaType = toscaType;
    }

    public void addError(String message) {
        if (Objects.isNull(this.errors)) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(message);
    }

    public void addWarning(String message) {
        if (Objects.isNull(this.warnings)) {
            this.warnings = new ArrayList<>();
        }
        this.warnings.add(message);
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public String getToscaType() {
        return toscaType;
    }

    @Override
    public String toString() {
        return "ElementErrorList{" +
            "toscaType='" + toscaType + '\'' +
            ", errors=" + errors +
            ", warnings=" + warnings +
            '}';
    }
}
