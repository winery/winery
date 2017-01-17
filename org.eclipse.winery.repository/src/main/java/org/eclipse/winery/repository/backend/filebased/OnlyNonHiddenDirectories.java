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
package org.eclipse.winery.repository.backend.filebased;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class OnlyNonHiddenDirectories implements DirectoryStream.Filter<Path> {

	@Override
	public boolean accept(Path entry) throws IOException {
		// we return only non-hidden directories
		// E.g., DS_Store of Mac OS X is a hidden directory
		return Files.isDirectory(entry) && !Files.isHidden(entry);
	}
}
