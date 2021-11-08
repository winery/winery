/*******************************************************************************
 * Copyright (c) 2017-2019 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.backend;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.eclipse.winery.model.ids.Namespace;
import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.repository.backend.filebased.NamespaceProperties;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface NamespaceManager {

    // if com.sun.xml.bind.marshaller.NamespacePrefixMapper cannot be resolved,
    // possibly
    // http://mvnrepository.com/artifact/com.googlecode.jaxb-namespaceprefixmapper-interfaces/JAXBNamespacePrefixMapper/2.2.4
    // helps
    // also com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper could be the
    // right package
    /**
     * Converts this NamespaceManager to a NamespacePrefixMapper that can be used for Marshalling entities to XML
     * through JAXB
     *
     * @return A NamespacePrefixMapper instance that uses {@link #getPrefix(String)} to determine prefixes for
     * serialization.
     */
    default NamespacePrefixMapper asPrefixMapper() {
        NamespaceManager owner = this;
        Logger LOGGER = LoggerFactory.getLogger(NamespaceManager.class);
        /**
         * Follows https://jaxb.java.net/2.2.5/docs/release-documentation.html#marshalling -changing-prefixes
         * <p>
         * See http://www.jarvana.com/jarvana/view/com/sun/xml/bind/jaxb-impl/2.2.2/ jaxb-impl-2.2.2-javadoc.jar!/com/sun/xml/bind/marshaller/
         * NamespacePrefixMapper.html for a JavaDoc of the NamespacePrefixMapper
         */
        return new NamespacePrefixMapper() {
            @Override
            public String getPreferredPrefix(@NonNull String namespaceUri, String suggestion, boolean requirePrefix) {
                LOGGER.trace("Mapping params: {}, {}, {}", namespaceUri, suggestion, requirePrefix);
                if (StringUtils.isEmpty(namespaceUri)) {
                    LOGGER.trace("Empty or null namespaceUri: null returned");
                    return null;
                }

                if ((!requirePrefix || "".equals(suggestion)) && namespaceUri.equals(Namespaces.TOSCA_NAMESPACE)) {
                    // in case no prefix is required and the namespace is the TOSCA namespace, 
                    //  there should be no prefix added at all to increase human-readability of the XML
                    LOGGER.trace("No prefix required or empty prefix suggested: requesting empty prefix from marshaller.");
                    // Returning empty string over "don't care"
                    return "";
                }
                final String prefix = owner.getPrefix(namespaceUri);
                LOGGER.trace("returned: {}", prefix);
                return prefix;
            }
        };
    }
    
    @Nullable default String getPrefix(Namespace namespace) {
        Objects.requireNonNull(namespace);

        String ns = namespace.getDecoded();
        return this.getPrefix(ns);
    }

    /**
     * Returns a prefix for the given namespace. With two different namespaces, to different prefixes are returned. The
     * returned prefixes are not persistent. Thus, two instances of a NamespaceManager might return different prefixes
     * when called in another order.
     */
    @Nullable String getPrefix(String namespace);

    /**
     * Determines whether the storage has a namespace prefix stored permanently. This differs from just issuing a
     * {@link #getPrefix(String)} request, which just determines something, but does not persist it between calls.
     */
    boolean hasPermanentProperties(String namespace);

    void removeNamespaceProperties(String namespace);

    /**
     * Permanently stores a prefix. No action, if namespace or prefix are null or empty.
     */
    void setNamespaceProperties(String namespace, NamespaceProperties properties);

    Map<String, NamespaceProperties> getAllNamespaces();

    @NonNull NamespaceProperties getNamespaceProperties(String namespace);

    /**
     * Add new properties for a namespace if it does not exist yet. Otherwise no action will be performed.
     *
     * @param namespace the namespace to be added
     */
    default void addPermanentNamespace(String namespace) {
        if (!hasPermanentProperties(namespace)) {
            this.setNamespaceProperties(namespace, this.getNamespaceProperties(namespace));
        }
    }

    void addAllPermanent(Collection<NamespaceProperties> properties);

    void replaceAll(Map<String, NamespaceProperties> map);

    /**
     * Removes all namespace mappings
     */
    void clear();

    boolean isPatternNamespace(String namespace);

    boolean isSecureCollection(String namespace);

    boolean isGeneratedNamespace(String namespace);
}
