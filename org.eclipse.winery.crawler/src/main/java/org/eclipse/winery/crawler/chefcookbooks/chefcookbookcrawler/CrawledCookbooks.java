/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.crawler.chefcookbooks.chefcookbookcrawler;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;

public class CrawledCookbooks {

    /**
     * Get all cookbook folders from the cookbooks directory.
     *
     * @return Returns the directorys of all available cookbooks in the directory.
     */
    public static String[] getDirectories(String directory) {
        File file = new File(directory);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        return directories;
    }

    public static void copyFolder(File sourceFolder, File destinationFolder) throws IOException {

        if (sourceFolder.isDirectory()) {
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir();
            }

            String files[] = sourceFolder.list();

            for (String file : files) {
                File srcFile = new File(sourceFolder, file);
                File destFile = new File(destinationFolder, file);
                copyFolder(srcFile, destFile);
            }
        } else {
            Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Delete a file.
     *
     * @param cookbookPath This is the path to the file which is going to deleted.
     */
    public static void deleteFile(String cookbookPath) {
        File cookbookDir = new File(cookbookPath);
        try {
            FileUtils.forceDelete(cookbookDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean fileExists(String filepath) {
        File tmpDir = new File(filepath);
        boolean exists = tmpDir.exists();
        return exists;
    }
}
