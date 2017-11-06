/*******************************************************************************
 * Copyright (c) 2013-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation, merge with org.eclipse.winery.repository.datatypes.ids.IdNames
 *******************************************************************************/
package org.eclipse.winery.common.ids;

/**
 * The names of ids, used to set XMLids for collections of things
 */
public class IdNames {

	public static final String DEPLOYMENTARTIFACTS = "deploymentartifacts";
	public static final String INSTANCESTATES = "instancestates";
	public static final String INTERFACES = "interfaces"; // used at node type
	public static final String INPUTPARAMETERS = "inputParameters";
	public static final String IMPLEMENTATIONARTIFACTS = "implementationartifacts";
	public static final String NODETEMPLATES = "nodetemplates";
	public static final String OUTPUTPARAMETERS = "outputParameters";
	public static final String PROPERTIES = "properties";
	public static final String RELATIONSHIPTEMPLATES = "relationshiptemplates";
	public static final String SOURCEINTERFACES = "sourceinterfaces";
	public static final String TARGETINTERFACES = "targetinterfaces";
	public static final String TOPOLOGYTEMPATE = "topologytemplate";

	// the files belonging to one artifact template are nested in the sub directory "files"
	public static final String FILES_DIRECTORY = "files";
	public static final String SOURCE_DIRECTORY = "source";

	public static final String SELF_SERVICE_PORTAL = "selfserviceportal";

	/**
	 * In this case we explicitly use '/' as a separator to get valid urls during retrieval of all files. See {@link
	 * org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates.FilesResource#getAllFileMetas()}
	 * and {@link org.eclipse.winery.repository.rest.datatypes.FileMeta#FileMeta(org.eclipse.winery.common.RepositoryFileReference)}
	 */
	public static final String SELF_SERVICE_PORTAL_FILES = IdNames.SELF_SERVICE_PORTAL + "/" + IdNames.FILES_DIRECTORY;

	public static final String CONSTRAINTTYPES = "constrainttypes";
	public static final String NAMESPACES = "namespaces";
	public static final String PLANLANGUAGES = "planlanguages";
	public static final String PLANTYPES = "plantypes";
}
