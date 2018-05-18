/*******************************************************************************
 * Copyright (c) 2012-2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.model.tosca.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.repository.backend.MockXMLElement;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// if com.sun.xml.bind.marshaller.NamespacePrefixMapper cannot be resolved,
// possibly
// http://mvnrepository.com/artifact/com.googlecode.jaxb-namespaceprefixmapper-interfaces/JAXBNamespacePrefixMapper/2.2.4
// helps
// also com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper could be the
// right package

/**
 * Bundles all general JAXB functionality
 */
public class JAXBSupport {

    // thread-safe JAXB as inspired by https://jaxb.java.net/guide/Performance_and_thread_safety.html
    // The other possibility: Each subclass sets JAXBContext.newInstance(theSubClass.class); in its static {} part.
    // This seems to be more complicated than listing all subclasses in initContext
    public final static JAXBContext context = JAXBSupport.initContext();

    private static final Logger LOGGER = LoggerFactory.getLogger(JAXBSupport.class);

    private final static PrefixMapper prefixMapper = new PrefixMapper();

    /**
     * Follows https://jaxb.java.net/2.2.5/docs/release-documentation.html#marshalling -changing-prefixes
     * <p>
     * See http://www.jarvana.com/jarvana/view/com/sun/xml/bind/jaxb-impl/2.2.2/ jaxb-impl-2.2.2-javadoc.jar!/com/sun/xml/bind/marshaller/
     * NamespacePrefixMapper.html for a JavaDoc of the NamespacePrefixMapper
     */
    private static class PrefixMapper extends NamespacePrefixMapper {

        @Override
        public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
            LOGGER.trace("Mapping params: {}, {}, {}", namespaceUri, suggestion, requirePrefix);
            if (StringUtils.isEmpty(namespaceUri)) {
                LOGGER.trace("Empty or null namespaceUri: null returned");
                return null;
            }

            if (!requirePrefix && namespaceUri.equals(Namespaces.TOSCA_NAMESPACE)) {
                // in case no prefix is required and the namespace is the TOSCA namespace, there should be no prefix added at all to increase human-readability of the XML
                LOGGER.trace("No prefix required: returning null.");
                return null;
            }

            final String prefix = RepositoryFactory.getRepository().getNamespaceManager().getPrefix(namespaceUri);
            LOGGER.trace("returned: {}", prefix);
            return prefix;
        }
    }

    private static JAXBContext initContext() {
        JAXBContext context;
        try {
            // For winery classes, eventually the package+jaxb.index method could be better. See http://stackoverflow.com/a/3628525/873282
            // @formatter:off
            context = JAXBContext.newInstance(
                //InjectorReplaceData.class,
                TDefinitions.class, // all other elements are referred by "@XmlSeeAlso"
                WinerysPropertiesDefinition.class,
                // for the self-service portal
                Application.class,
                // MockXMLElement is added for testing purposes only.
                MockXMLElement.class);
            // @formatter:on
        } catch (JAXBException e) {
            System.out.println("HALLO");
            System.out.println(e);
            LOGGER.error("Could not initialize JAXBContext", e);
            throw new IllegalStateException(e);
        }
        return context;
    }

    /**
     * Creates a marshaller
     *
     * @throws IllegalStateException if marshaller could not be instantiated
     */
    public static Marshaller createMarshaller(boolean includeProcessingInstruction) {
        Marshaller m;
        try {
            m = JAXBSupport.context.createMarshaller();
            // pretty printed output is required as the XML is sent 1:1 to the browser for editing
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty("com.sun.xml.bind.namespacePrefixMapper", JAXBSupport.prefixMapper);
            if (!includeProcessingInstruction) {
                // side effect of JAXB_FRAGMENT property (when true): processing instruction is not included
                m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            }
        } catch (JAXBException e) {
            JAXBSupport.LOGGER.error("Could not instantiate marshaller", e);
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
            return JAXBSupport.context.createUnmarshaller();
        } catch (JAXBException e) {
            JAXBSupport.LOGGER.error("Could not instantiate unmarshaller", e);
            throw new IllegalStateException(e);
        }
    }
}
