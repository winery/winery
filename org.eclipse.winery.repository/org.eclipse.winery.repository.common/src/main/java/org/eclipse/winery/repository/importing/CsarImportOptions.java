/*******************************************************************************
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
 *******************************************************************************/
package org.eclipse.winery.repository.importing;

/**
 * Contains the set of options applicable while importing a CSAR.
 */
public class CsarImportOptions {
    /**
     * if true: Contents of the repository are allowed to be overwritten if necessary.
     */
    private boolean overwrite;

    /**
     * if tru: WPD should be parsed asynchronously to speed up the import. Required, because
     * JUnit terminates the used ExecutorService.
     */
    private boolean asyncWPDParsing;

    /**
     * if true: Validates the CSAR being imported with the state stored in the accountability layer.
     */
    private boolean validate;

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public boolean isAsyncWPDParsing() {
        return asyncWPDParsing;
    }

    public void setAsyncWPDParsing(boolean asyncWPDParsing) {
        this.asyncWPDParsing = asyncWPDParsing;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }
}
