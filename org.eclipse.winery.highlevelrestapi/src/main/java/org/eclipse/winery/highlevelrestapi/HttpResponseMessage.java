/*******************************************************************************
 * Copyright (c) 2013 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.highlevelrestapi;

/**
 */
public class HttpResponseMessage {

    private int statusCode;
    private String responseBody;


    /**
     * @return the statusCode
     */
    public int getStatusCode() {
        return this.statusCode;
    }

    /**
     * @param statusCode the statusCode to set
     */
    protected void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @return the responseBody
     */
    public String getResponseBody() {
        return this.responseBody;
    }

    /**
     * @param responseBody the responseBody to set
     */
    protected void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

}
