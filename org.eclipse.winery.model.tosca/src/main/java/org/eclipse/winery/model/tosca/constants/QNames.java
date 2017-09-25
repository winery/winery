/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.model.tosca.constants;

import javax.xml.namespace.QName;

public class QNames {

    public static final QName QNAME_BORDER_COLOR = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "bordercolor");
    public static final QName QNAME_COLOR = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "color");

    // Boolean flag to indicate that the import is generated via the Winery Properties Defintion
    public static final QName QNAME_WINERYS_PROPERTIES_DEFINITION_ATTRIBUTE = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "wpd");

    // follows the naming conventions by https://github.com/OpenTOSCA/tosca-definitions/
    public static final QName QNAME_ARTIFACT_TYPE_WAR = new QName("http://www.opentosca.org/artifacttypes", "WAR");
}
