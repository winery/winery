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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides visitor methods to resolve Ruby code in Strings. "/usr/lib/jvm/java-#{node['java']['jdk_version'].to_i}-windows"
 */
public class CodeInStringVisitor extends ChefDSLBaseVisitor<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandVisitor.class.getName());

    private CookbookParseResult extractedCookbookConfigs;
    private String resolvedString;

    public CodeInStringVisitor(CookbookParseResult parseResult) {
        this.extractedCookbookConfigs = parseResult;
        this.resolvedString = "";
    }

    @Override
    public String visitArgPrimary(ChefDSLParser.ArgPrimaryContext ctx) {
        List<String> arg;
        PrimaryBaseVisitor baseVisitor = new PrimaryBaseVisitor(extractedCookbookConfigs);
        arg = ctx.primary().accept(baseVisitor);

        if (arg != null && arg.size() > 0) {
            resolvedString = arg.get(0);

            if (arg.size() > 1) {
                LOGGER.error("Argument of code in String is an array. This should not happen.");
            }
        }

        return resolvedString;
    }

    @Override
    public String aggregateResult(String aggregate, String nextResult) {
        return resolvedString;
    }
}
