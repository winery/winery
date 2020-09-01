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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class zip {

    /**
     * Unzip it
     *
     * @param zipFile      input zip file
     * @param outputFolder unpacked files output folder
     * @throws FileNotFoundException , IOException
     */
    static public List<String> unZipIt(final String zipFile, final String outputFolder) throws FileNotFoundException,
        IOException {
        if (!new File(zipFile).exists()) {
            throw new FileNotFoundException(zipFile + "not found!");
        }
        // unpacked files
        final List<String> fileList = new LinkedList<>();
        // buffer
        final byte[] buffer = new byte[1024];

        // create output directory if not exists
        final File folder = new File(outputFolder);
        // if (folder.exists()) {
        // delete(folder);
        // }
        folder.mkdir();

        // get the zip file content
        final ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));

        // get the zipped file list entry
        ZipEntry ze;
        ze = zis.getNextEntry();

        while (ze != null) {

            final String fileName = ze.getName();
            final File newFile = new File(outputFolder + fileName);
            if (!ze.isDirectory()) {
                fileList.add(ze.getName());

                // create all non exists folders
                new File(newFile.getParent()).mkdirs();

                // fill file
                final FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
            } else {
                newFile.mkdirs();
            }

            ze = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();

        return fileList;
    }

    /**
     * Generate list with every file in the folder recursively
     *
     * @param node     current folder
     * @param fileList list of files in folder
     * @param folder   original folder
     */
    public static List<String> generateFileList(final File node, List<String> fileList, final String folder) {

        // add file only
        if (node.isFile()) {
            final String file = node.toString();
            fileList.add(file.substring(folder.length(), file.length()));
        }

        // recursive call
        if (node.isDirectory()) {
            final String[] subNote = node.list();
            for (final String filename : subNote) {
                fileList = generateFileList(new File(node, filename), fileList, folder);
            }
        }
        return fileList;
    }

    /**
     * Zip all files in folder
     *
     * @param zipFile output ZIP file location
     * @param folder  , containing files to zip
     * @throws FileNotFoundException , IOException
     */
    static public void zipIt(final String zipFile, final String folder) throws FileNotFoundException, IOException {

        List<String> fileList = new LinkedList<>();
        fileList = generateFileList(new File(folder), fileList, folder);
        final byte[] buffer = new byte[1024];

        final FileOutputStream fos = new FileOutputStream(zipFile);
        final ZipOutputStream zos = new ZipOutputStream(fos);

        for (final String file : fileList) {
            final ZipEntry ze = new ZipEntry(file);
            zos.putNextEntry(ze);
            final FileInputStream in = new FileInputStream(folder + file);

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
        }

        zos.closeEntry();
        zos.close();
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

    /**
     *
     */
    static public void createTarFile(final String tarName, final String source, final String ending) {

        final String tarFile = source + File.separator + tarName;
        TarArchiveOutputStream tarOs = null;
        try {
            final FileOutputStream fos = new FileOutputStream(tarFile);
            final GZIPOutputStream gos = new GZIPOutputStream(new BufferedOutputStream(fos));
            tarOs = new TarArchiveOutputStream(gos);
            final File folder = new File(source);
            final File[] fileNames = folder.listFiles();
            for (final File file : fileNames) {
                if (!getFileExtension(file).equals(ending)) {
                    addFilesToTarGZ(file, tarOs);
                }
            }
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                tarOs.close();
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    static private void addFilesToTarGZ(final File file, final TarArchiveOutputStream tos) throws IOException {
        // New TarArchiveEntry
        tos.putArchiveEntry(new TarArchiveEntry(file, file.getName()));
        if (file.isFile()) {
            final FileInputStream fis = new FileInputStream(file);
            final BufferedInputStream bis = new BufferedInputStream(fis);
            // Write content of the file
            IOUtils.copy(bis, tos);
            tos.closeArchiveEntry();
            fis.close();
        } else if (file.isDirectory()) {
            // no need to copy any content since it is
            // a directory, just close the outputstream
            tos.closeArchiveEntry();
            for (final File cFile : file.listFiles()) {
                // recursively call the method for all the subfolders
                addFilesToTarGZ(cFile, tos);
            }
        }
    }
}
