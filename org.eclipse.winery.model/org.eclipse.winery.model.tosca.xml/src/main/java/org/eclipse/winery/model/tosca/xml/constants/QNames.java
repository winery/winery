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
package org.eclipse.winery.model.tosca.xml.constants;

import javax.xml.namespace.QName;

public class QNames {

    public static final QName QNAME_BORDER_COLOR = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "bordercolor");
    public static final QName QNAME_COLOR = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "color");

    // Boolean flag to indicate that the import is generated via the Winery Properties Defintion
    public static final QName QNAME_WINERYS_PROPERTIES_DEFINITION_ATTRIBUTE = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "wpd");

    // follows the naming conventions by https://github.com/OpenTOSCA/tosca-definitions/
    public static final QName QNAME_ARTIFACT_TYPE_WAR = new QName("http://www.opentosca.org/artifacttypes", "WAR");
}
