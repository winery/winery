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

package org.eclipse.winery.crawler.chefcookbooks.chefcookbook;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.model.tosca.TNodeType;

public class ParseResultToscaConverter {

    public List<TNodeType> convertCookbookConfigurationToToscaNode(CookbookParseResult cookbookParseResult) {
        List<TNodeType> nodeTypes = new ArrayList<>();

        List<ChefCookbookConfiguration> cookbookConfigsList = cookbookParseResult.getAllConfigsAsList();
        for (int count = 0; count < cookbookConfigsList.size(); count++) {
            nodeTypes.addAll(new CookbookConfigurationToscaConverter().convertCookbookConfigurationToToscaNode(cookbookConfigsList.get(count), count + 1));
        }
        return nodeTypes;
    }

    public void saveToscaNodeTypes(List<TNodeType> nodeTypes) {
        for (int i = 0; i < nodeTypes.size(); i++) {
            new CookbookConfigurationToscaConverter().saveToscaNodeType(nodeTypes.get(i));
        }
    }
}
