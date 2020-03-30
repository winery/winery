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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.IdUtil;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.constants.Namespaces;

import org.eclipse.jdt.annotation.NonNull;

public class ConsistencyErrorCollector {

    private static final String CONFIGURATION_ERROR = "Configuration Error";

    private static final QName CONFIG_Q_NAME = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "ConsistencyCheckerConfiguration");

    private Map<QName, ElementErrorList> errorList = new HashMap<>();

    /**
     * Use this only in exceptional cases. Please use the other methods to provide a {@link DefinitionsChildId}
     *
     * @param message The message to log.
     */
    public void error(@NonNull String message) {
        ElementErrorList element = this.errorList.get(CONFIG_Q_NAME);

        if (Objects.isNull(element)) {
            element = new ElementErrorList(CONFIGURATION_ERROR);
            this.errorList.put(CONFIG_Q_NAME, element);
        }

        element.addError(message);
    }

    public void error(@NonNull DefinitionsChildId id, @NonNull String message) {
        this.getElementFromList(id).addError(message);
    }

    public void warning(@NonNull DefinitionsChildId id, @NonNull String message) {
        this.getElementFromList(id).addWarning(message);
    }

    public @NonNull Map<QName, ElementErrorList> getErrorList() {
        return errorList;
    }

    private @NonNull ElementErrorList getElementFromList(@NonNull DefinitionsChildId id) {
        ElementErrorList element = this.errorList.get(id.getQName());

        if (Objects.isNull(element)) {
            String toscaType = IdUtil.getEverythingBetweenTheLastDotAndBeforeId(id.getClass());
            element = new ElementErrorList(toscaType);
            this.errorList.put(id.getQName(), element);
        }

        return element;
    }
}
