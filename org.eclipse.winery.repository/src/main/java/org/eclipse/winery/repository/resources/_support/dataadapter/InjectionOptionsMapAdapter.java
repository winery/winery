/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Karoline Saatkamp - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.repository.resources._support.dataadapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.winery.model.tosca.TTopologyTemplate;

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
