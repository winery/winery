/*******************************************************************************
 * Copyright (c) 2015-2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.bpmn2bpel.parser;

public interface JsonKeys {


	/*
	 * Field names of BPMN4Tosca Model
	 */

	public static final String NAME = "name";

	public static final String ID = "id";

	public static final String TYPE = "type";

	public static final String INPUT = "input";

	public static final String OUTPUT = "output";

	public static final String VALUE = "value";

	public static final String NODE_TEMPLATE = "node_template";

	public static final String NODE_OPERATION = "node_operation";

	public static final String NODE_INTERFACE_NAME = "interface";

	public static final String CONNECTIONS = "connections";

	public static final String CONDITIONS = "conditions";

	public static final String CONDITION = "condition";

	public static final String DEFAULT = "default";


	/*
	 * Exclusive-Gateway, Event, Management-Task Types
	 *
	 */
	public static final String NODE_TYPE_MGMT_TASK = "ToscaNodeManagementTask";

	public static final String NODE_TYPE_START_EVENT = "StartEvent";

	public static final String NODE_TYPE_END_EVENT = "EndEvent";

	public static final String NODE_TYPE_GATEWAY_EXCLUSIVE = "ExclusiveGateway";

	public static final String NODE_TYPE_GATEWAY_EXCLUSIVE_END = "ExclusiveGatewayEnd";


	/*
	 * Parameter Types
	 */
	public static final String PARAM_TYPE_VALUE_STRING = "string";

	public static final String PARAM_TYPE_VALUE_TOPOLOGY = "topology";

	public static final String PARAM_TYPE_VALUE_PLAN = "plan";

	public static final String PARAM_TYPE_VALUE_CONCAT = "concat";

	public static final String PARAM_TYPE_VALUE_IA = "implementation_artifact";

	public static final String PARAM_TYPE_VALUE_DA = "deployment_artifact";

}
