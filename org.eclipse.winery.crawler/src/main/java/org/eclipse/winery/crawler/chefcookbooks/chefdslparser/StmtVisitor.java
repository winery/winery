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

import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.ChefCookbookConfiguration;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.CookbookParseResult;

public class StmtVisitor extends ChefDSLBaseVisitor<CookbookParseResult> {

    private CookbookParseResult cookbookConfigs;

    public StmtVisitor(CookbookParseResult cookbookConfigurations) {
        this.cookbookConfigs = cookbookConfigurations;
    }

    @Override
    public CookbookParseResult visitStmtExpr(ChefDSLParser.StmtExprContext ctx) {
        ExprVisitor exprVisitor = new ExprVisitor(cookbookConfigs);
        cookbookConfigs = ctx.expr().accept(exprVisitor);

        return cookbookConfigs;
    }

    @Override
    public CookbookParseResult visitStmtDo(ChefDSLParser.StmtDoContext ctx) {

        List<String> values;

        if (ctx.getChildCount() > 7) {
            CollectionVisitor collectionVisitor = new CollectionVisitor(cookbookConfigs);
            values = ctx.call().function().primary().accept(collectionVisitor);
            String identifier = ctx.block_var().getText();
            CompstmtVisitor compstmtVisitor = new CompstmtVisitor(cookbookConfigs);
            for (int count = 0; count < values.size(); count++) {
                CookbookVisitor.variables.put(identifier, values.get(count));
                cookbookConfigs = ctx.inner_comptstmt().accept(compstmtVisitor);
                CookbookVisitor.variables.remove(identifier);
            }
        }

        return cookbookConfigs;
    }

    /**
     * This method is called when Chef Resources are found in a cookbook.
     */
    @Override
    public CookbookParseResult visitStmtDo2(ChefDSLParser.StmtDo2Context ctx) {

        if (ctx.call() != null && ctx.call().getText().contains("package")) {

            List<CookbookParseResult> parseResultList = cookbookConfigs.getListOfConfigsInOwnParseresult();

            CookbookParseResult filteredParseResult;

            List<ChefCookbookConfiguration> processedCookbookConfigs = new LinkedList<>();

            for (int countConfigs = 0; countConfigs < parseResultList.size(); countConfigs++) {
                filteredParseResult = parseResultList.get(countConfigs);
                CommandVisitor commandVisitor = new CommandVisitor(filteredParseResult);
                filteredParseResult = ctx.call().accept(commandVisitor);
                if (ctx.stmt() != null) {
                    for (ChefDSLParser.StmtContext stmtContext : ctx.stmt()) {
                        filteredParseResult = stmtContext.accept(new PackageStatementVisitor(filteredParseResult));
                    }
                }
                for (int i = 0; i < filteredParseResult.getNotatedPackages().size(); i++) {
                    if (cookbookConfigs.isInDependentRecipe()) {
                        filteredParseResult.getAllConfigsAsList().get(0).addRequiredPackage(filteredParseResult.getNotatedPackages().get(i));
                    } else {
                        filteredParseResult.getAllConfigsAsList().get(0).addInstalledPackage(filteredParseResult.getNotatedPackages().get(i));
                    }
                }
                filteredParseResult.clearNotatedPackage();
                processedCookbookConfigs.add(filteredParseResult.getAllConfigsAsList().get(0));
                filteredParseResult.clearConfigurations();
            }
            cookbookConfigs.replaceCookbookConfigs(processedCookbookConfigs);
        }

        return cookbookConfigs;
    }

    @Override
    public CookbookParseResult visitStmt10(ChefDSLParser.Stmt10Context ctx) {
        return cookbookConfigs;
    }

    @Override
    public CookbookParseResult aggregateResult(CookbookParseResult aggregate, CookbookParseResult nextResult) {
        return cookbookConfigs;
    }
}
