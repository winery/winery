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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

/**
 * This static-class eases HTTP-method execution by self-managed fault-handling
 * and automated Response-information processing
 */
public class LowLevelRestApi {

    // Local HttpClient used for every communication (Singleton implementation)
    private static HttpClient httpClient = new HttpClient();


    /**
     * Executes a passed HttpMethod (Method type is either PUT, POST, GET or
     * DELETE) and returns a HttpResponseMessage
     *
     * @param method Method to execute
     * @return HttpResponseMessage which contains all information about the
     * execution
     */
    public static HttpResponseMessage executeHttpMethod(HttpMethod method) {

        HttpResponseMessage responseMessage = null;

        try {
            System.out.println("Method invocation on URI: \n");
            System.out.println(method.getURI().toString());
            // Execute Request
            LowLevelRestApi.httpClient.executeMethod(method);
            responseMessage = LowLevelRestApi.extractResponseInformation(method);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            // Release Connection anyway
            method.releaseConnection();
        }

        // Extract response information and return
        return responseMessage;
    }

    /**
     * Extracts the response information from an executed HttpMethod
     *
     * @param method Executed Method
     * @return Packaged response information
     */
    private static HttpResponseMessage extractResponseInformation(HttpMethod method) {
        // Create and return HttpResponseMethod
        HttpResponseMessage responseMessage = new HttpResponseMessage();
        responseMessage.setStatusCode(method.getStatusCode());
        try {
            responseMessage.setResponseBody(method.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseMessage;

    }

}
