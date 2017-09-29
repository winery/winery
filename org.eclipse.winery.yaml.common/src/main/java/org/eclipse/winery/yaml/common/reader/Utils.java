/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Christoph Kleine - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.yaml.common.reader;

import java.io.File;

public class Utils {
    // TODO get file (relative, absolute, ...)
    public static String getFile(String path, String name) {
        return path + File.separator + name;
    }

    public static String getFileName(String file) {
        return file.substring(file.lastIndexOf(File.separator) + 1);
    }
}
