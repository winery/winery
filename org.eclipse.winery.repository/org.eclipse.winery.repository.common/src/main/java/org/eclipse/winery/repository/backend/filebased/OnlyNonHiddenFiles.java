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
package org.eclipse.winery.repository.backend.filebased;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.winery.common.Constants;

/**
 * Only non-hidden files. Also excludes file names ending with Constants.SUFFIX_MIMETYPE
 */
public class OnlyNonHiddenFiles implements DirectoryStream.Filter<Path> {

    @Override
    public boolean accept(Path entry) throws IOException {
        // we return only non-hidden files
        // and we do not return the file "FN.mimetype", which are used to store the mimetype of FN
        return !Files.isDirectory(entry) && !Files.isHidden(entry) && (!entry.getFileName().toString().endsWith(Constants.SUFFIX_MIMETYPE));
    }
}
