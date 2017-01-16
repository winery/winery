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
package org.eclipse.winery.repository.resources._support;

import java.io.IOException;

public interface IPersistable {

	/**
	 * @throws IOException if content could not be updated in the repository
	 * @throws IllegalStateException if an JAXBException occurred. This should
	 *             never happen.
	 */
	void persist() throws IOException;
}
