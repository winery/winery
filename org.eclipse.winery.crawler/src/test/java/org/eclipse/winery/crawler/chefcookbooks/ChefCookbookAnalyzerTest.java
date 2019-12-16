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

package org.eclipse.winery.crawler.chefcookbooks;

import java.io.File;
import java.util.List;

import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.CookbookParseResult;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbook.ChefCookbookConfiguration;
import org.eclipse.winery.crawler.chefcookbooks.chefcookbookcrawler.CrawledCookbooks;

import org.antlr.v4.runtime.CharStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.antlr.v4.runtime.CharStreams.fromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChefCookbookAnalyzerTest {

    private CharStream metadata;

    @BeforeEach
    public void initialize() {
        metadata = fromString("name 'openssh'\n" +
            "maintainer 'Chef Software, Inc.'\n" +
            "maintainer_email 'cookbooks@chef.io'\n" +
            "license 'Apache-2.0'\n" +
            "description 'Installs and configures OpenSSH client and daemon'\n" +
            "long_description IO.read(File.join(File.dirname(__FILE__), 'README.md'))\n" +
            "version '2.7.1'\n" +
            "\n" +
            "recipe 'openssh', 'Installs openssh'\n" +
            "recipe 'openssh::iptables', 'Set up iptables to allow SSH inbound'\n" +
            "\n" +
            "supports 'windows'\n" +
            "%w(aix centos).each do |os|\n" +
            "  supports os\n" +
            "end\n" +
            "supports 'ubuntu' , '16.04'\n" +
            "\n" +
            "depends 'iptables', '>= 1.0'\n" +
            "\n" +
            "source_url 'https://github.com/chef-cookbooks/openssh'\n" +
            "issues_url 'https://github.com/chef-cookbooks/openssh/issues'\n" +
            "chef_version '>= 12.1' if respond_to?(:chef_version)\n");
    }

    @Test
    public void testCompileMetadata() {
        List<ChefCookbookConfiguration> cookbookConfigs;
        CookbookParseResult extractedCookbookConfigs = new CookbookParseResult("openssh");
        extractedCookbookConfigs = ChefCookbookAnalyzer.compile(metadata, extractedCookbookConfigs);
        cookbookConfigs = extractedCookbookConfigs.getAllConfigsAsList();
        assertEquals(4, cookbookConfigs.size());

        for (int count = 0; count < cookbookConfigs.size(); count++) {
            assertEquals("openssh", cookbookConfigs.get(count).getName());
            assertEquals("Installs and configures OpenSSH client and daemon", cookbookConfigs.get(count).getDescription());
            assertTrue(cookbookConfigs.get(count).getDepends().containsKey("iptables"));
        }

        assertTrue(extractedCookbookConfigs.getCookbookConfigs().containsKey("aix"));
    }

    /**
     * This tests compiles the myapp cookbook. The myapp cookbook is a cookbook for testing purposes. It is intended to
     * be extended, to check new functionalities of the cookbook compiler
     */
    @Test
    public void compileMyAppCookbook() {
        String cookbookName = "myapp";
        String cookbookPath;
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(cookbookName).getFile());
        CookbookParseResult cookbookParseResult = new CookbookParseResult(cookbookName);
        cookbookPath = file.getAbsolutePath().replace("\\", "/");
        cookbookParseResult.setCookbookPath(cookbookPath);
        cookbookParseResult = new ChefCookbookAnalyzer().compileCookbook(cookbookParseResult, false);
        cookbookParseResult.clear();

        /**
         * Dependencies are stored in dependencies folder of the myapp cookbook
         * Delete downloaded dependencies
         */
        file = new File(classLoader.getResource("myapp/dependencies").getFile());
        CrawledCookbooks.deleteFile(file.getPath());
    }
}
