/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.backend;

import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.JAXBSupport;
import org.eclipse.winery.repository.backend.constants.MediaTypes;
import org.eclipse.winery.repository.datatypes.ids.elements.SelfServiceMetaDataId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SelfServiceMetaDataUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelfServiceMetaDataUtils.class);

    public static void ensureDataXmlExists(IRepository repository, SelfServiceMetaDataId id) throws IOException {
        RepositoryFileReference data_xml_ref = getDataXmlRef(id);
        if (!repository.exists(data_xml_ref)) {
            final Application application = new Application();
            BackendUtils.persist(application, data_xml_ref, MediaTypes.MEDIATYPE_TEXT_XML, repository);
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

    public static Application getApplication(IRepository repository, SelfServiceMetaDataId id) {
        RepositoryFileReference data_xml_ref = getDataXmlRef(id);
        if (repository.exists(data_xml_ref)) {
            Unmarshaller u = JAXBSupport.createUnmarshaller();
            try (InputStream is = repository.newInputStream(data_xml_ref)) {
                return (Application) u.unmarshal(is);
            } catch (IOException | JAXBException e) {
                LOGGER.error("Could not read from " + data_xml_ref, e);
                return new Application();
            }
        } else {
            return getDefaultApplicationData(repository, id);
        }
    }

    private static Application getDefaultApplicationData(IRepository repository, SelfServiceMetaDataId id) {
        Application app = new Application();
        app.setIconUrl("icon.jpg");
        app.setImageUrl("image.jpg");
        final TServiceTemplate serviceTemplate = repository.getElement((ServiceTemplateId) id.getParent());
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
