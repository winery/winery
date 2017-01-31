/*******************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Sebastian Wagner - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.bpmn2bpel;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class providing operations on files.
 */
public class FileUtil {

	private static Logger log = LoggerFactory.getLogger(FileUtil.class);

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
		log.debug("Creating BPEL process archive for Apache ODE");

		URI uri = URI.create("jar:file:" + zipFilePath.toUri().getPath());

		Map<String, String> env = new HashMap<String, String>();
		env.put("create", "true");

		try (FileSystem zipFileSystem = FileSystems.newFileSystem(uri, env)) {

			/* Iterate over files and add them to the zip */
			for (Path src : filePaths) {
				if (!Files.isDirectory(src)) {
					log.debug("Adding file " + src.getFileName() + " to process archive");
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
				log.debug("Deleting file " + src.toAbsolutePath());
				Files.delete(src);
			}
		}
	}

}
