/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation, more helper methods
 *     Lukas Harzentter - get namespaces for specific component
 *     Tino Stadelmaier - code cleaning
 *     Philipp Meyer - support for source directory
 *******************************************************************************/
package org.eclipse.winery.repository.backend;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.attribute.FileTime;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.common.ids.elements.TOSCAElementId;
import org.eclipse.winery.common.interfaces.IWineryRepositoryCommon;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.HasType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.repository.backend.xsd.XsdImportManager;
import org.eclipse.winery.repository.exceptions.WineryRepositoryException;

import org.apache.tika.mime.MediaType;

/**
 * Enables access to the winery repository via Ids defined in package {@link org.eclipse.winery.common.ids}
 *
 * In contrast to {@link org.eclipse.winery.repository.backend.IRepository}, this is NOT dependent on a particular
 * storage format for the properties. These two classes exist to make the need for reengineering explicit.
 *
 * This is a first attempt to offer methods via GenericId. It might happen, that methods, where GenericIds make sense,
 * are simply added to "IWineryRepository" instead of being added here.
 *
 * The ultimate goal is to get rid of this class and to have IWineryRepositoryCommon only.
 *
 * Currently, this class is used internally only
 */
interface IGenericRepository extends IWineryRepositoryCommon {

	/**
	 * Flags the given TOSCA element as existing. The respective resource itself creates appropriate data files.
	 *
	 * Pre-Condition: !exists(id)<br/> Post-Condition: exists(id)
	 *
	 * Typically, the given TOSCA element is created if a configuration is asked for
	 */
	boolean flagAsExisting(GenericId id);

	/**
	 * Checks whether the associated TOSCA element exists
	 *
	 * @param id the id to check
	 * @return true iff the TOSCA element belonging to the given ID exists
	 */
	boolean exists(GenericId id);

	/**
	 * Deletes the referenced object from the repository
	 */
	void forceDelete(RepositoryFileReference ref) throws IOException;

	/**
	 * @param ref reference to check
	 * @return true if the file associated with the given reference exists
	 */
	boolean exists(RepositoryFileReference ref);

	/**
	 * Puts the given content to the given file. Replaces existing content.
	 *
	 * If the parent of the reference does not exist, it is created.
	 *
	 * @param ref       the reference to the file. Must not be null.
	 * @param content   the content to put into the file. Must not be null.
	 * @param mediaType the media type of the file. Must not be null.
	 * @throws IOException if something goes wrong
	 */
	void putContentToFile(RepositoryFileReference ref, String content, MediaType mediaType) throws IOException;

	/**
	 * Puts the given content to the given file. Replaces existing content.
	 *
	 * If the parent of the reference does not exist, it is created.
	 *
	 * @param ref         the reference to the file
	 * @param inputStream the content to put into the file
	 * @throws IOException if something goes wrong
	 */
	void putContentToFile(RepositoryFileReference ref, InputStream inputStream, MediaType mediaType) throws IOException;

	/**
	 * Creates an opened inputStream of the contents referenced by ref. The stream has to be closed by the caller.
	 *
	 * @param ref the reference to the file
	 * @return an inputstream
	 * @throws IOException if something goes wrong
	 */
	InputStream newInputStream(RepositoryFileReference ref) throws IOException;

	/**
	 * Creates a stream of a ZIP file containing all files contained in the given id
	 *
	 * @param id  the id whose children should be zipped
	 * @param out the outputstream to write to
	 */
	void getZippedContents(final GenericId id, OutputStream out) throws WineryRepositoryException;


	/**
	 * Returns the size of the file referenced by ref
	 *
	 * @param ref a refernce to the file stored in the repository
	 * @return the size in bytes
	 * @throws IOException if something goes wrong
	 */
	long getSize(RepositoryFileReference ref) throws IOException;

	/**
	 * Returns the last modification time of the entry.
	 *
	 * @param ref the reference to the file
	 * @return the time of the last modification
	 * @throws IOException if something goes wrong
	 */
	FileTime getLastModifiedTime(RepositoryFileReference ref) throws IOException;

	/**
	 * Returns the mimetype belonging to the reference.
	 *
	 * @param ref the reference to the file
	 * @return the mimetype as string
	 * @throws IOException           if something goes wrong
	 * @throws IllegalStateException if an internal error occurs, which is not an IOException
	 */
	String getMimeType(RepositoryFileReference ref) throws IOException;

	/**
	 * @return the last change date of the file belonging to the given reference. NULL if the associated file does not
	 * exist.
	 */
	Date getLastUpdate(RepositoryFileReference ref);

	/**
	 * Returns all components available of the given id type
	 *
	 * @param idClass class of the Ids to search for
	 * @return empty set if no ids are available
	 */
	<T extends TOSCAComponentId> SortedSet<T> getAllTOSCAComponentIds(Class<T> idClass);

	/**
	 * Returns the set of <em>all</em> ids nested in the given reference
	 *
	 * The generated Ids are linked as child to the id associated to the given reference
	 *
	 * Required for getting plans nested in a service template: plans are nested below the PlansOfOneServiceTemplateId
	 *
	 * @param ref     a reference to the TOSCA element to be checked. The path belonging to this element is checked.
	 * @param idClass the class of the Id
	 * @return the set of Ids nested in the given reference. Empty set if there are no or the reference itself does not
	 * exist.
	 */
	<T extends TOSCAElementId> SortedSet<T> getNestedIds(GenericId ref, Class<T> idClass);

	/**
	 * Returns the set of files nested in the given reference
	 */
	SortedSet<RepositoryFileReference> getContainedFiles(GenericId id);

	/**
	 * Returns all namespaces used by all known TOSCA components
	 */
	Collection<Namespace> getUsedNamespaces();

	/**
	 * Returns all namespaces specific for a given TOSCA component
	 *
	 * @param clazz the TOSCA component class which namespaces' should be returned.
	 */
	Collection<Namespace> getComponentsNamespaces(Class<? extends TOSCAComponentId> clazz);

	/**
	 * @param clazz          the id class of the entities to discover
	 * @param qNameOfTheType the QName of the type, where all TOSCAComponentIds, where the associated element points to
	 *                       the type
	 */
	default <X extends TOSCAComponentId> Collection<X> getAllElementsReferencingGivenType(Class<X> clazz, QName qNameOfTheType) {
		Objects.requireNonNull(clazz);
		Objects.requireNonNull(qNameOfTheType);

		// we do not use any database system,
		// therefore we have to crawl through each node type implementation by ourselves
		return RepositoryFactory.getRepository().getAllTOSCAComponentIds(clazz)
			.stream()
			// The resource may have been freshly initialized due to existence of a directory
			// then it has no node type assigned leading to ntiRes.getType() being null
			// we ignore this error here
			.filter(id -> ((HasType) this.getDefinitions(id).getElement()).getTypeAsQName().equals(qNameOfTheType))
			.collect(Collectors.toList());
	}

	NamespaceManager getNamespaceManager();

	XsdImportManager getXsdImportManager();

	/**
	 * Updates the element belonging to the given TOSCAComponentId Regenerates wrapper definitions; thus all extensions
	 * at the wrapper definitions are lost
	 *
	 * @param id      the TOSCAComponentId to update
	 * @param element the element to set
	 * @throws IOException if persisting went wrong
	 */
	default void setElement(TOSCAComponentId id, TExtensibleElements element) throws IOException {
		// default implementation on the server side
		// the client side has to use the REST method
		Definitions definitions = BackendUtils.createWrapperDefinitions(id);
		definitions.setElement(element);
		BackendUtils.persist(id, definitions);
	}
}
