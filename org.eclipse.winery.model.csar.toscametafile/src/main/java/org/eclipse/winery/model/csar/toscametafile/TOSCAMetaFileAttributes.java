/*******************************************************************************
 * Copyright (c) 2013 Rene Trefft.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Rene Trefft - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.winery.model.csar.toscametafile;

/**
 * Predefined attribute names and values of a TOSCA meta file. <br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart <br />
 * <br />
 *
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 *
 */
public class TOSCAMetaFileAttributes {

	// of block 0
	final public static String TOSCA_META_VERSION = "TOSCA-Meta-Version";
	final public static String TOSCA_META_VERSION_VALUE = "1.0";
	final public static String CSAR_VERSION = "CSAR-Version";
	final public static String CSAR_VERSION_VALUE = "1.0";
	final public static String CREATED_BY = "Created-By";
	final public static String ENTRY_DEFINITIONS = "Entry-Definitions";
	final public static String TOPOLOGY = "Topology";
	final public static String DESCRIPTION = "Description";

	// of blocks > 0 (file blocks)
	final public static String NAME = "Name";
	final public static String CONTENT_TYPE = "Content-Type";

}
