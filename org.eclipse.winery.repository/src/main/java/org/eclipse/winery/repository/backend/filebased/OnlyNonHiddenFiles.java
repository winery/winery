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

import org.eclipse.winery.repository.Constants;

/**
 * Only non-hidden files. Also excludes file names ending with
 * Constants.SUFFIX_MIMETYPE
 */
public class OnlyNonHiddenFiles implements DirectoryStream.Filter<Path> {
	
	@Override
	public boolean accept(Path entry) throws IOException {
		// we return only non-hidden files
		// and we do not return the file "FN.mimetype", which are used to store the mimetype of FN
		return !Files.isDirectory(entry) && !Files.isHidden(entry) && (!entry.getFileName().toString().endsWith(Constants.SUFFIX_MIMETYPE));
	}
}
