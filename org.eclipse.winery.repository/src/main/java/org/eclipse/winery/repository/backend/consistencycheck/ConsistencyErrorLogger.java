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

import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.constants.Namespaces;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConsistencyErrorLogger {

    private static final String CONFIGURATION_ERROR = "Configuration Error";

    private static final QName CONFIG_Q_NAME = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "ConsistencyCheckerConfiguration");

    private Map<QName, ElementErrorList> errorList = new HashMap<>();

    public void error(String message) {
        ElementErrorList element = this.errorList.get(CONFIG_Q_NAME);

        if (Objects.isNull(element)) {
            element = new ElementErrorList(CONFIGURATION_ERROR);
            this.errorList.put(CONFIG_Q_NAME, element);
        }

        element.addError(message);
    }

    public void error(DefinitionsChildId id, String message) {
        this.getElementFromList(id).addError(message);
    }

    public void warning(DefinitionsChildId id, String message) {
        this.getElementFromList(id).addWarning(message);
    }

    public Map<QName, ElementErrorList> getErrorList() {
        return errorList;
    }

    private ElementErrorList getElementFromList(DefinitionsChildId id) {
        ElementErrorList element = this.errorList.get(id.getQName());

        if (Objects.isNull(element)) {
            String toscaType = Util.getEverythingBetweenTheLastDotAndBeforeId(id.getClass());
            element = new ElementErrorList(toscaType);
            this.errorList.put(id.getQName(), element);
        }

        return element;
    }
}
