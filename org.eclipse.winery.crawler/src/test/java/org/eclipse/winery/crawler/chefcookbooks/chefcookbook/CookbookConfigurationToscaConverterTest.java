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

import java.util.List;

import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.model.tosca.TNodeType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CookbookConfigurationToscaConverterTest {

    @Test
    public void createMyAppNode() {
        List<TNodeType> nodeTypes;
        ChefCookbookConfiguration cookbookConfiguration = new ChefCookbookConfiguration();
        cookbookConfiguration.setName("myapp");
        cookbookConfiguration.setVersion("1.0.0");
        cookbookConfiguration.setSupports(new Platform("ubuntu", "16.04"));
        cookbookConfiguration.addRequiredPackage(new ChefPackage("openjdk-8-jdk"));
        cookbookConfiguration.addRequiredPackage(new ChefPackage("openjdk-8-jre-headless"));
        cookbookConfiguration.addInstalledPackage(new ChefPackage("myapp", "1.1"));
        cookbookConfiguration.addInstalledPackage(new ChefPackage("myappaddon", "1.2"));
        nodeTypes = new CookbookConfigurationToscaConverter().convertCookbookConfigurationToToscaNode(cookbookConfiguration, 1);

        assertEquals(2, nodeTypes.size());
        assertEquals(true, nodeTypes.get(0).getCapabilityDefinitions().getCapabilityDefinition().get(0).getCapabilityType().toString().endsWith("myapp-1.1"));
        assertEquals(true, nodeTypes.get(0).getCapabilityDefinitions().getCapabilityDefinition().get(1).getCapabilityType().toString().endsWith("myappaddon-1.2"));
        assertEquals(true, nodeTypes.get(0).getRequirementDefinitions().getRequirementDefinition().get(1).getRequirementType().toString().endsWith("openjdk-8-jdk"));
        assertEquals(true, nodeTypes.get(0).getRequirementDefinitions().getRequirementDefinition().get(2).getRequirementType().toString().endsWith("openjdk-8-jre-headless"));

        assertEquals(true, nodeTypes.get(1).getCapabilityDefinitions().getCapabilityDefinition().get(0).getCapabilityType().toString().endsWith("ubuntu_16.04" + WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_VERSION_PREFIX + "1"));
    }
}
