/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.converter.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.filebased.AbstractFileBasedRepository;
import org.eclipse.winery.repository.backend.filebased.FileUtils;

import com.google.common.io.ByteStreams;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
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
                    logger.debug("Write tmp file: {}", targetPath.toString());
                }
            }
        } catch (Exception e) {
            logger.error("Create zip tmp file error: ", e);
        }
        return dir;
    }

    static public List<String> getFileListFromZip(final ZipInputStream zipFile, final String outputFolder) throws IOException {
        List<String> unpackedFileList = new LinkedList<>();

        // create output directory if not exists
        final File folder = new File(outputFolder);
        folder.mkdir();

        // get the zipped file list entry
        ZipEntry entry;

        try {
            entry = zipFile.getNextEntry();

            while (entry != null) {
                final String fileName = entry.getName();
                final File newFile = new File(outputFolder + fileName);
                if (!entry.isDirectory()) {
                    unpackedFileList.add(entry.getName());
                    // create all non exists folders
                    new File(newFile.getParent()).mkdirs();

                    // fill file
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zipFile.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    } catch (IOException e) {
                        logger.error("Error while reading file, {}", entry.getName(), e);
                    }
                } else {
                    newFile.mkdirs();
                }

                entry = zipFile.getNextEntry();
            }
        } catch (IOException e) {
            logger.error("Error while reading zip entry!", e);
        } finally {
            zipFile.closeEntry();
            zipFile.close();
        }

        return unpackedFileList;
    }

    /**
     * recursively delete files, folders and all subfolders
     */
    static public void delete(final File f) throws IOException {
        if (!f.exists()) {
            return;
        }
        if (f.isDirectory()) {
            for (final File c : f.listFiles()) {
                delete(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }

    /**
     * recursively delete files, folders and all subfolders with exception of files with the specified file ending in
     * the specified folder
     */
    static public void deleteFilesInFolder(final File f, final String ending) throws IOException {
        if (!f.exists()) {
            return;
        }
        if (f.isDirectory()) {
            for (final File c : f.listFiles()) {
                if (!getFileExtension(c).equals(ending)) {
                    delete(c);
                }
            }
        }
    }

    public static String getFileExtension(final File file) {
        final String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    public static String compressTarFile(final File tarFile) {
        try (InputStream in = Files.newInputStream(Paths.get(tarFile.getAbsolutePath()));
             GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(Files.newOutputStream(Paths.get(tarFile.getAbsolutePath() + ".gz")));
        ) {
            ByteStreams.copy(in, gzOut);
        } catch (IOException e) {
            logger.error("Error wile compressing tar bal", e);
        }

        return tarFile.getName() + ".gz";
    }

    public static String findFileLocation(TArtifactTemplate artifactTemplate, IRepository repository) throws UnsupportedEncodingException {
        String fileName = artifactTemplate.getArtifactReferences().getArtifactReference().get(0).getReference();
        String repositoryPath = ((AbstractFileBasedRepository) repository).getRepositoryRoot().toString();
        return repositoryPath + "/" + URLDecoder.decode(fileName, "utf-8");
    }

    public static Path getTmpDir(Path path) {
        try {
            if (Objects.isNull(tmpBase)) {
                tmpBase = Files.createTempDirectory("winery");
            }
        } catch (Exception e) {
            logger.error("Failed to create tmp dir with name 'winery'", e);
        }

        Path result = tmpBase.resolve(path);
        try {
            Files.createDirectories(result);
        } catch (IOException e) {
            logger.error("Failed to create tmp dir '{}':\n {}", result, e);
        }
        return result;
    }

    public static byte[] getHashValueOfFile(File file) throws IOException {
        return DigestUtils.md5(new FileInputStream(file));
    }
}
