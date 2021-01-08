/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.edmm;

import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EdmmUtilsTest extends TestWithGitBackedRepository {

    @Test
    @Disabled
    void getAllToscaLightCompliantServiceTemplates() throws Exception {
        this.setRevisionTo("origin/plain");

        Map<QName, TServiceTemplate> allToscaLightCompliantModels = EdmmUtils.getAllToscaLightCompliantModels();

        assertEquals(15, allToscaLightCompliantModels.size());
    }
}
