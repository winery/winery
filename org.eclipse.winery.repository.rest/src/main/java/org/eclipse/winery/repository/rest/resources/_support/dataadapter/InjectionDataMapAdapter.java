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

package org.eclipse.winery.repository.rest.resources._support.dataadapter;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.winery.model.tosca.TTopologyTemplate;

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
