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
 ********************************************************************************/
package org.eclipse.winery.repository.backend.filebased;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.Unmarshaller;

import org.eclipse.winery.common.Constants;
import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XmlId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.repository.JAXBSupport;

import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When it comes to a storage of plain files, we use Java 7's nio internally. Therefore, we intend to expose the stream
 * types offered by java.nio.Files: BufferedReader/BufferedWriter
 */
public class XMLRepository extends AbstractFileBasedRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLRepository.class);

    /**
     * @param repositoryRoot root of the filebased repository
     */
    public XMLRepository(Path repositoryRoot) {
        super(repositoryRoot);
    }

    @Override
    public Definitions definitionsFromRef(RepositoryFileReference ref) throws IOException {
        try {
            InputStream is = newInputStream(ref);
            Unmarshaller unmarshaller = JAXBSupport.createUnmarshaller();
            return (Definitions) unmarshaller.unmarshal(is);
        } catch (Exception e) {
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
        } else {
            this.setMimeType(ref, mediaType);
        }
        Path targetPath = this.ref2AbsolutePath(ref);
        writeInputStreamToPath(targetPath, inputStream);
    }

    @Override
    public boolean exists(RepositoryFileReference ref) {
        return Files.exists(this.ref2AbsolutePath(ref));
    }

    public <T extends DefinitionsChildId> SortedSet<T> getDefinitionsChildIds(Class<T> idClass, boolean omitDevelopmentVersions) {
        SortedSet<T> res = new TreeSet<>();
        String rootPathFragment = Util.getRootPathFragment(idClass);
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
                            XMLRepository.LOGGER.debug("Internal error at determining id constructor", e);
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
                            XMLRepository.LOGGER.debug("Internal error at invocation of id constructor", e);
                            // abort everything, return invalid result
                            return res;
                        }
                        res.add(id);
                    }
                }
            }
        } catch (IOException e) {
            XMLRepository.LOGGER.debug("Cannot close ds", e);
        }

        return res;
    }
}
