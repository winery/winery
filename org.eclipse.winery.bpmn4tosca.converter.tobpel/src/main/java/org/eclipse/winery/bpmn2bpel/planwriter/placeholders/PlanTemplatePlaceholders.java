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
package org.eclipse.winery.bpmn2bpel.planwriter.placeholders;

public interface PlanTemplatePlaceholders {

	public static final String PLAN_NAMESPACE = "plan_namespace";

	public static final String PLAN_NAME = "plan_name";

	public static final String PLAN_WSDL_NAME = "plan_wsdl_name";

	public static final String PLT_CLIENT_NAME = "plt_client_name";

	public static final String PL_CLIENT_NAME = "pl_client_name";

	public static final String PL_CLIENT_MY_ROLE = "pl_client_pl_myrole_name";

	public static final String PL_CLIENT_PARTNER_ROLE = "pl_client_partnerrole_name";

	public static final String PLAN_INPUT_VAR_MSG_TYPE = "plan_input_var_msg_type";

	public static final String PLAN_INPUT_VAR_NAME = "plan_input_var_name";

	public static final String PLAN_INIT_RCV_NAME = "plan_init_rcv_name";

	public static final String PLAN_INIT_RCV_OPERATION = "plan_init_rcv_operation";

	public static final String PLAN_INIT_RCV_PORT_TYPE = "plan_init_rcv_pt";

	public static final String PLAN_OUTPUT_VAR_NAME = "plan_output_var_name";

	public static final String PLAN_OUTPUT_VAR_MSG_TYPE = "plan_output_var_msg_type";

	public static final String PLAN_END_INV_NAME = "plan_end_inv_name";

	public static final String PLAN_END_INV_OPERATION = "plan_end_inv_operation";

	public static final String PLAN_END_INV_PORT_TYPE = "plan_end_inv_callback_pt";

	public static final String SERVICE_INVOKER_WSDL = "service_invoker_wsdl";

	public static final String SERVICE_INVOKER_XSD = "service_invoker_xsd";


}
