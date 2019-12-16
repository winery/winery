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
import java.util.List;

import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.CookbookParseResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BooleanExprVisitor extends ChefDSLBaseVisitor<Boolean> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrimaryBaseVisitor.class.getName());

    private CookbookParseResult parseResult;

    public BooleanExprVisitor(CookbookParseResult existingParseResult) {
        this.parseResult = existingParseResult;
    }

    @Override
    public Boolean visitExprAnd(ChefDSLParser.ExprAndContext ctx) {
        Boolean firstExpr = null;
        Boolean secondExpr = null;

        BooleanExprVisitor booleanExprVisitor = new BooleanExprVisitor(parseResult);
        firstExpr = ctx.expr(0).accept(booleanExprVisitor);
        secondExpr = ctx.expr(1).accept(booleanExprVisitor);

        firstExpr = resolveNullArgument(firstExpr);
        secondExpr = resolveNullArgument(secondExpr);

        return (firstExpr && secondExpr);
    }

    @Override
    public Boolean visitExprOr(ChefDSLParser.ExprOrContext ctx) {
        Boolean firstExpr = null;
        Boolean secondExpr = null;

        BooleanExprVisitor booleanExprVisitor = new BooleanExprVisitor(parseResult);
        firstExpr = ctx.expr(0).accept(booleanExprVisitor);
        secondExpr = ctx.expr(1).accept(booleanExprVisitor);

        firstExpr = resolveNullArgument(firstExpr);
        secondExpr = resolveNullArgument(secondExpr);

        return (firstExpr || secondExpr);
    }

    @Override
    public Boolean visitArgAnd(ChefDSLParser.ArgAndContext ctx) {
        Boolean firstExpr = null;
        Boolean secondExpr = null;

        BooleanExprVisitor booleanExprVisitor = new BooleanExprVisitor(parseResult);
        firstExpr = ctx.arg(0).accept(booleanExprVisitor);
        secondExpr = ctx.arg(1).accept(booleanExprVisitor);

        firstExpr = resolveNullArgument(firstExpr);
        secondExpr = resolveNullArgument(secondExpr);

        return (firstExpr && secondExpr);
    }

    @Override
    public Boolean visitArgOr(ChefDSLParser.ArgOrContext ctx) {
        Boolean firstExpr = null;
        Boolean secondExpr = null;

        BooleanExprVisitor booleanExprVisitor = new BooleanExprVisitor(parseResult);
        firstExpr = ctx.arg(0).accept(booleanExprVisitor);
        secondExpr = ctx.arg(1).accept(booleanExprVisitor);

        firstExpr = resolveNullArgument(firstExpr);
        secondExpr = resolveNullArgument(secondExpr);

        return (firstExpr || secondExpr);
    }

    @Override
    public Boolean visitExprArg(ChefDSLParser.ExprArgContext ctx) {
        Boolean exprResult;
        String arg = null;
        BooleanExprVisitor booleanExprVisitor = new BooleanExprVisitor(parseResult);

        exprResult = ctx.arg().accept(booleanExprVisitor);

        exprResult = resolveNullArgument(exprResult);
        return exprResult;
    }

    @Override
    public Boolean visitArgGreater(ChefDSLParser.ArgGreaterContext ctx) {
        String firstArgument;
        String secondArgument;
        Boolean exprResult;
        PrimaryBaseVisitor primaryVisitor = new PrimaryBaseVisitor(parseResult);
        List<String> firstArgList;
        List<String> secondArgList;

        firstArgList = ctx.arg(0).accept(primaryVisitor);
        secondArgList = ctx.arg(1).accept(primaryVisitor);

        if (firstArgList != null && secondArgList != null && firstArgList.size() == 1 && secondArgList.size() == 1) {
            firstArgument = firstArgList.get(0);
            secondArgument = secondArgList.get(0);
            exprResult = Double.parseDouble(firstArgument) > Double.parseDouble(secondArgument);
        } else {
            LOGGER.info("One of the compared Arguments is an array or null. This is not implemented yet. \n" +
                firstArgList + " is compared to: " + secondArgList);
            exprResult = false;
        }

        return exprResult;
    }

    @Override
    public Boolean visitArgGreaterEqual(ChefDSLParser.ArgGreaterEqualContext ctx) {
        String firstArgument;
        String secondArgument;
        Boolean exprResult;
        PrimaryBaseVisitor primaryVisitor = new PrimaryBaseVisitor(parseResult);
        List<String> firstArgList;
        List<String> secondArgList;

        firstArgList = ctx.arg(0).accept(primaryVisitor);
        secondArgList = ctx.arg(1).accept(primaryVisitor);

        if (firstArgList != null && secondArgList != null && firstArgList.size() == 1 && secondArgList.size() == 1) {
            firstArgument = firstArgList.get(0);
            secondArgument = secondArgList.get(0);
            exprResult = Double.parseDouble(firstArgument) >= Double.parseDouble(secondArgument);
        } else {
            LOGGER.info("One of the compared Arguments is an array or null. This is not implemented yet. \n" +
                firstArgList + " is compared to: " + secondArgList);
            exprResult = false;
        }

        return exprResult;
    }

    @Override
    public Boolean visitArgLess(ChefDSLParser.ArgLessContext ctx) {
        String firstArgument;
        String secondArgument;
        Boolean exprResult;
        PrimaryBaseVisitor primaryVisitor = new PrimaryBaseVisitor(parseResult);
        List<String> firstArgList;
        List<String> secondArgList;

        firstArgList = ctx.arg(0).accept(primaryVisitor);
        secondArgList = ctx.arg(1).accept(primaryVisitor);

        if (firstArgList != null && secondArgList != null && firstArgList.size() == 1 && secondArgList.size() == 1) {
            firstArgument = firstArgList.get(0);
            secondArgument = secondArgList.get(0);
            exprResult = Double.parseDouble(firstArgument) < Double.parseDouble(secondArgument);
        } else {
            LOGGER.info("One of the compared Arguments is an array or null. This is not implemented yet. \n" +
                firstArgList + " is compared to: " + secondArgList);
            exprResult = false;
        }

        return exprResult;
    }

    @Override
    public Boolean visitArgLessEqual(ChefDSLParser.ArgLessEqualContext ctx) {
        String firstArgument;
        String secondArgument;
        Boolean exprResult;
        PrimaryBaseVisitor primaryVisitor = new PrimaryBaseVisitor(parseResult);
        List<String> firstArgList;
        List<String> secondArgList;

        firstArgList = ctx.arg(0).accept(primaryVisitor);
        secondArgList = ctx.arg(1).accept(primaryVisitor);

        if (firstArgList != null && secondArgList != null && firstArgList.size() == 1 && secondArgList.size() == 1) {
            firstArgument = firstArgList.get(0);
            secondArgument = secondArgList.get(0);
            exprResult = Double.parseDouble(firstArgument) <= Double.parseDouble(secondArgument);
        } else {
            LOGGER.info("One of the compared Arguments is an array or null. This is not implemented yet. \n" +
                firstArgList + " is compared to: " + secondArgList);
            exprResult = false;
        }

        return exprResult;
    }

    @Override
    public Boolean visitArgEqual(ChefDSLParser.ArgEqualContext ctx) {
        String firstArgument;
        String secondArgument;
        Boolean exprResult;
        PrimaryBaseVisitor primaryVisitor = new PrimaryBaseVisitor(parseResult);
        List<String> firstArgList;
        List<String> secondArgList;

        firstArgList = ctx.arg(0).accept(primaryVisitor);
        secondArgList = ctx.arg(1).accept(primaryVisitor);

        if (firstArgList != null && secondArgList != null && firstArgList.size() == 1 && secondArgList.size() == 1) {
            firstArgument = firstArgList.get(0);
            secondArgument = secondArgList.get(0);
            exprResult = firstArgument.equals(secondArgument);
        } else {
            LOGGER.info("One of the compared Arguments is an array or null. This is not implemented yet. \n" +
                firstArgList + " is compared to: " + secondArgList);
            exprResult = false;
        }

        return exprResult;
    }

    @Override
    public Boolean visitExprCommand(ChefDSLParser.ExprCommandContext ctx) {
        Boolean exprResult;
        BooleanExprVisitor booleanExprVisitor = new BooleanExprVisitor(parseResult);
        exprResult = ctx.command().accept(booleanExprVisitor);
        return exprResult;
    }

    @Override
    public Boolean visitOperationPrimary(ChefDSLParser.OperationPrimaryContext ctx) {
        String argument;
        String operation;
        List<String> argumentList;
        List<String> callArguments = null;
        Boolean exprResult = false;

        PrimaryBaseVisitor primaryVisitor = new PrimaryBaseVisitor(parseResult);
        CallArgsVisitor callArgsVisitor = new CallArgsVisitor(parseResult);
        argumentList = ctx.primary().accept(primaryVisitor);

        if (argumentList != null && argumentList.size() == 1) {
            argument = argumentList.get(0);
            if (ctx.operation() != null && ctx.call_args() != null) {
                operation = ctx.operation().getText();
                switch (operation) {
                    case "start_with?":
                        callArguments = ctx.call_args().accept(callArgsVisitor);
                        for (int i = 0; i < callArguments.size(); i++) {
                            if (argument.startsWith(callArguments.get(i))) {
                                exprResult = true;
                                break;
                            }
                        }
                        break;

                    default:
                        LOGGER.info("Operation \"" + argumentList + "is not implemented in" + this.getClass());
                        break;
                }
            }
        } else {
            LOGGER.info("Argument is an array. This is not implemented. \n" +
                "Argument" + argumentList);
            exprResult = false;
        }

        return exprResult;
    }

    @Override
    public Boolean visitPrimFuncCall(ChefDSLParser.PrimFuncCallContext ctx) {
        String argument;
        String operation;
        List<String> argumentList;
        List<String> callArguments = null;
        Boolean exprResult = false;

        PrimaryBaseVisitor primaryVisitor = new PrimaryBaseVisitor(parseResult);
        CallArgsVisitor callArgsVisitor = new CallArgsVisitor(parseResult);
        argumentList = ctx.primary().accept(primaryVisitor);

        argumentList = new ArrayList<>();
        argumentList.add("das müsste false sein");
        if (argumentList != null && argumentList.size() == 1) {
            argument = argumentList.get(0);
            if (ctx.function() != null && ctx.function().call_args() != null) {
                operation = ctx.function().operation().getText();
                switch (operation) {
                    case "start_with?":
                        callArguments = ctx.function().call_args().accept(callArgsVisitor);
                        for (int i = 0; i < callArguments.size(); i++) {
                            if (argument.startsWith(callArguments.get(i))) {
                                exprResult = true;
                                break;
                            }
                        }
                        break;

                    default:
                        LOGGER.info("Operation \"" + argumentList + "is not implemented in" + this.getClass());
                        break;
                }
            }
        } else {
            LOGGER.info("Argument is an array. This is not implemented. \n" +
                "Argument" + argumentList);
            exprResult = false;
        }

        return exprResult;
    }

    @Override
    public Boolean visitPrimCompstmtInBrackets(ChefDSLParser.PrimCompstmtInBracketsContext ctx) {
        Boolean exprResult;
        BooleanExprVisitor booleanExprVisitor = new BooleanExprVisitor(parseResult);
        exprResult = ctx.inner_comptstmt().accept(booleanExprVisitor);
        exprResult = resolveNullArgument(exprResult);
        return exprResult;
    }

    @Override
    public Boolean visitArgIndexOf(ChefDSLParser.ArgIndexOfContext ctx) {
        Boolean exprResult = null;
        String leftArgument = null;
        String rightArgument = null;
        List<String> argList;
        PrimaryBaseVisitor argPrimaryVisitor = new PrimaryBaseVisitor(parseResult);
        argList = ctx.arg(0).accept(argPrimaryVisitor);
        if (argList != null && argList.size() == 1) {
            leftArgument = argList.get(0);
            argList = ctx.arg(0).accept(argPrimaryVisitor);
            if (argList != null && argList.size() == 1) {
                rightArgument = argList.get(0);
                rightArgument = regexToString(rightArgument);
                exprResult = leftArgument.contains(rightArgument);
            }
        }

        exprResult = resolveNullArgument(exprResult);
        return exprResult;
    }

    public String regexToString(String rightArgument) {
        if (rightArgument.startsWith("/") && rightArgument.endsWith("/")) {
            rightArgument = rightArgument.substring(1, rightArgument.length() - 1);
        }
        return rightArgument;
    }

    @Override
    public Boolean visitArgPrimary(ChefDSLParser.ArgPrimaryContext ctx) {
        Boolean exprResult = null;
        String arg = null;
        if (ctx.primary().getClass() == ChefDSLParser.PrimCompstmtInBracketsContext.class) {
            BooleanExprVisitor booleanExprVisitor = new BooleanExprVisitor(parseResult);
            exprResult = ctx.primary().accept(booleanExprVisitor);
        } else if (ctx.primary().getClass() == ChefDSLParser.PrimFuncCallContext.class) {
            BooleanExprVisitor booleanExprVisitor = new BooleanExprVisitor(parseResult);
            exprResult = ctx.primary().accept(booleanExprVisitor);
        } else {

            PrimaryBaseVisitor argPrimaryVisitor = new PrimaryBaseVisitor(parseResult);
            List<String> argList = ctx.primary().accept(argPrimaryVisitor);

            if (argList != null && argList.size() == 1) {
                arg = argList.get(0);
            }
            if ("false".equals(arg)) {
                exprResult = false;
            } else if ("true".equals(arg)) {
                exprResult = true;
            } else {
                exprResult = null;
                LOGGER.error("Argument is not an expected boolean. If argument is null, " +
                    "Primary Visitor is not implemented. \n" +
                    "Argument is:" + ctx.primary().getText());
            }
        }
        exprResult = resolveNullArgument(exprResult);
        return exprResult;
    }

    /**
     * Helper method to resolve null arguments. Arguments are when the corresponding visitor is not implemented.
     *
     * @param arg Boolean argument which is resolved and nullchecked.
     * @return Returns false when argument is null.
     */
    private Boolean resolveNullArgument(Boolean arg) {
        if (arg == null) {
            arg = false;
        }
        return arg;
    }
}
