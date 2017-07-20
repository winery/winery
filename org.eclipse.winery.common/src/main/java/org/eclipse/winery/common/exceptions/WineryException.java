/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.winery.common.exceptions;

/**
 * Base exception for all exceptions
 */
public abstract class WineryException extends Exception {
	
	public WineryException(String message, Throwable cause) {
		super(message, cause);
	}

	public WineryException(String message) {
		super(message);
	}
}

