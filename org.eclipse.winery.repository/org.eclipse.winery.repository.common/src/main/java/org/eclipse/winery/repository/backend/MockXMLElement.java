/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.backend;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class for testing getAny()
 * <p>
 * It has to be in src/main as src/test is not compiled during production, but
 * the jaxbcontext is initialized in src/test and cannot be updated in src/main
 * <p>
 * Included in {@link org.eclipse.winery.repository.JAXBSupport#initContext()}
 */
@XmlRootElement(namespace = "http://test.winery.opentosca.org", name = "MockXmlElement")
public class MockXMLElement {

    @XmlElement(namespace = "http://test.winery.opentosca.org", name = "mock")
    public String mock = "mock";
}
