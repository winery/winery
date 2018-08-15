/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.yaml.common.reader.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.winery.model.tosca.Definitions;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reader {
    private static final Logger LOGGER = LoggerFactory.getLogger(Reader.class);

    private Unmarshaller unmarshaller;

    public Reader() {
        // TODO: There is some similar method in the BackendUtils
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Definitions.class);
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            LOGGER.error("Could not read from file", e);
        }
    }

    public Definitions parse(InputStream inputStream) throws JAXBException {
        return (Definitions) unmarshaller.unmarshal(inputStream);
    }

    /**
     * @return null in the case of an error +
     */
    public @Nullable Definitions parse(@NonNull Path fileName) {
        File file = fileName.toFile();
        try {
            return parse(new FileInputStream(file));
        } catch (Exception e) {
            LOGGER.error("Could not read from file", e);
        }
        return null;
    }

    /**
     * @return null in the case of an error
     */
    public @Nullable Definitions parse(@NonNull Path path, Path name) {
        return parse(path.resolve(name));
    }
}
