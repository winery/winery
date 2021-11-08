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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.winery.crawler.chefcookbooks.ChefCookbookAnalyzer;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.ChefAttribute;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.Platform;
import org.eclipse.winery.crawler.chefcookbooks.constants.ChefDslConstants;
import org.eclipse.winery.crawler.chefcookbooks.constants.Defaults;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.ChefPackage;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.CookbookParseResult;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.ChefCookbookConfiguration;
import org.eclipse.winery.crawler.chefcookbooks.helper.ChefDslHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandVisitor extends ChefDSLBaseVisitor<CookbookParseResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandVisitor.class.getName());

    private CookbookParseResult extractedCookbookConfigs;

    public CommandVisitor(CookbookParseResult cookbookConfigurations) {
        this.extractedCookbookConfigs = cookbookConfigurations;
    }

    @Override
    public CookbookParseResult visitOperationCallArgs(ChefDSLParser.OperationCallArgsContext ctx) {
        String operationIdentifier = ctx.operation().getText();
        if (operationIdentifier.endsWith("package")) operationIdentifier = ChefDslConstants.PACKAGE;

        switch (operationIdentifier) {
            case ChefDslConstants.COOKBOOK_NAME:
                processCookbookName(ctx);
                break;

            case ChefDslConstants.COOKBOOK_VERSION:
                processCookbookVersion(ctx);
                break;

            case ChefDslConstants.COOKBOOK_DESCRIPTION:
                processCookbookDescription(ctx);

                break;

            case ChefDslConstants.COOKBOOK_SUPPORTS:
                if (extractedCookbookConfigs.isInRecursiveTransformation() == false) {
                    processSupportsCommand(ctx);
                }
                break;

            case ChefDslConstants.COOKBOOK_DEPENDS:
                processDependsCommand(ctx);
                break;

            case "default":
                processDefaultAttribute(ctx);
                break;
            case ChefDslConstants.PACKAGE:
                processPackageCommand(ctx);
                break;
            case ChefDslConstants.INCLUDE_RECIPE:
                processIncludeRecipeCommand(ctx);
                break;

            default:
                if (extractedCookbookConfigs != null && !extractedCookbookConfigs.getNotatedPackages().isEmpty()) {
                    for (int count = 0; count < extractedCookbookConfigs.getNotatedPackages().size(); count++) {
                        extractedCookbookConfigs.getNotatedPackages().get(count).addProperty(ctx.operation().getText(), ctx.call_args().getText());
                    }
                }
                break;
        }
        return extractedCookbookConfigs;
    }

    @Override
    public CookbookParseResult visitOperationPrimary(ChefDSLParser.OperationPrimaryContext ctx) {
        String primaryIdentifier = ctx.primary().getText();
        String operationIdentifier = ctx.operation().getText();

        switch (primaryIdentifier) {
            case "node":
                switch (operationIdentifier) {
                    case "default":
                        processDefaultAttribute(ctx);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        return extractedCookbookConfigs;
    }

    private void processPackageCommand(ChefDSLParser.OperationCallArgsContext ctx) {
        Class parentClass;
        parentClass = ctx.getParent().getClass();
        List<String> installedPackages;
        CallArgsVisitor callArgsVisitor;

        List<CookbookParseResult> parseResultList = extractedCookbookConfigs.getListOfConfigsInOwnParseresult();

        CookbookParseResult filteredParseResult;

        List<ChefCookbookConfiguration> processedCookbookConfigs = new LinkedList<>();

        for (int countConfigs = 0; countConfigs < parseResultList.size(); countConfigs++) {
            //Parse result with a single configuration
            filteredParseResult = parseResultList.get(countConfigs);
            callArgsVisitor = new CallArgsVisitor(filteredParseResult);
            installedPackages = ctx.call_args().accept(callArgsVisitor);

            for (int i = 0; i < installedPackages.size(); i++) {

                if (parentClass == ChefDSLParser.ExprCommandContext.class) {
                    if (extractedCookbookConfigs.isInDependentRecipe()) {
                        filteredParseResult.getAllConfigsAsList().get(0).addRequiredPackage(new ChefPackage(installedPackages.get(i)));
                    } else {
                        filteredParseResult.getAllConfigsAsList().get(0).addInstalledPackage(new ChefPackage(installedPackages.get(i)));
                    }
                } else if (parentClass == ChefDSLParser.CallContext.class) {
                    extractedCookbookConfigs.addNotatedPackage(new ChefPackage(installedPackages.get(i)));
                }
            }

            processedCookbookConfigs.add(filteredParseResult.getAllConfigsAsList().get(0));
            filteredParseResult.clearConfigurations();
        }
        extractedCookbookConfigs.replaceCookbookConfigs(processedCookbookConfigs);
    }

    /**
     * Process command "depends" in metadata.rb file of a cookbook. This describes dependencies to other cookbooks.
     *
     * @param ctx Rule context of the command.
     * @see <a href="https://docs.chef.io/config_rb_metadata.html">/a>
     */
    private void processDependsCommand(ChefDSLParser.OperationCallArgsContext ctx) {
        CallArgsVisitor callArgsVisitor = new CallArgsVisitor(extractedCookbookConfigs);
        List callArgs = ctx.call_args().accept(callArgsVisitor);

        List<ChefCookbookConfiguration> cookbookConfigs;
        String depends = callArgs.get(0).toString();
        String dependsVersion;
        cookbookConfigs = extractedCookbookConfigs.getAllConfigsAsList();
        if (callArgs.size() > 1) {
            dependsVersion = callArgs.get(1).toString();
        } else {
            dependsVersion = ChefDslConstants.SUPPORTSALLCOOKBOOKVERSIONS;
        }
        for (int count = 0; count < cookbookConfigs.size(); count++) {
            cookbookConfigs.get(count).addDepends(depends, dependsVersion);
        }
        extractedCookbookConfigs.putCookbookConfigsAsList(cookbookConfigs);
    }

    /**
     * Process command "supports" in metadata.rb file of a cookbook. This describes which platforms are supported by the
     * cookbook.
     *
     * @param ctx Rule context of the command.
     * @see <a href="https://docs.chef.io/config_rb_metadata.html">/a>
     */
    private void processSupportsCommand(ChefDSLParser.OperationCallArgsContext ctx) {
        CallArgsVisitor callArgsVisitor = new CallArgsVisitor(extractedCookbookConfigs);
        List callArgs = ctx.call_args().accept(callArgsVisitor);

        List<ChefCookbookConfiguration> cookbookConfigs;
        String supports = callArgs.get(0).toString();
        String version;
        cookbookConfigs = extractedCookbookConfigs.getAllConfigsAsList();

        if (callArgs.size() > 1) {
            version = callArgs.get(1).toString();
        } else {
            version = ChefDslConstants.SUPPORTSALLPLATFORMVERSIONS;
        }

        if (cookbookConfigs.get(0).getSupports().getName().equals(Defaults.COOKBOOKCONFIG_SUPPORTS_NO_PLATFORM)) {
            cookbookConfigs.get(0).setSupports(new Platform(supports, version));
            extractedCookbookConfigs.clearConfigurations();
        } else {
            ChefCookbookConfiguration newConfig = new ChefCookbookConfiguration(cookbookConfigs.get(0));
            newConfig.setSupports(new Platform(supports, version));
            cookbookConfigs.add(newConfig);
            extractedCookbookConfigs.clearConfigurations();
        }
        extractedCookbookConfigs.putCookbookConfigsAsList(cookbookConfigs);
    }

    /**
     * Process command "description" in metadata.rb file of a cookbook. Adds the description to all cookbook
     * configurations.
     *
     * @param ctx Rule context of the command.
     * @see <a href="https://docs.chef.io/config_rb_metadata.html">/a>
     */
    private void processCookbookDescription(ChefDSLParser.OperationCallArgsContext ctx) {
        CallArgsVisitor callArgsVisitor = new CallArgsVisitor(extractedCookbookConfigs);
        List callArgs = ctx.call_args().accept(callArgsVisitor);

        List<ChefCookbookConfiguration> cookbookConfigs;
        String description = callArgs.get(0).toString();
        cookbookConfigs = extractedCookbookConfigs.getAllConfigsAsList();
        for (int count = 0; count < cookbookConfigs.size(); count++) {
            cookbookConfigs.get(count).setDescription(description);
        }
        extractedCookbookConfigs.putCookbookConfigsAsList(cookbookConfigs);
    }

    /**
     * Process command "name" in metadata.rb file of a cookbook. Adds a the name of the cookbook to all configurations.
     *
     * @param ctx Rule context of the command.
     * @see <a href="https://docs.chef.io/config_rb_metadata.html">/a>
     */
    private void processCookbookName(ChefDSLParser.OperationCallArgsContext ctx) {
        CallArgsVisitor callArgsVisitor = new CallArgsVisitor(extractedCookbookConfigs);
        List callArgs = ctx.call_args().accept(callArgsVisitor);

        String name = callArgs.get(0).toString();
        if (extractedCookbookConfigs.getAllConfigsAsList().size() == 0) {
            ChefCookbookConfiguration componentType = new ChefCookbookConfiguration();
            componentType.setName(name);
            extractedCookbookConfigs.putCookbookConfig(componentType);
        } else {
            List<ChefCookbookConfiguration> cookbookConfigs;
            cookbookConfigs = extractedCookbookConfigs.getAllConfigsAsList();
            for (int count = 0; count < cookbookConfigs.size(); count++) {
                cookbookConfigs.get(count).setVersion(name);
            }
            extractedCookbookConfigs.replaceCookbookConfigs(cookbookConfigs);
        }
    }

    /**
     * Process command "version" in metadata.rb file of a cookbook. Adds a the version of the cookbook to all
     * configurations.
     *
     * @param ctx Rule context of the command.
     * @see <a href="https://docs.chef.io/config_rb_metadata.html">/a>
     */
    private void processCookbookVersion(ChefDSLParser.OperationCallArgsContext ctx) {
        CallArgsVisitor callArgsVisitor = new CallArgsVisitor(extractedCookbookConfigs);
        List callArgs = ctx.call_args().accept(callArgsVisitor);

        List<ChefCookbookConfiguration> cookbookConfigs;
        String version = callArgs.get(0).toString();
        cookbookConfigs = extractedCookbookConfigs.getAllConfigsAsList();
        for (int count = 0; count < cookbookConfigs.size(); count++) {
            cookbookConfigs.get(count).setVersion(version);
        }
        extractedCookbookConfigs.putCookbookConfigsAsList(cookbookConfigs);
    }

    /**
     * Processes a default chef attribute. Wraps each cookbook configuration in a parse result and processes it.
     *
     * @param ctx Rule context of the command.
     */
    private void processDefaultAttribute(ChefDSLParser.OperationCallArgsContext ctx) {
        List<CookbookParseResult> parseResultList = extractedCookbookConfigs.getListOfConfigsInOwnParseresult();

        CookbookParseResult filteredParseResult;

        List<ChefCookbookConfiguration> processedCookbookConfigs = new LinkedList<>();

        for (int countConfigs = 0; countConfigs < parseResultList.size(); countConfigs++) {
            // Process one cookbook configuration per iteration wrapped in a cookbook parse result. 
            filteredParseResult = parseResultList.get(countConfigs);

            AssignAttributeVisitor assignAttributeVisitor = new AssignAttributeVisitor(filteredParseResult);
            ChefAttribute chefAttribute = ctx.call_args().accept(assignAttributeVisitor);

            if (chefAttribute != null && chefAttribute.getName() != null && chefAttribute.getValues() != null) {
                filteredParseResult.addAttributeToAllConfigs(chefAttribute.getName(), chefAttribute.getValues());
            }

            processedCookbookConfigs.add(filteredParseResult.getAllConfigsAsList().get(0));
        }

        extractedCookbookConfigs.replaceCookbookConfigs(processedCookbookConfigs);
    }

    /**
     * Processes a default chef attribute. Wraps each cookbook configuration in a parse result and processes it.
     *
     * @param ctx Rule context of the command.
     */
    private void processDefaultAttribute(ChefDSLParser.OperationPrimaryContext ctx) {
        List<CookbookParseResult> parseResultList = extractedCookbookConfigs.getListOfConfigsInOwnParseresult();

        CookbookParseResult filteredParseResult;

        List<ChefCookbookConfiguration> processedCookbookConfigs = new LinkedList<>();

        for (int countConfigs = 0; countConfigs < parseResultList.size(); countConfigs++) {
            // Process one cookbook configuration per iteration wrapped in a cookbook parse result. 
            filteredParseResult = parseResultList.get(countConfigs);

            AssignAttributeVisitor assignAttributeVisitor = new AssignAttributeVisitor(filteredParseResult);
            ChefAttribute chefAttribute = ctx.call_args().accept(assignAttributeVisitor);

            if (chefAttribute != null && chefAttribute.getName() != null && chefAttribute.getValues() != null) {
                filteredParseResult.addAttributeToAllConfigs(chefAttribute.getName(), chefAttribute.getValues());
            }

            processedCookbookConfigs.add(filteredParseResult.getAllConfigsAsList().get(0));
        }

        extractedCookbookConfigs.replaceCookbookConfigs(processedCookbookConfigs);
    }

    /**
     * Processes "include_recipe" command. When command is called in a cookbook the cookbook in the argument of the
     * command is executed.
     */
    private void processIncludeRecipeCommand(ChefDSLParser.OperationCallArgsContext ctx) {
        String recipePath;
        boolean isDependentRecipe;

        String[] resolvedArgument;
        CallArgsVisitor callArgsVisitor = new CallArgsVisitor(extractedCookbookConfigs);
        List<String> callArgs = ctx.call_args().accept(callArgsVisitor);

        if (callArgs != null && callArgs.size() == 1) {
            if (ChefDslHelper.hasChefAttributeInString(ctx.call_args().getText())) {
                // resolve
            }

            isDependentRecipe = extractedCookbookConfigs.isInDependentRecipe();

            resolvedArgument = ChefDslHelper.resolveRecipeCall(callArgs.get(0));
            if (resolvedArgument[0].equals(extractedCookbookConfigs.getCookbookName())) {
                recipePath = extractedCookbookConfigs.getCookbookPath() + "/recipes/" + resolvedArgument[1];
            } else if (extractedCookbookConfigs.getAllConfigsAsList().get(0).getDepends().containsKey(resolvedArgument[0])) {
                if (isDependentRecipe == false) {
                    extractedCookbookConfigs.getAllConfigsAsList().get(0).addDependentRecipes(resolvedArgument[0], resolvedArgument[1]);
                }
                recipePath = extractedCookbookConfigs.getCookbookPath() + "/dependencies/" + resolvedArgument[0] + "/recipes/" + resolvedArgument[1];
                extractedCookbookConfigs.setInDependentRecipe(true);
            } else {
                recipePath = "";
            }

            CookbookParseResult newParseResult = ChefCookbookAnalyzer.getParseResultFromFile(extractedCookbookConfigs, recipePath);
            extractedCookbookConfigs = newParseResult;
            extractedCookbookConfigs.setInDependentRecipe(isDependentRecipe);
        } else {
            LOGGER.info("Include_recipe call is null or an array. This is not implemented. \n" +
                "Statement is: " + ctx.getText());
        }
    }

    @Override
    public CookbookParseResult aggregateResult(CookbookParseResult aggregate, CookbookParseResult nextResult) {
        return extractedCookbookConfigs;
    }
}
