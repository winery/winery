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
package org.eclipse.winery.repository.converter.support;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptUtils.class);

    public static String resolveScriptArtifact(ArtifactTemplateId newScriptDeploymentArtifactId, ArtifactTemplateId updatedScriptArtifactId, IRepository repository) throws IOException {

        TArtifactTemplate scriptDeploymentArtifactTemplate = repository.getElement(newScriptDeploymentArtifactId);
        String targetFileLocation = Utils.findFileLocation(scriptDeploymentArtifactTemplate, repository);

        TArtifactTemplate updatedScriptArtifactTemplate = repository.getElement(updatedScriptArtifactId);
        String originalScriptFileLocation = Utils.findFileLocation(updatedScriptArtifactTemplate, repository);

        String tempLocation = new File(targetFileLocation).getParentFile().getPath();
        new File(tempLocation).mkdirs();
        ScriptUtils.deleteFilesInFolder(new File(tempLocation), "tar");

        final BufferedReader br = new BufferedReader(new FileReader(originalScriptFileLocation));
        String line = null;

        while ((line = br.readLine()) != null) {
            final String[] words = line.replaceAll("[;&]", "").split("\\s+");
            List<String> strings = Arrays.asList(words);

            Iterator<String> it = strings.iterator();
            int number = 0;

            if (it.hasNext() && StringUtils.isNotBlank(line)) {
                String word = it.next();
                if (word.isEmpty() || "sudo".equals(word) || "-E".equals(word)) {
                    number++;
                    word = it.next();
                }
                if (it.hasNext() && "apt-get".equals(word)) {
                    word = it.next();
                    while (word.startsWith("-")) {
                        word = it.next();
                    }
                    if (strings.size() >= 3 + number && word.equals("install")) {
                        // FIXME: Open this to debug in Linux Machines
//                        it.forEachRemaining(s -> downloadAptDependencies(s));
                        final String dependency = "dependency.tar";

                        final String tarLocation = tempLocation + "/" + dependency;

                        createTarFile(tempLocation, tarLocation);
                        deleteFilesInFolder(new File(tempLocation), "tar");
                        String updatedScript = updateScriptFile(line);
                        createFile(originalScriptFileLocation, updatedScript);

                        // Set references
                        BackendUtils.synchronizeReferences(repository, newScriptDeploymentArtifactId);
                        repository.setElement(newScriptDeploymentArtifactId, scriptDeploymentArtifactTemplate);
                        return updatedScript;
                    }
                }
            }
        }
        return "";
    }

    public static String getFileExtension(final File file) {
        final String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    static public void createTarFile(final String source, final String tarlocation) {
        try (
            FileOutputStream fos = new FileOutputStream(tarlocation);
            GZIPOutputStream gos = new GZIPOutputStream(new BufferedOutputStream(fos));
            TarArchiveOutputStream tarOs = new TarArchiveOutputStream(gos);
        ) {
            final File folder = new File(source);
            final File[] fileNames = folder.listFiles();
            if (fileNames != null) {
                for (final File file : fileNames) {
                    if (!getFileExtension(file).equals("tar")) {
                        addFilesToTarGZ(file, tarOs);
                    }
                }
            }
        } catch (final IOException e) {
            LOGGER.error("Error while creating tar-ball", e);
        }
    }

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
            // no need to copy any content since it is a directory, just close the output stream
            tos.closeArchiveEntry();
            for (final File cFile : file.listFiles()) {
                // recursively call the method for all the subfolders
                addFilesToTarGZ(cFile, tos);
            }
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

    static public void createFile(final String filename, final String content) throws IOException {
        if (new File(filename).getParent() != null) {
            new File(new File(filename).getParent()).mkdirs();
        }
        new File(filename).delete();
        try (final FileWriter bw = new FileWriter(filename)) {
            bw.write(content);
        }
    }

    static public String updateScriptFile(String line) {
        return "#//Self-contained CSAR//" + line + '\n' +
            "csarRoot=$(find ~ -maxdepth 1 -path \"*.csar\");" + "\n" +
            "IFS=';' read -ra NAMES <<< \"$DAs\";" + "\n" +
            "for i in \"${NAMES[@]}\"; do" + "\n" +
            "    IFS=',' read -ra PATH <<< \"$i\"; " + "\n" +
            "        dirName=$(/usr/bin/sudo /usr/bin/dirname $csarRoot${PATH[1]})" + "\n" +
            "        baseName=$(/usr/bin/sudo /usr/bin/basename $csarRoot${PATH[1]})" + "\n" +
            "        filename=\"${baseName%.*}\"" + "\n" +
            "    if [[ \"${PATH[1]}\" == *.tar ]];" + "\n" +
            "    then" + "\n" +
            "        cd $dirName" + "\n" +
            "        /usr/bin/sudo mkdir -p $filename" + "\n" +
            "        /bin/tar  -xvzf $baseName -C $filename" + "\n" +
            "    fi" + "\n" +
            "done" + "\n" +
            "export DEBIAN_FRONTEND=noninteractive" + "\n" +
            "/usr/bin/sudo -E /usr/bin/dpkg -i -R -E -B $filename";
    }

    static public void downloadAptDependencies(String packet) {
        Runtime rt = Runtime.getRuntime();
        String cmd =
            "apt-get download $(apt-cache depends --recurse --no-recommends --no-suggests --no-conflicts --no-breaks --no-replaces --no-enhances --no-pre-depends "
                + packet + " | grep \"^\\w\")";

        LOGGER.info("Executing command: " + cmd);

        try {
            rt.exec(new String[] {"bash", "-c", cmd})
                .waitFor();
        } catch (Exception e) {
            LOGGER.error("Error while downloading artifacts...", e);
        }
    }
}
