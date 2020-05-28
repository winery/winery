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
package org.eclipse.winery.common;

/**
 * Meta class to handle things, where a String (URI, NCName, ...) may be URLencoded
 */
public class StringEncodedAndDecoded implements Comparable<StringEncodedAndDecoded> {

    private String decoded = null;
    private String encoded = null;

    /**
     * @param uri        the URI to store
     * @param URLencoded true iff the given URI is URLencoded
     */
    public StringEncodedAndDecoded(String uri, boolean URLencoded) {
        if (URLencoded) {
            this.encoded = uri;
        } else {
            this.decoded = uri;
        }
    }

    public String getDecoded() {
        if (this.decoded == null) {
            this.decoded = Util.URLdecode(this.encoded);
        }
        return this.decoded;
    }

    public String getEncoded() {
        if (this.encoded == null) {
            this.encoded = Util.URLencode(this.decoded);
        }
        return this.encoded;
    }

    @Override
    public int hashCode() {
        return this.getDecoded().hashCode();
    }

    /**
     * @return the URL path fragment to be used in an URL
     */
    public String getPathFragment() {
        return this.getEncoded();
    }

    @Override
    public String toString() {
        return this.getDecoded();
    }

    @Override
    public int compareTo(StringEncodedAndDecoded o) {
        return this.getDecoded().compareTo(o.getDecoded());
    }

    /**
     * Compares with the given object. Equality checking is made based on the decoded String
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof String) {
            return this.getDecoded().equals(o);
        } else if (o instanceof StringEncodedAndDecoded) {
            return ((StringEncodedAndDecoded) o).getDecoded().equals(this.getDecoded());
        } else {
            return false;
        }
    }
}
