/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources;

import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

public interface IHasTypeReference {

	/**
	 * @return the QName of the type with full namespace, never null (according
	 *         to spec)
	 */
	QName getType();

	/**
	 * Sets the type and directly persists the resource
	 */
	Response setType(QName type);

	/**
	 * Calls setType(QName) with QName.valueOf(typeStr)
	 *
	 * Directly persists the resource
	 *
	 * @param typeStr a textual representation of a QName
	 */
	Response setType(String typeStr);

}
