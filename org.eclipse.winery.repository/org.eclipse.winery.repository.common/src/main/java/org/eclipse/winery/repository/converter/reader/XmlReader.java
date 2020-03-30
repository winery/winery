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
package org.eclipse.winery.repository.converter.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.winery.model.tosca.Definitions;

public class XmlReader {

    private Unmarshaller unmarshaller;

    public XmlReader() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Definitions.class);
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public Definitions parse(InputStream inputStream) throws JAXBException {
        return (Definitions) unmarshaller.unmarshal(inputStream);
    }

    public Definitions parse(Path fileName) throws JAXBException {
        File file = fileName.toFile();
        try {
            return parse(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Definitions parse(Path path, Path name) throws Exception {
        return parse(path.resolve(name));
    }
}
