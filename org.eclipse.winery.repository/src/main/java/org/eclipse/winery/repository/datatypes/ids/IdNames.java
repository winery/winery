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
 *     Philipp Meyer - support for source directory
 *******************************************************************************/
package org.eclipse.winery.repository.datatypes.ids;

/**
 * This class is the brother of {@link org.eclipse.winery.common.ids.IdNames}
 *
 * It includes all id names used additionally in the local ids
 */
public class IdNames {

	// the files belonging to one artifact template are nested in the sub
	// directory "files"
	public static final String ARTIFACTTEMPLATEFILESDIRECTORY = "files";
	public static final String ARTIFACTTEMPLATESRCDIRECTORY = "src";

	public static final String CONSTRAINTTYPES = "constrainttypes";
	public static final String NAMESPACES = "namespaces";
	public static final String PLANLANGUAGES = "planlanguages";
	public static final String PLANTYPES = "plantypes";

}
