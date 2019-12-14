/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
/**
 * This package defines the data structures for key/value property handling
 *
 * The XML Schema is generated based on the user configuration. The namespace
 * for the schema is the namespace of the respective type with
 * {@code /propertiesdefinition/<localname>} appended, where {@code <localname>}
 * is the local name of the entity type, where the properties definition is
 * defined.
 *
 */
@XmlSchema(namespace = Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, elementFormDefault = XmlNsForm.QUALIFIED)
package org.eclipse.winery.model.tosca.kvproperties;

import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

import org.eclipse.winery.model.tosca.constants.Namespaces;

