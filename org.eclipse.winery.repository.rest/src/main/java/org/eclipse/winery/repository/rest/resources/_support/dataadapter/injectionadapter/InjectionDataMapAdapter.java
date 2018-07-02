/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources._support.dataadapter.injectionadapter;

import org.eclipse.winery.model.tosca.TTopologyTemplate;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

public class InjectionDataMapAdapter extends XmlAdapter<Injections, Map<String, TTopologyTemplate>> {

    @Override
    public Map<String, TTopologyTemplate> unmarshal(Injections injections) throws Exception {
        Map<String, TTopologyTemplate> mapInjections = new HashMap<>();
        for (Injection injection : injections.getInjections()) {
            mapInjections.put(injection.nodeID, injection.injectedTopologyFragment);
        }
        return mapInjections;
    }

    @Override
    public Injections marshal(Map<String, TTopologyTemplate> mapInjections) throws Exception {
        Injections injections = new Injections();
        for (Map.Entry<String, TTopologyTemplate> entry : mapInjections.entrySet()) {
            injections.addInjection(new Injection(entry.getKey(), entry.getValue()));
        }
        return injections;
    }
}
