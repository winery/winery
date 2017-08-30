/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.backend;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.JAXBSupport;
import org.eclipse.winery.repository.backend.constants.MediaTypes;
import org.eclipse.winery.repository.datatypes.ids.elements.SelfServiceMetaDataId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfServiceMetaDataUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(SelfServiceMetaDataUtils.class);

	public static void ensureDataXmlExists(SelfServiceMetaDataId id) throws IOException {
		RepositoryFileReference data_xml_ref = getDataXmlRef(id);
		if (!RepositoryFactory.getRepository().exists(data_xml_ref)) {
			final Application application = new Application();
			BackendUtils.persist(application, data_xml_ref, MediaTypes.MEDIATYPE_TEXT_XML);
		}
	}

	public static RepositoryFileReference getDataXmlRef(SelfServiceMetaDataId id) {
		return new RepositoryFileReference(id, "data.xml");
	}

	public static RepositoryFileReference getIconJpgRef(SelfServiceMetaDataId id) {
		return new RepositoryFileReference(id, "icon.jpg");
	}

	public static RepositoryFileReference getImageJpgRef(SelfServiceMetaDataId id) {
		return new RepositoryFileReference(id, "image.jpg");
	}

	public static Application getApplication(SelfServiceMetaDataId id) {
		RepositoryFileReference data_xml_ref = getDataXmlRef(id);
		if (RepositoryFactory.getRepository().exists(data_xml_ref)) {
			Unmarshaller u = JAXBSupport.createUnmarshaller();
			try (InputStream is = RepositoryFactory.getRepository().newInputStream(data_xml_ref)) {
				return (Application) u.unmarshal(is);
			} catch (IOException | JAXBException e) {
				LOGGER.error("Could not read from " + data_xml_ref, e);
				return new Application();
			}
		} else {
			return getDefaultApplicationData(id);
		}
	}

	private static Application getDefaultApplicationData(SelfServiceMetaDataId id) {
		Application app = new Application();
		app.setIconUrl("icon.jpg");
		app.setImageUrl("image.jpg");
		final TServiceTemplate serviceTemplate = RepositoryFactory.getRepository().getElement((ServiceTemplateId) id.getParent());
		app.setDisplayName(serviceTemplate.getName());
		List<TDocumentation> documentation = serviceTemplate.getDocumentation();
		if ((documentation != null) && (!documentation.isEmpty())) {
			TDocumentation doc = documentation.get(0);
			List<Object> content = doc.getContent();
			if ((content != null) && (!content.isEmpty())) {
				app.setDescription(content.get(0).toString());
			}
		}
		return app;
	}
}
