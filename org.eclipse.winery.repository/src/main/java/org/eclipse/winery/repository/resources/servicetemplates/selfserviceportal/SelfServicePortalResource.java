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
package org.eclipse.winery.repository.resources.servicetemplates.selfserviceportal;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.selfservice.Application.Options;
import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.repository.JAXBSupport;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.datatypes.ids.elements.SelfServiceMetaDataId;
import org.eclipse.winery.repository.resources._support.IPersistable;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;

import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfServicePortalResource implements IPersistable {

	private static final Logger LOGGER = LoggerFactory.getLogger(SelfServicePortalResource.class);

	public final RepositoryFileReference data_xml_ref;
	public final RepositoryFileReference icon_jpg_ref;
	public final RepositoryFileReference image_jpg_ref;

	private final ServiceTemplateResource serviceTemplateResource;

	private final Application application;

	private final SelfServiceMetaDataId id;


	public SelfServicePortalResource(ServiceTemplateId serviceTemplateId) {
		this(null, serviceTemplateId);
	}

	public SelfServicePortalResource(ServiceTemplateResource serviceTemplateResource) {
		this(serviceTemplateResource, (ServiceTemplateId) serviceTemplateResource.getId());
	}

	/**
	 * @param serviceTemplateResource may be null
	 * @param serviceTemplateId       the id, must not be null
	 */
	private SelfServicePortalResource(ServiceTemplateResource serviceTemplateResource, ServiceTemplateId serviceTemplateId) {
		this.serviceTemplateResource = serviceTemplateResource;
		this.id = new SelfServiceMetaDataId(serviceTemplateId);
		this.data_xml_ref = new RepositoryFileReference(this.id, "data.xml");
		this.icon_jpg_ref = new RepositoryFileReference(this.id, "icon.jpg");
		this.image_jpg_ref = new RepositoryFileReference(this.id, "image.jpg");
		this.application = this.getData();
	}

	SelfServiceMetaDataId getId() {
		return this.id;
	}

	public void ensureDataXmlExists() {
		if (!Repository.INSTANCE.exists(this.data_xml_ref)) {
			// this.application is already initialized with a default value.
			// So we just need to persist this resource
			BackendUtils.persist(this);
		}
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public Application getData() {
		if (Repository.INSTANCE.exists(this.data_xml_ref)) {
			Unmarshaller u = JAXBSupport.createUnmarshaller();
			try (InputStream is = Repository.INSTANCE.newInputStream(this.data_xml_ref)) {
				return (Application) u.unmarshal(is);
			} catch (IOException | JAXBException e) {
				SelfServicePortalResource.LOGGER.error("Could not read from " + this.data_xml_ref, e);
				return new Application();
			}
		} else {
			return this.getDefaultApplicationData();
		}
	}

	private Application getDefaultApplicationData() {
		Application app = new Application();
		app.setIconUrl("icon.jpg");
		app.setImageUrl("image.jpg");
		if (this.serviceTemplateResource != null) {
			app.setDisplayName(this.serviceTemplateResource.getName());
			List<TDocumentation> documentation = this.serviceTemplateResource.getServiceTemplate().getDocumentation();
			if ((documentation != null) && (!documentation.isEmpty())) {
				TDocumentation doc = documentation.get(0);
				List<Object> content = doc.getContent();
				if ((content != null) && (!content.isEmpty())) {
					app.setDescription(content.get(0).toString());
				}
			}
		}
		return app;
	}

	@Override
	public void persist() throws IOException {
		BackendUtils.persist(this.application, this.data_xml_ref, MediaType.TEXT_XML_TYPE);
	}

	@PUT
	@Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
	public Response onPutXML(Application data) {
		String content = Utils.getXMLAsString(data);
		return BackendUtils.putContentToFile(this.data_xml_ref, content, MediaType.TEXT_XML_TYPE);
	}

	@Path("icon.jpg")
	@GET
	public Response getIcon(@HeaderParam("If-Modified-Since") String modified) {
		RepositoryFileReference ref = new RepositoryFileReference(this.id, "icon.jpg");
		return BackendUtils.returnRepoPath(ref, modified);
	}

	@Path("icon.jpg")
	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response putIcon(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataBodyPart body) {
		ensureDataXmlExists();
		RepositoryFileReference ref = new RepositoryFileReference(this.id, "icon.jpg");
		return BackendUtils.putContentToFile(ref, uploadedInputStream, body.getMediaType());
	}

	@Path("image.jpg")
	@GET
	public Response getImage(@HeaderParam("If-Modified-Since") String modified) {
		RepositoryFileReference ref = new RepositoryFileReference(this.id, "image.jpg");
		return BackendUtils.returnRepoPath(ref, modified);
	}

	@Path("image.jpg")
	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response putImage(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataBodyPart body) {
		ensureDataXmlExists();
		RepositoryFileReference ref = new RepositoryFileReference(this.id, "image.jpg");
		return BackendUtils.putContentToFile(ref, uploadedInputStream, body.getMediaType());
	}

	@Path("displayname")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response onPutOnDisplayName(Application value) {
		this.application.setDisplayName(value.getDisplayName());
		return BackendUtils.persist(this);
	}

	@Path("description")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response onPutOnDescription(Application value) {
		this.application.setDescription(value.getDescription());
		return BackendUtils.persist(this);
	}

	@Path("options/")
	public OptionsResource getOptionsResource() {
		Options options = this.application.getOptions();
		if (options == null) {
			options = new Options();
			this.application.setOptions(options);
		}
		return new OptionsResource(options.getOption(), this);
	}

	/**
	 * @return the internal application object. Used for the export.
	 */
	public Application getApplication() {
		return this.application;
	}

	/**
	 * Used in JSP only
	 */
	@Path("xml")
	@GET
	@Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_XML})
	public String getApplicationAsXMLStringEncoded() {
		String res;
		if (Repository.INSTANCE.exists(this.data_xml_ref)) {
			StringWriter sw = new StringWriter();
			try (InputStream is = Repository.INSTANCE.newInputStream(this.data_xml_ref)) {
				IOUtils.copy(is, sw);
			} catch (IOException e) {
				SelfServicePortalResource.LOGGER.error("Could not read from file", e);
			}
			res = sw.toString();
		} else {
			// return skeleton for application
			// application object is already filled with default values if no file exists in repo
			res = Utils.getXMLAsString(this.getApplication());
		}
		return res;
	}
}
