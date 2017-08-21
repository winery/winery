/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API, implementation, update 
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.imports.xsdimports;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.XMLConstants;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.definitions.imports.XSDImportId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImport;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.imports.genericimports.GenericImportResource;

import org.apache.xerces.xs.XSModel;
import org.restdoc.annotations.RestDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Even if we are not a component instance, we use that infrastructure to manage
 * imports. Some hacks will be necessary. However, these are less effort than
 * doing a clean design
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

	/**
	 * public required by XSDImportsResource
	 *
	 * @return null if XSD file does not exist
	 */
	public RepositoryFileReference getXSDFileReference() {
		String loc = this.getLocation();
		if (loc == null) {
			return null;
		}
		return new RepositoryFileReference(this.id, loc);
	}

	/**
	 * @return null if no file is associated
	 */
	private XSModel getXSModel() {
		final RepositoryFileReference ref = this.getXSDFileReference();
		return BackendUtils.getXSModel(ref);
	}

	@GET
	@RestDoc(methodDescription = "May be used by the modeler to generate an XML editor based on the XML schema")
	// we cannot use "MimeTypes.MIMETYPE_XSD" here as the latter is "text/xml" and org.eclipse.winery.repository.resources.AbstractComponentInstanceResource.getDefinitionsAsResponse() also produces text/xml
	@Produces("text/xsd")
	public Response getXSD() {
		String location;
		if ((location = this.getLocation()) == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		RepositoryFileReference ref = new RepositoryFileReference(this.id, location);
		return RestUtils.returnRepoPath(ref, null);
	}

}
