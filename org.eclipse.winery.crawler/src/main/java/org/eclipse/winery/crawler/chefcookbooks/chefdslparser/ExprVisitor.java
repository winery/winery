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

import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.ChefAttribute;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.CookbookParseResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprVisitor extends ChefDSLBaseVisitor<CookbookParseResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExprVisitor.class.getName());

    private CookbookParseResult cookbookConfigs;

    public ExprVisitor(CookbookParseResult cookbookConfigurations) {
        this.cookbookConfigs = cookbookConfigurations;
    }

    @Override
    public CookbookParseResult visitExprCommand(ChefDSLParser.ExprCommandContext ctx) {
        CommandVisitor commandVisitor = new CommandVisitor(cookbookConfigs);
        cookbookConfigs = ctx.command().accept(commandVisitor);
        return cookbookConfigs;
    }

    @Override
    public CookbookParseResult visitExprArg(ChefDSLParser.ExprArgContext ctx) {
        CookbookParseResult newCookbookParseResult;
        if (ctx.getChildCount() == 1 && ctx.getChild(0).getClass() == ChefDSLParser.ArgPrimaryContext.class) {
            PrimaryStatementVisitor primaryStatementVisitor = new PrimaryStatementVisitor(cookbookConfigs);
            newCookbookParseResult = ctx.arg().accept(primaryStatementVisitor);
            if (newCookbookParseResult instanceof CookbookParseResult) {
                cookbookConfigs = newCookbookParseResult;
            } else {
                LOGGER.info("This should not have happened");
            }
        } else if (ctx.getChild(0).getClass() == ChefDSLParser.ArgAssignContext.class || ctx.getChild(0).getClass() == ChefDSLParser.ArgTernaryContext.class) {
            // This if statement is to assign local variables
            AssignAttributeVisitor assignAttributeVisitor = new AssignAttributeVisitor(cookbookConfigs);
            ChefAttribute chefAttribute = ctx.arg().accept(assignAttributeVisitor);

            if (chefAttribute != null && chefAttribute.getName() != null && chefAttribute.getValues() != null) {
                try {
                    CookbookVisitor.variables.put(chefAttribute.getName(), (String) chefAttribute.getValues().get(0));
                } catch (Exception e) {
                    CookbookVisitor.variables.put(chefAttribute.getName(), "");
                }
            }
        }
        return cookbookConfigs;
    }

    @Override
    public CookbookParseResult aggregateResult(CookbookParseResult aggregate, CookbookParseResult nextResult) {
        return cookbookConfigs;
    }
}
