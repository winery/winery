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
 *******************************************************************************/
package org.eclipse.winery.repository.export.entries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provides access to and metadata about the contents of one of the entries of a csar archive 
 */
public interface CsarEntry {
    /**
     * Opens an input stream that allows reading the contents of the entry
     * @return an open input stream
     * @throws IOException if an error occurs when creating the input stream
     */
    InputStream getInputStream() throws IOException;

    /**
     * Writes the contents of the entry to an output stream.
     * @param outputStream the output stream to write the contents to
     * @throws IOException if an error occurs when writing the contents to the output stream
     */
    void writeToOutputStream(OutputStream outputStream) throws IOException;
    
}
