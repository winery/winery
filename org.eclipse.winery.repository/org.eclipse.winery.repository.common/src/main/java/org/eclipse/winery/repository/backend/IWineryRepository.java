/*******************************************************************************
 * Copyright (c) 2013-2019 Contributors to the Eclipse Foundation
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
import java.util.List;
import java.util.SortedSet;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.exceptions.QNameAlreadyExistsException;
import org.eclipse.winery.common.interfaces.QNameWithName;
import org.eclipse.winery.model.ids.GenericId;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

/**
 * This interface is used by the repository client to get access to the
 * repository.
 * <p>
 * This interface should be removed and the client should be able to use
 * "IWineryRepositoryCommon" only.
 */
public interface IWineryRepository extends IWineryRepositoryCommon {

    /**
     * Returns all namespaces used by all known definition children and namespaces
     * where a prefix is defined for
     * <p>
     * Currently used by <code>index.jsp</code> only. Can be removed if jsp-based topology modeler is gone.
     * <p>
     * String is used as return type as Java's QName also uses String as
     * parameter to denote a namespace
     */
    SortedSet<String> getNamespaces();

    /**
     * Returns a list of the QNames of all available types. Types can be node
     * types, service templates, artifact types, artifact templates.
     * <p>
     * This method obsoletes methods like "getQNameListOfAllArtifactTypes": One
     * just has to call getQNameListOfAllTypes(TArtifactType.class)
     * <p>
     * TExtensibleElements has to be used because of TServiceTemplate
     *
     * Currently used by <code>index.jsp</code> only. Can be removed if jsp-based topology modeler is gone.
     *
     * @return List of QNames of all types
     */
    <T extends TExtensibleElements> List<QName> getQNameListOfAllTypes(Class<T> type);

    /**
     * Get the TEntityType belonging to the given QName
     *
     * @return null if there is no data on the server
     */
    <T extends TEntityType> T getType(QName qname, Class<T> type);

    /**
     * Queries the repository for instances of the given type. Returns pairs of
     * QNames and names. The names are added as some component instances do
     * carry a name.
     * <p>
     * If the component instance does not carry an explicit name, the localName
     * of the QName is used as name.
     *
     * @param type the type to get all instances of
     * @return a collection of QName/name pairs
     */
    Collection<QNameWithName> getListOfAllInstances(Class<? extends DefinitionsChildId> type);

    /**
     * Returns the associated name for the given id.
     * <p>
     * Since not all TOSCA entities have names, this method may only be used for
     * entities supporting names. If it is used for entities not having a name,
     * null is returned.
     *
     * @param id references the entity to query for a name
     * @return the name or null if no name is available
     */
    String getName(GenericId id);

    /**
     * Returns a list of all available types. Types can be node types, service
     * templates, artifact types. Note that artifact templates are
     * TEntityTemplates and thus cannot be retrieved by this method.
     * <p>
     * We have to use TExtensibleElements instead of TEntityTypes to offer fetching of service templates, too.
     * <p>
     * This method obsoletes methods like "getAllArtifactTypes": One just has to
     * call getallTypes(TArtifactType.class)
     *
     * @return List of all types
     */
    <T extends TExtensibleElements> Collection<T> getAllTypes(Class<T> type);

    /**
     * @return List of all types with associated elements (such as deployment
     * artifacts). Each type is nested in a separate Definitions Element
     */
    <T extends TEntityType> Collection<TDefinitions> getAllTypesWithAssociatedElements(Class<T> type);

    /**
     * Returns the topology template associated to the given service template
     *
     * Currently used by <code>index.jsp</code> only. Can be removed if jsp-based topology modeler is gone.
     *
     * @param serviceTemplate a QName of the sericeTemplate with full namespace
     * @return null if nothing is found
     */
    default TTopologyTemplate getTopologyTemplate(QName serviceTemplate) {
        return this.getTopologyTemplate(serviceTemplate, "servicetemplates", "topologytemplate");
    }

    /**
     * Returns the topology template associated to the given service template nested in the "parentPath" with the element "elementPath".
     * The URL is constructed as follows:
     *
     * <code>topologyTemplateURL = repositoryURL + parentPath + Util.DoubleURLencode(serviceTemplateQName) + elementPath;</code>
     *
     * Currently used by <code>index.jsp</code> only. Can be removed if jsp-based topology modeler is gone.
     *
     * @param serviceTemplate a QName of the sericeTemplate with full namespace
     * @param parentPath      the parent path to use - with leading and trailing /
     * @return null if nothing is found
     * @parem elementPath the element path to use - with leading and traling /
     */
    TTopologyTemplate getTopologyTemplate(QName serviceTemplate, String parentPath, String elementPath);

    /**
     * Replaces (or creates) the provided topology template
     *
     * @param serviceTemplate  the service template the given topolgoy template
     *                         belongs to
     * @param topologyTemplate the topology template to use
     */
    void setTopologyTemplate(QName serviceTemplate, TTopologyTemplate topologyTemplate) throws Exception;

    /**
     * Returns a reference to the artifact type registered for the given file
     * extensions. Returns null if no such artifact type exists.
     *
     * @param extension the file extension to look up.
     * @return Reference in the form of a QName to the artifact type matching
     * the given file extension.
     */
    QName getArtifactTypeQNameForExtension(String extension);

    /**
     * Creates a component of the type idClass.
     */
    void createComponent(QName qname, Class<? extends DefinitionsChildId> idClass) throws QNameAlreadyExistsException;

    /**
     * Creates the specified artifact template
     *
     * @param qname        the namespace and name of the artifact template
     * @param artifactType the artifact type of the artifact template
     * @throws QNameAlreadyExistsException if the given QName already exists
     */
    void createArtifactTemplate(QName qname, QName artifactType) throws QNameAlreadyExistsException;
}
