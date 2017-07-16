/*******************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Sebastian Wagner - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.bpmn2bpel.model;

import javax.xml.namespace.QName;

public class ManagementTask extends Task {

	private String interfaceName;;

	private QName nodeTemplateId;

	private String nodeOperation;

	public QName getNodeTemplateId() {
		return nodeTemplateId;
	}

	public void setNodeTemplateId(QName nodeTemplateId) {
		this.nodeTemplateId = nodeTemplateId;
	}

	public String getNodeOperation() {
		return nodeOperation;
	}

	public void setNodeOperation(String nodeOperation) {
		this.nodeOperation = nodeOperation;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

}
