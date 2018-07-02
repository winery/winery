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

import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;

import java.util.Collections;
import java.util.EnumSet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsistencyCheckerTest {

    @Test
    public void nodeTypeImplementationNamespace() throws Exception {
        NodeTypeImplementationId id = new NodeTypeImplementationId("http://winery.opentosca.org/test/nodetypeimplementations/fruits", "baobab_impl", false);
        ConsistencyErrorLogger errorLogger = new ConsistencyErrorLogger();
        ConsistencyChecker.checkNamespaceUri(errorLogger, EnumSet.of(ConsistencyCheckerVerbosity.NONE), id);
        assertEquals(Collections.emptyMap(), errorLogger.getErrorList());
    }
}
