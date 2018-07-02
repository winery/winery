/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.exceptions;

import org.eclipse.winery.common.exceptions.WineryException;

/**
 * Base exception for all repository exceptions
 * <p>
 * To be used if a sub-class is not worth generating
 */
public class WineryRepositoryException extends WineryException {

    public WineryRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public WineryRepositoryException(String message) {
        super(message);
    }
}
