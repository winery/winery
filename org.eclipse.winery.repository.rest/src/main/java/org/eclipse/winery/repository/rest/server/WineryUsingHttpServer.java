/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.FilebasedRepository;
import org.eclipse.winery.repository.rest.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;

public class WineryUsingHttpServer {

    public static final int REPOSITORY_UI_PORT = 4200;

    private static final Logger LOGGER = LoggerFactory.getLogger(WineryUsingHttpServer.class);

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

    /**
     * Starts the repository UI on port {@value #REPOSITORY_UI_PORT}
     */
    public static Server createHttpServerForRepositoryUi() throws Exception {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(REPOSITORY_UI_PORT);
        server.setConnectors(new Connector[] {connector});
        ResourceHandler rh0 = new ResourceHandler();
        ContextHandler context0 = new ContextHandler();
        context0.setContextPath("/");
        // Path indexHtmlPath = MavenTestingUtils.getProjectFilePath("../org.eclipse.winery.repository.ui/dist/index.html").getParent();
        Path indexHtmlPath = MavenTestingUtils.getBasePath().resolve("org.eclipse.winery.repository.ui").resolve("dist");
        if (Files.exists(indexHtmlPath)) {
            LOGGER.debug("Serving UI from " + indexHtmlPath.toString());
        } else {
            // not sure, why we sometimes have to use `getParent()`.
            indexHtmlPath = MavenTestingUtils.getBasePath().getParent().resolve("org.eclipse.winery.repository.ui").resolve("dist");
            if (Files.exists(indexHtmlPath)) {
                LOGGER.debug("Serving UI from " + indexHtmlPath.toString());
            } else {
                LOGGER.error("Path does not exist " + indexHtmlPath);
            }
        }
        context0.setBaseResource(Resource.newResource(indexHtmlPath.toFile()));
        context0.setHandler(rh0);
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] {context0});

        server.setHandler(contexts);

        return server;
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

        //context.addFilter(RequestLoggingFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        context.addServlet(DefaultServlet.class, "/");

        h.setInitOrder(1);
    }

    /**
     * When in IntelliJ, /tmp/winery-repository is used. See /src/test/resources/winery.properties
     */
    public static void main(String[] args) throws Exception {
        // initialize repository
        new Prefs(true);

        Server server = createHttpServer();
        server.start();

        Server uiServer = createHttpServerForRepositoryUi();
        uiServer.start();

        IRepository repository = RepositoryFactory.getRepository();
        if (repository instanceof FilebasedRepository) {
            LOGGER.debug("Using path " + ((FilebasedRepository) repository).getRepositoryRoot());
        } else {
            LOGGER.debug("Repository is not filebased");
        }

        server.join();
        uiServer.join();
    }
}
