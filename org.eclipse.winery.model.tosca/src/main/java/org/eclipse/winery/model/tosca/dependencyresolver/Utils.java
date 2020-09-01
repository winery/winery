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

package org.eclipse.winery.model.tosca.dependencyresolver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {

    /**
     * returns length of path, i.o. number of directories
     *
     * @param file
     * @return length
     */
    static public int getPathLength(final String file) {

        if (file == null) {
            throw new NullPointerException();
        }
        int count = 0;
        String path = new File(file).getParent();
        while (path != null) {
            path = new File(path).getParent();
            count++;
        }
        return count;
    }

    /**
     * Create file and all parent folders with given content
     *
     * @param filename
     * @param content
     * @throws IOException
     */
    static public void createFile(final String filename, final String content) throws IOException {
        if (new File(filename).getParent() != null) {
            new File(new File(filename).getParent()).mkdirs();
        }
        new File(filename).delete();
        final FileWriter bw = new FileWriter(filename);
        bw.write(content);
        bw.close();
    }

    public static String correctName(final String name) {
        // return name;
        return name.replace('%', 'P').replace(':', '_').replace('+', 'p').replace('/', '_').replace('.', '_');
    }

    public static String readAllBytesJava7(final String filePath) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
        return content;
    }
}
