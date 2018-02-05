/*******************************************************************************
 * Copyright (c) 2013-2016 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.common.interfaces;

import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.definitions.*;
import org.eclipse.winery.model.tosca.*;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * Enables access to the winery repository via Ids defined in package {@link org.eclipse.winery.common.ids}
 * <p>
 * Methods are moved from {@link org.eclipse.winery.repository.backend.IGenericRepository} to here as soon there is an
 * implementation for them. The ultimate goal is to eliminate IGenericRepository
 * <p>
 * These methods are shared between {@link IWineryRepository} and {@link org.eclipse.winery.repository.backend.IRepository}
 */
public interface IWineryRepositoryCommon {

    /**
     * Loads the TDefinition element belonging to the given id.
     * <p>
     * Even if the given id does not exist in the repository (<code>!exists(id)</code>), an empty wrapper definitions
     * with an empty element is generated
     *
     * @param id the DefinitionsChildId to load
     */
    Definitions getDefinitions(DefinitionsChildId id);

    // in case one needs a new element, just copy and paste one of the following methods and adapt it.

    default TNodeTypeImplementation getElement(NodeTypeImplementationId id) {
        return (TNodeTypeImplementation) this.getDefinitions(id).getElement();
    }

    default TRelationshipTypeImplementation getElement(RelationshipTypeImplementationId id) {
        return (TRelationshipTypeImplementation) this.getDefinitions(id).getElement();
    }

    default TNodeType getElement(NodeTypeId id) {
        return (TNodeType) this.getDefinitions(id).getElement();
    }

    default TRelationshipType getElement(RelationshipTypeId id) {
        return (TRelationshipType) this.getDefinitions(id).getElement();
    }

    default TServiceTemplate getElement(ServiceTemplateId id) {
        return (TServiceTemplate) this.getDefinitions(id).getElement();
    }

    default TArtifactTemplate getElement(ArtifactTemplateId id) {
        return (TArtifactTemplate) this.getDefinitions(id).getElement();
    }

    default TArtifactType getElement(ArtifactTypeId id) {
        return (TArtifactType) this.getDefinitions(id).getElement();
    }

    default TPolicyTemplate getElement(PolicyTemplateId id) {
        return (TPolicyTemplate) this.getDefinitions(id).getElement();
    }

    default TCapabilityType getElement(CapabilityTypeId id) {
        return (TCapabilityType) this.getDefinitions(id).getElement();
    }

    default TRequirementType getElement(RequirementTypeId id) {
        return (TRequirementType) this.getDefinitions(id).getElement();
    }

    default TPolicyType getElement(PolicyTypeId id) {
        return (TPolicyType) this.getDefinitions(id).getElement();
    }

    /**
     * Deletes the TOSCA element <b>and all sub elements</b> referenced by the given id from the repository
     * <p>
     * We assume that each id is a directory
     */
    void forceDelete(GenericId id) throws IOException;

    /**
     * Renames a definition child id
     *
     * @param oldId the old id
     * @param newId the new id
     */
    void rename(DefinitionsChildId oldId, DefinitionsChildId newId) throws IOException;

    /**
     * Deletes all definition children nested in the given namespace
     *
     * @param definitionsChildIdClazz the type of definition children to delete
     * @param namespace               the namespace to delete
     */
    void forceDelete(Class<? extends DefinitionsChildId> definitionsChildIdClazz, Namespace namespace) throws IOException;

    /**
     * Returns the stored type for the given template
     *
     * @param template the template to determine the type for
     * @throws NullPointerException if template.getType() returns null
     */
    // we suppress "unchecked" as we use Class.forName
    @SuppressWarnings("unchecked")
    default TEntityType getTypeForTemplate(TEntityTemplate template) {
        QName type = template.getType();
        Objects.requireNonNull(type);

        // Possibilities:
        // a) try all possibly types whether an appropriate QName exists
        // b) derive type class from template class. Determine appropriate template element afterwards.
        // We go for b)

        String instanceResourceClassName = template.getClass().toString();
        int idx = instanceResourceClassName.lastIndexOf('.');
        // get everything from ".T", where "." is the last dot
        instanceResourceClassName = instanceResourceClassName.substring(idx + 2);
        // strip off "Template"
        instanceResourceClassName = instanceResourceClassName.substring(0, instanceResourceClassName.length() - "Template".length());
        // add "Type"
        instanceResourceClassName += "Type";

        // an id is required to instantiate the resource
        String idClassName = "org.eclipse.winery.common.ids.definitions." + instanceResourceClassName + "Id";

        org.slf4j.Logger LOGGER = LoggerFactory.getLogger(this.getClass());
        LOGGER.debug("idClassName: {}", idClassName);

        // Get instance of id class having "type" as id
        Class<? extends DefinitionsChildId> idClass;
        try {
            idClass = (Class<? extends DefinitionsChildId>) Class.forName(idClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Could not determine id class", e);
        }
        Constructor<? extends DefinitionsChildId> idConstructor;
        try {
            idConstructor = idClass.getConstructor(QName.class);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Could not get QName id constructor", e);
        }
        DefinitionsChildId typeId;
        try {
            typeId = idConstructor.newInstance(type);
        } catch (InstantiationException | IllegalAccessException
            | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException("Could not instantiate type", e);
        }

        final Definitions definitions = this.getDefinitions(typeId);
        return (TEntityType) definitions.getElement();
    }
}
