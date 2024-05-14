/*******************************************************************************
 * Copyright (c) 2021-2023 Contributors to the Eclipse Foundation
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.winery.common.Constants;
import org.eclipse.winery.model.researchobject.ResearchObject;
import org.eclipse.winery.repository.JAXBSupport;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.repository.datatypes.ids.elements.ResearchObjectDirectoryId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResearchObjectUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResearchObjectUtils.class);

    public static RepositoryFileReference getMetaDataXmlRef(ResearchObjectDirectoryId id) {
        return new RepositoryFileReference(id, Constants.DIRNAME_RESEARCH_OBJECT_METADATA_FILE);
    }

    public static RepositoryFileReference getFilesRef(ResearchObjectDirectoryId id) {
        return new RepositoryFileReference(id, Constants.DIRNAME_RESEARCH_OBJECT_FILES);
    }

    public static Path getFilesPath(IRepository repository, RepositoryFileReference files_ref) {
        return repository.ref2AbsolutePath(files_ref);
    }

    public static ResearchObject getResearchObject(IRepository repository, ResearchObjectDirectoryId id) {
        RepositoryFileReference data_xml_ref = getMetaDataXmlRef(id);
        if (repository.exists(data_xml_ref)) {
            Unmarshaller u = JAXBSupport.createUnmarshaller();
            try (InputStream is = repository.newInputStream(data_xml_ref)) {
                return (ResearchObject) u.unmarshal(is);
            } catch (IOException | JAXBException e) {
                LOGGER.error("Could not read from " + data_xml_ref, e);
                return new ResearchObject();
            }
        } else {
            LOGGER.info("No MetaData is stored for ResearchObject " + id);
            return new ResearchObject();
        }
    }

    public static ResearchObject.Metadata getResearchObjectMetadata(IRepository repository, ResearchObjectDirectoryId id) {
        ResearchObject.Metadata metadata = getResearchObject(repository, id).getMetadata();
        if (metadata == null) {
            metadata = new ResearchObject.Metadata();
        }
        return metadata;
    }

    public static InputStream putResearchObjectMetadata(ResearchObject.Metadata metadata, IRepository repository, ResearchObjectDirectoryId id) {
        ResearchObject ro = getResearchObject(repository, id);
        ro.setMetadata(metadata);
        return getAsInputStream(ro, repository);
    }

    public static ResearchObject.Publication getResearchObjectPublication(IRepository repository, ResearchObjectDirectoryId id) {
        ResearchObject.Publication publication = getResearchObject(repository, id).getPublication();
        if (publication == null) {
            publication = new ResearchObject.Publication();
        }
        return publication;
    }

    public static InputStream putResearchObjectPublication(ResearchObject.Publication publication, IRepository repository, ResearchObjectDirectoryId id) {
        ResearchObject ro = getResearchObject(repository, id);
        ro.setPublication(publication);
        return getAsInputStream(ro, repository);
    }

    private static InputStream getAsInputStream(ResearchObject ro, IRepository repository) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Marshaller m = JAXBSupport.createMarshaller(true, repository.getNamespaceManager().asPrefixMapper());
            m.marshal(ro, out);
            byte[] data = out.toByteArray();
            try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
                return in;
            }
        } catch (IOException | JAXBException e) {
            LOGGER.error("Could not read " + ro.getMetadata().getTitle(), e);
            throw new IllegalStateException(e);
        }
    }

    public static String correctPath(String pathString) {
        Path path = Paths.get(pathString);
        if (path.startsWith(File.separator)) {
            return path.toString().substring(1);
        }
        return path.toString();
    }

    public static boolean isURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
