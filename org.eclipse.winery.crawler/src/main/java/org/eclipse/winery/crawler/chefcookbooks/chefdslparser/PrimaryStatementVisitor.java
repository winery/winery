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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.ChefCookbookConfiguration;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.CookbookParseResult;

import org.antlr.v4.runtime.tree.ParseTree;

/**
 * This class contains of visitor methods to resolve statements. Supports Case Statement. Supports If Statement.
 */
public class PrimaryStatementVisitor extends ChefDSLBaseVisitor<CookbookParseResult> {

    private static final Logger LOGGER = Logger.getLogger(PrimaryStatementVisitor.class.getName());

    private CookbookParseResult extractedCookbookConfigs;

    public PrimaryStatementVisitor(CookbookParseResult cookbookConfigurations) {
        this.extractedCookbookConfigs = cookbookConfigurations;
    }

    @Override
    public CookbookParseResult visitCaseStatement(ChefDSLParser.CaseStatementContext ctx) {

        WhenArgsVisitor whenArgsVisitor;

        CaseConditionVisitor caseConditionVisitor;

        List caseConditionList;

        String caseCondition;

        List<String> whenArgs = new ArrayList<>();

        List<CookbookParseResult> parseResultList = extractedCookbookConfigs.getListOfConfigsInOwnParseresult();

        CookbookParseResult filteredParseResult;

        List<ChefCookbookConfiguration> processedCookbookConfigs = new LinkedList<>();

        Boolean elseActive = false;

        for (int countConfigs = 0; countConfigs < parseResultList.size(); countConfigs++) {
            filteredParseResult = parseResultList.get(countConfigs);

            caseConditionVisitor = new CaseConditionVisitor(filteredParseResult);
            caseConditionList = ctx.inner_comptstmt(0).accept(caseConditionVisitor);

            if (caseConditionList.size() == 1) {
                caseCondition = (String) caseConditionList.get(0);
            } else {
                LOGGER.info("Case condition list has more than one argument. This case is not implemented yet! \n + " +
                    "Cookbook name: " + extractedCookbookConfigs.getCookbookName() + "\n" +
                    "Case Statement: " + ctx.getText());
                return extractedCookbookConfigs;
            }

            whenArgsVisitor = new WhenArgsVisitor(filteredParseResult);

            for (int iterChild = 2; iterChild < ctx.getChildCount(); iterChild++) { //counter starts at third index because first two childs can be ignored
                ParseTree child = ctx.getChild(iterChild);

                if (child instanceof ChefDSLParser.When_argsContext) {
                    whenArgs = child.accept(whenArgsVisitor);
                } else if ("else".equals(child.getText())) {
                    whenArgs.clear();
                    elseActive = true;
                } else if (child instanceof ChefDSLParser.Inner_comptstmtContext) {

                    CompstmtVisitor compstmtVisitor = new CompstmtVisitor(filteredParseResult);

                    if (whenArgs.contains(caseCondition) || elseActive) {
                        filteredParseResult = child.accept(compstmtVisitor);

                        // Check if next child is not another statement.
                        if (iterChild < ctx.getChildCount() - 1 && !(ctx.getChild(iterChild + 1) instanceof ChefDSLParser.Inner_comptstmtContext)) {
                            break;
                        }
                    }
                }
            }

            processedCookbookConfigs.add(filteredParseResult.getAllConfigsAsList().get(0));
            // Reset
            filteredParseResult.clearConfigurations();
            elseActive = false;
        }

        extractedCookbookConfigs.replaceCookbookConfigs(processedCookbookConfigs);

        return extractedCookbookConfigs;
    }

    @Override
    public CookbookParseResult visitIf_statement(ChefDSLParser.If_statementContext ctx) {
        Boolean expr = false;
        BooleanExprVisitor booleanExprVisitor;

        List<CookbookParseResult> parseResultList = extractedCookbookConfigs.getListOfConfigsInOwnParseresult();

        CookbookParseResult filteredParseResult;

        List<ChefCookbookConfiguration> processedCookbookConfigs = new LinkedList<>();

        Boolean elseActive = false;

        for (int countConfigs = 0; countConfigs < parseResultList.size(); countConfigs++) {
            filteredParseResult = parseResultList.get(countConfigs);

            booleanExprVisitor = new BooleanExprVisitor(filteredParseResult);

            for (int iterChild = 0; iterChild < ctx.getChildCount(); iterChild++) {

                ParseTree child = ctx.getChild(iterChild);
                if (child instanceof ChefDSLParser.ExprContext) {
                    expr = ctx.expr(0).accept(booleanExprVisitor);
                } else if ("else".equals(child.getText())) {
                    elseActive = true;
                } else if (child instanceof ChefDSLParser.Inner_comptstmtContext) {
                    if (expr == true || elseActive) {
                        CompstmtVisitor compstmtVisitor = new CompstmtVisitor(filteredParseResult);
                        filteredParseResult = child.accept(compstmtVisitor);

                        // Check if next child is not another statement.
                        if (iterChild < ctx.getChildCount() - 1 && !(ctx.getChild(iterChild + 1) instanceof ChefDSLParser.Inner_comptstmtContext)) {
                            break;
                        }
                    }
                }
            }

            processedCookbookConfigs.add(filteredParseResult.getAllConfigsAsList().get(0));
            filteredParseResult.clearConfigurations();
            elseActive = false;
        }
        extractedCookbookConfigs.replaceCookbookConfigs(processedCookbookConfigs);

        return extractedCookbookConfigs;
    }

    @Override
    public CookbookParseResult visitUnlessStatement(ChefDSLParser.UnlessStatementContext ctx) {
        return extractedCookbookConfigs;
    }

    @Override
    public CookbookParseResult aggregateResult(CookbookParseResult aggregate, CookbookParseResult nextResult) {
        return extractedCookbookConfigs;
    }
}
