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
package org.eclipse.winery.repository.resources.imports.xsdimports;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.XMLConstants;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.definitions.imports.XSDImportId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImport;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.resources.imports.genericimports.GenericImportResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
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

	// we need "unchecked", because of the parsing of the cache
	@SuppressWarnings("unchecked")
	public Collection<String> getAllDefinedLocalNames(short type) {
		RepositoryFileReference ref = this.getXSDFileReference();
		if (ref == null) {
			return Collections.emptySet();
		}
		Date lastUpdate = Repository.INSTANCE.getLastUpdate(ref);

		String cacheFileName = "definedLocalNames " + Integer.toString(type) + ".cache";
		RepositoryFileReference cacheRef = new RepositoryFileReference(this.id, cacheFileName);
		boolean cacheNeedsUpdate = true;
		if (Repository.INSTANCE.exists(cacheRef)) {
			Date lastUpdateCache = Repository.INSTANCE.getLastUpdate(cacheRef);
			if (lastUpdate.compareTo(lastUpdateCache) <= 0) {
				cacheNeedsUpdate = false;
			}
		}

		List<String> result;
		if (cacheNeedsUpdate) {

			XSModel model = this.getXSModel();
			if (model == null) {
				return Collections.emptySet();
			}
			XSNamedMap components = model.getComponents(type);
			//@SuppressWarnings("unchecked")
			int len = components.getLength();
			result = new ArrayList<>(len);
			for (int i = 0; i < len; i++) {
				XSObject item = components.item(i);
				// if queried for TYPE_DEFINITION, then XSD base types (such as IDREF) are also returned
				// We want to return only types defined in the namespace of this resource
				if (item.getNamespace().equals(this.id.getNamespace().getDecoded())) {
					result.add(item.getName());
				}
			}

			String cacheContent = null;
			try {
				cacheContent = Utils.mapper.writeValueAsString(result);
			} catch (JsonProcessingException e) {
				XSDImportResource.LOGGER.error("Could not generate cache content", e);
			}
			try {
				Repository.INSTANCE.putContentToFile(cacheRef, cacheContent, MediaType.APPLICATION_JSON_TYPE);
			} catch (IOException e) {
				XSDImportResource.LOGGER.error("Could not update cache", e);
			}
		} else {
			// read content from cache
			// cache should contain most recent information
			try (InputStream is = Repository.INSTANCE.newInputStream(cacheRef)) {
				result = Utils.mapper.readValue(is, java.util.List.class);
			} catch (IOException e) {
				XSDImportResource.LOGGER.error("Could not read from cache", e);
				result = Collections.emptyList();
			}
		}
		return result;
	}

	public Collection<String> getAllDefinedElementsLocalNames() {
		return this.getAllDefinedLocalNames(XSConstants.ELEMENT_DECLARATION);
	}

	public Collection<String> getAllDefinedTypesLocalNames() {
		return this.getAllDefinedLocalNames(XSConstants.TYPE_DEFINITION);
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
		return BackendUtils.returnRepoPath(ref, null);
	}

}
