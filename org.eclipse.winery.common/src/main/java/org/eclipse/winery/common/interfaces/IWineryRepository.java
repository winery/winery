/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.common.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

/**
 * This interface is used by the repository client to get access to the
 * repository.
 *
 * This interface should be removed and the client should be able to use
 * "IWineryRepositoryCommon" only.
 */
public interface IWineryRepository extends IWineryRepositoryCommon {

	/**
	 * Returns all namespaces used by all known TOSCA components and namespaces
	 * where a prefix is defined for
	 *
	 * String is used as return type as Java's QName also uses String as
	 * parameter to denote a namespace
	 */
	SortedSet<String> getNamespaces();

	/**
	 * Returns a list of the QNames of all available types. Types can be node
	 * types, service templates, artifact types, artifact templates.
	 *
	 * This method obsoletes methods like "getQNameListOfAllArtifactTypes": One
	 * just has to call getQNameListOfAllTypes(TArtifactType.class)
	 *
	 * @return List of QNames of all types
	 */
	<T extends TEntityType> List<QName> getQNameListOfAllTypes(Class<T> type);

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
	 *
	 * If the component instance does not carry an explicit name, the localName
	 * of the QName is used as name.
	 *
	 * @param type the type to get all instances of
	 * @return a collection of QName/name pairs
	 */
	Collection<QNameWithName> getListOfAllInstances(Class<? extends TOSCAComponentId> type);

	/**
	 * Returns the associated name for the given id.
	 *
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
	 *
	 * We have to use TExtensibleElements instead of TEntityTypes to offer fetching of service templates, too.
	 *
	 * This method obsoletes methods like "getAllArtifactTypes": One just has to
	 * call getallTypes(TArtifactType.class)
	 *
	 * @return List of all types
	 */
	<T extends TExtensibleElements> Collection<T> getAllTypes(Class<T> type);

	/**
	 * @return List of all types with associated elements (such as deployment
	 *         artifacts). Each type is nested in a separate Definitions Element
	 */
	<T extends TEntityType> Collection<TDefinitions> getAllTypesWithAssociatedElements(Class<T> type);

	/**
	 * Returns the topology template associated to the given service template
	 *
	 * @param serviceTemplate a QName of the sericeTemplate with full namespace
	 * @return null if nothing is found
	 */
	TTopologyTemplate getTopologyTemplate(QName serviceTemplate);

	/**
	 * Replaces (or creates) the provided topology template
	 *
	 * @param serviceTemplate the service template the given topolgoy template
	 *            belongs to
	 * @param topologyTemplate the topology template to use
	 */
	void setTopologyTemplate(QName serviceTemplate, TTopologyTemplate topologyTemplate) throws Exception;

	/**
	 * Returns a reference to the artifact type registered for the given file
	 * extensions. Returns null if no such artifact type exists.
	 *
	 * @param extension the file extension to look up.
	 * @return Reference in the form of a QName to the artifact type matching
	 *         the given file extension.
	 */
	QName getArtifactTypeQNameForExtension(String extension);

	/**
	 * Creates a component of the type idClass.
	 */
	void createComponent(QName qname, Class<? extends TOSCAComponentId> idClass) throws QNameAlreadyExistsException;

	/**
	 * Creates the specified artifact template
	 *
	 * @param qname the namespace and name of the artifact template
	 * @param artifactType the artifact type of the artifact template
	 * @throws QNameAlreadyExistsException if the given QName already exists
	 */
	void createArtifactTemplate(QName qname, QName artifactType) throws QNameAlreadyExistsException;
}
