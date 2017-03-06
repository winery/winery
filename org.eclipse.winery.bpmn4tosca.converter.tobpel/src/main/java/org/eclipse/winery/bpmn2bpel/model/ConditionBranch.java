/*******************************************************************************
 * Copyright 2017 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     ZTE - support of more gateways
 *******************************************************************************/
package org.eclipse.winery.bpmn2bpel.model;

public class ConditionBranch extends GatewayBranch {
	private boolean isDefault;
	private String condition;
	
	public ConditionBranch(String id, String condition, boolean isDefault) {
		super();
		
		this.id = id;
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
