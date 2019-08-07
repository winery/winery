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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.crawler.chefcookbooks.constants.Defaults;

public class CookbookParseResult {

    private String cookbookName;

    private String cookbookPath;

    // Boolean to mark that compiler is in an dependent cookbook recipe
    private boolean isInDependentRecipe;

    private HashMap<String, HashMap<String, ChefCookbookConfiguration>> cookbookConfigs;

    private HashMap<String, String> attributes;

    private boolean isInRecursiveTransformation;

    // saves packages until all information are extracted
    private ArrayList<ChefPackage> notatedPackages;

    public CookbookParseResult(String cookbookName) {
        this.cookbookName = cookbookName;
        this.cookbookPath = null;
        this.cookbookConfigs = new HashMap<>();
        this.attributes = new HashMap<>();
        this.notatedPackages = new ArrayList<>();
        this.isInDependentRecipe = false;
        this.isInRecursiveTransformation = false;
    }

    public CookbookParseResult(CookbookParseResult cookbookParseResult) {
        this.cookbookName = cookbookParseResult.cookbookName;
        this.cookbookPath = cookbookParseResult.cookbookPath;
        this.cookbookConfigs = new HashMap<>();
        for (int i = 0; i < cookbookParseResult.getAllConfigsAsList().size(); i++) {
            this.putCookbookConfig(new ChefCookbookConfiguration(cookbookParseResult.getAllConfigsAsList().get(i)));
        }
        this.attributes = (HashMap<String, String>) cookbookParseResult.attributes.clone();
        this.notatedPackages = (ArrayList<ChefPackage>) cookbookParseResult.notatedPackages.clone();
        this.isInDependentRecipe = cookbookParseResult.isInDependentRecipe;
        this.isInRecursiveTransformation = cookbookParseResult.isInRecursiveTransformation;
    }

    /**
     * Get the name of the cookbook this parse result is for.
     *
     * @return Returns the name of the cookbook.
     */
    public String getCookbookName() {
        return cookbookName;
    }

    /**
     * Set Cookbook name for which this result is.
     *
     * @param cookbookName Name of the cookbook which this result is for.
     */
    public void setCookbookName(String cookbookName) {
        this.cookbookName = cookbookName;
    }

    public boolean isInDependentRecipe() {
        return isInDependentRecipe;
    }

    public void setInDependentRecipe(boolean inDependentRecipe) {
        this.isInDependentRecipe = inDependentRecipe;
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }

