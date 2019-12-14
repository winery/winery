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
package org.eclipse.winery.repository.rest.resources.imports;

import io.swagger.annotations.Api;

import org.eclipse.winery.model.ids.EncodingUtil;
import org.eclipse.winery.model.ids.definitions.imports.WsdlImportId;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentsResource;
import org.eclipse.winery.repository.rest.resources.imports.genericimports.GenericImportsResource;
import org.eclipse.winery.repository.rest.resources.imports.wsdlimports.WsdlImportsResource;
import org.eclipse.winery.repository.rest.resources.imports.xsdimports.XSDImportsResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Arrays;
import java.util.List;

/**
 * The specification does not nest the sequence of import elements in an imports container. We introduce such a
 * container to be consistent with the other resource naming
 */
@Api(tags = "Imports")
public class ImportsResource {

    @Path("{id}/")
    public AbstractComponentsResource getXSDsResource(@PathParam("id") String id) {
        // once: decoding for browser locations
        id = EncodingUtil.URLdecode(id);
        // once again: real URI
        id = EncodingUtil.URLdecode(id);
        if (id.equals("http://www.w3.org/2001/XMLSchema")) {
            // Models http://www.w3.org/2001/XMLSchema. We do not use xsd instead of the
            // encoded namespace, because this induces special cases at many places
            return new XSDImportsResource();
        } else if (id.equalsIgnoreCase(WsdlImportId.WSDL_URI)) {
            return new WsdlImportsResource();
        } else {
            return new GenericImportsResource(id);
        }
    }

    @GET
    public List<?> getAllImportTypes() {
        // TODO use backend to determine a complete list
        return Arrays.asList("http://www.w3.org/2001/XMLSchema");
    }
}
