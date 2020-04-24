/*******************************************************************************
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
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.artifacts;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;
import org.junit.jupiter.api.Test;

public class GenericArtifactsResourceTest extends AbstractResourceTest {

    @Test
    public void generateArtifactAutoGenereatatesWarType() throws Exception {
        this.setRevisionTo("15cd64e30770ca7986660a34e1a4a7e0cf332f19"); // empty repository
        this.assertNotFound("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/");
        this.assertNotFound("artifacttypes/http%253A%252F%252Fopentosca.org%252Fartifacttypes/WAR/");

        this.assertPost("nodetypes/", "entitytypes/nodetypes/baobab_create.json");
        this.assertPut("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/", "entitytypes/nodetypes/baobab_with_interfaces.xml");

        this.assertPost("nodetypeimplementations/", "entitytypes/nodetypes/iageneration/baboab_nodetype_implementation.json");
        this.assertPost("nodetypeimplementations/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab_impl/implementationartifacts/", "entitytypes/nodetypes/iageneration/baobab_implementation_artifact.json");

        this.assertGet("artifacttypes/http%253A%252F%252Fopentosca.org%252Fartifacttypes/WAR/", "entitytypes/artifacttypes/war.xml");
    }
}
