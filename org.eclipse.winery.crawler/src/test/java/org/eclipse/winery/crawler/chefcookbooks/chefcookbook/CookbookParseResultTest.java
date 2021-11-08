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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CookbookParseResultTest {

    private ChefCookbookConfiguration cookbookConfiguration = new ChefCookbookConfiguration();
    private CookbookParseResult cookbookParseResult = new CookbookParseResult("testcookbook");

    @BeforeAll
    public void beforeAll() {
        cookbookConfiguration.setName("java");
        cookbookConfiguration.setSupports(new Platform("ubuntu", "18.04"));
        cookbookParseResult.putCookbookConfig(cookbookConfiguration);
    }

    @Test
    public void addAttributeToAllConfigs() {
        String attributeValue = "testvalue";
        List<String> expectedValues = new ArrayList<>(Arrays.asList(attributeValue));
        ChefCookbookConfiguration secondCookbookConfiguration = new ChefCookbookConfiguration(cookbookConfiguration);
        secondCookbookConfiguration.setSupports(new Platform("windows", "10"));
        cookbookParseResult.putCookbookConfig(secondCookbookConfiguration);
        cookbookParseResult.addAttributeToAllConfigs("testname", Collections.singletonList(attributeValue));
        assertEquals(expectedValues, cookbookParseResult.getAllConfigsAsList().get(0).getAttribute("testname"));
        assertEquals(expectedValues, cookbookParseResult.getAllConfigsAsList().get(1).getAttribute("testname"));
    }

    @Test
    public void getCookbookConfigsByPlatform() {
        cookbookParseResult.getCookbookConfigsByPlatform("windows");
        assertTrue(cookbookParseResult.getCookbookConfigsByPlatform("windows").containsKey("10"));
        assertFalse(cookbookParseResult.getCookbookConfigsByPlatform("windows").containsKey("18.04"));
    }

    @Test
    public void putCookbookConfigsAsList() {
        CookbookParseResult parseResult = new CookbookParseResult("test");
        List<ChefCookbookConfiguration> configList = new LinkedList<>();
        ChefCookbookConfiguration chefConfig = new ChefCookbookConfiguration();
        ChefCookbookConfiguration chefConfig1 = new ChefCookbookConfiguration();
        ChefCookbookConfiguration chefConfig2 = new ChefCookbookConfiguration();
        chefConfig.setName("config1");
        chefConfig1.setName("config2");
        chefConfig2.setName("config3");
        chefConfig.setSupports(new Platform("ubuntu", "16.04"));
        chefConfig1.setSupports(new Platform("ubuntu", "18.04"));
        chefConfig2.setSupports(new Platform("windows", "18.04"));
        configList.add(chefConfig);
        configList.add(chefConfig1);
        configList.add(chefConfig2);
        parseResult.putCookbookConfigsAsList(configList);

        assertEquals(3, parseResult.getAllConfigsAsList().size());
    }

    @Test
    public void getListOfConfigsInOwnParseresult() {
        CookbookParseResult parseResult = new CookbookParseResult("test");
        List<ChefCookbookConfiguration> configList = new LinkedList<>();
        ChefCookbookConfiguration chefConfig = new ChefCookbookConfiguration();
        ChefCookbookConfiguration chefConfig1 = new ChefCookbookConfiguration();
        ChefCookbookConfiguration chefConfig2 = new ChefCookbookConfiguration();
        chefConfig.setName("config1");
        chefConfig1.setName("config2");
        chefConfig2.setName("config3");
        chefConfig.setSupports(new Platform("ubuntu", "16.04"));
        chefConfig1.setSupports(new Platform("ubuntu", "18.04"));
        chefConfig2.setSupports(new Platform("windows", "18.04"));
        configList.add(chefConfig);
        configList.add(chefConfig1);
        configList.add(chefConfig2);
        parseResult.putCookbookConfigsAsList(configList);

        List<CookbookParseResult> parseResultList = parseResult.getListOfConfigsInOwnParseresult();
        assertEquals(3, parseResultList.size());
        assertEquals("test", parseResultList.get(0).getCookbookName());
        assertEquals("test", parseResultList.get(1).getCookbookName());
        assertEquals("test", parseResultList.get(2).getCookbookName());
    }

    @Test
    public void getListOfConfigsInOwnParseresultEmptyConfigs() {
        CookbookParseResult parseResult = new CookbookParseResult("test");
        List<CookbookParseResult> parseResultList = parseResult.getListOfConfigsInOwnParseresult();
        assertEquals(1, parseResultList.size());
        assertEquals("test", parseResultList.get(0).getCookbookName());
    }

    @Test
    void setCookbookPath() {
        String cookbookPath = "folder/cookbbokname";
        cookbookParseResult.setCookbookPath(cookbookPath);
        assertEquals(cookbookPath, cookbookParseResult.getCookbookPath());

        cookbookParseResult.setCookbookPath(cookbookPath);
        assertEquals(cookbookPath, cookbookParseResult.getCookbookPath());
    }
}
