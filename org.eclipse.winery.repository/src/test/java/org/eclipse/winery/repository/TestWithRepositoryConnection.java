/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository;

import java.io.IOException;

import org.junit.BeforeClass;

/**
 * @deprecated Switch to AbstractResourceTest
 */
@Deprecated
public abstract class TestWithRepositoryConnection {

	@BeforeClass
	public static void connectToProvider() throws IOException {
		// Initialize preferences
		// We do not need them, but constructing them has the side effect that Repository.INSTANCE is != null
		new PrefsTestEnabledUsingConfiguredRepository();
	}

}
