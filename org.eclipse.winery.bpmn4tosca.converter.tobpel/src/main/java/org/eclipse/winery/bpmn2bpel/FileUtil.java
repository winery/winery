/*******************************************************************************
 * Copyright (c) 2015-2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.bpmn2bpel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class providing operations on files.
 */
public class FileUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

	public static Path createTempDir(String subdirectory) throws IOException {

		String systemTmpDir = System.getProperty("java.io.tmpdir");
		Path tempDirPath = Paths.get(systemTmpDir, subdirectory);
		if (Files.notExists(tempDirPath)) {
			return Files.createDirectory(tempDirPath);
		} else {
			return tempDirPath;
		}

	}

	public static Path writeStringToFile(String content, Path targetPath) throws IOException {
		return Files.write(targetPath, content.getBytes(), StandardOpenOption.CREATE_NEW);
	}

	public static Path createApacheOdeProcessArchive(Path zipFilePath, List<Path> filePaths) throws IOException {
		LOGGER.debug("Creating BPEL process archive for Apache ODE");

		URI uri = URI.create("jar:file:" + zipFilePath.toUri().getPath());

		Map<String, String> env = new HashMap<String, String>();
		env.put("create", "true");

		try (FileSystem zipFileSystem = FileSystems.newFileSystem(uri, env)) {

			/* Iterate over files and add them to the zip */
			for (Path src : filePaths) {
				if (!Files.isDirectory(src)) {
					LOGGER.debug("Adding file " + src.getFileName() + " to process archive");
					Path dest = zipFileSystem.getPath(src.getFileName().toString());
					Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
		return zipFilePath;
	}

	public static void deleteFiles(List<Path> filePaths) throws IOException {
		for (Path src : filePaths) {
			if (!Files.isDirectory(src)) {
				LOGGER.debug("Deleting file " + src.toAbsolutePath());
				Files.delete(src);
			}
		}
	}

}
