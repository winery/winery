/*******************************************************************************
 * Copyright (c) 2013-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.common.constants;

/**
 * See also {@link org.eclipse.winery.repository.backend.constants.MediaTypes}
 */
public class MimeTypes {

    public static final String MIMETYPE_TOSCA_DEFINITIONS = "application/vnd.oasis.tosca.definitions";

    // text/xsd is NOT used for XSD as text/xml is rendered correctly in browsers
    public static final String MIMETYPE_XSD = "text/xml";

    // source: https://stackoverflow.com/q/332129/873282
    public static final String MIMETYPE_YAML = "text/yaml";

    public static final String MIMETYPE_ZIP = "application/zip";
}
