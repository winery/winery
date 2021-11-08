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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.ChefCookbookConfiguration;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.CookbookParseResult;

public class CookbookVisitor extends ChefDSLBaseVisitor<CookbookParseResult> {

    public static HashMap<String, String> variables = new HashMap<String, String>();
    private CookbookParseResult cookbookConfigs;

    public CookbookVisitor(CookbookParseResult cookbookConfigurations) {
        this.cookbookConfigs = cookbookConfigurations;
    }

    @Override
    public CookbookParseResult visitProgram(ChefDSLParser.ProgramContext ctx) {
        List<CookbookParseResult> parseResultList = cookbookConfigs.getListOfConfigsInOwnParseresult();

        CookbookParseResult filteredParseResult;

        List<ChefCookbookConfiguration> processedCookbookConfigs = new LinkedList<>();

        // If parse result have multiple cookbook configurations, the AST is visited for each configuration.
        for (int countConfigs = 0; countConfigs < parseResultList.size(); countConfigs++) {
            filteredParseResult = parseResultList.get(countConfigs);

            for (int count = 0; count < ctx.compstmt().size(); count++) {
                CompstmtVisitor compstmtVisitor = new CompstmtVisitor(filteredParseResult);
                filteredParseResult = ctx.compstmt(count).accept(compstmtVisitor);
            }

            processedCookbookConfigs.add(filteredParseResult.getAllConfigsAsList().get(0));
            filteredParseResult.clearConfigurations();
        }

        // If parseresult have no cookbook configurations, the AST is walked a single time
        // This should only happen when metadata.rb is parsed.
        if (cookbookConfigs.getNumOfCookbookConfigs() == 0) {
            CompstmtVisitor compstmtVisitor = new CompstmtVisitor(cookbookConfigs);
            for (int count = 0; count < ctx.compstmt().size(); count++) {
                cookbookConfigs = ctx.compstmt(count).accept(compstmtVisitor);
            }
        } else if (cookbookConfigs.getNumOfCookbookConfigs() > 0) {
            cookbookConfigs.replaceCookbookConfigs(processedCookbookConfigs);
        }
        return cookbookConfigs;
    }

    @Override
    public CookbookParseResult aggregateResult(CookbookParseResult aggregate, CookbookParseResult nextResult) {
        return cookbookConfigs;
    }
}
