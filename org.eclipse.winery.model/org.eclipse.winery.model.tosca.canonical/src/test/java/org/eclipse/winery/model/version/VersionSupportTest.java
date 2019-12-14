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

package org.eclipse.winery.model.version;

import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.model.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// TODO deal with move of #getVersion and #getNameWitoutVersion onto DefinitionsChildId
public class VersionSupportTest {

    @Test
    public void getVersionWithLeadingDashButNoComponentVersion() {
        DefinitionsChildId id = new ServiceTemplateId("http://example.org/tosca/versioning", "myServiceTemplate_-w1-wip5", false);
        WineryVersion version = id.getVersion();

        assertEquals("", version.getComponentVersion());
        assertEquals(1, version.getWineryVersion());
        assertEquals(5, version.getWorkInProgressVersion());
    }
    
    @Test
    public void getVersion() {
        String componentVersion = "1.0.0";
        int wineryVersion = 6;
        int wipVersion = 3;

        DefinitionsChildId id = getDefinitionChildId("http://example.org/tosca/versions", "myElement", componentVersion, wineryVersion, wipVersion);
        WineryVersion version = id.getVersion();

        assertEquals(componentVersion, version.getComponentVersion());
        assertEquals(wineryVersion, version.getWineryVersion());
        assertEquals(wipVersion, version.getWorkInProgressVersion());
    }

    @Test
    public void getNameWithoutVersionFromDefinitionsChildId() {
        String name = "myElementTest";
        DefinitionsChildId id = getDefinitionChildId("http://example.org/tosca/versions", name, "1.2.3", 1, 1);

        assertEquals(name, id.getNameWithoutVersion());
    }

    @Test
    public void getQNameWithComponentVersionOnly() {
        String name = "myElementTest";
        String namespace = "http://example.org/tosca/versions";
        String componentVersion = "1.2.3";

        DefinitionsChildId id = getDefinitionChildId(namespace, name, componentVersion, 1, 1);

        assertEquals("{" + namespace + "}" + name + WineryVersion.WINERY_VERSION_SEPARATOR + componentVersion,
            VersionSupport.getQNameWithComponentVersionOnly(id));
    }

    @Test
    public void getNewIdName() {
        String id = "myId_w1-wip56";
        String appendix = "test";
        String expectedId = "myId_w1-wip56-" + appendix + "-w1-wip1";
        ServiceTemplateId serviceTemplateId = new ServiceTemplateId("https://ex.org/tosca/sts", id, false);

        assertEquals(expectedId, VersionSupport.getNewComponentVersionId(serviceTemplateId, appendix));
    }

    @Test
    public void getNewIdIfComponentVersionIsAvailable() {
        String id = "myId_component-version-w1";
        String appendix = "test";
        String expectedId = "myId_component-version-w1-" + appendix + "-w1-wip1";
        ArtifactTypeId serviceTemplateId = new ArtifactTypeId("https://ex.org/tosca/sts", id, false);

        assertEquals(expectedId, VersionSupport.getNewComponentVersionId(serviceTemplateId, appendix));
    }

    private DefinitionsChildId getDefinitionChildId(String namespace, String name, String componentVersion, int wineryVersion, int wipVersion) {
        String elementName = name
            + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + componentVersion
            + WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_VERSION_PREFIX + wineryVersion
            + WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_WIP_VERSION_PREFIX + wipVersion;

        return new ServiceTemplateId(namespace, elementName, false);
    }
}
