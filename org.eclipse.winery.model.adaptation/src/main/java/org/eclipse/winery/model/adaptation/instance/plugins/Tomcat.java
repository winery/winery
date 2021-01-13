/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.instance.plugins;

import java.util.Map;
import java.util.Set;

import org.eclipse.winery.model.adaptation.instance.InstanceModelRefinementPlugin;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

public class Tomcat extends InstanceModelRefinementPlugin {
    
    public Tomcat() {
        super("Tomcat");
    }
    
    @Override
    public TTopologyTemplate apply(TTopologyTemplate template, Map<String, String> additionalInputs) {
        return null;
    }

    @Override
    public Set<String> determineAdditionalInputs(TTopologyTemplate template) {
        return null;
    }

    @Override
    public boolean isApplicable(TTopologyTemplate template) {
        return false;
    }
}
