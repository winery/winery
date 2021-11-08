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

package org.eclipse.winery.crawler.chefcookbooks.chefdslparser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.ChefCookbookConfiguration;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.CookbookParseResult;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.Platform;
import org.eclipse.winery.crawler.chefcookbooks.constants.Defaults;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This class provides functionalitity to extract information from metadata.json. In a normal chef cookbook the
 * metadata.rb is required, but there are cookbooks without existing metadata.rb but a metadata.json file instead.
 */
public class MetadataJsonVisitor {

    /**
     * This method provides functionality to extract the information from the metadata.json file of a chef cookbook.
     * Normally chef cookbooks must have a metadata.rb file. This method is a backup and called when cookbook doesn't
     * contain a metadata.rb file.
     *
     * @param parseResult A cookbook parse result. Must contain the path to the cookbook.
     * @return Returns cookbook parse result with added information from metadata.json file.
     */
    public CookbookParseResult visitMetadataJson(CookbookParseResult parseResult) {

        JSONParser jsonParser = new JSONParser();
        String path = parseResult.getCookbookPath();
        String metadataPath = path + "/metadata.json";

        try (FileReader reader = new FileReader(metadataPath)) {
            //Read JSON file
            JSONObject metadata = (JSONObject) jsonParser.parse(reader);

            // Read and process name attribute
            String name = (String) metadata.get("name");
            parseResult = processCookbookName(parseResult, name);

            String version = (String) metadata.get("version");
            parseResult = processCookbookVersion(parseResult, version);

            // Read and process description attribute
            String description = (String) metadata.get("description");
            parseResult = processCookbookDescription(parseResult, description);

            JSONObject platforms = (JSONObject) metadata.get("platforms");
            parseResult = processSupportedPlatforms(parseResult, platforms);

            // Read and process cookbook dependencies
            JSONObject dependencies = (JSONObject) metadata.get("dependencies");
            parseResult = processCookbookDependencies(parseResult, dependencies);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parseResult;
    }

    /**
     * Process command "name" in metadata.rb file of a cookbook. Adds a the name of the cookbook to all configurations.
     *
     * @param parseResult An existing parse result.
     * @param name        Name of a cookbook.
     * @see <a href="https://docs.chef.io/config_rb_metadata.html">/a>
     */
    private CookbookParseResult processCookbookName(CookbookParseResult parseResult, String name) {
        if (parseResult.getAllConfigsAsList().size() == 0) {
            ChefCookbookConfiguration componentType = new ChefCookbookConfiguration();
            componentType.setName(name);
            parseResult.putCookbookConfig(componentType);
        } else {
            List<ChefCookbookConfiguration> cookbookConfigs;
            cookbookConfigs = parseResult.getAllConfigsAsList();
            for (int count = 0; count < cookbookConfigs.size(); count++) {
                cookbookConfigs.get(count).setVersion(name);
            }
            parseResult.replaceCookbookConfigs(cookbookConfigs);
        }
        return parseResult;
    }

    /**
     * Process command "version" in metadata.rb file of a cookbook. This is the unique version of a cookbook.
     *
     * @param parseResult An existing parse result with a cookbook configuration.
     * @param version     Version of a cookbook.
     * @return Returns the CookbookParseResult with added description.
     * @see <a href="https://docs.chef.io/config_rb_metadata.html">/a>
     */
    private CookbookParseResult processCookbookVersion(CookbookParseResult parseResult, String version) {
        List<ChefCookbookConfiguration> cookbookConfigs;
        cookbookConfigs = parseResult.getAllConfigsAsList();
        for (int count = 0; count < cookbookConfigs.size(); count++) {
            cookbookConfigs.get(count).setVersion(version);
        }
        parseResult.replaceCookbookConfigs(cookbookConfigs);
        return parseResult;
    }

    /**
     * Process command "depends" in metadata.rb file of a cookbook. This describes dependencies to other cookbooks.
     *
     * @param parseResult An existing parse result with a cookbook configuration.
     * @param description Description of a cookbook.
     * @return Returns the CookbookParseResult with added description.
     * @see <a href="https://docs.chef.io/config_rb_metadata.html">/a>
     */
    private CookbookParseResult processCookbookDescription(CookbookParseResult parseResult, String description) {
        List<ChefCookbookConfiguration> cookbookConfigs;
        cookbookConfigs = parseResult.getAllConfigsAsList();
        for (int count = 0; count < cookbookConfigs.size(); count++) {
            cookbookConfigs.get(count).setDescription(description);
        }
        parseResult.replaceCookbookConfigs(cookbookConfigs);
        return parseResult;
    }

    /**
     * This method provides the functionality to extract the dependent cookbooks with version constraints.
     *
     * @param dependencies The content from the "dependencies" field of the metadata.json file as an JSON Opject.
     */
    private CookbookParseResult processCookbookDependencies(CookbookParseResult parseResult, JSONObject dependencies) {
        List<ChefCookbookConfiguration> cookbookConfigs;
        String depends;
        String dependsVersion;
        cookbookConfigs = parseResult.getAllConfigsAsList();

        for (Object key : dependencies.keySet()) {
            depends = key.toString();

            for (int count = 0; count < cookbookConfigs.size(); count++) {

                dependsVersion = dependencies.get(key).toString();
                cookbookConfigs.get(count).addDepends(depends, dependsVersion);
            }
        }
        parseResult.replaceCookbookConfigs(cookbookConfigs);
        return parseResult;
    }

    /**
     * This method provides the functionality to extract the supported platforms with version constraints. A cookbook
     * configuration is added for each extracted platform.
     *
     * @param parseResult Cookbook parse result with path to cookbook and a cookbook configuration which contains the
     *                    name of the cookbook from the metadata.
     * @param platforms   The content from the "platforms" field of the metadata.json file as an JSON Opject.
     * @return Parse result with generated coobook configurations.
     */
    private CookbookParseResult processSupportedPlatforms(CookbookParseResult parseResult, JSONObject platforms) {
        if (parseResult.isInRecursiveTransformation() == true) {
            return parseResult;
        }
        List<ChefCookbookConfiguration> cookbookConfigs;
        String supports;
        String version;

        for (Object key : platforms.keySet()) {
            supports = key.toString();
            cookbookConfigs = parseResult.getAllConfigsAsList();

            version = platforms.get(key).toString();

            if (cookbookConfigs.get(0).getSupports().getName().equals(Defaults.COOKBOOKCONFIG_SUPPORTS_NO_PLATFORM)) {
                cookbookConfigs.get(0).setSupports(new Platform(supports, version));
                parseResult.clearConfigurations();
            } else {
                ChefCookbookConfiguration newConfig = new ChefCookbookConfiguration(cookbookConfigs.get(0));
                newConfig.setSupports(new Platform(supports, version));
                cookbookConfigs.add(newConfig);
                parseResult.clearConfigurations();
            }
            parseResult.putCookbookConfigsAsList(cookbookConfigs);
        }
        return parseResult;
    }
}
