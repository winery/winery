/*******************************************************************************
 * Copyright (c) 2012-2019 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.common;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

public class Constants {

    /**
     * repository specific
     **/
    public static final String DEFAULT_REPO_NAME = "winery-repository";
    // this directory is checked for existence. If it does not exist
    // $HOME/DEFAULT_REPO_NAME is used
    public static final Path GLOBAL_REPO_PATH_WINDOWS = Paths.get("C:/" + Constants.DEFAULT_REPO_NAME);

    public static final String MASTER_BRANCH = "master";

    /**
     * file-system in general
     **/
    public static final String newline = System.getProperty("line.separator");
    public static final String URL_SEPARATOR = "/";

    // Location of the local repository
    public static final String DEFAULT_LOCAL_REPO_NAME = "workspace";
    public static final String FILE_GIT_IGNORE = ".gitignore";

    // Path to images for extensions
    // Currently, we require the format <filenamextension>.png
    public static final String PATH_MIMETYPEIMAGES = "/images/mime-types/";

    // suffix for BPMN4TOSCA
    public static final String SUFFIX_BPMN4TOSCA = ".bpmn4tosca";

    // suffix for CSAR files
    public static final String SUFFIX_CSAR = ".csar";

    // suffix for ZIP files
    public static final String SUFFIX_ZIP = ".zip";

    // suffix for files in the directory PATH_MIMETYPEIMAGES, including "."
    public static final String SUFFIX_MIMETYPEIMAGES = ".png";

    // suffix for files storing the mimetype of the belonging files
    // used in implementors if IRepository of no appropriate implementation for storing a mimetype is available
    public static final String SUFFIX_MIMETYPE = ".mimetype";

    // suffix for all property files
    public static final String SUFFIX_PROPERTIES = ".properties";

    public static final String SUFFIX_JSON = ".json";

    // suffix for all files storing Definitions
    // following  line 2935 of TOSCA cos01
    public static final String SUFFIX_TOSCA_DEFINITIONS = ".tosca";

    // at each new start of the application, the modified date changes
    // reason: the default values of the properties or the JSP could have
    // changed
    public static final Date LASTMODIFIEDDATE_FOR_404 = Calendar.getInstance().getTime();

    public static final String TOSCA_PLANTYPE_BUILD_PLAN = "http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan";
    public static final String TOSCA_PLANTYPE_TERMINATION_PLAN = "http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/TerminationPlan";
    public static final String TOSCA_PLANTYPE_MANAGEMENT_PLAN = "http://opentosca.org/tosca/plantypes/management";

    public static final String DIRNAME_SELF_SERVICE_METADATA = "SELFSERVICE-Metadata";

    public static final String LICENSE_FILE_NAME = "LICENSE";
    public static final String README_FILE_NAME = "README.md";
}
