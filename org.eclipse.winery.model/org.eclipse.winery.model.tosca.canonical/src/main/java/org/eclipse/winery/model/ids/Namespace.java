/*******************************************************************************
 * Copyright (c) 2013-2015 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.ids;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Namespace extends StringEncodedAndDecoded {

    private static final Logger LOGGER = LoggerFactory.getLogger(Namespace.class);

    public Namespace(String uri, boolean URLencoded) {
        super(uri, URLencoded);
        if (uri == null || uri.isEmpty()) {
            Namespace.LOGGER.error("Empty URI has been passed to Namespace constructor.");
            // throw new IllegalArgumentException("uri must not be empty or null.");
        }
    }
}
