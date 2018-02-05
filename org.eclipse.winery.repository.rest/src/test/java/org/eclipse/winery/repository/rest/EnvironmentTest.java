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
package org.eclipse.winery.repository.rest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.eclipse.winery.repository.configuration.Environment;
import org.eclipse.winery.repository.configuration.FileBasedRepositoryConfiguration;
import org.junit.Assert;
import org.junit.Test;

public class EnvironmentTest {

	@Test
	public void environmentProvidesDataFromMinimalWineryProperties() throws Exception {
		final Optional<FileBasedRepositoryConfiguration> filebasedRepositoryConfiguration = Environment
			.getFilebasedRepositoryConfiguration();
		Assert.assertTrue(filebasedRepositoryConfiguration.isPresent());
		final Path repositoryPath = filebasedRepositoryConfiguration.get().getRepositoryPath().get();
		Assert.assertEquals(Paths.get("/tmp/winery-repository"), repositoryPath);
	}
}
