/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
 * This package contains the model classes of the canonical model used for winery-specific extension of tosca definitions.
 * These extensions can not be represented in the TOSCA YAML Standard, since that standard does not expose such a capability.
 * Opposed to that, the XML Standard allows for such extensions by virtue of specifying a TOSCA Definitions file as a
 * collection of Elements <b>derived from</b> <tt>tDefinitions</tt>.
 *
 * This notably also includes the specialized KeyValue storage facilities through
 * {@link org.eclipse.winery.model.tosca.extensions.kvproperties}, impacting both EntityTemplates and EntityTypes.
 */
@javax.xml.bind.annotation.XmlSchema(
    namespace = org.eclipse.winery.model.tosca.constants.Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE,
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
package org.eclipse.winery.model.tosca.extensions;
