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

public abstract class PrefsTestEnabled extends Prefs {

	/**
	 * @param initializeRepository true if the repository should be initialized
	 *            as provided in winery.properties
	 */
	protected PrefsTestEnabled(boolean initializeRepository) throws IOException {
		super(initializeRepository);
	}

	@Override
	public String getResourcePath() {
		return "http://www.example.org/winery/test";
	}

}
