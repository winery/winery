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

import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.CookbookParseResult;

/**
 * This is the root visitor. Semantic analysis starts here.
 */
public class CompstmtVisitor extends ChefDSLBaseVisitor<CookbookParseResult> {

    private CookbookParseResult cookbookConfigs;

    public CompstmtVisitor(CookbookParseResult cookbookConfigurations) {
        this.cookbookConfigs = cookbookConfigurations;
    }

    @Override
    public CookbookParseResult visitCompstmtA(ChefDSLParser.CompstmtAContext ctx) {
        CookbookParseResult newParseResult;
        if (ctx.stmt() != null) {
            newParseResult = ctx.stmt().accept(new StmtVisitor(cookbookConfigs));
            if (newParseResult != null) {
                cookbookConfigs = new CookbookParseResult(newParseResult);
            }
            if (ctx.expr() != null) {
                for (ChefDSLParser.ExprContext exprContext : ctx.expr()) {
                    newParseResult = exprContext.accept(new ExprVisitor(cookbookConfigs));
                    if (newParseResult != null) {
                        cookbookConfigs = new CookbookParseResult(newParseResult);
                    }
                }
            }
        }
        return cookbookConfigs;
    }

    @Override
    public CookbookParseResult visitInner_comptstmt(ChefDSLParser.Inner_comptstmtContext ctx) {
        CookbookParseResult newParseResult;
        if (ctx.stmt() != null) {
            newParseResult = ctx.stmt().accept(new StmtVisitor(cookbookConfigs));
            if (newParseResult != null) {
                cookbookConfigs = new CookbookParseResult(newParseResult);
            }
            if (ctx.expr() != null) {
                for (ChefDSLParser.ExprContext exprContext : ctx.expr()) {
                    newParseResult = exprContext.accept(new ExprVisitor(cookbookConfigs));
                    if (newParseResult != null) {
                        cookbookConfigs = new CookbookParseResult(newParseResult);
                    }
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
