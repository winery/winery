/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.winery.repository.backend.filebased.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

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
        Path dir = getTmpDir("zip").toPath();
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

    public static InputStream zipPath(Path path) {
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

    public static File getTmpDir(String name) {
        try {
            Path path = Files.createTempDirectory("winery");
            File file = new File(path.getParent().toString() + File.separator + "winery" + File.separator + name);
            path.toFile().delete();

            if (file.exists()) deleteTmpDir(file);
            if (!file.exists()) file.mkdirs();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteTmpDir(File file) {
        FileUtils.forceDelete(file.toPath());
    }
}
