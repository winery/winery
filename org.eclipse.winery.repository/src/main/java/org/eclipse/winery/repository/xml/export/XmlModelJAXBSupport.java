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

package org.eclipse.winery.repository.xml.export;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.tosca.xml.XTDefinitions;
import org.eclipse.winery.model.tosca.extensions.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.repository.backend.MockXMLElement;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bundles all general JAXB functionality
 */
public class XmlModelJAXBSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(org.eclipse.winery.repository.JAXBSupport.class);

    // thread-safe JAXB as inspired by https://jaxb.java.net/guide/Performance_and_thread_safety.html
    // The other possibility: Each subclass sets JAXBContext.newInstance(theSubClass.class); in its static {} part.
    // This seems to be more complicated than listing all subclasses in initContext
    private static final JAXBContext CONTEXT = XmlModelJAXBSupport.initContext();

    private static JAXBContext initContext() {
        JAXBContext context;
        try {
            // For winery classes, eventually the package+jaxb.index method could be better.
            // See http://stackoverflow.com/a/3628525/873282
            context = JAXBContext.newInstance(
                XTDefinitions.class, // all other elements are referred by "@XmlSeeAlso"
                Application.class,
                WinerysPropertiesDefinition.class,
                MockXMLElement.class // MockXMLElement is added for testing purposes only.
            );
        } catch (JAXBException e) {
            LOGGER.error("Could not initialize JAXBContext", e);
            throw new IllegalStateException(e);
        }
        return context;
    }

    public static JAXBContext getContext() {
        return CONTEXT;
    }

    public static Marshaller createMarshaller(boolean includeProcessingInstruction) {
        return createMarshaller(includeProcessingInstruction, null);
    }

    /**
     * Creates a marshaller.
     *
     * IMPORTANT: always create a new instance and do not reuse the marhaller, otherwise the input-stream will throw a
     * NullPointerException! see https://stackoverflow.com/questions/11114665/org-xml-sax-saxparseexception-premature-end-of-file-for-valid-xml
     *
     * @throws IllegalStateException if marshaller could not be instantiated
     */
    public static Marshaller createMarshaller(boolean includeProcessingInstruction, NamespacePrefixMapper prefixMapper) {
        Marshaller m;
        try {
            m = CONTEXT.createMarshaller();
            // pretty printed output is required as the XML is sent 1:1 to the browser for editing
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            try {
                if (prefixMapper != null) {
                    m.setProperty("com.sun.xml.bind.namespacePrefixMapper", prefixMapper);
                } else {
                    LOGGER.warn("No Prefix Mapper was passed for Marshaller creation!");
                }
            } catch (PropertyException e) {
                // Namespace-Prefixing is not supported by the used Provider. Nothing we can do about that
            }
            if (!includeProcessingInstruction) {
                // side effect of JAXB_FRAGMENT property (when true): processing instruction is not included
                m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            }
        } catch (JAXBException e) {
            LOGGER.error("Could not instantiate marshaller", e);
            throw new IllegalStateException(e);
        }

        return m;
    }

    /**
     * Creates an unmarshaller
     *
     * @throws IllegalStateException if unmarshaller could not be instantiated
     */
    public static Unmarshaller createUnmarshaller() {
        try {
            return CONTEXT.createUnmarshaller();
        } catch (JAXBException e) {
            LOGGER.error("Could not instantiate unmarshaller", e);
            throw new IllegalStateException(e);
        }
    }
}
