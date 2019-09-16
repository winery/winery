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

public class ArgsVisitor extends ChefDSLBaseVisitor<List> {

    private CookbookParseResult extractedCookbookConfigs;

    public ArgsVisitor(CookbookParseResult cookbookConfigurations) {
        this.extractedCookbookConfigs = cookbookConfigurations;
    }

    @Override
    public List visitArgs(ChefDSLParser.ArgsContext ctx) {
        ArgVisitor argVisitor = new ArgVisitor(extractedCookbookConfigs);
        List args = new ArrayList();

        for (int count = 0; count < ctx.getChildCount(); count++) {
            if (!ctx.getChild(count).getText().equals(",")) {
                List argsNew = ctx.getChild(count).accept(argVisitor);
                if (argsNew != null) {
                    args.addAll(argsNew);
                } else {
                    args.add(ctx.getChild(count).getText());
                }
            }
        }
        return args;
    }
}
