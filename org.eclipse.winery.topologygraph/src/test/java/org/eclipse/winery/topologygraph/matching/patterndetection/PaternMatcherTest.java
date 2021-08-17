/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.topologygraph.matching.patterndetection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.winery.common.configuration.Environments;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PaternMatcherTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ToscaBehaviorPatternMatcherTest.class);
    static String workspaceRepositoryRoot;
    static boolean workspaceRepositoryTenant;

    /**
     * This sets the repository root to a test directory where the multirepository will be temporarily be initialized.
     * This additionaly saves the workspace repository root to set it back later.
     */
    @BeforeAll
    static void getRepositoryRootBeforeTestAndSetUpTestRoot() {
        workspaceRepositoryRoot = Environments.getInstance().getRepositoryConfig().getRepositoryRoot();
        workspaceRepositoryTenant = Environments.getInstance().getRepositoryConfig().isTenantRepository();
        Path testRepositoryRoot = Paths.get(System.getProperty("java.io.tmpdir")).resolve("test-repository");
        if (!Files.exists(testRepositoryRoot)) {
            try {
                Files.createDirectory(testRepositoryRoot);
            } catch (IOException ioex) {
                LOGGER.debug("Error while creating test directory", ioex);
            }
        } else {
            try {
                FileUtils.cleanDirectory(testRepositoryRoot.toFile());
            } catch (IOException e) {
                LOGGER.debug("Error clearing out the test directory before test execution.", e);
            }
        }
        Environments.getInstance().getRepositoryConfig().setRepositoryRoot(testRepositoryRoot.toString());
        Environments.getInstance().getRepositoryConfig().setTenantRepository(false);
    }

    /**
     * This sets the repository root in the config back to the workspace repository root.
     */
    @AfterAll
    static void setRepositoryRootBack() {
        try {
            FileUtils.deleteDirectory(new File(Environments.getInstance().getRepositoryConfig().getRepositoryRoot()));
        } catch (IOException e) {
            LOGGER.error("Error while cleanup. Could not delete temporary directory.", e);
        }
        Environments.getInstance().getRepositoryConfig().setRepositoryRoot(workspaceRepositoryRoot);
        Environments.getInstance().getRepositoryConfig().setTenantRepository(workspaceRepositoryTenant);
    }
}
