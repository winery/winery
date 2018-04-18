/********************************************************************************
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
 ********************************************************************************/

package org.eclipse.winery.common;

import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.common.version.WineryVersion;

import org.junit.Assert;
import org.junit.Test;

public class VersionUtilsTest {

    @Test
    public void getVersionWithStandardVersionIdentifier() {
        this.getVersion("2.6.4", 5, 679834);
    }

    @Test
    public void getVersionWithComplexVersionIdentifier() {
        this.getVersion("0.6.2-beta34-5", 2899, 0);
    }

    @Test
    public void getVersionWithoutComponentVersion() {
        this.getVersion("", 46, 8);
    }

    @Test
    public void getVersionWithoutManagementVersions() {
        this.getVersion("3.525.2", 0, 8);
    }

    @Test
    public void getVersionWithoutManagementAndWipVersion() {
        this.getVersion("3.525.2-wip3", 0, 0);
    }

    @Test
    public void getVersionWithNoVersionInTheName() {
        WineryVersion version = VersionUtils.getVersion("myExampleComponent");
        Assert.assertEquals("", version.toString());
    }

    @Test
    public void getVersionFromNameWithMultipleUnderscores() {
        WineryVersion version = VersionUtils.getVersion("myExample_Component_v1.3.0-w1");
        Assert.assertEquals("v1.3.0", version.getComponentVersion());
        Assert.assertEquals(1, version.getWineryVersion());
        Assert.assertEquals(0, version.getWorkInProgressVersion());
    }

    private void getVersion(String cVersion, int wVersion, int wipVersion) {
        WineryVersion wineryVersion = VersionUtils.getVersion(getComponentName("name", cVersion, wVersion, wipVersion));

        Assert.assertEquals(cVersion, wineryVersion.getComponentVersion());
        Assert.assertEquals(wVersion, wineryVersion.getWineryVersion());
        Assert.assertEquals(wipVersion, wineryVersion.getWorkInProgressVersion());
    }

    @Test
    public void getNameWithoutVersion() {
        String nameWithoutVersion = "myNewCoolComponent";
        String name = VersionUtils.getNameWithoutVersion(getComponentName(nameWithoutVersion, "2", 2, 1));
        Assert.assertEquals(nameWithoutVersion, name);
    }

    @Test
    public void getNameWithoutVersionFromUnderscoreSeparatedString() {
        String nameWithoutVersion = "my_uncool_name_with_underscores_and_no_version";
        String name = VersionUtils.getNameWithoutVersion(nameWithoutVersion);
        Assert.assertEquals(nameWithoutVersion, name);
    }

    private String getComponentName(String name, String componentVersion, int wineryVersion, int wipVersion) {
        return name
            + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + componentVersion
            + WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_VERSION_PREFIX + wineryVersion
            + WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_WIP_VERSION_PREFIX + wipVersion;
    }

    @Test
    public void getVersionWithCurrentFlag() {
        String name = getComponentName("myComponent", "12.54.3", 1, 0);
        Assert.assertEquals(true, VersionUtils.getVersionWithCurrentFlag(name, name).isCurrentVersion());
    }

    @Test
    public void getVersion() {
        String componentVersion = "1.0.0";
        int wineryVersion = 6;
        int wipVersion = 3;

        DefinitionsChildId id = getDefinitionChildId("http://example.org/tosca/versions", "myElement", componentVersion, wineryVersion, wipVersion);
        WineryVersion version = VersionUtils.getVersion(id);

        Assert.assertEquals(componentVersion, version.getComponentVersion());
        Assert.assertEquals(wineryVersion, version.getWineryVersion());
        Assert.assertEquals(wipVersion, version.getWorkInProgressVersion());
    }

    @Test
    public void getNameWithoutVersionFromDefinitionsChildId() {
        String name = "myElementTest";
        DefinitionsChildId id = getDefinitionChildId("http://example.org/tosca/versions", name, "1.2.3", 1, 1);

        Assert.assertEquals(name, VersionUtils.getNameWithoutVersion(id));
    }

    private DefinitionsChildId getDefinitionChildId(String namespace, String name, String componentVersion, int wineryVersion, int wipVersion) {
        String elementName = name
            + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + componentVersion
            + WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_VERSION_PREFIX + wineryVersion
            + WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_WIP_VERSION_PREFIX + wipVersion;

        return new ServiceTemplateId(namespace, elementName, false);
    }
}
