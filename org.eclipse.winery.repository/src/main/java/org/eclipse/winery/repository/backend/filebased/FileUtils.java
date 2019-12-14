/*******************************************************************************
 * Copyright (c) 2012-2019 Contributors to the Eclipse Foundation
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
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.EnumSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.file.FileVisitResult.CONTINUE;

public class FileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Deletes given path. If path a file, it is directly deleted. If it is a directory, the directory is recursively
     * deleted.
     * <p>
     * Does not try to change read-only files to read-write files
     * <p>
     * Only uses Java7's nio, does not fall back to Java6.
     *
     * @param path the path to delete
     */
    public static void forceDelete(Path path) {
        if (Files.isDirectory(path)) {
            try {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            FileUtils.LOGGER.debug("Could not delete file", e.getMessage());
                        }
                        return CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        if (exc == null) {
                            try {
                                Files.delete(dir);
                            } catch (IOException e) {
                                FileUtils.LOGGER.debug("Could not delete dir", e);
                            }
                            return CONTINUE;
                        } else {
                            FileUtils.LOGGER.debug("Could not delete file", exc);
                            return CONTINUE;
                        }
                    }
                });
            } catch (IOException e) {
                FileUtils.LOGGER.debug("Could not delete dir", e);
            }
        } else {
            try {
                Files.delete(path);
            } catch (IOException e) {
                FileUtils.LOGGER.debug("Could not delete file", e.getMessage());
            }
        }
    }

    /**
     * Creates the given directory including its parent directories, if they do not exist.
     */
    public static void createDirectory(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent == null) {
            throw new IOException("No parent found");
        }
        if (!Files.exists(parent)) {
            FileUtils.createDirectory(parent);
        }
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
    }

    /**
     * Copy every file from source to target except those listed in ignoreFiles
     */
    public static void copyFiles(Path source, Path target, List<String> ignoreFiles) {
        if (Files.isDirectory(source) && Files.isDirectory(target)) {
            try {
                Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

                    @Override
                    public synchronized FileVisitResult preVisitDirectory(Path file, BasicFileAttributes attrs) {
                        Path newDir = target.resolve(source.relativize(file));

                        if (ignoreFiles.contains(file.getFileName().toString())) {
                            return FileVisitResult.SKIP_SUBTREE;
                        } else {
                            try {
                                Files.copy(file, newDir);
                            } catch (FileAlreadyExistsException ex) {
                                return FileVisitResult.CONTINUE;
                            } catch (IOException ex) {
                                return FileVisitResult.SKIP_SUBTREE;
                            }
                        }

                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path dir, BasicFileAttributes attrs) throws IOException {
                        Path newFile = target.resolve(source.relativize(dir));

                        if (ignoreFiles.contains(dir.getFileName().toString())) {
                            return FileVisitResult.SKIP_SUBTREE;
                        } else {
                            try {
                                Files.copy(dir, newFile);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }

                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        if (exc == null) {
                            Path newDir = target.resolve(source.relativize(dir));
                            try {
                                FileTime time = Files.getLastModifiedTime(dir);
                                Files.setLastModifiedTime(newDir, time);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                FileUtils.LOGGER.debug("Could not copy dir", e);
            }
        } else {
            try {
                Files.delete(source);
            } catch (IOException e) {
                FileUtils.LOGGER.debug("Could not copy file", e.getMessage());
            }
        }
    }

    /**
     * Delete every File in the given path except those listed in ignoreFiles
     */
    public static void deleteFiles(Path path, List<String> ignoreFiles) {
        FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                try {

                    if (!ignoreFiles.contains(file.getFileName().toString())) {
                        forceDelete(file);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return FileVisitResult.CONTINUE;
            }
        };

        try {
            Files.walkFileTree(path, EnumSet.noneOf(FileVisitOption.class), 1, fv);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
