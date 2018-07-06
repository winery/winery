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

import java.util.Collections;
import java.util.EnumSet;

import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConsistencyCheckerTest extends TestWithGitBackedRepository {

    @Test
    public void nodeTypeImplementationNamespaceHasNoErrors() throws Exception {
        NodeTypeImplementationId id = new NodeTypeImplementationId("http://winery.opentosca.org/test/nodetypeimplementations/fruits", "baobab_impl", false);
        ConsistencyErrorLogger errorLogger = new ConsistencyErrorLogger();
        ConsistencyChecker.checkNamespaceUri(errorLogger, EnumSet.of(ConsistencyCheckerVerbosity.NONE), id);
        assertEquals(Collections.emptyMap(), errorLogger.getErrorList());
    }

    public ConsistencyErrorLogger checkRevisionWithoutDocumentation(String revision) throws Exception {
        this.setRevisionTo(revision);
        EnumSet<ConsistencyCheckerVerbosity> verbosity = EnumSet.of(ConsistencyCheckerVerbosity.OUTPUT_NUMBER_OF_TOSCA_COMPONENTS);
        ConsistencyCheckerConfiguration configuration = new ConsistencyCheckerConfiguration
            (false, false, verbosity, repository);
        return ConsistencyChecker.checkCorruption(configuration);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "83714d54cefe30792a5ad181af7cf036a77baf9e" // origin/plain in a working version
    })
    public void noErrors(String revision) throws Exception {
        final ConsistencyErrorLogger errorLogger = checkRevisionWithoutDocumentation(revision);
        assertNotNull(errorLogger);
        assertEquals(Collections.emptyMap(), errorLogger.getErrorList());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "20f6d0a", // origin/plain in a non-working version
        "0750656975fec2d8a7c8400df85e37b1f2cdb5ac", // origin/black in a non-working version
        "304b62b06556afa1a7227164a9c0d2c9a1178b8f"  // origin/fruits in a non-working version
    })
    public void hasErrors(String revision) throws Exception {
        final ConsistencyErrorLogger errorLogger = checkRevisionWithoutDocumentation(revision);
        assertNotNull(errorLogger);
        // TODO: Add test for concrete error message
        assertNotEquals(Collections.emptyMap(), errorLogger.getErrorList());
    }
}
