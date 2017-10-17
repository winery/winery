/*******************************************************************************
 * Copyright (c) 2013-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.model.tosca.kvproperties;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PropertyDefinitions")
public class PropertyDefinitionKVList extends ArrayList<PropertyDefinitionKV> {

	private static final long serialVersionUID = -6442041855597987094L;

	@XmlElement(name = "PropertyDefinition")
	public List<PropertyDefinitionKV> getPropertyDefinitionKVs() {
		return this;
	}

}
