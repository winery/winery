/*******************************************************************************
 * Copyright (c) 2013-2017 Contributors to the Eclipse Foundation
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
     * In this case we explicitly use '/' as a separator to get valid urls during retrieval of all files. @see
     * org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates.FilesResource#getAllFileMetas()
     * and @see org.eclipse.winery.repository.rest.datatypes.FileMeta#FileMeta(org.eclipse.winery.common.RepositoryFileReference)
     */
    public static final String SELF_SERVICE_PORTAL_FILES = IdNames.SELF_SERVICE_PORTAL + "/" + IdNames.FILES_DIRECTORY;

    public static final String CONSTRAINTTYPES = "constrainttypes";
    public static final String NAMESPACES = "namespaces";
    public static final String PLANLANGUAGES = "planlanguages";
    public static final String PLANTYPES = "plantypes";
    public static final String SOLUTIONS = "solutions";
}
