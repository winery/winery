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

/**
 * The DiagnosticsSetter class represents a diagnostic message with details about an error
 * such as its line and column positions, the message, the problem description, and the context.
 */
package org.eclipse.winery.lsp.Server.ServerCore.Validation;

public class DiagnosticsSetter {
    private int ErrorLine;
    private int ErrorColumn = 0;
    private int ErrorEndColumn = 1;
    private String ErrorMessage;
    private String ErrorProblem;
    private String ErrorContext;
    
    public int getErrorEndColumn() {
        return ErrorEndColumn;
    }

    public void setErrorEndColumn(int errorEndColumn) {
        ErrorEndColumn = errorEndColumn;
    }

    public int getErrorLine() {
        return ErrorLine;
    }

    public void setErrorLine(int errorLine) {
        ErrorLine = errorLine;
    }

    public int getErrorColumn() {
        return ErrorColumn;
    }

    public void setErrorColumn(int errorColumn) {
        ErrorColumn = errorColumn;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public String getErrorProblem() {
        return ErrorProblem;
    }

    public void setErrorProblem(String errorProblem) {
        ErrorProblem = errorProblem;
    }

    public String getErrorContext() {
        return ErrorContext;
    }

    public void setErrorContext(String errorContext) {
        ErrorContext = errorContext;
    }
}
