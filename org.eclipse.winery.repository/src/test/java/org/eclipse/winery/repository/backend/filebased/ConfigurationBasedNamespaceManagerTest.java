/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.backend.filebased;

import org.apache.commons.configuration.BaseConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigurationBasedNamespaceManagerTest {

    private ConfigurationBasedNamespaceManager configurationBasedNamespaceManager;

    @BeforeEach
    public void initializeNamespaceManager() {
        this.configurationBasedNamespaceManager = new ConfigurationBasedNamespaceManager(new BaseConfiguration());
    }

    @Test
    public void openToscaNodeTypesCorrectlyProposed() {
        assertEquals("otntyIexample", this.configurationBasedNamespaceManager.generatePrefixProposal("http://opentosca.org/nodetypes/example", 0));
    }

    @Test
    public void nonOpenToscaNodeTypesCorrectlyProposed() {
        assertEquals("ntyIexample", this.configurationBasedNamespaceManager.generatePrefixProposal("http://example.org/nodetypes/example", 0));
    }

    @Test
    public void propertiesDefinitionNamespaceProducesNiceString() {
        assertEquals("otntypdIexample", this.configurationBasedNamespaceManager.generatePrefixProposal("http://opentosca.org/nodetypes/example/propertiesdefinition/winery", 0));
    }

    @Test
    public void correctPrefixForOpenToscaAndTypeButNoSubType() {
        assertEquals("otateIgeneral", this.configurationBasedNamespaceManager.generatePrefixProposal("http://opentosca.org/artifacttemplates", 0));
    }

    @Test
    public void correctPrefixForNamespacewithoutType() {
        assertEquals("tosca", this.configurationBasedNamespaceManager.generatePrefixProposal("http://example.org/tosca/", 0));
    }

    @Test
    public void openToscaNodeTypesCorrect() {
        assertEquals("otntyIexample", this.configurationBasedNamespaceManager.generatePrefixProposal("http://opentosca.org/nodetypes/example", 0));
    }

    @Test
    public void openToscaNodeTypesCorrectAtSecondCall() {
        assertEquals("otntyIexample", this.configurationBasedNamespaceManager.getPrefix("http://opentosca.org/nodetypes/example"));
        assertEquals("otntyIexample", this.configurationBasedNamespaceManager.getPrefix("http://opentosca.org/nodetypes/example"));
    }

    @Test
    public void openToscaNodeTypesCorrectAtSimilarNamespaces() {
        assertEquals("otntyIexample", this.configurationBasedNamespaceManager.getPrefix("http://opentosca.org/nodetypes/example"));
        assertEquals("otntyIexample1", this.configurationBasedNamespaceManager.getPrefix("http://opentosca.org/nodetypes/example/example"));
    }

    @Test
    public void openToscaNodeTypesCorrectAtSimilarNamespacesWithUniqueness() {
        assertEquals("otntyIexample", this.configurationBasedNamespaceManager.getPrefix("http://opentosca.org/nodetypes/example"));
        assertEquals("otntyIexample1", this.configurationBasedNamespaceManager.getPrefix("http://opentosca.org/nodetypes/example/example"));

        // try again -> same prefixes have to be returned
        assertEquals("otntyIexample", this.configurationBasedNamespaceManager.getPrefix("http://opentosca.org/nodetypes/example"));
        assertEquals("otntyIexample1", this.configurationBasedNamespaceManager.getPrefix("http://opentosca.org/nodetypes/example/example"));
    }

    @Test
    public void xmlNullNamespaceHasPrefix() {
        // the XML null namespace is the empty string
        assertEquals("null", this.configurationBasedNamespaceManager.getPrefix(""));
    }

    @Test
    public void nullNamespaceHasPrefix() {
        assertEquals("null", this.configurationBasedNamespaceManager.getPrefix((String) null));
    }
}
