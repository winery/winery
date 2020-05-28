/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
import java.net.URL;

import org.apache.commons.io.IOUtils;

/**
 * Provides access to an entry type that represents a remote file referenced using a valid URL.
 */
public class RemoteRefBasedCsarEntry implements CsarEntry {
    public final URL url;

    public RemoteRefBasedCsarEntry(URL url) {
        this.url = url;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return url.openStream();
    }

    @Override
    public void writeToOutputStream(OutputStream outputStream) throws IOException {
        try (InputStream is = this.getInputStream()) {
            IOUtils.copy(is, outputStream);
        }
    }

    public URL getUrl() {
        return url;
    }
}
