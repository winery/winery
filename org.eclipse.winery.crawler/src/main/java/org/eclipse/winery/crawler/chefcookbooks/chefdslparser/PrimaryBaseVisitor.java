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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.CookbookParseResult;
import org.eclipse.winery.crawler.chefcookbooks.helper.ChefDslHelper;
import org.eclipse.winery.crawler.chefcookbooks.helper.RubyFunctionHelper;

import org.antlr.v4.runtime.tree.ParseTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrimaryBaseVisitor extends CollectionVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrimaryBaseVisitor.class.getName());

    private CookbookParseResult extractedCookbookConfigs;

    public PrimaryBaseVisitor(CookbookParseResult cookbookConfigurations) {
        super(cookbookConfigurations);
        this.extractedCookbookConfigs = cookbookConfigurations;
    }

    @Override
    public List visitArgPrimary(ChefDSLParser.ArgPrimaryContext ctx) {
        List attributeValue = null;
        PrimaryBaseVisitor argPrimaryVisitor = new PrimaryBaseVisitor(extractedCookbookConfigs);
        attributeValue = ctx.primary().accept(argPrimaryVisitor);

        if (attributeValue == null) {
            attributeValue = new ArrayList();
        }

        return attributeValue;
    }

    @Override
    public List visitVarname(ChefDSLParser.VarnameContext ctx) {
        List attributeValue = new ArrayList();
        String varName = ctx.getText();
        String varValue;
        try {
            varValue = CookbookVisitor.variables.get(varName);
        } catch (Exception e) {
            varValue = varName;
            e.printStackTrace();
        }
        attributeValue.add(varValue);

        return attributeValue;
    }

    @Override
    public List visitString(ChefDSLParser.StringContext ctx) {
        List<String> attributeValue = new ArrayList();
        String literal = ctx.getChild(0).getText();
        Integer stringLength = literal.length();
        literal = literal.substring(1, stringLength - 1);
        if (ChefDslHelper.hasChefAttributeInString(literal)) {
            literal = ChefDslHelper.resolveRubyStringWithCode(extractedCookbookConfigs, literal);
        }
        attributeValue.add(literal);
        return attributeValue;
    }

    @Override
    public List visitLitSymbol(ChefDSLParser.LitSymbolContext ctx) {
        List<String> attributeValue = new ArrayList();
        String literal = ctx.getChild(0).getText();
        attributeValue.add(literal);
        return attributeValue;
    }

    @Override
    public List visitCaseStatement(ChefDSLParser.CaseStatementContext ctx) {
        List<String> attributeValue = new ArrayList();

        WhenArgsVisitor whenArgsVisitor;

        CaseConditionVisitor caseConditionVisitor;

        List caseConditionList;
        String caseCondition;

        List<String> whenArgs = new ArrayList<>();

        Boolean elseActive = false;

        caseConditionVisitor = new CaseConditionVisitor(extractedCookbookConfigs);
        caseConditionList = ctx.inner_comptstmt(0).accept(caseConditionVisitor);

        if (caseConditionList != null && caseConditionList.size() == 1) {

            caseCondition = (String) caseConditionList.get(0);
        } else {
            LOGGER.info("This should not happen! Case condition has more than one argument.");
            return attributeValue;
        }

        whenArgsVisitor = new WhenArgsVisitor(extractedCookbookConfigs);

        for (int iterChild = 2; iterChild < ctx.getChildCount(); iterChild++) { //counter starts at third index because first two childs can be ignored
            ParseTree child = ctx.getChild(iterChild);

            if (child instanceof ChefDSLParser.When_argsContext) {
                whenArgs = child.accept(whenArgsVisitor);
            } else if ("else".equals(child.getText())) {
                whenArgs.clear();
                elseActive = true;
            } else if (child instanceof ChefDSLParser.Inner_comptstmtContext) {
                PrimaryBaseVisitor primaryBaseVisitor = new PrimaryBaseVisitor(extractedCookbookConfigs);

                if (whenArgs.contains(caseCondition) || elseActive) {
                    attributeValue = child.accept(primaryBaseVisitor);

                    if (iterChild < ctx.getChildCount() - 1 && !(ctx.getChild(iterChild + 1) instanceof ChefDSLParser.Inner_comptstmtContext)) {
                        break;
                    }
                }
            }
        }

        return attributeValue;
    }

    @Override
    public List visitPrimInt(ChefDSLParser.PrimIntContext ctx) {
        List<String> attributeValue = new ArrayList();
        String value = ctx.getText();
        attributeValue.add(value);
        return attributeValue;
    }

    @Override
    public List visitPrimFloat(ChefDSLParser.PrimFloatContext ctx) {
        List<String> attributeValue = new ArrayList();
        String value = ctx.getText();
        attributeValue.add(value);
        return attributeValue;
    }

    @Override
    public List visitPrimBoolean(ChefDSLParser.PrimBooleanContext ctx) {
        List<String> attributeValue = new ArrayList();
        String value = ctx.getText();
        attributeValue.add(value);
        return attributeValue;
    }

    @Override
    public List visitPrim11(ChefDSLParser.Prim11Context ctx) {
        if (ctx.getText().startsWith("node")) {
            return extractedCookbookConfigs.getAllConfigsAsList().get(0).getAttribute(ctx.getText().substring(4));
        } else
            return Collections.singletonList(ctx.getText());
    }

    @Override
    public List visitPrimOhaiFunc(ChefDSLParser.PrimOhaiFuncContext ctx) {
        List<String> attributeValue = new ArrayList();
        String literal;
        int stringLength;
        HashSet<String> arguments = new HashSet<>();
        String ohaiFunction = ctx.ohaiArg().getText();

        if (ctx.literal() != null) {
            for (ChefDSLParser.LiteralContext literalContext : ctx.literal()) {
                literal = literalContext.getText();
                stringLength = literal.length();
                literal = literal.substring(1, stringLength - 1);
                arguments.add(literal);
            }
        }

        if (extractedCookbookConfigs.getNumOfCookbookConfigs() == 1) {
            switch (ohaiFunction) {
                case "platform_family?":
                    attributeValue.add(Boolean.toString(extractedCookbookConfigs.getAllConfigsAsList().get(0).hasPlatformFamily(arguments)));
                    break;

                case "platform?":
                    attributeValue.add(Boolean.toString(extractedCookbookConfigs.getAllConfigsAsList().get(0).hasPlatform(arguments)));
                    break;

                default:
                    LOGGER.info("Ohai Function \"" + ohaiFunction + "\" is not implemented.");
                    attributeValue.add(null);
                    break;
            }
        } else {
            LOGGER.error("Parse result has " + extractedCookbookConfigs.getNumOfCookbookConfigs() + " cookbook configurations" +
                "\n At this point it should have only one cookbook configuration.");
        }
        return attributeValue;
    }

    @Override
    public List visitPrimCompstmtInBrackets(ChefDSLParser.PrimCompstmtInBracketsContext ctx) {
        List attributeValue;
        PrimaryBaseVisitor primaryBaseVisitor = new PrimaryBaseVisitor(extractedCookbookConfigs);
        attributeValue = ctx.inner_comptstmt().accept(primaryBaseVisitor);
        return attributeValue;
    }

    @Override
    public List visitPrimFuncCall(ChefDSLParser.PrimFuncCallContext ctx) {
        List primaryValue;
        List convertedValueList = new ArrayList();
        String functionName;
        PrimaryBaseVisitor booleanExprVisitor = new PrimaryBaseVisitor(extractedCookbookConfigs);
        primaryValue = ctx.primary().accept(booleanExprVisitor);

        if (primaryValue != null && ctx.function().getChildCount() == 1) {
            functionName = ctx.function().getText();
            switch (functionName) {
                case "to_i":
                    Integer convertedValue;
                    for (int i = 0; i < primaryValue.size(); i++) {
                        convertedValue = RubyFunctionHelper.stringToInt((String) primaryValue.get(i));
                        if (convertedValue != null) {
                            convertedValueList.add(convertedValue.toString());
                        }
                    }
                    break;

                default:
                    break;
            }
        } else {
            convertedValueList = null;
        }

        return convertedValueList;
    }

    /**
     * This visit method should only be called in the evaluation of the ternary operator.
     */
    @Override
    public List visitArgAssign(ChefDSLParser.ArgAssignContext ctx) {
        List exprResult;
        PrimaryBaseVisitor primaryBaseVisitor = new PrimaryBaseVisitor(extractedCookbookConfigs);
        exprResult = ctx.arg().accept(primaryBaseVisitor);
        return exprResult;
    }

    @Override
    public List aggregateResult(List aggregate, List nextResult) {
        if (aggregate == null) {
            return nextResult;
        }

        if (nextResult == null) {
            return aggregate;
        }

        aggregate.add(nextResult);

        return aggregate;
    }
}
