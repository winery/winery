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

package org.eclipse.winery.tools.deployablecomponents.crawler;

import org.eclipse.winery.tools.deployablecomponents.commons.Dockerfile;

import java.io.IOException;
import java.util.List;

/* Extend this interface to support more sources of dockerfiles.
The crawler implementation has to take care of possible rate limits of the corresponding service provider!
*/
public interface ICrawlerImplementation {

    List<Dockerfile> crawlDockerfiles() throws IOException;

    void setStartPoint(int number);
}
