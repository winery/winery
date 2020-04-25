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

package org.eclipse.winery.repository.xml.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.repository.JAXBSupport;
import org.eclipse.winery.repository.xml.XmlRepository;
import org.eclipse.winery.repository.xml.export.XmlModelJAXBSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToCanonical {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToCanonical.class);
    private final XmlRepository repository;

    public ToCanonical(XmlRepository repository) {
        this.repository = repository;
    }
    
    public TDefinitions convert(org.eclipse.winery.model.tosca.xml.TDefinitions node) {
        // NOTE This only works if the canonical model is a binary-compatible deserialization of the xml model.
        //  If that is no longer the case through modifications, this WILL fail!
        Marshaller m = XmlModelJAXBSupport.createMarshaller(true, repository.getNamespaceManager().asPrefixMapper());
        Unmarshaller u = JAXBSupport.createUnmarshaller();
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            m.marshal(node, buffer);
            try (ByteArrayInputStream in = new ByteArrayInputStream(buffer.toByteArray())) {
                return (TDefinitions) u.unmarshal(in);
            } 
        } catch (JAXBException | IOException e) {
            LOGGER.error("Could not convert XML model to canonical model due to underlying exception", e);
            return null;
        }
    }
}
