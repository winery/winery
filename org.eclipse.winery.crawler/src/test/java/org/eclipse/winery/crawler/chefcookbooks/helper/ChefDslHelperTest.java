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

package org.eclipse.winery.crawler.chefcookbooks.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.ChefCookbookConfiguration;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.CookbookParseResult;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class ChefDslHelperTest {

    @Test
    public void hasChefAttributeInString() {
        String rubyStringWithAttribute = "This is a normal String#{node['test']['attribute']} with an attribute";
        assertTrue(ChefDslHelper.hasChefAttributeInString(rubyStringWithAttribute));
    }

    @ParameterizedTest(name = "{index} => ''{3}''")
    @MethodSource("getStringWithAttributeArguments")
    public void resolveRubyStringWithAttributes(String rubyStringWithAttribute, String attributeValue, String expectedRubyString) {
        List<String> attributeValueList = new ArrayList(Arrays.asList(attributeValue));
        ChefCookbookConfiguration cookbookConfiguration = new ChefCookbookConfiguration();
        cookbookConfiguration.setName("test");
        cookbookConfiguration.putAttribute("['test']['attribute']", attributeValueList);

        assertEquals(expectedRubyString, ChefDslHelper.resolveRubyStringWithAttributes(cookbookConfiguration, rubyStringWithAttribute));
    }

    @ParameterizedTest(name = "{index} => ''{3}''")
    @MethodSource("getStringWithAttributeArguments")
    public void resolveRubyStringWithCode(String rubyStringWithAttribute, String attributeValue, String expectedRubyString) {
        List<String> attributeValueList = new ArrayList(Arrays.asList(attributeValue));
        ChefCookbookConfiguration cookbookConfiguration = new ChefCookbookConfiguration();
        cookbookConfiguration.setName("test");
        cookbookConfiguration.putAttribute("['test']['attribute']", attributeValueList);
        CookbookParseResult parseResult = new CookbookParseResult("test");
        parseResult.putCookbookConfig(cookbookConfiguration);
        assertEquals(expectedRubyString, ChefDslHelper.resolveRubyStringWithCode(parseResult, rubyStringWithAttribute));
    }

    private static Stream<Arguments> getStringWithAttributeArguments() {
        String attributeValue = "testVal";
        return Stream.of(
            Arguments.of("#{1} This is a normal #{2} String with attribute: #{node['test']['attribute']} !", attributeValue,
                "1 This is a normal 2 String with attribute: " + attributeValue + " !"),
            Arguments.of("#{1} This is a normal #{2} String with attribute: #{node['test']['attribute']}", attributeValue,
                "1 This is a normal 2 String with attribute: " + attributeValue),
            Arguments.of("This is a normal #{2} String with attribute: #{node['test']['attribute']}", attributeValue,
                "This is a normal 2 String with attribute: " + attributeValue)
        );
    }

    @ParameterizedTest(name = "{index} => ''{3}''")
    @MethodSource("getRecipeCalls")
    void resolveRecipeName(String recipeCall, String expectedCookbookName, String expectedRecipe) {
        String[] parts = ChefDslHelper.resolveRecipeCall(recipeCall);
        String cookbookName = parts[0];
        String recipe = parts[1];
        assertEquals(expectedCookbookName, cookbookName);
        assertEquals(expectedRecipe, recipe);
    }

    private static Stream<Arguments> getRecipeCalls() {
        return Stream.of(
            Arguments.of("java::default", "java", "default.rb"),
            Arguments.of("java::windows", "java", "windows.rb"),
            Arguments.of("", null, null),
            Arguments.of("java", "java", "default.rb")
        );
    }
}
