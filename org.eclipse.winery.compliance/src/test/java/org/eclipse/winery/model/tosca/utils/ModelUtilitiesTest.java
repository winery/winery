/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.tosca.utils;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ModelUtilitiesTest extends TestWithGitBackedRepository {

    @Test
    public void getTargetLabel() {
        TNodeTemplate nodeTemplate = new TNodeTemplate();
        assertFalse(ModelUtilities.getTargetLabel(nodeTemplate).isPresent());

        ModelUtilities.setTargetLabel(nodeTemplate, "");
        assertFalse(ModelUtilities.getTargetLabel(nodeTemplate).isPresent());

        ModelUtilities.setTargetLabel(nodeTemplate, "UNDEFined");
        assertFalse(ModelUtilities.getTargetLabel(nodeTemplate).isPresent());

        ModelUtilities.setTargetLabel(nodeTemplate, "TARGETLABEL");
        assertTrue(ModelUtilities.getTargetLabel(nodeTemplate).isPresent());
        assertEquals("targetlabel", ModelUtilities.getTargetLabel(nodeTemplate).get());
    }
}
