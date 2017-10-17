/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest;

import java.io.IOException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;

/**
 * This class is a stripped-down version of WineryUsingHttpServer. The server is required for testing only. If no code
 * duplication was desired, a new project "org.eclipse.winery.repository.rest.test" had to be created. This is much more
 * effort and hard to explain to newcomers. Therefore, we "just" maintain a copy of the class here.
 */
public class WineryUsingHttpServerForTesting {

	public static Server createHttpServer(int port) throws IOException {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/winery");
		addServlet(context, "");
		Server server = new Server(port);
		server.setHandler(context);
		return server;
	}

	/**
	 * Creates a server for the REST backend on URL localhost:8080/winery
	 */
	public static Server createHttpServer() throws IOException {
		return createHttpServer(8080);
	}

	private static void addServlet(ServletContextHandler context, String s) {
		// Add the filter, and then use the provided FilterHolder to configure it
		FilterHolder cors = context.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
		cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,HEAD");
		cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

		ServletHolder h = context.addServlet(com.sun.jersey.spi.container.servlet.ServletContainer.class, "/*");
		h.setInitParameter("com.sun.jersey.config.property.packages", "org.eclipse.winery.repository.rest.resources");
		h.setInitParameter("com.sun.jersey.config.feature.FilterForwardOn404", "false");
		h.setInitParameter("com.sun.jersey.config.feature.CanonicalizeURIPath", "true");
		h.setInitParameter("com.sun.jersey.config.feature.NormalizeURI", "true");
		h.setInitParameter("com.sun.jersey.config.feature.Redirect", "true");
		h.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
		h.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");

		h.setInitOrder(1);
	}
}
