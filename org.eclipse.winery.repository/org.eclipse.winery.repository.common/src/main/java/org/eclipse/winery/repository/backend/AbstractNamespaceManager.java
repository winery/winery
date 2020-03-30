/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

import java.util.Objects;
import java.util.Set;

import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.constants.Namespaces;

public abstract class AbstractNamespaceManager implements NamespaceManager {

    /**
     * Generates a string indicating the kind of definitions children maintained by the namespace
     *
     * @return empty string if nothing could be matched
     */
    private String generateDefinitionsChildTypeAbbreviation(String namespace) {
        Objects.requireNonNull(namespace);

        String mid;
        if (namespace.contains("servicetemplates/")) {
            mid = "ste";
        } else if (namespace.contains("nodetypes/")) {
            mid = "nty";
        } else if (namespace.contains("nodetypeimplementations/")) {
            mid = "ntyi";
        } else if (namespace.contains("relationshiptypes/")) {
            mid = "nty";
        } else if (namespace.contains("relationshiptypeimplementations/")) {
            mid = "rtyi";
        } else if (namespace.contains("artifacttypes/")) {
            mid = "aty";
        } else if (namespace.contains("artifacttemplates/")) {
            mid = "ate";
        } else if (namespace.contains("requirementtypes/")) {
            mid = "rty";
        } else if (namespace.contains("capabilitytypes/")) {
            mid = "cty";
        } else if (namespace.contains("policytypes/")) {
            mid = "pty";
        } else if (namespace.contains("policytemplates/")) {
            mid = "pte";
        } else if (namespace.contains("compliancerules/")) {
            mid = "cr";
        } else if (namespace.contains("types/")) {
            mid = "ty";
        } else if (namespace.contains("templates/")) {
            mid = "te";
        } else {
            mid = "";
        }

        return mid;
    }

    /**
     * Tries to generate a prefix based on the last part of the URL
     */
    public String generatePrefixProposal(String namespace, int round) {
        Objects.requireNonNull(namespace);
        String[] split = namespace.split("/");
        if (split.length == 0) {
            // Fallback if no slashes are in the namespace - just count from ns0 onwards
            return String.format("ns%d", round);
        } else {
            boolean isWineryPropertiesDefinitionNamespace = namespace.endsWith(TEntityType.NS_SUFFIX_PROPERTIESDEFINITION_WINERY);
            String suffix;
            if (isWineryPropertiesDefinitionNamespace) {
                suffix = "pd";
            } else {
                suffix = "";
            }

            String lastSignificantNamespacePart;
            int shift;
            if (isWineryPropertiesDefinitionNamespace) {
                // the namespace ends with "/propertiesdefinition/winery", so the part before "propertiesdefinition" is interesting
                shift = 3;
            } else {
                shift = 1;
            }
            lastSignificantNamespacePart = split[split.length - shift].replaceAll("[^A-Za-z]+", "");

            String prefix;
            // generate special prefixes for known namespace "groups"
            if (namespace.startsWith(Namespaces.URI_START_OPENTOSCA)) {
                prefix = "ot";
            } else {
                prefix = "";
            }

            String mid = this.generateDefinitionsChildTypeAbbreviation(namespace);

            if (mid.isEmpty() && !namespace.endsWith("/")) {
                // special handling for cases such as "http://opentosca.org/artifacttemplates" required?
                mid = this.generateDefinitionsChildTypeAbbreviation(namespace + "/");
                if (!mid.isEmpty()) {
                    // The last part of the namespace was the type, so we do not have a "real" last significant namespace part
                    // and thus replace it by "general"
                    lastSignificantNamespacePart = "general";
                }
            }

            final String SEPARATOR;
            if (prefix.isEmpty() && mid.isEmpty() && suffix.isEmpty()) {
                SEPARATOR = "";
            } else {
                // W3C allows for characters such as "-", "·", and "😜"
                // (see https://www.w3.org/TR/REC-xml/#NT-NameChar),
                // but the current java implementation cannot handle it
                SEPARATOR = "I";
            }

            if (lastSignificantNamespacePart.isEmpty()) {
                if (prefix.isEmpty()) {
                    if ((round == 0) && namespace.isEmpty()) {
                        // special treatment for empty namespaces
                        return "null";
                    }
                    prefix = "ns";
                }
                return String.format("%s%s%s%s%d", prefix, mid, suffix, SEPARATOR, round);
            } else {
                if (round == 0) {
                    return String.format("%s%s%s%s%s", prefix, mid, suffix, SEPARATOR, lastSignificantNamespacePart);
                } else {
                    return String.format("%s%s%s%s%s%d", prefix, mid, suffix, SEPARATOR, lastSignificantNamespacePart, round);
                }
            }
        }
    }

    /**
     * Generates a prefix for the given namespace. There must not be a prefix existing for the namespace.
     */
    protected String generatePrefix(String namespace) {
        Objects.requireNonNull(namespace);

        String prefix;
        Set<String> allPrefixes = getAllPrefixes(namespace);

        int round = 0;
        do {
            prefix = generatePrefixProposal(namespace, round);
            round++;
        } while (allPrefixes.contains(prefix));
        return prefix;
    }

    protected abstract Set<String> getAllPrefixes(String namespace);
}
