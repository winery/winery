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
package org.eclipse.winery.model.tosca.constants;

import javax.xml.namespace.QName;

public class QNames {

    public static final QName QNAME_BORDER_COLOR = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "bordercolor");
    public static final QName QNAME_COLOR = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "color");

    // Boolean flag to indicate that the import is generated via the Winery Properties Defintion
    public static final QName QNAME_WINERYS_PROPERTIES_DEFINITION_ATTRIBUTE = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "wpd");
}
