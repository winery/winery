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
package org.eclipse.winery.repository.backend;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for low-level repository administration
 */
public interface IRepositoryAdministration {

	/**
	 * Dumps the content of the repository to the given output stream
	 *
	 * @param out stream to use to dump the data to. Currently, a ZIP output
	 *            stream is returned.
	 */
	void doDump(OutputStream out) throws IOException;

	/**
	 * Removes all data
	 */
	void doClear();

	/**
	 * Imports the content of the given stream into the repsotiry.
	 *
	 * @param in the stream to use. Currently, only ZIP input is supported.
	 */
	void doImport(InputStream in);
}
