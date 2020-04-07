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

public class CaseConditionVisitor extends ChefDSLBaseVisitor<List> {

    private List result;

    private CookbookParseResult extractedCookbookConfigs;

    public CaseConditionVisitor(CookbookParseResult cookbookConfigurations) {
        this.extractedCookbookConfigs = cookbookConfigurations;
    }

    @Override
    public List visitArgPrimary(ChefDSLParser.ArgPrimaryContext ctx) {

        PrimaryBaseVisitor argPrimaryVisitor = new PrimaryBaseVisitor(extractedCookbookConfigs);
        result = ctx.primary().accept(argPrimaryVisitor);

        return result;
    }

    @Override
    public List aggregateResult(List aggregate, List nextResult) {
        return result;
    }
}
