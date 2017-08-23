/**
 * Copyright (c) 2017 University of Stuttgart. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 and the Apache License 2.0 which both accompany this
 * distribution, and are available at http://www.eclipse.org/legal/epl-v10.html and
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors: 
 * - Michael Wurster - initial API and implementation
 */
package org.eclipse.winery.repository.rest;

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
		Assert.assertEquals("/tmp/winery-repository",
			filebasedRepositoryConfiguration.get().getRepositoryPath());
	}
}
