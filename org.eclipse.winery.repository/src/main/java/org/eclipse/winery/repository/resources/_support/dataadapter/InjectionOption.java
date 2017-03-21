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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.eclipse.winery.common.constants.Namespaces;
import org.eclipse.winery.model.tosca.TNodeTemplate;

public class InjectionOption {
	@XmlElement(name = "hostedNodeID")
	protected String hostedNodeID;

	@XmlElementWrapper (name = "hostOptions")
	@XmlElement(namespace = Namespaces.TOSCA_NAMESPACE, name = "NodeTemplate")
	protected List<TNodeTemplate> hostNodeTemplateOptions;

	public InjectionOption() {
	}

	public InjectionOption(String hostedNodeID, List<TNodeTemplate> hostNodeTemplateOptions) {
		this.hostedNodeID = hostedNodeID;
		this.hostNodeTemplateOptions = hostNodeTemplateOptions;
	}
}
