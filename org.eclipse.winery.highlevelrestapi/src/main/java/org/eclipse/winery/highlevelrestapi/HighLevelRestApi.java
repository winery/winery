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

import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;

/**
 * This class wraps HTTP-Method functionality and thereby abstracts from low
 * level code to simplify the usage.
 */
public class HighLevelRestApi {

	/**
	 * This method implements the HTTP Put Method
	 *
	 * @param uri Resource URI
	 * @param requestPayload Content which has to be put into the Resource
	 * @return ResponseCode of HTTP Interaction
	 */
	@SuppressWarnings("deprecation")
	public static HttpResponseMessage Put(String uri, String requestPayload, String acceptHeaderValue) {

		PutMethod method = new PutMethod(uri);
		// requestPayload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		// requestPayload;

		HighLevelRestApi.setAcceptHeader(method, acceptHeaderValue);
		method.setRequestBody(requestPayload);

		HttpResponseMessage responseMessage = LowLevelRestApi.executeHttpMethod(method);

		// kill <?xml... in front of response
		HighLevelRestApi.cleanResponseBody(responseMessage);

		return responseMessage;
	}

	/**
	 * This method implements the HTTP Post Method
	 *
	 * @param uri Resource URI
	 * @param requestPayload Content which has to be posted into the Resource
	 * @return ResponseCode of HTTP Interaction
	 */
	@SuppressWarnings("deprecation")
	public static HttpResponseMessage Post(String uri, String requestPayload, String acceptHeaderValue) {

		PostMethod method = null;
		if (uri.contains("?")) {
			System.out.println("Found query trying to split");
			String[] split = uri.split("\\?");
			System.out.println("Raw URI part: " + split[0]);
			System.out.println("Raw Query part: " + split[1]);
			method = new PostMethod(split[0]);
			method.setQueryString(HighLevelRestApi.createNameValuePairArrayFromQuery(split[1]));
		} else {
			method = new PostMethod(uri);
			;
		}
		method.setRequestBody(requestPayload);
		HighLevelRestApi.setAcceptHeader(method, acceptHeaderValue);
		HttpResponseMessage responseMessage = LowLevelRestApi.executeHttpMethod(method);
		HighLevelRestApi.cleanResponseBody(responseMessage);
		return responseMessage;
	}

	/**
	 * This method implements the HTTP Get Method
	 *
	 * @param uri Resource URI
	 * @return Content represented by the Resource URI
	 */
	public static HttpResponseMessage Get(String uri, String acceptHeaderValue) {
		System.out.println("Setting URI to: \n");
		System.out.println(uri);
		GetMethod method = null;
		if (uri.contains("?")) {
			System.out.println("Found query trying to split");
			String[] split = uri.split("\\?");
			System.out.println("Raw URI part: " + split[0]);
			System.out.println("Raw Query part: " + split[1]);
			method = new GetMethod(split[0]);
			method.setQueryString(HighLevelRestApi.createNameValuePairArrayFromQuery(split[1]));
		} else {
			method = new GetMethod(uri);
		}
		HighLevelRestApi.setAcceptHeader(method, acceptHeaderValue);
		HttpResponseMessage responseMessage = LowLevelRestApi.executeHttpMethod(method);
		HighLevelRestApi.cleanResponseBody(responseMessage);
		return responseMessage;
	}

	private static NameValuePair[] createNameValuePairArrayFromQuery(String query) {
		// example:
		// csarID=Moodle.csar&serviceTemplateID={http://www.example.com/tosca/ServiceTemplates/Moodle}Moodle&nodeTemplateID={http://www.example.com/tosca/ServiceTemplates/Moodle}VmApache
		System.out.println("Splitting query: " + query);
		String[] pairs = query.trim().split("&");
		NameValuePair[] nameValuePairArray = new NameValuePair[pairs.length];
		int count = 0;
		for (String pair : pairs) {
			System.out.println("Splitting query pair: " + pair);
			String[] keyValue = pair.split("=");
			NameValuePair nameValuePair = new NameValuePair();
			System.out.println("Key: " + keyValue[0] + " Value: " + keyValue[1]);
			nameValuePair.setName(keyValue[0]);
			nameValuePair.setValue(keyValue[1]);
			nameValuePairArray[count] = nameValuePair;
			count++;
		}
		return nameValuePairArray;
	}

	/**
	 * This method implements the HTTP Delete Method
	 *
	 * @param uri Resource URI
	 * @return ResponseCode of HTTP Interaction
	 */
	public static HttpResponseMessage Delete(String uri, String acceptHeaderValue) {

		DeleteMethod method = new DeleteMethod(uri);
		HighLevelRestApi.setAcceptHeader(method, acceptHeaderValue);
		HttpResponseMessage responseMessage = LowLevelRestApi.executeHttpMethod(method);
		HighLevelRestApi.cleanResponseBody(responseMessage);
		return responseMessage;
	}

	private static void setAcceptHeader(HttpMethodBase method, String value) {
		if (!value.equals("")) {
			method.setRequestHeader("Accept", value);
		} else {
			method.setRequestHeader("Accept", "application/xml");
		}
	}

	private static void cleanResponseBody(HttpResponseMessage responseMessage) {
		System.out.println("ResponseBody: \n");
		System.out.println(responseMessage.getResponseBody());
		// @formatter:off
		String temp = responseMessage
				.getResponseBody()
				.replace(
						"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>",
						"");
		// @formatter:on
		responseMessage.setResponseBody(temp);
	}
}
