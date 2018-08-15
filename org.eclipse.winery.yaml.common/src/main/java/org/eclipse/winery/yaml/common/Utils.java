/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.yaml.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.winery.repository.backend.filebased.FileUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
    private static Path tmpBase;

    public static String getFile(String path, String name) {
        return path + File.separator + name;
    }

    public static String getFileName(String file) {
        return file.substring(file.lastIndexOf(File.separator) + 1);
    }

    public static String getFileName(File file) {
        return file.getName();
    }

    public static Path unzipFile(InputStream in) {
        Path dir = Utils.getTmpDir(Paths.get("zip"));
        FileUtils.forceDelete(dir);
        try (ZipInputStream inputStream = new ZipInputStream(in)) {
            ZipEntry entry;
            while (Objects.nonNull(entry = inputStream.getNextEntry())) {
                if (!entry.isDirectory()) {
                    Path targetPath = dir.resolve(entry.getName());
                    Files.createDirectories(targetPath.getParent());
                    Files.copy(inputStream, targetPath);
                    LOGGER.debug("Write tmp file: {}", targetPath.toString());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Create zip tmp file error: ", e);
        }
        return dir;
    }

    public static @Nullable InputStream zipPath(@NonNull Path path) {
        File zipFile = new File(path.getParent().toString() + File.separator + "tmp.zip");
        try (
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos)
        ) {
            Files.walk(path)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        zos.putNextEntry(new ZipEntry(path.relativize(file).toString()));
                        Files.copy(file, zos);
                        zos.closeEntry();
                    } catch (Exception e) {

                    }
                });
            return new FileInputStream(zipFile);
        } catch (Exception e) {
            LOGGER.error("Create zip tmp file error: ", e);
        }
        return null;
    }

    /**
     * @return null in the case of an exception
     */
    public static @Nullable Path getTmpDir(Path path) {
        try {
            if (Objects.isNull(tmpBase)) {
                tmpBase = Files.createTempDirectory("winery");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to create tmp dir with name 'winery'", e);
        }

        Path result = tmpBase.resolve(path);
        try {
            Files.createDirectories(result);
        } catch (IOException e) {
            LOGGER.error("Failed to create tmp dir '{}':\n {}", result, e);
        }
        return result;
    }

    public static void deleteTmpDir(Path path) {
        try {
            Files.deleteIfExists(tmpBase);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] getHashValueOfFile(File file) throws IOException {
        return DigestUtils.md5(new FileInputStream(file));
    }
}
