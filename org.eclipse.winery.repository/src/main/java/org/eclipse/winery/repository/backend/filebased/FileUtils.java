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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.file.FileVisitResult.CONTINUE;

public class FileUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);


	/**
	 * Deletes given path. If path a file, it is directly deleted. If it is a
	 * directory, the directory is recursively deleted.
	 *
	 * Does not try to change read-only files to read-write files
	 *
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
	 * Creates the given directory including its parent directories, if they do
	 * not exist.
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

	// public static Response readContentFromFile(RepositoryFileReference ref) {
	// try {
	// Repository.INSTANCE.readContentFromFile(ref);
	// }
	// }

}
