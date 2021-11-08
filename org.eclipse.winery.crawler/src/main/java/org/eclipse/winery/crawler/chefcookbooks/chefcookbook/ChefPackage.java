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

package org.eclipse.winery.crawler.chefcookbooks.chefcookbook;

import org.eclipse.winery.crawler.chefcookbooks.constants.ChefDslConstants;

public class ChefPackage {

    private String name;

    private String packageName;

    private String version;

    // Action of the package. 
    // See: https://docs.chef.io/resource_package.html
    private String action;

    public ChefPackage(String name) {
        this.name = name;
        this.packageName = name;
        this.action = ":install";
    }

    public ChefPackage(String name, String version) {
        this.name = name;
        this.version = version;
        this.packageName = name;
        this.action = ":install";
    }

    public ChefPackage(ChefPackage chefPackage) {
        this.name = chefPackage.name;
        this.packageName = chefPackage.packageName;
        this.version = chefPackage.version;
        this.action = chefPackage.action;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * This method adds properties to a chef package in the correct way.
     *
     * @return Returns true if the property is added, else false. If false, the property to add is not supported by this
     * class.
     */
    public boolean addProperty(String propertyName, String propertyValue) {
        Boolean propertyAdded = false;

        switch (propertyName) {
            case ChefDslConstants.PACKAGE_NAME_PROPERTY:
                setPackageName(propertyValue);
                propertyAdded = true;
                break;
            case ChefDslConstants.PACKAGE_VERSION_PROPERTY:
                setVersion(propertyValue);
                propertyAdded = true;
                break;
            case "action":
                setAction(propertyValue);
                propertyAdded = true;
                break;
            default:
                propertyAdded = false;
                break;
        }
        return propertyAdded;
    }
}
