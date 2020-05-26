/********************************************************************************
 * Copyright (c) 2012-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.winery.common.Constants;
import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.model.ids.GenericId;
import org.eclipse.winery.model.ids.IdUtil;
import org.eclipse.winery.model.ids.Namespace;
import org.eclipse.winery.model.ids.XmlId;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.xml.TDefinitions;
import org.eclipse.winery.repository.JAXBSupport;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.constants.MediaTypes;
import org.eclipse.winery.repository.backend.filebased.AbstractFileBasedRepository;
import org.eclipse.winery.repository.backend.filebased.OnlyNonHiddenDirectories;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.repository.xml.converter.FromCanonical;
import org.eclipse.winery.repository.xml.converter.ToCanonical;
import org.eclipse.winery.repository.xml.export.XmlModelJAXBSupport;

import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When it comes to a storage of plain files, we use Java 7's nio internally. Therefore, we intend to expose the stream
 * types offered by java.nio.Files: BufferedReader/BufferedWriter
 */
// FIXME this needs to start conversions between the canonical and the xml model
public class XmlRepository extends AbstractFileBasedRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlRepository.class);

    /**
     * @param repositoryRoot root of the filebased repository
     */
    public XmlRepository(Path repositoryRoot) {
        super(repositoryRoot);
    }

    @Override
    public org.eclipse.winery.model.tosca.TDefinitions definitionsFromRef(RepositoryFileReference ref) throws IOException {
        try {
            InputStream is = newInputStream(ref);
            Unmarshaller unmarshaller = XmlModelJAXBSupport.createUnmarshaller();
            ToCanonical converter = new ToCanonical(this);
            return converter.convert((TDefinitions) unmarshaller.unmarshal(is));
        } catch (Exception e) {
            LOGGER.info("Failed to read definitions from reference {}", ref, e);
            return null;
        }
    }

    @Override
    public boolean exists(GenericId id) {
        Path absolutePath = this.id2AbsolutePath(id);
        return Files.exists(absolutePath);
    }

    @Override
    public void putContentToFile(RepositoryFileReference ref, InputStream inputStream, MediaType mediaType) throws IOException {
        if (mediaType == null) {
            // quick hack for storing mime type called this method
            assert (ref.getFileName().endsWith(Constants.SUFFIX_MIMETYPE));
            // we do not need to store the mime type of the file containing the mime type information
        } else if (mediaType == MediaTypes.MEDIATYPE_TOSCA_DEFINITIONS) {
            LOGGER.warn("Attempting to write definitions with putContentToFile. Redirecting call to putDefinitions");
            try {
                // convert the InputStream to an object that we can throw at putDefinitions
                org.eclipse.winery.model.tosca.TDefinitions canonical = (org.eclipse.winery.model.tosca.TDefinitions) 
                    JAXBSupport.createUnmarshaller().unmarshal(inputStream);
                putDefinition(BackendUtils.getIdForRef(ref), canonical);
            } catch (JAXBException e) {
                LOGGER.error("Could not deserialize given input stream as a canonical TDefinitions instance", e);
                throw new IllegalStateException(e);
            }
        } else {
            this.setMimeType(ref, mediaType);
            Path targetPath = this.ref2AbsolutePath(ref);
            writeInputStreamToPath(targetPath, inputStream);
        }
    }

    @Override
    public void putDefinition(DefinitionsChildId id, org.eclipse.winery.model.tosca.TDefinitions content) throws IOException {
        // implementation is partially copied from BackendUtils.persist
        RepositoryFileReference ref = BackendUtils.getRefOfDefinitions(id);
        FromCanonical converter = new FromCanonical(this);
        TDefinitions definitions = converter.convert(content);
        Path serializationTarget = ref2AbsolutePath(ref);
        Files.createDirectories(serializationTarget.getParent());
        try (OutputStream out = Files.newOutputStream(serializationTarget, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            Marshaller m = XmlModelJAXBSupport.createMarshaller(true, this.getNamespaceManager().asPrefixMapper());
            m.marshal(definitions, out);
        } catch (JAXBException e) {
            LOGGER.error("Could not put content to file", e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean exists(RepositoryFileReference ref) {
        return Files.exists(this.ref2AbsolutePath(ref));
    }

    public <T extends DefinitionsChildId> SortedSet<T> getDefinitionsChildIds(Class<T> idClass, boolean omitDevelopmentVersions) {
        SortedSet<T> res = new TreeSet<>();
        String rootPathFragment = IdUtil.getRootPathFragment(idClass);
        Path dir = makeAbsolute(Paths.get(rootPathFragment));
        if (!Files.exists(dir)) {
            // return empty list if no ids are available
            return res;
        }
        assert (Files.isDirectory(dir));
        final OnlyNonHiddenDirectories onhdf = new OnlyNonHiddenDirectories();

        // list all directories contained in this directory
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, onhdf)) {
            for (Path nsP : ds) {
                // the current path is the namespace
                Namespace ns = new Namespace(nsP.getFileName().toString(), true);
                try (DirectoryStream<Path> idDS = Files.newDirectoryStream(nsP, onhdf)) {
                    for (Path idP : idDS) {
                        XmlId xmlId = new XmlId(idP.getFileName().toString(), true);
                        if (omitDevelopmentVersions) {
                            WineryVersion version = VersionUtils.getVersion(xmlId.getDecoded());

                            if (version.toString().length() > 0 && version.getWorkInProgressVersion() > 0) {
                                continue;
                            }
                        }
                        Constructor<T> constructor;
                        try {
                            constructor = idClass.getConstructor(Namespace.class, XmlId.class);
                        } catch (Exception e) {
                            LOGGER.debug("Internal error at determining id constructor", e);
                            // abort everything, return invalid result
                            return res;
                        }
                        T id;
                        try {
                            id = constructor.newInstance(ns, xmlId);
                        } catch (InstantiationException
                            | IllegalAccessException
                            | IllegalArgumentException
                            | InvocationTargetException e) {
                            LOGGER.debug("Internal error at invocation of id constructor", e);
                            // abort everything, return invalid result
                            return res;
                        }
                        res.add(id);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.debug("Cannot close ds", e);
        }

        return res;
    }
}
