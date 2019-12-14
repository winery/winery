/*******************************************************************************
 * Copyright (c) 2012-2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.imports.xsdimports;

import io.swagger.annotations.ApiOperation;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.model.ids.definitions.imports.GenericImportId;
import org.eclipse.winery.model.ids.definitions.imports.XSDImportId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImport;
import org.eclipse.winery.repository.backend.ImportUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.imports.genericimports.GenericImportResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.XMLConstants;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Even if we are not a component instance, we use that infrastructure to manage imports. Some hacks will be necessary.
 * However, these are less effort than doing a clean design
 */
public class XSDImportResource extends GenericImportResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(XSDImportResource.class);


    public XSDImportResource(XSDImportId id) {
        super(id);
    }

    @Override
    protected TExtensibleElements createNewElement() {
        TImport imp = new TImport();
        imp.setImportType(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return imp;
    }

    @GET
    @ApiOperation(value = "May be used by the modeler to generate an XML editor based on the XML schema")
    // we cannot use "MimeTypes.MIMETYPE_XSD" here as the latter is "text/xml" and org.eclipse.winery.repository.resources.AbstractComponentInstanceResource.getDefinitionsAsResponse() also produces text/xml
    @Produces("text/xsd")
    public Response getXSD() {
        final Optional<String> location = ImportUtils.getLocation((GenericImportId) id);
        if (!location.isPresent()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        RepositoryFileReference ref = new RepositoryFileReference(this.id, location.get());
        return RestUtils.returnRepoPath(ref, null);
    }

    @GET
    @Path("alldeclaredelementslocalnames")
    public List<String> getAllDeclaredElementsLocalNames() {
        return RepositoryFactory.getRepository().getXsdImportManager().getAllDeclaredElementsLocalNames().stream()
            .filter(namespaceAndDefinedLocalNames -> namespaceAndDefinedLocalNames.getNamespace().equals(id.getNamespace()))
            .flatMap(namespaceAndDefinedLocalNames -> namespaceAndDefinedLocalNames.getDefinedLocalNames().stream())
            .sorted()
            .collect(Collectors.toList());
    }

    @GET
    @Path("alldefinedtypeslocalnames")
    public List<String> getAllDefinedTypesLocalNames() {
        return RepositoryFactory.getRepository().getXsdImportManager().getAllDefinedTypesLocalNames().stream()
            .filter(namespaceAndDefinedLocalNames -> namespaceAndDefinedLocalNames.getNamespace().equals(id.getNamespace()))
            .flatMap(namespaceAndDefinedLocalNames -> namespaceAndDefinedLocalNames.getDefinedLocalNames().stream())
            .sorted()
            .collect(Collectors.toList());
    }
}
