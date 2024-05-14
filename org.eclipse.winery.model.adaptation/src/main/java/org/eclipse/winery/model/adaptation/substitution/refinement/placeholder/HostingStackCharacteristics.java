/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.substitution.refinement.placeholder;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;

public class HostingStackCharacteristics {

    private TNodeTemplate matchingNodeTemplate;
    private Set<String> hostingStackKVPropertyNames = new HashSet<>();
    private Set<QName> hostingStackCapabilityTypeQName = new HashSet<>();

    public HostingStackCharacteristics(TNodeTemplate matchingNodeTemplate, Set<String> hostingStackKVPropertiesNames, Set<QName> hostingStackCapabilityTypesQName) {
        this.matchingNodeTemplate = matchingNodeTemplate;
        this.hostingStackKVPropertyNames = hostingStackKVPropertiesNames;
        this.hostingStackCapabilityTypeQName = hostingStackCapabilityTypesQName;
    }

    public HostingStackCharacteristics(TNodeTemplate matchingNodeTemplate) {
        this.matchingNodeTemplate = matchingNodeTemplate;
    }

    public HostingStackCharacteristics() {
    }

    public TNodeTemplate getMatchingNodeTemplate() {
        return matchingNodeTemplate;
    }

    public void setMatchingNodeTemplate(TNodeTemplate matchingNodeTemplate) {
        this.matchingNodeTemplate = matchingNodeTemplate;
    }

    public Set<String> getHostingStackKVProperties() {
        return hostingStackKVPropertyNames;
    }

    public void setHostingStackKVProperties(Set<String> hostingStackKVPropertiesNames) {
        this.hostingStackKVPropertyNames = hostingStackKVPropertiesNames;
    }

    public Set<QName> getHostingStackCapabilityTypes() {
        return hostingStackCapabilityTypeQName;
    }

    public void setHostingStackCapabilityTypes(Set<QName> hostingStackCapabilityTypesQName) {
        this.hostingStackCapabilityTypeQName = hostingStackCapabilityTypesQName;
    }

    public void addKVPropertyToProperty(String propertyName) {
        this.hostingStackKVPropertyNames.add(propertyName);
    }

    public void addKVPropertyToProperties(Set<String> propertyNames) {
        this.hostingStackKVPropertyNames.addAll(propertyNames);
    }

    public void addCapability(QName capabilityName) {
        this.hostingStackCapabilityTypeQName.add(capabilityName);
    }

    public void addCapabilities(Set<QName> capabilityNames) {
        this.hostingStackCapabilityTypeQName.addAll(capabilityNames);
    }
}
