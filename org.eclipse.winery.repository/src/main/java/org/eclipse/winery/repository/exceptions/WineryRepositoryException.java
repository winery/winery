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
package org.eclipse.winery.repository.exceptions;

import org.eclipse.winery.common.exceptions.WineryException;

/**
 * Base exception for all repository exceptions
 */
public abstract class WineryRepositoryException extends WineryException {
	
	public WineryRepositoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public WineryRepositoryException(String message) {
		super(message);
	}
}
