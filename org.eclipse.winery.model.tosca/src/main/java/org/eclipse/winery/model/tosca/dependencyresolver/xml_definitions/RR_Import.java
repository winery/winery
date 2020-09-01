/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.tosca.dependencyresolver.xml_definitions;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author jery Class for simple creating TOSCA imports
 */
public class RR_Import {

	@XmlAttribute(name = "namespace", required = true)
	public String namespace;
	@XmlAttribute(name = "location", required = true)
	public String location;
	@XmlAttribute(name = "importType", required = true)
	public String importType;

	/**
	 * Constructor for Import
	 * 
	 * @param ns
	 *            namespace
	 * @param l
	 *            location
	 * @param it
	 *            import type
	 */
	RR_Import(String ns, String l, String it) {
		namespace = ns;
		location = l;
		importType = it;
	}
}
