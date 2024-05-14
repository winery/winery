/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.edmm;

import java.io.File;
import java.nio.file.Files;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class EdmmResourceTest extends AbstractResourceTest {

    private static final String REVISION = "7ba859b04ba1487b1778fa44b8d45aa1d31528b1";
    private static final String TRANSFORM = "edmm/transform";
    private static final String CHECK_MODEL = "edmm/check-model-support";
    private static final String PETCLINIC_TOPOLOGY_CLOUD = "servicetemplates/https%253A%252F%252Fedmm.uni-stuttgart.de%252Fservicetemplates/PetClinic-Cloud/";
    private static final String PETCLINIC_TOPOLOGY_IAAS = "servicetemplates/https%253A%252F%252Fedmm.uni-stuttgart.de%252Fservicetemplates/PetClinic-IaaS/";

    @Test
    public void testSupportForPetclinicCloud() throws Exception {
        this.setRevisionTo(REVISION);
        this.assertGet(PETCLINIC_TOPOLOGY_CLOUD + CHECK_MODEL, "edmm/petclinic-cloud.check-model-support.json");
    }

    @Test
    @Disabled("For some reason the result looks sometimes differently")
    public void testSupportForPetclinicIaas() throws Exception {
        this.setRevisionTo(REVISION);
        this.assertGet(PETCLINIC_TOPOLOGY_IAAS + CHECK_MODEL, "edmm/petclinic-iaas.check-model-support.json");
    }

    @Test
    public void testOneToOneMapForPetclinicIaas() throws Exception {
        this.setRevisionTo(REVISION);
        this.assertGet(PETCLINIC_TOPOLOGY_IAAS + "edmm/one-to-one-map", "edmm/petclinic-iaas.one-to-one-map.json");
    }

    private File transformHelper(String topology, String target) throws Exception {
        byte[] zipFileByteArray = this.start()
            .get(callURL(topology + TRANSFORM + "?target=" + target))
            .then()
            .statusCode(200)
            .extract()
            .response()
            .getBody()
            .asByteArray();
        File zipFile = Files.createTempFile("EdmmResource-transform-result-", ".zip").toFile();
        FileUtils.writeByteArrayToFile(zipFile, zipFileByteArray);
        return zipFile;
    }
}
