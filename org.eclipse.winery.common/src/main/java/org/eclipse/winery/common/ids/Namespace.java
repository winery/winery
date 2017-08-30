/*******************************************************************************
 * Copyright (c) 2013,2015 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.common.ids;

import org.eclipse.winery.common.StringEncodedAndDecoded;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Namespace extends StringEncodedAndDecoded {

	private static final Logger LOGGER = LoggerFactory.getLogger(Namespace.class);

	public Namespace(String uri, boolean URLencoded) {
		super(uri, URLencoded);
		if (StringUtils.isEmpty(uri)) {
			Namespace.LOGGER.error("Empty URI has been passed to Namespace constructor.");
			// throw new IllegalArgumentException("uri must not be empty or null.");
		}
	}
}
