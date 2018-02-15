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

package org.eclipse.winery.repository.rest.resources._support.dataadapter.injectionAdapter;

import org.eclipse.winery.model.tosca.TTopologyTemplate;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InjectionOptionsMapAdapter extends XmlAdapter<InjectionOptions, Map<String, List<TTopologyTemplate>>> {

    @Override
    public Map<String, List<TTopologyTemplate>> unmarshal(InjectionOptions injectionOptions) throws Exception {
        Map<String, List<TTopologyTemplate>> mapInjections = new HashMap<>();
        for (InjectionOption injection : injectionOptions.getInjectionOption()) {
            mapInjections.put(injection.nodeID, injection.injectionOptions);
        }
        return mapInjections;
    }

    @Override
    public InjectionOptions marshal(Map<String, List<TTopologyTemplate>> mapInjectionOptions) throws Exception {
        InjectionOptions injectionOptions = new InjectionOptions();
        for (Map.Entry<String, List<TTopologyTemplate>> entry : mapInjectionOptions.entrySet()) {
            injectionOptions.addInjectionOptions(new InjectionOption(entry.getKey(), entry.getValue()));
        }
        return injectionOptions;
    }
}
