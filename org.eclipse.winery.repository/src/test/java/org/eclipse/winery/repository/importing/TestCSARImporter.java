/*******************************************************************************
 * Copyright (c) 2012-2013,2015 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.importing;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.repository.PrefsTestEnabledUsingConfiguredRepository;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestCSARImporter {

	/**
	 * Ensure that Repository.INSTANCE exists
	 */
	@BeforeClass
	public static void setupPrefs() throws Exception {
		// Initialize preferences
		// We do not need them directly, but constructing them has the side effect that Repository.INSTANCE is != null
		new PrefsTestEnabledUsingConfiguredRepository();
	}

	/**
	 * Quick hack to test Moodle Import
	 *
	 * Currently, no CSARs are put into the test resources, we rely on local
	 * CSARs
	 */
	@Test
	public void testMoodleImport() throws Exception {
		CSARImporter i = new CSARImporter();
		Path p = FileSystems.getDefault().getPath("C:\\Users\\Oliver\\BTSync\\Projects\\OpenTOSCA\\MoodleInteropCSAR\\trunk");
		List<String> errors = new ArrayList<String>();
		i.importFromDir(p, errors, true, false);
	}
}
