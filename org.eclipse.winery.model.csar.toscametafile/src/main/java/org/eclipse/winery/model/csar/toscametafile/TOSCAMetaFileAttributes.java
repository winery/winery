/*******************************************************************************
 * Copyright (c) 2013-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.csar.toscametafile;

/**
 * Predefined attribute names and values of a TOSCA meta file
 */
public class TOSCAMetaFileAttributes {

    // of block 0
    final public static String TOSCA_META_VERSION = "TOSCA-Meta-Version";
    final public static String TOSCA_META_VERSION_VALUE = "1.0";
    final public static String CSAR_VERSION = "CSAR-Version";
    final public static String CSAR_VERSION_VALUE = "1.0";
    final public static String YAML_CSAR_VERSION_VALUE = "1.1";
    final public static String CREATED_BY = "Created-By";
    final public static String ENTRY_DEFINITIONS = "Entry-Definitions";
    final public static String TOPOLOGY = "Topology";
    final public static String DESCRIPTION = "Description";

    // of blocks > 0 (file blocks)
    final public static String CONTENT_TYPE = "Content-Type";
    final public static String HASH = "SHA-256";
    final public static String NAME = "Name";
    final public static String IMMUTABLE_ADDRESS = "Swarm-address";
}
