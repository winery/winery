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
package org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions.interfaces;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;
import org.junit.jupiter.api.Test;

public class InterfacesResourceTest extends AbstractResourceTest {

    @Test
    public void addNodeTemplateInterface() throws Exception {
        this.setRevisionTo("1b494c401fb89b9a743e772f890f559c7375a555");
        this.assertNoContentPost("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/boundarydefinitions/interfaces/",
            "entitytypes/servicetemplates/interfaces/addNodeTemplateInterface.json");
    }

    @Test
    public void addRelationshipTemplateInterface() throws Exception {
        this.setRevisionTo("d73752decce913f540e38ce7a151815e78251cf7");
        this.assertNoContentPost("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/boundarydefinitions/interfaces/",
            "entitytypes/servicetemplates/interfaces/addRelationshipTemplateInterface.json");
    }

    @Test
    public void addPlanInterface() throws Exception {
        this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
        this.assertNoContentPost("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/boundarydefinitions/interfaces/",
            "entitytypes/servicetemplates/interfaces/addPlanInterface.json");
    }

    @Test
    public void addAllInterfacesSimultaneously() throws Exception {
        this.setRevisionTo("d73752decce913f540e38ce7a151815e78251cf7");
        this.assertNoContentPost("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/boundarydefinitions/interfaces/",
            "entitytypes/servicetemplates/interfaces/addAllInterfacesAtOnce.json");
    }

    @Test
    public void getInterfaces() throws Exception {
        this.setRevisionTo("c37dbe8b27aeb145a7ec2b7a3d878fc495717e59");
        this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/boundarydefinitions/interfaces/",
            "entitytypes/servicetemplates/interfaces/getAllInterfaces.json");
    }
}