    public void putAttribute(String key, String value) {
        this.attributes.put(key, value);
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    // Get all configurations of a cookbook
    public HashMap<String, HashMap<String, ChefCookbookConfiguration>> getCookbookConfigs() {
        return cookbookConfigs;
    }

    // Get all configurations with a specific platform name
    public HashMap<String, ChefCookbookConfiguration> getCookbookConfigsByPlatform(String platformName) {
        return cookbookConfigs.get(platformName);
    }

    public void setCookbookConfigs(HashMap<String, HashMap<String, ChefCookbookConfiguration>> cookbookConfigs) {
        this.cookbookConfigs = cookbookConfigs;
    }

    public void putCookbookConfig(ChefCookbookConfiguration cookbookConfiguration) {
        String outerKey = cookbookConfiguration.getSupports().getName();
        String innerKey = cookbookConfiguration.getSupports().getVersion();
        if (cookbookConfigs.containsKey(outerKey)) {
            cookbookConfigs.get(outerKey).put(innerKey, cookbookConfiguration);
        } else {
            HashMap<String, ChefCookbookConfiguration> innerMap = new HashMap<>();
            innerMap.put(innerKey, cookbookConfiguration);
            cookbookConfigs.put(outerKey, innerMap);
        }
    }

    // Clear cookbook configurations and attributes
    public void clear() {
        cookbookConfigs.clear();
        attributes.clear();
    }

    public void clearAttributes() {
        attributes.clear();
    }

    // delete all cookbook configurations
    public void clearConfigurations() {
        cookbookConfigs.clear();
    }

    // Put Cookbook configurations as a list to parse result.
    public void putCookbookConfigsAsList(List<ChefCookbookConfiguration> cookbookConfigList) {
        for (int count = 0; count < cookbookConfigList.size(); count++) {
            ChefCookbookConfiguration cookbookConfiguration = cookbookConfigList.get(count);
            putCookbookConfig(cookbookConfiguration);
        }
    }

    /**
     * Get all cookbook configuartions as a list.
     *
     * @return Returns a list of all cookbook configurations.
     */
    public List<ChefCookbookConfiguration> getAllConfigsAsList() {
        List<ChefCookbookConfiguration> configList = new LinkedList<>();
        for (Map.Entry<String, HashMap<String, ChefCookbookConfiguration>> entry : cookbookConfigs.entrySet()) {
            HashMap<String, ChefCookbookConfiguration> value = entry.getValue();
            for (Map.Entry<String, ChefCookbookConfiguration> innerentry : value.entrySet()) {
                String innerkey = innerentry.getKey();
                configList.add(value.get(innerkey));
            }
        }
        return configList;
    }

    /**
     * Make a note of a package from which not all information is available yet.
     *
     * @param chefPackage The package to notate.
     */
    public void addNotatedPackage(ChefPackage chefPackage) {
        notatedPackages.add(chefPackage);
    }

    public ArrayList<ChefPackage> getNotatedPackages() {
        return notatedPackages;
    }

    public void clearNotatedPackage() {
        notatedPackages.clear();
    }

    /**
     * Add a attribute to all existing configurations in parse result.
     *
     * @param name  Name of the attribute.
     * @param value Value of the attribute.
     */
    public void addAttributeToAllConfigs(String name, List value) {
        for (Map.Entry<String, HashMap<String, ChefCookbookConfiguration>> entry : cookbookConfigs.entrySet()) {
            for (Map.Entry<String, ChefCookbookConfiguration> innerentry : entry.getValue().entrySet()) {
                innerentry.getValue().putAttribute(name, value);
            }
        }
    }

    public void replaceCookbookConfigs(List<ChefCookbookConfiguration> cookbookConfigList) {
        this.clearConfigurations();
        this.putCookbookConfigsAsList(cookbookConfigList);
    }

    /**
     * Get a list of all cookbook configurations where each has its own parse result. This enables processing each
     * configuration on its own in the parser.
     *
     * @return Returns a list of parse results where each parse result has one cookbook configuration.
     */
    public List<CookbookParseResult> getListOfConfigsInOwnParseresult() {
        List<CookbookParseResult> parseResultsList = new ArrayList<>();
        List<ChefCookbookConfiguration> cookbookConfigs = this.getAllConfigsAsList();
        if (cookbookConfigs.size() > 0) {
            for (int i = 0; i < cookbookConfigs.size(); i++) {
                CookbookParseResult parseResult = new CookbookParseResult(this);
                parseResult.clearConfigurations();
                parseResult.putCookbookConfig(cookbookConfigs.get(i));
                parseResultsList.add(parseResult);
            }
        } else {
            CookbookParseResult parseResult = new CookbookParseResult(this);
            parseResultsList.add(parseResult);
        }
        return parseResultsList;
    }

    /**
     * Get number of cookbook configuations in parse result.
     *
     * @return Returns amount of cookbook configurations.
     */
    public int getNumOfCookbookConfigs() {
        return this.getAllConfigsAsList().size();
    }

    /**
     * Get location of the cookbook.
     *
     * @return Returns the location of a cookbook.
     */
    public String getCookbookPath() {
        return cookbookPath;
    }

    /**
     * Set location of cookbook.
     *
     * @param cookbookPath The file path to cookbook.
     */
    public void setCookbookPath(String cookbookPath) {
        if (cookbookPath.endsWith("/")) {
            this.cookbookPath = cookbookPath.substring(0, cookbookPath.length() - 1);
        } else {
            this.cookbookPath = cookbookPath;
        }
    }

    public void prepareForDependencie(String cookbookName) {
        this.setCookbookName(cookbookName);
        this.setCookbookPath(this.getCookbookPath() + Defaults.DEPENDENCIE_FOLDER + "/" + cookbookName);
        this.attributes = new HashMap<>();
        this.notatedPackages = new ArrayList<>();
        this.isInDependentRecipe = false;
        this.isInRecursiveTransformation = true;
        List<ChefCookbookConfiguration> cookbookConfigurations = new ArrayList<>();
        for (int i = 0; i < this.getAllConfigsAsList().size(); i++) {
            if (this.getAllConfigsAsList().get(i).getDependentRecipes(cookbookName) == null) {
                //this.getAllConfigsAsList().remove(i);
            } else {
                ChefCookbookConfiguration cookbookConfiguration = new ChefCookbookConfiguration(this.getAllConfigsAsList().get(i));
                cookbookConfiguration.prepareForDependencie(cookbookName);
                cookbookConfigurations.add(cookbookConfiguration);
            }
        }
        replaceCookbookConfigs(cookbookConfigurations);
    }

    public boolean isInRecursiveTransformation() {
        return isInRecursiveTransformation;
    }

    public void setInRecursiveTransformation(boolean inRecursiveTransformation) {
        isInRecursiveTransformation = inRecursiveTransformation;
    }
}
