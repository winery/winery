/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
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
