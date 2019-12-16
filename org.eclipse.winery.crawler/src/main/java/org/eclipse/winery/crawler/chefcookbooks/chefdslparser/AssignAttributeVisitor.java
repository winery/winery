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

import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.ChefAttribute;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.CookbookParseResult;

public class AssignAttributeVisitor extends ChefDSLBaseVisitor<ChefAttribute> {

    private CookbookParseResult extractedCookbookConfigs;
    private ChefAttribute chefAttribute;

    public AssignAttributeVisitor(CookbookParseResult cookbookConfigurations) {
        this.extractedCookbookConfigs = cookbookConfigurations;
    }

    @Override
    public ChefAttribute visitArgAssign(ChefDSLParser.ArgAssignContext ctx) {
        String attributeName = ctx.lhs().getText();
        ChefAttribute attribute = null;
        ArrayList attributeValue;

        if (ctx.arg().getClass() == ChefDSLParser.ArgPrimaryContext.class) {
            PrimaryBaseVisitor argPrimaryVisitor = new PrimaryBaseVisitor(extractedCookbookConfigs);
            attributeValue = (ArrayList) ctx.arg().accept(argPrimaryVisitor);

            if (attributeName != null && attributeValue != null) {
                attribute = new ChefAttribute(attributeName, attributeValue);
            }
        } else {
            ArgVisitor argVisitor = new ArgVisitor(extractedCookbookConfigs);
            attribute = (ChefAttribute) ctx.arg().accept(argVisitor);
        }

        chefAttribute = attribute;
        return chefAttribute;
    }

    /**
     * This visit method resolves the ternary operator of the Ruby language. Description of operator: If Condition is
     * true ? Then value X : Otherwise value Y
     *
     * @return Returns an Attribute with constisting of name and value.
     */
    @Override
    public ChefAttribute visitArgTernary(ChefDSLParser.ArgTernaryContext ctx) {
        List<String> ifValue;
        List<String> elseValue;
        BooleanExprVisitor booleanExprVisitor = new BooleanExprVisitor(extractedCookbookConfigs);
        Boolean condition = ctx.arg(0).accept(booleanExprVisitor);

        AssignAttributeVisitor assignAttributeVisitor = new AssignAttributeVisitor(extractedCookbookConfigs);
        PrimaryBaseVisitor primaryBaseVisitor = new PrimaryBaseVisitor(extractedCookbookConfigs);
        ifValue = ctx.arg(1).accept(primaryBaseVisitor);
        elseValue = ctx.arg(2).accept(primaryBaseVisitor);

        ChefAttribute lhs = ctx.arg(0).accept(assignAttributeVisitor);

        if (lhs != null) {
            chefAttribute = new ChefAttribute(lhs.getName());
            if (condition == true) {
                chefAttribute.addAttribute((ArrayList<String>) ifValue);
            } else if (condition == false) {
                chefAttribute.addAttribute((ArrayList<String>) elseValue);
            }
        }
        return chefAttribute;
    }

    @Override
    public ChefAttribute visitArg8(ChefDSLParser.Arg8Context ctx) {
        return new ChefAttribute("");
    }

    @Override
    public ChefAttribute visitLhs(ChefDSLParser.LhsContext ctx) {
        return new ChefAttribute(ctx.getText());
    }

    @Override
    public ChefAttribute aggregateResult(ChefAttribute aggregate, ChefAttribute nextResult) {
        return chefAttribute;
    }
}
