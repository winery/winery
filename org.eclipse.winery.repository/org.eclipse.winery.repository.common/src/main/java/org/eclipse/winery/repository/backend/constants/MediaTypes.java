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
