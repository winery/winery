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
package org.eclipse.winery.common.version;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.adr.embedded.ADR;

/**
 * Utility class for working with versions.
 */
public class VersionUtils {

    public static final Pattern VERSION_PATTERN = Pattern.compile("_((?<component>[^_]*)-)?w(?<winery>\\d+)(-wip(?<wip>\\d+))?$");
    public static final String COMPONENT_VERSION_GROUP = "component";
    public static final String WINERY_VERSION_GROUP = "winery";
    public static final String WIP_VERSION_GROUP = "wip";

    public static String getNameWithoutVersion(String id) {
        Matcher m = VERSION_PATTERN.matcher(id);
        int idLength = id.length();

        if (m.find()) {
            idLength = m.start();
        }

        return id.substring(0, idLength);
    }

    @ADR(18)
    public static WineryVersion getVersion(String id) {
        Matcher m = VERSION_PATTERN.matcher(id);

        if (m.find()) {
            String componentVersion = Objects.nonNull(m.group(COMPONENT_VERSION_GROUP)) ? m.group(COMPONENT_VERSION_GROUP) : "";
            // winery group is not optional
            int wineryVersion = Integer.parseInt(m.group(WINERY_VERSION_GROUP));
            int workInProgressVersion = Objects.nonNull(m.group(WIP_VERSION_GROUP)) ? Integer.parseInt(m.group(WIP_VERSION_GROUP)) : 0;

            return new WineryVersion(componentVersion, wineryVersion, workInProgressVersion);
        }

        return new WineryVersion();
    }

    public static WineryVersion getVersionWithCurrentFlag(String id, String requestingElementInSet) {
        WineryVersion version = getVersion(id);
        version.setCurrentVersion(id.equals(requestingElementInSet));
        return version;
    }

    public static WineryVersion getNewWineryVersion(List<WineryVersion> versions) {
        WineryVersion[] version = new WineryVersion[1];
        version[0] = versions.stream().filter(WineryVersion::isCurrentVersion)
            .findFirst()
            .orElseThrow(NullPointerException::new);

        if (!version[0].isReleasable()) {
            versions.forEach(wineryVersion -> {
                if (Objects.nonNull(version[0].getComponentVersion()) && version[0].getComponentVersion().equals(wineryVersion.getComponentVersion())
                    && wineryVersion.getWineryVersion() > version[0].getWineryVersion()) {
                    version[0] = wineryVersion;
                }
            });
        }

        return new WineryVersion(version[0].getComponentVersion(), version[0].getWineryVersion() + 1, 1);
    }
}
