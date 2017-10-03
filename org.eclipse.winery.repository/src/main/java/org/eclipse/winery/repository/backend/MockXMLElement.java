/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
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
@XmlRootElement(namespace = "http://test.winery.opentosca.org", name = "MockXmlElement")
public class MockXMLElement {

	@XmlElement(namespace = "http://test.winery.opentosca.org", name = "mock")
	public String mock = "mock";
}
