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

package org.eclipse.winery.model.tosca.dependencyresolver.Abstract;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.eclipse.winery.model.tosca.dependencyresolver.CSAR_handler;
import org.eclipse.winery.model.tosca.dependencyresolver.Utils;

public abstract class Language {

    // List of package managers supported by language
    protected List<PackageManager> packetManagers;

    // Extensions for this language
    protected List<String> extensions;

    // Language Name
    protected String Name;

    // To access package topology
    protected CSAR_handler ch;

    // List with already created packages
    protected List<String> created_packages;

    /**
     * get Language name
     *
     * @return
     */
    public String getName() {
        return this.Name;
    }

    /**
     * Get supported extensions
     *
     * @return list with extensions
     */
    public List<String> getExtensions() {
        return this.extensions;
    }

    /**
     * Proceed file, transfer it to package managers
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws JAXBException
     */
    public void proceed() throws FileNotFoundException, IOException, JAXBException {
        if (this.ch == null) {
            throw new NullPointerException();
        }
        for (final String f : this.ch.getFiles()) {
            for (final String suf : this.extensions) {
                if (f.toLowerCase().endsWith(suf.toLowerCase())) {
                    final List<String> packages = new LinkedList<>();
                    for (final PackageManager pm : this.packetManagers) {
                        packages.addAll(pm.proceed(this.ch.getFolder() + f, f));
                    }
                    if (packages.size() > 0 && this.ch.getResolving() == CSAR_handler.Resolving.Single) {
                        final List<String> templist = new LinkedList<>();
                        for (final String temp : packages) {
                            templist.add(Utils.correctName(temp));
                        }
                        createTOSCA_Node(templist, f);
                        this.ch.AddDependenciesScript(Utils.correctName(f), getNodeName(f));
                    }
                }
            }
        }
    }

    /**
     * Generate node name for specific packages
     *
     * @param packet
     * @param source
     * @return
     */
    public String getNodeName(final String packet, final String source) {
        return Utils.correctName(this.Name + "_" + packet + "_" + source.replace("/", "_"));
    }

    /**
     * Generate node name for specific packages
     *
     * @param source
     * @return
     */
    public String getNodeName(final String source) {
        return Utils.correctName(this.Name + "_for_" + source.replace("/", "_"));

    }

    /**
     * Generate Node for TOSCA Topology
     *
     * @param packet
     * @param source
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    public abstract String createTOSCA_Node(String packet, String source) throws IOException, JAXBException;

    public abstract String createTOSCA_Node(List<String> packages, String source) throws IOException, JAXBException;

    public void expandTOSCA_Node(final List<String> packages, final String source) throws IOException, JAXBException {
        this.ch.expandTOSCA_Node(packages, Utils.correctName(source.replace("/", "_")));
    }
}
