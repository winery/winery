/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.backend.filebased.model;

import java.nio.file.Path;

public class ConvertedPath {

    private Path originalPath;
    private Path convertedPath;

    public ConvertedPath(Path originalPath, Path convertedPath) {
        this.originalPath = originalPath;
        this.convertedPath = convertedPath;
    }

    public ConvertedPath() {
    }

    public Path getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(Path originalPath) {
        this.originalPath = originalPath;
    }

    public Path getConvertedPath() {
        return convertedPath;
    }

    public void setConvertedPath(Path convertedPath) {
        this.convertedPath = convertedPath;
    }
}
