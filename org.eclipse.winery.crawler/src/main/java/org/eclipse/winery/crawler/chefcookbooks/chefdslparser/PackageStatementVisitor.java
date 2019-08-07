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

import java.util.List;

import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.CookbookParseResult;
import org.eclipse.winery.crawler.chefcookbooks.constants.ChefDslConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides methods, to handle stmts in the Chef package resource and all the derived package resource
 * versions. Examples: windows_package, package, homebrew_package..
 */
public class PackageStatementVisitor extends StmtVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandVisitor.class.getName());

    private CookbookParseResult extractedCookbookConfigs;

    public PackageStatementVisitor(CookbookParseResult cookbookConfigurations) {
        super(cookbookConfigurations);
        this.extractedCookbookConfigs = cookbookConfigurations;
    }

    @Override
    public CookbookParseResult visitStmtExpr(ChefDSLParser.StmtExprContext ctx) {
        PackageStatementVisitor exprVisitor = new PackageStatementVisitor(extractedCookbookConfigs);
        extractedCookbookConfigs = ctx.expr().accept(exprVisitor);

        return extractedCookbookConfigs;
    }

    @Override
    public CookbookParseResult visitExprCommand(ChefDSLParser.ExprCommandContext ctx) {
        PackageStatementVisitor commandVisitor = new PackageStatementVisitor(extractedCookbookConfigs);
        extractedCookbookConfigs = ctx.command().accept(commandVisitor);
        return extractedCookbookConfigs;
    }

    @Override
    public CookbookParseResult visitOperationCallArgs(ChefDSLParser.OperationCallArgsContext ctx) {
        String operationIdentifier = ctx.operation().getText();
        List<String> callArgs;
        CallArgsVisitor callArgsVisitor;

        if (extractedCookbookConfigs != null && !extractedCookbookConfigs.getNotatedPackages().isEmpty()) {
            callArgsVisitor = new CallArgsVisitor(extractedCookbookConfigs);
            callArgs = ctx.call_args().accept(callArgsVisitor);

            if (operationIdentifier.equals(ChefDslConstants.PACKAGE_NAME_PROPERTY)) {
                if (callArgs.size() > 1) {
                    LOGGER.error("package_name has more than one argument. This should not happen! Line: " + ctx.getText());
                }
            }

            switch (operationIdentifier) {
                case ChefDslConstants.PACKAGE_VERSION_PROPERTY:

                    if (!callArgs.isEmpty()) {
                        for (int count = 0; count < extractedCookbookConfigs.getNotatedPackages().size(); count++) {
                            extractedCookbookConfigs.getNotatedPackages().get(count).addProperty(operationIdentifier, callArgs.get(count));
                        }
                    }
                    break;

                case ChefDslConstants.PACKAGE_ACTION_PROPERTY:
                    for (int count = 0; count < extractedCookbookConfigs.getNotatedPackages().size(); count++) {
                        extractedCookbookConfigs.getNotatedPackages().get(count).addProperty(operationIdentifier, callArgs.get(0));
                    }
                case ChefDslConstants.PACKAGE_NAME_PROPERTY:
                    extractedCookbookConfigs.getNotatedPackages().get(0).addProperty(operationIdentifier, callArgs.get(0));
                default:
                    break;
            }
        }
        return extractedCookbookConfigs;
    }

    @Override
    public CookbookParseResult aggregateResult(CookbookParseResult aggregate, CookbookParseResult nextResult) {
        return extractedCookbookConfigs;
    }
}
