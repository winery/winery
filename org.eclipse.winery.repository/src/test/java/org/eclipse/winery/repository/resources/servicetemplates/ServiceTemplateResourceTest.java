/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 *     Oliver Kopp - test of create instance
 *******************************************************************************/
package org.eclipse.winery.repository.resources.servicetemplates;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.repository.resources.AbstractResourceTest;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ServiceTemplateResourceTest extends AbstractResourceTest {

    @Test
    @Ignore
    public void addServicetemplate() throws Exception {
        this.setRevisionTo("337119ea2e694e70b994bcb3d97295856c0ab0f6");
        this.assertPost("servicetemplates/", "entitytypes/servicetemplates/baobab_create_servicetemplate.json");
        this.assertGet("servicetemplates/","entitytypes/servicetemplates/baobab_inital.json");
    }

    @Test
    public void getServicetemplate() throws Exception {
        this.setRevisionTo("a5fd2da6845e9599138b7c20c1fd9d727c1df66f");
        this.assertGet("servicetemplates/","entitytypes/servicetemplates/baobab_inital.json");
    }

    @Test
    public void createInstance() throws Exception {
        this.setRevisionTo("8cf0ce80c2c40c6ec178ef8e5bdc4e2fcdecc7f9");
        ServiceTemplateId id = new ServiceTemplateId("http://winery.opentosca.org/test/servicetemplates/fruits", "farm", false);
        ServiceTemplateResource serviceTemplateResource = new ServiceTemplateResource(id);
        Assert.assertNotNull(serviceTemplateResource);
    }
}
