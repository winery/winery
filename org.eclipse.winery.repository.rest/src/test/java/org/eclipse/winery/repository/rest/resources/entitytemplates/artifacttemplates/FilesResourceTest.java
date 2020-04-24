/*******************************************************************************
 * Copyright (c) 2012-2015 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;
import org.junit.jupiter.api.Test;

public class FilesResourceTest extends AbstractResourceTest {

    @Test
    public void getEmptyFiles() throws Exception {
        this.setRevisionTo("d955202333ec48ca6cc3bfbbd71c4c6e4fab9d90");
        this.assertGet("artifacttemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fartifacttemplates/ArtifactTemplateWithoutAnyFiles/files",
            "entitytemplates/artifacttemplates/emptyFilesAndSources.json");
    }

    @Test
    public void getEmptySources() throws Exception {
        this.setRevisionTo("d955202333ec48ca6cc3bfbbd71c4c6e4fab9d90");
        this.assertGet("artifacttemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fartifacttemplates/ArtifactTemplateWithoutAnyFiles/source",
            "entitytemplates/artifacttemplates/emptyFilesAndSources.json");
    }

    @Test
    public void getOneSourceElement() throws Exception {
        this.setRevisionTo("a000df440a138f988f8129cf4ac262fca2e380cc");
        this.assertGet("artifacttemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fartifacttemplates/ArtifactTemplateWithFilesAndSources/source",
            "entitytemplates/artifacttemplates/firstSourceElement.json");
    }

    @Test
    public void getOneFileElement() throws Exception {
        this.setRevisionTo("a000df440a138f988f8129cf4ac262fca2e380cc");
        this.assertGet("artifacttemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fartifacttemplates/ArtifactTemplateWithFilesAndSources/files",
            "entitytemplates/artifacttemplates/firstFileElement.json");
    }
}
