/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
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
package org.eclipse.winery.common.propertydefinitionkv;

import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

import org.eclipse.winery.model.tosca.Namespaces;

