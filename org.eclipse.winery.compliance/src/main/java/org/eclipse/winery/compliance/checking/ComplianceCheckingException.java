/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
package org.eclipse.winery.compliance.checking;

public class ComplianceCheckingException extends Exception {

	public static final String EMPTY_COMPLIANCE_RULE = "ComplianceRuleChecker invalid: identifierTemplate and requiredStructureTemplate must not both be null";
	public static final String IDENTIFIER_NOT_IN_REQUIREDSTRUCTURE = "ComplianceRuleChecker invalid: identifierTemplate can not be mapped to the requiredStructureTemplate";
	public static final String WHITELISTING_NOT_YET_IMPLEMENTED = "ComplianceRuleChecker invalid: The whitelisting feature is not yet implemented";
	public static final String NO_TEMPLATE_TO_CHECK = "ComplianceRuleChecker invalid: No topology to search in provided";

	public ComplianceCheckingException(String cause) {
		super(cause);
	}
}
