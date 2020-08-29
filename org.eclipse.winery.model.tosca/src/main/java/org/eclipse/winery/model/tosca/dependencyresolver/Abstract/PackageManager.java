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
import java.util.List;

import javax.xml.bind.JAXBException;

import org.eclipse.winery.model.tosca.dependencyresolver.CSAR_handler;

/**
 * Package manager used by language
 *
 * @author jery
 *
 */
public abstract class PackageManager {

    // Name of manager
    static public String Name;

    protected Language language;

    protected CSAR_handler ch;

    /**
     * Proceed given file with different source (like archive)
     *
     * @param filename
     *
     * @param source
     * @return TODO
     * @throws FileNotFoundException
     * @throws IOException
     * @throws JAXBException
     */
    public abstract List<String> proceed(String filename, String source) throws FileNotFoundException, IOException,
                                                                         JAXBException;
}
