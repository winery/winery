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
import java.net.URI;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

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
        URI uri;
        try {
            uri = url.toURI();
        } catch (Exception e) {
            throw new IOException("Could not determine URI", e);
        }
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        return entity.getContent();
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
