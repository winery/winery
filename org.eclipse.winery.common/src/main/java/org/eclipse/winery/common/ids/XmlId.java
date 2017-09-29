/**
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
package org.eclipse.winery.common.ids;

import org.eclipse.winery.common.StringEncodedAndDecoded;

/**
 * Handles an ID given in the XML
 *
 * We need to have this class as IDs are also passed at URIs at requests. To
 * ease handling, we use StringEncodedAndDecoded
 *
 * There is no check for valid XMLids (AKA allowed NCname characters). This is
 * OK as, for instance, properties make use of this fact and store the name as
 * ID
 */
public class XmlId extends StringEncodedAndDecoded {

	public XmlId(String id, boolean URLencoded) {
		super(id, URLencoded);
	}

}
