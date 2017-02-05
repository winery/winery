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
package org.eclipse.winery.repository.backend;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class for testing getAny()
 *
 * It has to be in src/main as src/test is not compiled during production, but
 * the jaxbcontext is initialized in src/test and cannot be updated in src/main
 *
 * Included in {@link org.eclipse.winery.repository.JAXBSupport.initContext()}
 */
@XmlRootElement
public class MockXMLElement {

	@XmlElement
	public String mock = "mock";
}
