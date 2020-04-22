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
package org.eclipse.winery.model.threatmodeling;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * Simple container class for api responses with generated getter/setters.
 */

public class Threat {
    private String templateName;
    private List<QName> mitigations = new ArrayList<>();
    private String namespace;
    private LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
    private List<ThreatTarget> targets = new ArrayList<>();

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public List<ThreatTarget> getTargets() {
        return targets;
    }

    // adds a threat to the default array by QName
    public void addTarget(String nodeTemplate, QName nodeType) {
        ThreatTarget target = new ThreatTarget();
        target.setNodeTemplate(nodeTemplate);
        target.setNodeType(nodeType);
        this.targets.add(target);
    }

    public List<QName> getMitigations() {
        return mitigations;
    }

    public void addMitigations(QName mitigation) {
        this.mitigations.add(mitigation);
    }

    public LinkedHashMap<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(LinkedHashMap<String, Object> properties) {
        this.properties = properties;
    }
}
