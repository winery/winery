/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.backend.filebased;

import java.io.File;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.transformation.edmm.EdmmMappingItem;
import org.eclipse.winery.model.transformation.edmm.EdmmType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JsonBasedEdmmManagerTest {

    @Test
    void getMappings() throws Exception {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("edmmMapping.json").toURI());
        JsonBasedEdmmManager manager = new JsonBasedEdmmManager(file);

        assertNotNull(manager.getOneToOneMappings());
        assertEquals(3, manager.getOneToOneMappings().size());
        assertNotNull(manager.getTypeMappings());
        assertEquals(2, manager.getTypeMappings().size());

        EdmmMappingItem item = manager.getOneToOneMappings().get(0);
        assertEquals(QName.valueOf("{https://ex.org/test/tosca}hostedOn"), item.toscaType);
        assertEquals(EdmmType.HOSTED_ON, item.edmmType);
    }
}
