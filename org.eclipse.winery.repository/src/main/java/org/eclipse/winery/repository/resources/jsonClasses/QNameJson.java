/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.jsonClasses;

import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;

import javax.xml.namespace.QName;

public class QNameJson {

	private String name;
	private QName qName;

	public QNameJson() {}

	public QNameJson(TOSCAComponentId id) {
		this.name = id.getXmlId().getDecoded();
		this.qName = id.getQName();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public QName getqName() {
		return qName;
	}

	public void setqName(QName qName) {
		this.qName = qName;
	}
}
