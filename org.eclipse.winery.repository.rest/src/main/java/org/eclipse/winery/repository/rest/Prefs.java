/*******************************************************************************
 * Copyright (c) 2012-2019 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest;

import java.util.Objects;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.winery.common.ToscaDocumentBuilderFactory;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.rest.websockets.GitWebSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class Prefs implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Prefs.class);

    // Required for jersey which uses java.util.logging. See https://stackoverflow.com/a/43242620/873282
    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    /**
     * This constructor is called at handling at servlets, too. Therefore, the visibility stays public If testing is
     * needed, an additional Boolean parameter has to be passed (see below)
     */
    public Prefs() {
        // globally use unix line endings - see http://stackoverflow.com/a/6128248/873282
        System.setProperty("line.separator", "\n");
    }

    /**
     * Constructor for Unit testing ONLY!
     * <p>
     * <emph>Do not call! (except from Unit testing code)</emph>
     *
     * @param initializeRepository true if the repository should be initialized as provided in winery.properties
     */
    public Prefs(boolean initializeRepository) throws Exception {
        this();
        if (initializeRepository) {
            this.doRepositoryInitialization();
        }
    }

    /**
     * Initialization code for the repository. Should go into separate class, but being here should be OK for a
     * prototype
     * <p>
     * Called from both the constructor for JUnit and the servlet-based initialization
     * <p>
     */
    private void doRepositoryInitialization() throws Exception {
        RepositoryFactory.reconfigure();
        if (RepositoryFactory.getRepository() instanceof GitBasedRepository) {
            GitWebSocket socket = new GitWebSocket();
            ((GitBasedRepository) RepositoryFactory.getRepository()).registerForEvents(socket);
        }
    }

    /**
     * When the servlet is initialized, this method is called.
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        Objects.requireNonNull(context);

        // first set default URLs
        // they will be overwritten with the configuration later
        initializeUrlConfigurationWithDefaultValues(context);

        try {
            this.doRepositoryInitialization();
        } catch (Exception e) {
            LOGGER.error("Could not initialize", e);
        }

        // Initialize XSD validation in the background. Takes up a few seconds.
        // If we do not do it here, the first save by a user takes a few seconds, which is inconvenient
        LOGGER.debug("Initializing XML validation");
        @SuppressWarnings("unused")
        ToscaDocumentBuilderFactory tdbf = ToscaDocumentBuilderFactory.INSTANCE;
        LOGGER.debug("Initialized XML validation");
    }

    private void initializeUrlConfigurationWithDefaultValues(ServletContext ctx) {
        String basePath = ctx.getContextPath();
        if (basePath.endsWith("/")) {
            basePath = basePath.substring(0, basePath.length() - 1);
        }
        int pos = basePath.lastIndexOf("/");
        if (pos <= 0) {
            basePath = "/";
        } else {
            basePath = basePath.substring(0, pos);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // nothing to do at tear down
    }
}
