/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.converter.support;

import javax.xml.XMLConstants;

/**
 * Defines namespaces of YAML and related ones. For general namespaces used in Winery, see {@link
 * org.eclipse.winery.model.tosca.constants.Namespaces}
 */
public class Namespaces {
    public static final String TOSCA_NS = "http://docs.oasis-open.org/tosca/ns/simple/yaml/1.3";
    public static final String DEFAULT_NS = "http://www.eclipse.org/winery/ns/simple/yaml/1.3/default";
    public static final String YAML_NS = "http://www.yaml.org/type";
    public static final String XML_NS = XMLConstants.W3C_XML_SCHEMA_NS_URI;
}
