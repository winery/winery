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

package org.eclipse.winery.tools.deployablecomponents.commons;

public class Component {
    private String name;
    private String version;
    private String versionOperator;

    public Component(String name, String version, String versionOperator) {
        this.name = name;
        this.version = version;
        this.versionOperator = versionOperator;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getVersionOperator() {
        return versionOperator;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Component)) {
            return false;
        }

        Component that = (Component) other;
        return this.versionOperator.equals(that.getVersionOperator())
            && this.version.equals(that.getVersion())
            && this.name.equals(that.getName());
    }

    @Override
    public int hashCode() {
        int hashCode = 1;

        hashCode = hashCode * 37 + this.name.hashCode();
        hashCode = hashCode * 37 + this.version.hashCode();
        hashCode = hashCode * 37 + this.versionOperator.hashCode();

        return hashCode;
    }
}
