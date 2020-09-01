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

package org.eclipse.winery.model.tosca.dependencyresolver.Docker;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.eclipse.winery.model.tosca.dependencyresolver.Abstract.Language;
import org.eclipse.winery.model.tosca.dependencyresolver.CSAR_handler;

public class Dockerfile extends Language {

    /**
     * Constructor list extensions
     */
    public Dockerfile(final CSAR_handler new_ch) {
        this.ch = new_ch;
        this.Name = "Docker";
        this.extensions = new LinkedList<>();
        this.extensions.add("docker.zip"); // convention: dockerfile is contained in a *.zip file containing
        // "docker.zip" as name

        this.created_packages = new LinkedList<>();

        this.packetManagers = new LinkedList<>();
        this.packetManagers.add(new Docker());
    }

    @Override
    public String createTOSCA_Node(final String packet, final String source) throws IOException, JAXBException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String createTOSCA_Node(final List<String> packages, final String source) throws IOException, JAXBException {
        // TODO Auto-generated method stub
        return null;
    }
}
