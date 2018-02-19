/*******************************************************************************
 * Copyright (c) 2012-2018 Contributors to the Eclipse Foundation
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

import org.eclipse.winery.common.ToscaDocumentBuilderFactory;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.FilebasedRepository;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.configuration.Environment;
import org.eclipse.winery.repository.rest.websockets.GitWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.net.URL;
import java.util.Objects;

public class Prefs implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Prefs.class);

    /**
     * This constructor is called at handling at servlets, too. Therefore, the visibility stays public If testing is
     * needed, an additional Boolean paramater has to be passed (see below)
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

        // emulate behavior of doInitialization(Context)
        URL resource = this.getClass().getClassLoader().getResource("winery.properties");
        LOGGER.debug("URL: {}", resource.toString());
        Environment.copyConfiguration(resource);

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
     * Pre-Condition: Environment is loaded
     */
    private void doRepositoryInitialization() throws Exception {
        RepositoryFactory.reconfigure();
        if (RepositoryFactory.getRepository() instanceof GitBasedRepository) {
            GitWebSocket socket = new GitWebSocket();
            ((GitBasedRepository) RepositoryFactory.getRepository()).registerForEvents(socket);
        }
    }

    /**
     * Initializes Winery using the given context
     */
    private void doInitialization(ServletContext ctx) {
        Objects.requireNonNull(ctx);
        Environment.getUrlConfiguration().setRepositoryApiUrl(ctx.getContextPath());

        // first set default URLs
        // they will be overwritten with the configuration later
        initializeUrlConfigurationWithDefaultValues(ctx);

        // overwrite configuration with local configuration in all cases
        // if winery.property exists in the root of the default repository path (~/winery-repository), load it
        File propFile = new File(FilebasedRepository.getDefaultRepositoryFilePath(), "winery.properties");
        Prefs.LOGGER.info("Trying " + propFile.getAbsolutePath());
        if (propFile.exists()) {
            Prefs.LOGGER.info("Found");
            try {
                Environment.copyConfiguration(propFile.toPath());
            } catch (Exception e) {
                Prefs.LOGGER.error("Could not load repository-local winery.properties", e);
            }
        } else {
            Prefs.LOGGER.info("Not found");
        }

        try {
            this.doRepositoryInitialization();
        } catch (Exception e) {
            LOGGER.error("Could not initialize", e);
        }

        // Initialize XSD validation in the background. Takes up a few seconds.
        // If we do not do it here, the first save by a user takes a few seconds, which is inconvenient
        Prefs.LOGGER.debug("Initializing XML validation");
        @SuppressWarnings("unused")
        ToscaDocumentBuilderFactory tdbf = ToscaDocumentBuilderFactory.INSTANCE;
        Prefs.LOGGER.debug("Initialized XML validation");
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
        Environment.getUrlConfiguration().setTopologyModelerUrl(basePath + "winery-topologymodeler");
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        this.doInitialization(servletContextEvent.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // nothing to do at tear down
    }
}
