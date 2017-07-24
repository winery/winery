/*******************************************************************************
 * Copyright (c) 2017 ZTE Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     ZTE - support of more gateways
 *******************************************************************************/
package org.eclipse.winery.bpmn2bpel.model;

public class ConditionBranch extends GatewayBranch {
	private boolean isDefault;
	private String condition;

	public ConditionBranch(String id, String condition, boolean isDefault) {
		super(id);

		this.condition = condition;
		this.isDefault = isDefault;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
}
