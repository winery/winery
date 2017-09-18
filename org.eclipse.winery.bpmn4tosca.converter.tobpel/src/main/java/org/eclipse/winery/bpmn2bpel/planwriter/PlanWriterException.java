/*******************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Sebastian Wagner - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.bpmn2bpel.planwriter;

public class PlanWriterException extends Exception {

	public PlanWriterException() {
	}

	public PlanWriterException(String message) {
		super(message);
	}

	public PlanWriterException(Throwable cause) {
		super(cause);
	}

	public PlanWriterException(String message, Throwable cause) {
		super(message, cause);
	}

	public PlanWriterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
