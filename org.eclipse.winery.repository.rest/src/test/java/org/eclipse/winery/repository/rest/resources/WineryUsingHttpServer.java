/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources;

import java.io.IOException;

import org.eclipse.winery.repository.rest.Prefs;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class WineryUsingHttpServer {

	public static Server createHttpServer(int port) throws IOException {

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/winery");
		addServlet(context, "");

		// TODO serve jsp - maybe https://www.eclipse.org/jetty/documentation/current/embedding-jetty.html could be of help
/*
		String[] subs = {"imports", "servicetemplates", "nodetypes", "nodetypeimplementations", "relationshiptypes", "relationshiptypeimplementations", "requirementtypes", "capabilitytypes", "artifacttypes", "artifacttemplates", "policytypes", "policytempaltes", "admin", "API", "other", "test"};
		Arrays.stream(subs).forEach(s -> {
		});
*/

		Server server = new Server(port);
		server.setHandler(context);
		return server;
	}

	public static Server createHttpServer() throws IOException {
		return createHttpServer(8080);
	}

	private static void addServlet(ServletContextHandler context, String s) {
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

	public static void main(String[] args) throws Exception {
		// initialize repository
		new Prefs(true);

		Server server = createHttpServer();

		server.start();
		server.join();
	}

}
