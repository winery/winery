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
package org.eclipse.winery.repository.backend.constants;

import org.eclipse.winery.common.constants.MimeTypes;

import org.apache.tika.mime.MediaType;

/**
 * see also {@link org.eclipse.winery.common.constants.MimeTypes}
 */
public class MediaTypes {

	public static final MediaType MEDIATYPE_TOSCA_DEFINITIONS = MediaType.parse(MimeTypes.MIMETYPE_TOSCA_DEFINITIONS);
	public static final MediaType MEDIATYPE_APPLICATION_JSON = MediaType.parse("application/json");
	public static final MediaType MEDIATYPE_TEXT_XML = MediaType.parse("text/xml");
	public static final MediaType MEDIATYPE_XSD = MediaType.parse(MimeTypes.MIMETYPE_XSD);

}
