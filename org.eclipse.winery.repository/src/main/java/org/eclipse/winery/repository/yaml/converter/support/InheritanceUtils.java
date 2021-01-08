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

package org.eclipse.winery.repository.yaml.converter.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.HasInheritance;
import org.eclipse.winery.model.tosca.HasType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.yaml.YamlRepository;

import org.eclipse.jdt.annotation.NonNull;

public class InheritanceUtils {
    /**
     * Gets the inheritance ancestry of an type/type implementation element represented by a DefinitionsChildId.
     *
     * @param id the DefinitionsChildId that represents the type/type implementation element for which to get the
     *           ancestry
     * @return A list of ancestors of the passed id. At the position 0 we always have the element itself.
     * @throws IllegalArgumentException if the QName inferred by the id does not belong to a type/type implementation
     *                                  element.
     */
    public static List<HasInheritance> getInheritanceHierarchy(@NonNull DefinitionsChildId id, IRepository repository) throws IllegalArgumentException {
        // this only ever gets called in the Yaml to Canonical converter.
        // therefore we inject a repository here
        Map<QName, TExtensibleElements> map = repository.getQNameToElementMapping(id.getClass());
        List<HasInheritance> result = new ArrayList<>();
        QName currentQName = id.getQName();
        TExtensibleElements currentElement;
        HasType currentParent;
        final boolean isYaml =
            Environments.getInstance().getRepositoryConfig().getProvider() == RepositoryConfigurationObject.RepositoryProvider.YAML;

        while (currentQName != null) {
            currentElement = map.get(currentQName);

            if (currentElement == null) {
                throw new IllegalArgumentException(currentQName.toString()
                    + " does not exist in the repository.");
            }

            if (!(currentElement instanceof HasInheritance)) {
                throw new IllegalArgumentException(currentQName.toString()
                    + "does not belong to a type/type implementation element.");
            }

            result.add((HasInheritance) currentElement);
            currentParent = ((HasInheritance) currentElement).getDerivedFrom();

            if (currentParent != null) {
                currentQName = currentParent.getTypeAsQName();

                // if we are in yaml mode, the root type is not stored in the repository.
                if (isYaml && YamlRepository.ROOT_TYPE_QNAME.equals(currentQName)) {
                    currentQName = null;
                }
            } else {
                currentQName = null;
            }
        }

        return result;
    }
}
