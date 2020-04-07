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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.winery.crawler.chefcookbooks.constants.Defaults;
import org.eclipse.winery.crawler.chefcookbooks.constants.OhaiFunctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChefCookbookConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChefCookbookConfiguration.class.getName());

    private String name;

    private String version;

    private Platform supports;

    /**
     * The dependencies to other cookbooks. First String is name of dependency. Second String is version constraint.
     */
    private LinkedHashMap<String, String> depends;

    private String description;

    private HashMap<String, List> attributes;

    private LinkedHashMap<String, ChefPackage> installedPackages;

    private LinkedHashMap<String, ChefPackage> requiredPackages;

    private HashMap<String, List<String>> dependentRecipes;

    // List of recipes to run
    private List<String> runlist;

    public ChefCookbookConfiguration(ChefCookbookConfiguration componentType) {
        this.name = componentType.name;
        this.version = componentType.version;
        this.supports = new Platform(componentType.supports);
        this.depends = (LinkedHashMap<String, String>) componentType.depends.clone();
        this.description = componentType.description;
        this.installedPackages = (LinkedHashMap<String, ChefPackage>) componentType.installedPackages.clone();
        this.requiredPackages = (LinkedHashMap<String, ChefPackage>) componentType.requiredPackages.clone();
        this.attributes = (HashMap<String, List>) componentType.attributes.clone();
        this.dependentRecipes = (HashMap<String, List<String>>) componentType.dependentRecipes.clone();
        this.runlist = componentType.runlist;
    }

    public ChefCookbookConfiguration() {
        this.name = null;
        this.version = null;
        this.supports = new Platform(Defaults.COOKBOOKCONFIG_SUPPORTS_NO_PLATFORM);
        this.depends = new LinkedHashMap<>();
        this.description = null;
        this.installedPackages = new LinkedHashMap<>();
        this.requiredPackages = new LinkedHashMap<>();
        this.attributes = new HashMap<>();
        this.dependentRecipes = new HashMap<>();
        this.runlist = new LinkedList<>();
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

    public Platform getSupports() {
        return supports;
    }

    public void setSupports(Platform supports) {
        this.supports = supports;
        this.setOhaiPlatformAttributes(supports.getName());
        this.setOhaiPlatformVersionAttribute(supports.getVersion());
    }

    public LinkedHashMap<String, String> getDepends() {
        return depends;
    }

    public void setDepends(LinkedHashMap depends) {
        this.depends = depends;
    }

    public void addDepends(String depends, String version) {
        this.depends.put(depends, version);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This method adds a found package to installed packages of chef configuration. Add in this context means that
     * package is added to list if action is ":install", ":nothing" or ":upgrade", else if action is ":remove" or
     * ":purge" the package is removed from the list according to chef documentation.
     *
     * @param installedPackage Package that is installed in this configuration.
     */
    public void addInstalledPackage(ChefPackage installedPackage) {
        String action = installedPackage.getAction();
        if (":nothing".equals(action) || ":install".equals(action) || "upgrade".equals(action)) {
            installedPackages.put(installedPackage.getName(), installedPackage);
        } else if (":remove".equals(action) || ":purge".equals(action)) {
            installedPackages.remove(installedPackage.getName());
        }
    }

    public LinkedHashMap<String, ChefPackage> getRequiredPackages() {
        return requiredPackages;
    }

    /**
     * This method adds a found package to requried packages of chef configuration. Add in this context means that
     * package is added to list if action is ":install", ":nothing" or ":upgrade", else if action is ":remove" or
     * ":purge" the package is removed from the list according to chef documentation.
     *
     * @param requiredPackage Package that is installed by a dependent cookbook in this configuration.
     */
    public void addRequiredPackage(ChefPackage requiredPackage) {
        String action = requiredPackage.getAction();
        if (":nothing".equals(action) || ":install".equals(action) || "upgrade".equals(action)) {
            requiredPackages.put(requiredPackage.getName(), requiredPackage);
        } else if (":remove".equals(action) || ":purge".equals(action)) {
            installedPackages.remove(requiredPackage.getName());
        }
    }

    public LinkedHashMap<String, ChefPackage> getInstalledPackages() {
        return installedPackages;
    }

    public List getAttribute(String key) {
        return attributes.get(key);
    }

    public void putAttribute(String key, List value) {
        this.attributes.put(key, value);
    }

    public HashMap<String, List> getAttributes() {
        return attributes;
    }

    // Print method for debugging purposes.
    public void printConfiguration() {
        LOGGER.info("name: " + this.getName() + "\n"
            + "version: " + this.getVersion() + "\n"
            + "description: " + this.getDescription() + "\n"
            + "supports: " + this.getSupports().getName() + ", " + "version: " + this.getSupports().getVersion() + "\n"
            + "depends: " + this.getDepends() + "\n"
            + "packages: " + this.getInstalledPackages().size());
        for (int i = 0; i < installedPackages.size(); i++) {
            ChefPackage chefPackage = getPackageByIndex(installedPackages, i);
            LOGGER.info("Installed Package: " + chefPackage.getPackageName() + " Version: " + chefPackage.getVersion() + " Action: " + chefPackage.getAction() + " -Ressourcename: " + chefPackage.getName());
        }

        for (int i = 0; i < requiredPackages.size(); i++) {
            ChefPackage chefPackage = getPackageByIndex(requiredPackages, i);
            LOGGER.info("Required Package: " + chefPackage.getPackageName() + " Version: " + chefPackage.getVersion() + " Action: " + chefPackage.getAction() + " -Ressourcename: " + chefPackage.getName());
        }
    }

    /**
     * Sets the Ohai attributes that are dependant from the platform name.
     *
     * @param platformName The name of the platform.
     */
    public void setOhaiPlatformAttributes(String platformName) {
        this.putAttribute(OhaiFunctions.OHAI_PLATFORM_ATTRIBUTE_NAME, Collections.singletonList(platformName));
        this.putAttribute(OhaiFunctions.OHAI_PLATFORMFAMILY_ATTRIBUTE_NAME, Collections.singletonList(OhaiFunctions.getPlatformFamilyFromPlatform(platformName)));
    }

    /**
     * Sets the Ohai attributes that are dependant from the platform name.
     *
     * @param platformVersion The name of the platform.
     */
    public void setOhaiPlatformVersionAttribute(String platformVersion) {
        this.putAttribute(OhaiFunctions.OHAI_PLATFORMVERSION_ATTRIBUTE_NAME, Collections.singletonList(platformVersion));
    }

    /**
     * This function resolves the platform?('platform') function of the Chef DSL
     *
     * @return Returns true if configuration has one of the passed platforms.
     */
    public boolean hasPlatform(HashSet platforms) {
        String platformName = (String) attributes.get(OhaiFunctions.OHAI_PLATFORM_ATTRIBUTE_NAME).get(0);
        return (platforms.contains(platformName));
    }

    /**
     * This function resolves the platform_family?('platformfamily') function of the Chef DSL
     *
     * @param platformFamilies Set of the passed platform families.
     * @return Returns true if configuration has one of the passed platforms.
     */
    public boolean hasPlatformFamily(HashSet platformFamilies) {
        String platformName = (String) attributes.get(OhaiFunctions.OHAI_PLATFORMFAMILY_ATTRIBUTE_NAME).get(0);
        return (platformFamilies.contains(platformName));
    }

    public static ChefPackage getPackageByIndex(LinkedHashMap<String, ChefPackage> hMap, int index) {
        return (ChefPackage) hMap.values().toArray()[index];
    }

    public List<String> getDependentRecipes(String cookbook) {
        return dependentRecipes.get(cookbook);
    }

    public List<String> getRunlist() {
        return runlist;
    }

    public void setRunlist(List<String> runlist) {
        this.runlist = runlist;
    }

    public void addDependentRecipes(String cookbook, String recipe) {
        if (this.dependentRecipes.get(cookbook) == null) {
            this.dependentRecipes.put(cookbook, new LinkedList<>());
            this.dependentRecipes.get(cookbook).add(recipe);
        } else {
            this.dependentRecipes.get(cookbook).add(recipe);
        }
    }

    public void prepareForDependencie(String cookbook) {
        this.name = cookbook;
        this.version = null;
        this.depends = new LinkedHashMap<>();
        this.description = null;
        this.installedPackages = new LinkedHashMap<>();
        this.requiredPackages = new LinkedHashMap<>();
        this.runlist = this.getDependentRecipes(cookbook);
        this.dependentRecipes = new HashMap<>();
    }
}
