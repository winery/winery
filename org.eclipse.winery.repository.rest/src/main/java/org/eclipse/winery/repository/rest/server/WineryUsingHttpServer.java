/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
package org.eclipse.winery.repository.rest.server;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.AbstractFileBasedRepository;
import org.eclipse.winery.repository.rest.Prefs;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WineryUsingHttpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WineryUsingHttpServer.class);

    public static Server createHttpServer(int port) {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/winery");
        addServlet(context);
        Server server = new Server(port);
        server.setHandler(context);
        return server;
    }

    /**
     * Creates a server for the REST backend on URL localhost:8080/winery
     */
    public static Server createHttpServer() {
        return createHttpServer(8080);
    }

    private static void addServlet(ServletContextHandler context) {
        // Add the filter, and then use the provided FilterHolder to configure it
        FilterHolder cors = context.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,HEAD,OPTIONS");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

        // this mirrors org.eclipse.winery.repository.rest\src\main\webapp\WEB-INF\web.xml
        ServletHolder h = context.addServlet(ServletContainer.class, "/*");
        h.setInitParameter("jersey.config.server.provider.packages",
            "org.eclipse.winery.repository.rest.resources");
        h.setInitParameter("jersey.config.server.provider.classnames",
            "org.glassfish.jersey.logging.LoggingFeature," +
                "org.glassfish.jersey.media.multipart.MultiPartFeature," +
                "org.eclipse.winery.common.json.JsonFeature"
        );

        //context.addFilter(RequestLoggingFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        context.addServlet(DefaultServlet.class, "/");

        h.setInitOrder(1);
    }

    public static void main(String[] args) throws Exception {
        // Initialize repository
        new Prefs(true);

        Server server = createHttpServer();
        server.start();

        IRepository repository = RepositoryFactory.getRepository();
        if (repository instanceof AbstractFileBasedRepository) {
            LOGGER.debug("Using path " + repository.getRepositoryRoot());
        } else {
            LOGGER.debug("Repository is not filebased");
        }

        // Waits until server is finished.
        // Will never happen, thus user has to press Ctrl+C.
        // See also https://stackoverflow.com/a/14981621/873282.
        server.join();
    }
}
