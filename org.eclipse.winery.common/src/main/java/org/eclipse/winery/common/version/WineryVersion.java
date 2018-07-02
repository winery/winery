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

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.adr.embedded.ADR;
import org.apache.commons.lang3.StringUtils;

public class WineryVersion implements Comparable<WineryVersion> {

    public static String WINERY_NAME_FROM_VERSION_SEPARATOR = "_";
    public static String WINERY_VERSION_SEPARATOR = "-";
    public static String WINERY_VERSION_PREFIX = "w";
    public static String WINERY_WIP_VERSION_PREFIX = "wip";

    private String componentVersion;
    private int wineryVersion;
    private int workInProgressVersion;
    private boolean currentVersion;
    private boolean latestVersion;
    private boolean releasable;
    private boolean editable;

    public WineryVersion() {
        this("", 0, 0);
    }

    public WineryVersion(String componentVersion, int wineryVersion, int workInProgressVersion) {
        this.componentVersion = componentVersion;
        this.wineryVersion = Math.abs(wineryVersion);
        this.workInProgressVersion = Math.abs(workInProgressVersion);
        this.latestVersion = false;
        this.releasable = false;
        // to support editing of not versioned definitions
        this.editable = !this.isVersionedInWinery();
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    public int getWineryVersion() {
        return wineryVersion;
    }

    public void setWineryVersion(int wineryVersion) {
        this.wineryVersion = wineryVersion;
    }

    public int getWorkInProgressVersion() {
        return workInProgressVersion;
    }

    public void setWorkInProgressVersion(int workInProgressVersion) {
        this.workInProgressVersion = workInProgressVersion;
    }

    public boolean isCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(boolean currentVersion) {
        this.currentVersion = currentVersion;
    }

    public boolean isLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(boolean latestVersion) {
        this.latestVersion = latestVersion;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isReleasable() {
        return releasable;
    }

    /**
     * Sets the releasable flag of this version but ensures that already released versions are not.
     */
    public void setReleasable(boolean releasable) {
        this.releasable = releasable && this.workInProgressVersion > 0;
    }

    @JsonIgnore
    public boolean isVersionedInWinery() {
        return toString().length() > 0;
    }

    @Override
    @ADR(19)
    public int compareTo(WineryVersion o) {
        if (Objects.isNull(o)) {
            return 1;
        }

        int cVersion = this.componentVersion.compareToIgnoreCase(o.componentVersion);
        if (cVersion < 0) {
            return -1;
        } else if (cVersion > 0) {
            return 1;
        }

        if (this.wineryVersion < o.wineryVersion) {
            return -1;
        } else if (this.wineryVersion > o.wineryVersion) {
            return 1;
        }

        if (this.wineryVersion > 0 && this.workInProgressVersion == 0) {
            return 1;
        } else if (o.wineryVersion > 0 && o.workInProgressVersion == 0) {
            return -1;
        }

        if (this.workInProgressVersion < o.workInProgressVersion) {
            return -1;
        } else if (this.workInProgressVersion > o.workInProgressVersion) {
            return 1;
        }

        return 0;
    }

    @Override
    public String toString() {
        String versionString = componentVersion;

        if (this.wineryVersion > 0) {
            versionString += (StringUtils.isEmpty(versionString) ? "" : WINERY_VERSION_SEPARATOR)
                + WINERY_VERSION_PREFIX + wineryVersion;

            if (this.workInProgressVersion > 0) {
                versionString += WINERY_VERSION_SEPARATOR + WINERY_WIP_VERSION_PREFIX + this.workInProgressVersion;
            }
        }

        return versionString;
    }
}
