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

package org.eclipse.winery.crawler.chefcookbooks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.ParseResultToscaConverter;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbookcrawler.CrawlCookbookRunnable;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbookcrawler.CrawledCookbooks;
import org.eclipse.winery.crawler.chefcookbooks.chefdslparser.MetadataJsonVisitor;
import org.eclipse.winery.crawler.chefcookbooks.constants.ChefDslConstants;
import org.eclipse.winery.crawler.chefcookbooks.constants.Defaults;
import org.eclipse.winery.crawler.chefcookbooks.chefdslparser.CookbookVisitor;
import org.eclipse.winery.crawler.chefcookbooks.chefdslparser.ChefDSLLexer;
import org.eclipse.winery.crawler.chefcookbooks.chefdslparser.ChefDSLParser;
import org.eclipse.winery.crawler.chefcookbooks.helper.RubyCodeHelper;
import org.eclipse.winery.crawler.chefcookbooks.kitchenparser.ChefKitchenYmlParser;
import org.eclipse.winery.crawler.chefcookbooks.kitchenparser.KitchenUtilities;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.CookbookParseResult;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.ChefCookbookConfiguration;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.Platform;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.antlr.v4.runtime.CharStreams.fromFileName;

public class ChefCookbookAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChefCookbookAnalyzer.class.getName());

    /**
     * Main Method to extract deployment architectures from chef cookbooks.
     */
    public void main() {
        String[] availableCookbooks;
        String cookbookName;
        CookbookParseResult extractedCookbookConfigs;
        String lastCoobook = "";
        
        while (CrawledCookbooks.getDirectories(Defaults.COOKBOOK_PATH).length > 0) {
            availableCookbooks = CrawledCookbooks.getDirectories(Defaults.COOKBOOK_PATH);
            cookbookName = availableCookbooks[0];
            if (!lastCoobook.equals(cookbookName)) {
                extractedCookbookConfigs = new CookbookParseResult(cookbookName);
                extractedCookbookConfigs.setCookbookPath(Defaults.COOKBOOK_PATH + "/" + cookbookName);

                extractedCookbookConfigs = compileCookbook(extractedCookbookConfigs, true);

                CrawledCookbooks.deleteFile(extractedCookbookConfigs.getCookbookPath());
                extractedCookbookConfigs.clear();
                
                lastCoobook = cookbookName;
            }
        }
    }

    /**
     * Method compiles a cookbook.
     * All information about used versions of softwares and platfroms are extracted and mapped to cookbook 
     * configuration in the parse result.
     * Method crawls depenencies and analyzes them too.
     * @param cookbookParseResult Initialized parse result. Must store the path of the cookbook.
     * @return Returns the compiled cookbook in the parse result.
     */
    public CookbookParseResult compileCookbook(CookbookParseResult cookbookParseResult, boolean saveToToscaRepo) {
        
        CookbookParseResult oldParseResult = new CookbookParseResult(cookbookParseResult);
        List<CrawlCookbookRunnable> crawlCookbookRunnableList = new ArrayList<>();
        
        //Compile metadata.
        cookbookParseResult = returnComponentTypesFromMetadata(cookbookParseResult);

        // cookbookParseResult is null when cookbook has no metadata.rb or metadata.json file.
        if ( cookbookParseResult == null) {
            return oldParseResult;
        }

        //Crawl dependent cookbooks in threads.
        if (cookbookParseResult.getAllConfigsAsList().size() > 0) {
            LinkedHashMap<String,String> dependencies = cookbookParseResult.getAllConfigsAsList().get(0).getDepends();
            String pathDependencies = cookbookParseResult.getCookbookPath() + Defaults.DEPENDENCIE_FOLDER;
            for (String key : dependencies.keySet()) {
                String versionRestriciton = dependencies.get(key);
                CrawlCookbookRunnable crawlDependency = new CrawlCookbookRunnable(key, versionRestriciton, pathDependencies, Defaults.TEMP_FOLDER_PATH);
                crawlDependency.start();
                crawlCookbookRunnableList.add(crawlDependency);
            }
        }

        // Compile kitchen.yml
        if (cookbookParseResult.isInRecursiveTransformation() == false ) {
            cookbookParseResult = addPlatformVersionInformationFromKitchen(cookbookParseResult);
        }

        // Wait for the dependencies to download.
        for (int i = 0; i < crawlCookbookRunnableList.size(); i++) {
            try {
                crawlCookbookRunnableList.get(i).join();
            } catch (InterruptedException e) {
                LOGGER.info("Crawling process of dependent cookbook " + crawlCookbookRunnableList.get(i).getThreadName() + " interrupted");
            }
        }

        // Compile attribute files.
        cookbookParseResult = returnComponentTypesFromAttributes(cookbookParseResult);

        // Compile recipes, starting from default.rb recipe.
        cookbookParseResult = returnComponentTypesFromRecipes(cookbookParseResult);

        // Resolve dependencies
        for (Map.Entry<String,String> entry : cookbookParseResult.getAllConfigsAsList().get(0).getDepends().entrySet()) {
            String key = entry.getKey();
            LOGGER.info("Cookbook: " + key + " is resolved now");
            CookbookParseResult dependentParseResult = new CookbookParseResult(cookbookParseResult);
            dependentParseResult.prepareForDependencie(key);
            if (dependentParseResult.getAllConfigsAsList().size() > 0) {
                compileCookbook(dependentParseResult, saveToToscaRepo);
            }
        }

        // Print information about cookbooks for debugging
        List<ChefCookbookConfiguration> cookbookConfigsList = cookbookParseResult.getAllConfigsAsList();
        for (int count = 0; count < cookbookConfigsList.size(); count++) {
            cookbookConfigsList.get(count).printConfiguration();
        }

        /**
         * Convert extacted configuations to TOSCA node types.
         * Extracted platforms are also converted to TOSCA node types.
         * Saves the extracted Node types to repository.
         */
        if (saveToToscaRepo) {
            new ParseResultToscaConverter().saveToscaNodeTypes(new ParseResultToscaConverter().convertCookbookConfigurationToToscaNode(cookbookParseResult));
        }
        
        LOGGER.info("Finished parsing");
        return cookbookParseResult;
    }

    /**
     * Method compiles ruby files of a cookbook and adds the extracted information to an existing parse result of a cookbook.
     * @param input The Ruby file to compile as an input stream.
     * @param cookbookConfigs existing parse result of cookbook.
     * @return Returns the parse result with new extracted information
     */
    public static CookbookParseResult compile(CharStream input, CookbookParseResult cookbookConfigs) {
        CookbookParseResult extractedCookbookConfigs;
        ChefDSLLexer chefDSLLexer = new ChefDSLLexer(input);
        CommonTokenStream commonTokenStream = new CommonTokenStream(chefDSLLexer);
        ChefDSLParser chefDSLParser = new ChefDSLParser(commonTokenStream);
        
        CookbookVisitor cookbookVisitor = new CookbookVisitor(cookbookConfigs);
        extractedCookbookConfigs = cookbookVisitor.visit(chefDSLParser.program());
        return extractedCookbookConfigs;
        
    }

    /**
     * Parse metadata.rb of a Chef cookbook and return all configurations.
     * @param cookbookConfigs Initialized parse result of a cookbook, containing the path to the coobook.
     * @return Returns parse result of cookbook with information found metadata.rb file.
     */
    private CookbookParseResult returnComponentTypesFromMetadata(CookbookParseResult cookbookConfigs) {
        String metadataRbPath = cookbookConfigs.getCookbookPath() + ChefDslConstants.METADATA_RB_PATH;
        String metadataJsonPath = cookbookConfigs.getCookbookPath() + ChefDslConstants.METADATA_JSON_PATH;
        LOGGER.info("Parsing metadata.rb ...");
        if (CrawledCookbooks.fileExists(metadataRbPath)) {
            return getParseResultFromFile(cookbookConfigs, metadataRbPath);
        } else if (CrawledCookbooks.fileExists(metadataJsonPath)) {
            return new MetadataJsonVisitor().visitMetadataJson(cookbookConfigs);
        } else {
            return null;
        }
    }

    /**
     * Parse metadata.rb of a Chef cookbook and return all configurations.
     * @param cookbookConfigs The parse result of a cookbook, containing information from metadata.rb and kitchen.yml.
     * @return Returns parse result of cookbook with information found in file.
     */
    private CookbookParseResult returnComponentTypesFromAttributes(CookbookParseResult cookbookConfigs) {

        File folder;
        File[] files;
        String attributePath;
        String defaultAttributesPath;
        
        //  Compile Attributes from cookbook which is analyzed
        if (cookbookConfigs.isInRecursiveTransformation() == false) {
            attributePath = cookbookConfigs.getCookbookPath() + ChefDslConstants.ATTRIBUTES_PATH;
            defaultAttributesPath = attributePath + ChefDslConstants.DEFAULT_RB_PATH;
            cookbookConfigs = getParseResultFromFile(cookbookConfigs, defaultAttributesPath);

            folder = new File(attributePath);
            files = folder.listFiles();

            if (files != null) {
                for (File attributeFile : files) {
                    if (!attributeFile.getName().equals(ChefDslConstants.DEFAULT_RUBYFILE)) {
                        cookbookConfigs = getParseResultFromFile(cookbookConfigs, attributeFile.getAbsolutePath().replace("\\", "/"));
                    }
                }
            }
        }

        // Compile Attributes from dependent cookbooks
        for (Map.Entry<String, String> entry : cookbookConfigs.getAllConfigsAsList().get(0).getDepends().entrySet()) {
            String key = entry.getKey();

            attributePath = cookbookConfigs.getCookbookPath() + Defaults.DEPENDENCIE_FOLDER + "/" + key + ChefDslConstants.ATTRIBUTES_PATH;
            defaultAttributesPath = attributePath + ChefDslConstants.DEFAULT_RB_PATH;
            cookbookConfigs = getParseResultFromFile(cookbookConfigs, defaultAttributesPath);

            folder = new File(attributePath);
            files = folder.listFiles();

            if (files != null) {
                for (File attributeFile : files) {
                    if (!attributeFile.getName().equals(ChefDslConstants.DEFAULT_RUBYFILE)) {
                        cookbookConfigs = getParseResultFromFile(cookbookConfigs, attributeFile.getAbsolutePath().replace("\\", "/"));
                    }
                }
            }
        }
        return cookbookConfigs;
    }

    /**
     * Parse recipes of a Chef cookbook.
     * @param cookbookConfigs Parse result of a cookbook, containing information from metadata.rb 
     *                        and all attribute files from cookbooks and dependent cookbooks.
     * @return Returns parse result with added information about installed and required packages.
     */
    private CookbookParseResult returnComponentTypesFromRecipes(CookbookParseResult cookbookConfigs) {
        if (cookbookConfigs.isInRecursiveTransformation() == false ) {
            String defaultRecipePath = cookbookConfigs.getCookbookPath() + ChefDslConstants.RECIPES_PATH + ChefDslConstants.DEFAULT_RB_PATH;
            LOGGER.info("Parsing Recipes: default.rb ...");
            return getParseResultFromFile(cookbookConfigs, defaultRecipePath);
        } else {
            List<CookbookParseResult> parseResultList = cookbookConfigs.getListOfConfigsInOwnParseresult();

            CookbookParseResult filteredParseResult;

            List<ChefCookbookConfiguration> processedCookbookConfigs = new LinkedList<>();

            // If parse result have multiple cookbook configurations, the AST is visited for each configuration.
            for (int countConfigs = 0; countConfigs < parseResultList.size(); countConfigs++) {
                filteredParseResult = parseResultList.get(countConfigs);
                List<String> runlist = filteredParseResult.getAllConfigsAsList().get(0).getRunlist();
                for ( int i = 0; i < runlist.size() ; i++) {
                    String RecipePath = cookbookConfigs.getCookbookPath() + ChefDslConstants.RECIPES_PATH + "/" + runlist.get(i);
                    LOGGER.info("Parsing Recipes: " + runlist.get(i) + "...");
                    filteredParseResult = getParseResultFromFile(filteredParseResult, RecipePath);
                }
                
                processedCookbookConfigs.add(filteredParseResult.getAllConfigsAsList().get(0));
                filteredParseResult.clearConfigurations();
            }
            cookbookConfigs.replaceCookbookConfigs(processedCookbookConfigs);
            
            return cookbookConfigs;
        }
        
    }

    /**
     * This method parses ruby files from chef cookbooks for deployment architecture information.
     * @param cookbookConfigs Present parsing result where new information are added.
     * @param rbFilePath Path to file that should be parsed.
     * @return New deployment architecture information from the parsed file.
     * @throws IOException if the file can not be read.
     */
    public static CookbookParseResult getParseResultFromFile(CookbookParseResult cookbookConfigs, String rbFilePath) {
        CookbookParseResult extractedCookbookConfigs;
        if (CrawledCookbooks.fileExists(rbFilePath)) {
            try {
                File file = RubyCodeHelper.prepareCodeFromFile(rbFilePath);
                CharStream input = fromFileName(file.getAbsolutePath().replace("\\", "/"));
                LOGGER.info("Parsing File : " + rbFilePath);
                extractedCookbookConfigs = compile(input, cookbookConfigs);
                file.delete();
            } catch (IOException e) {
                LOGGER.error("Can't read file! Filepath: " + rbFilePath);
                extractedCookbookConfigs = cookbookConfigs;
            }
        } else {
            //LOGGER.error("File does not exist! Filepath: " + rbFilePath);
            extractedCookbookConfigs = cookbookConfigs;
        }

        return extractedCookbookConfigs;
    }

    /**
     * Parse kitchen.yml and add Platform information to existing configurations
     * @param cookbookConfigs Parse result of a cookbook, containing information from metadata.rb.
     * @return Returns parse result with added information.
     */
    private CookbookParseResult addPlatformVersionInformationFromKitchen(CookbookParseResult cookbookConfigs) {
        
        List<ChefCookbookConfiguration> cookbookConfigurationList = cookbookConfigs.getAllConfigsAsList();
        List<String> platformNames;
        ChefKitchenYmlParser ymlParser = new ChefKitchenYmlParser(cookbookConfigs);
        String platformName;
        String existingPlatformVersion;
        String newPlatformVersion;
        String newPlatformName;
        int index;
        
        int offsetForMinus;

        boolean platformFound;
        
        platformNames = ymlParser.getPlatformNames();

        if (platformNames == null) return cookbookConfigs;

        for (int countPlatformNames = 0; countPlatformNames < platformNames.size(); countPlatformNames++) {
            platformFound = false;
            platformNames.set(countPlatformNames, KitchenUtilities.correctPlatformName(platformNames.get(countPlatformNames)));
            
            for (int countChefCookbookConfigurations = 0; countChefCookbookConfigurations < cookbookConfigurationList.size(); countChefCookbookConfigurations++) {
                // Name of platform in existing cookbook configurations
                platformName = cookbookConfigurationList.get(countChefCookbookConfigurations).getSupports().getName();
                // Platfrom version in existing cookbook configurations
                existingPlatformVersion = cookbookConfigurationList.get(countChefCookbookConfigurations).getSupports().getVersion();
                // Check if platformnames start with a platform name of existing 
                index = platformNames.get(countPlatformNames).indexOf(platformName);
                
                if (KitchenUtilities.skipPlatform(platformName, platformNames.get(countPlatformNames))) continue;
                                   
                switch (index) {
                    case 0:
                        
                        if (platformNames.get(countPlatformNames).contains("-")) offsetForMinus = 1;
                        else offsetForMinus = 0;
                        
                        newPlatformVersion = platformNames.get(countPlatformNames).substring(platformName.length() + offsetForMinus);
                        // Platform name is in an existing configuration and has no specified version
                        if (existingPlatformVersion.equals(ChefDslConstants.SUPPORTSALLPLATFORMVERSIONS) && platformName.length() != platformNames.get(countPlatformNames).length()) {
                            cookbookConfigurationList.get(countChefCookbookConfigurations).setSupports(new Platform(platformName, newPlatformVersion));
                        } else { //Platform name is in an existing configuration but version is already specified
                            cookbookConfigurationList.add(countChefCookbookConfigurations + 1 , new ChefCookbookConfiguration(cookbookConfigurationList.get(countChefCookbookConfigurations)));
                            cookbookConfigurationList.get(countChefCookbookConfigurations + 1).setSupports(new Platform(platformName, newPlatformVersion));
                        }
                        platformFound = true;
                        break;
                        
                    case -1:
                        if (!platformFound && (countChefCookbookConfigurations == cookbookConfigurationList.size() - 1)) {
                            index = platformNames.get(countPlatformNames).indexOf("-");
                            if (index == -1) {
                                newPlatformName = platformNames.get(countPlatformNames);
                                newPlatformVersion = "";
                            } else {
                                newPlatformName = platformNames.get(countPlatformNames).substring(0, index);
                                newPlatformVersion = platformNames.get(countPlatformNames).substring(index + 1);
                            }
                            
                            if (!cookbookConfigurationList.get(cookbookConfigurationList.size() - 1).getSupports().getName().equals(Defaults.COOKBOOKCONFIG_SUPPORTS_NO_PLATFORM)) {
                                cookbookConfigurationList.add(new ChefCookbookConfiguration(cookbookConfigurationList.get(countChefCookbookConfigurations)));
                            }
                            
                            cookbookConfigurationList.get(cookbookConfigurationList.size() - 1).setSupports(new Platform(newPlatformName, newPlatformVersion));
                            
                            platformFound = true;
                        }
                        break;
                        
                    default:
                        break;
                }
                
                if (platformFound) break;
            }
        }
        cookbookConfigs.clearConfigurations();
        cookbookConfigs.putCookbookConfigsAsList(cookbookConfigurationList);

        return cookbookConfigs;
    }
}
