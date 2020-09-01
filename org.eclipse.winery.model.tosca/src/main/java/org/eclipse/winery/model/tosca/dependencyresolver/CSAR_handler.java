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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBException;

import org.eclipse.winery.model.tosca.dependencyresolver.Abstract.Language;

// unpack

/**
 * @author jery
 */
public class CSAR_handler {

    public static final String ArchitectureFileName = "arch";
    public static final String Definitions = "Definitions/";
    // Download and proceed packets
    public MetaFile metaFile;
    // Updates service templates
    public Topology_Handler service_template;
    public Boolean debug = false;
    private final Package_Handler packet_handler;
    // Metafile description
    // input CSAR file name
    private String CSAR;
    // folder containing extracted files
    private String folder;
    // extracted files
    private List<String> files;
    // architecture of packages
    private String architecture;
    private Resolving resolving;

    /**
     * init system
     *
     * @param filename CSAR archive
     */
    public CSAR_handler(final String filename) throws FileNotFoundException, IOException {
        this.metaFile = new MetaFile();
        init(filename);
        this.packet_handler = new Package_Handler(this);
        this.service_template = new Topology_Handler(this);
    }

    public Resolving getResolving() {
        return this.resolving;
    }

    /**
     * Download and add packet to csar
     *
     * @param packet name to download
     */
    public List<String> getPacket(final Language language, final String packet,
                                  final String source) throws JAXBException, IOException {
        return this.packet_handler.getPacket(language, packet, source);
    }

    /**
     * Update Service Template
     *
     * @param reference to script, which downloads packet
     * @param packet    to be added to TOSCA
     */
    public void AddDependenciesScript(final String reference, final String packet) throws JAXBException, IOException {
        this.service_template.addDependencyToScript(reference, packet);
    }

    /**
     * Update service template
     *
     * @param source packet, which needs target packet
     * @param target new packet needed by source
     */
    public void AddDependenciesPacket(final String source, final String target,
                                      final String dependencyType) throws JAXBException, IOException {
        this.service_template.addDependencyToPacket(source, target, dependencyType);
    }

    public void expandTOSCA_Node(final List<String> packages, final String source) throws IOException, JAXBException {
        this.service_template.expandTOSCA_Nodes(packages, source);
    }

    /**
     * extract archive and read architecture
     *
     * @param filename CSAR archive
     */
    public void init(final String filename) throws FileNotFoundException, IOException {
        if (filename == null) {
            throw new NullPointerException();
        }
        this.CSAR = filename;
        unpack();
        readArchitecture();
        chooseResolving();

        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("START: " + timestamp);
    }

    /**
     * List extracted files
     *
     * @return list with files
     */
    public List<String> getFiles() {
        // List<String> fullFiles = new LinkedList<String>();
        // for (String s : files)
        // fullFiles.add(folder + s);
        return this.files;
    }

    /**
     * Get folder containing extracted files
     *
     * @return folder name
     */
    public String getFolder() {
        return this.folder;
    }

    /**
     * Unpack CSAR
     */
    private void unpack() throws FileNotFoundException, IOException {
        this.folder = this.CSAR + "_temp_references_resolver";
        final File folderfile = new File(this.folder);
        this.folder = folderfile + File.separator;
        zip.delete(new File(this.folder));
        this.files = zip.unZipIt(this.CSAR, this.folder);
        this.metaFile.init(this.folder);
    }

    /**
     * Pack changed CSAR back to zip
     *
     * @param filename target archive filename
     */
    public void pack(final String filename) throws FileNotFoundException, IOException {
        this.metaFile.pack(this.folder);
        if (filename == null) {
            throw new NullPointerException();
        }
        zip.zipIt(filename, this.folder);
    }

    /**
     * Get archive filename
     *
     * @return archive filename
     */
    public String getCSARname() {
        return this.CSAR;
    }

    /**
     * Get current architecture
     *
     * @return architecture
     */
    public String getArchitecture() {
        return this.architecture;
    }

    /**
     * Set specific architecture
     */
    public void setArchitecture(final String arch) throws IOException {
        if (arch == null) {
            throw new NullPointerException();
        }
        this.architecture = arch;

        // delete old file
        final File fArch = new File(this.folder + Resolver.folder + ArchitectureFileName);
        fArch.delete();

        // create new file
        final FileWriter bw = new FileWriter(fArch);
        bw.write(arch);
        bw.flush();
        bw.close();
    }

    /**
     * reads Architecture from extracted data or from user input
     */
    // no need to close user input
    @SuppressWarnings("resource")
    public void readArchitecture() throws IOException {
        final File arch = new File(this.folder + Resolver.folder + ArchitectureFileName);
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(arch));
            final String line = br.readLine();
            br.close();
            if (line != null && !line.equals("")) {
                this.architecture = line;
            } else {
                new File(this.folder + Resolver.folder + ArchitectureFileName).delete();
                throw new FileNotFoundException();
            }
        } catch (final FileNotFoundException e) {
            new File(this.folder + Resolver.folder).mkdir();
            final FileWriter bw = new FileWriter(arch);
            System.out.println("Please enter the architecure. (default: i386)");
            System.out.println("Example: i386, amd64, arm, noarch.");
            System.out.print("architecture: ");
            this.architecture = new Scanner(System.in).nextLine();
            if (this.architecture.equals("")) {
                this.architecture = "i386";
            }
            this.architecture = ":" + this.architecture;
            if (this.architecture.equals(":noarch")) {
                this.architecture = "";
            }
            bw.write(this.architecture);
            bw.close();
        }
        this.metaFile.addFileToMeta(Resolver.folder + ArchitectureFileName, "text/txt");
    }

    public void chooseResolving() {
        System.out.println("Please select the type of resolving");
        System.out.println("Supported modes: single, mirror, expand, archive. (default: archive)");
        System.out.print("resolving: ");
        final String temp = new Scanner(System.in).nextLine();
        if (temp.equals("single")) {
            this.resolving = Resolving.Single;
            System.out.println("Resolving accepted: single");
        } else if (temp.equals("mirror")) {
            this.resolving = Resolving.Mirror;
            System.out.println("Resolving accepted: mirror");
        } else if (temp.equals("expand")) {
            this.resolving = Resolving.Expand;
            System.out.println("Resolving accepted: expand");
        } else {
            this.resolving = Resolving.Archive;
            System.out.println("Resolving accepted: Archive");
        }
    }

    public static enum Resolving {
        Mirror, Single, Expand, Archive
    }
}
