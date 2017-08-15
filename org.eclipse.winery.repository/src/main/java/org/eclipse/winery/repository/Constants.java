/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Philipp Meyer- support for source directory
 *******************************************************************************/
package org.eclipse.winery.repository;

import java.util.Calendar;
import java.util.Date;

public class Constants {

	/** repository specific **/
	public static final String DEFAULT_REPO_NAME = "winery-repository";
	// this directory is checked for existence. If it does not exist
	// $HOME/DEFAULT_REPO_NAME is used
	public static final String GLOBAL_REPO_PATH_WINDOWS = "C:\\" + Constants.DEFAULT_REPO_NAME;

	/** file-system in general **/
	public static final String newline = System.getProperty("line.separator");

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

	// suffix for all files storing Definitions
	// following  line 2935 of TOSCA cos01
	public static final String SUFFIX_TOSCA_DEFINITIONS = ".tosca";

	// at each new start of the application, the modified date changes
	// reason: the default values of the properties or the JSP could have
	// changed
	public static final Date LASTMODIFIEDDATE_FOR_404 = Calendar.getInstance().getTime();

	public static final String TOSCA_PLANTYPE_BUILD_PLAN = "http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan";
	public static final String TOSCA_PLANTYPE_TERMINATION_PLAN = "http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/TerminationPlan";

	public static final String DIRNAME_SELF_SERVICE_METADATA = "SELFSERVICE-Metadata";

	/* used for IA generation */
	//public static final String NAMESPACE_ARTIFACTTYPE_WAR = "http://www.opentosca.org/types";
	public static final String NAMESPACE_ARTIFACTTYPE_WAR = "http://www.example.com/ToscaTypes";
	public static final String LOCALNAME_ARTIFACTTYPE_WAR = "WAR";

}
