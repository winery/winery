/*******************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.lsp.Server.ServerAPI.API;

public interface ClientLogManager {
    /**
    * This class provides a set of generic APIs to the server to 
    * publish various types of logs.
    * <p>
    * When we publish diagnostics via protocol operations, we 
    * need to generate a specific set of data models which is common. Therefore, a single 
    * logger instance is going to be used within the server’s core implementation. 
    * </p>
    *
    */    
    
    /**
     * Log an Info message to the client.
     *
     * @param message {@link String}
     */
    void publishInfo(String message);

    /**
     * Log a Log message to the client.
     *
     * @param message {@link String}
     */
    void publishLog(String message);

    /**
     * Log an Error message to the client.
     *
     * @param message {@link String}
     */
    void publishError(String message);

    /**
     * Log a Warning message to the client.
     *
     * @param message {@link String}
     */
    void publishWarning(String message);

    /**
     * Show an error message to the client.
     *
     * @param message message to be shown
     */
    void showErrorMessage(String message);

    /**
     * Show an info message to the client.
     *
     * @param message message to be shown
     */
    void showInfoMessage(String message);

    /**
     * Show a log message to the client.
     *
     * @param message message to be shown
     */
    void showLogMessage(String message);
}
