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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import static com.google.common.collect.Lists.newArrayList;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComplianceRuleCheckingResult")
public class ServiceTemplateCheckingResult {

	@XmlElement(name = "satisfied")
	public List<QName> satisfied;

	@XmlElement(name = "unsatisfied")
	public List<QName> unsatisfied;

	@XmlElement(name = "exception")
	public List<QName> exception;

	public ServiceTemplateCheckingResult() {
	}

	public ServiceTemplateCheckingResult(List<QName> satisfied, List<QName> unsatisfied, List<QName> exception) {
		this.satisfied = satisfied;
		this.unsatisfied = unsatisfied;
		this.exception = exception;
	}

	public List<QName> getSatisfied() {
		if (satisfied == null) {
			satisfied = newArrayList();
		}
		return satisfied;
	}

	public List<QName> getUnsatisfied() {
		if (unsatisfied == null) {
			unsatisfied = newArrayList();
		}
		return unsatisfied;
	}

	public List<QName> getException() {
		if (exception == null) {
			exception = newArrayList();
		}
		return exception;
	}
}
