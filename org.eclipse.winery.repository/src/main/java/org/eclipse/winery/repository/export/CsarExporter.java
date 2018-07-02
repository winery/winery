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
package org.eclipse.winery.repository.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.IdNames;
import org.eclipse.winery.common.ids.admin.NamespacesId;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.selfservice.Application.Options;
import org.eclipse.winery.model.selfservice.ApplicationOption;
import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.GitInfo;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IGenericRepository;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.SelfServiceMetaDataUtils;
import org.eclipse.winery.repository.backend.constants.MediaTypes;
import org.eclipse.winery.repository.configuration.Environment;
import org.eclipse.winery.repository.datatypes.ids.elements.DirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.SelfServiceMetaDataId;
import org.eclipse.winery.repository.datatypes.ids.elements.ServiceTemplateSelfServiceFilesDirectoryId;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * This class exports a CSAR crawling from the the given GenericId. Currently, only ServiceTemplates are supported.
 * commons-compress is used as an output stream should be provided. An alternative implementation is to use Java7's Zip
 * File System Provider
 */
public class CsarExporter {

    public static final String PATH_TO_NAMESPACES_PROPERTIES = "winery/Namespaces.properties";

    private static final Logger LOGGER = LoggerFactory.getLogger(CsarExporter.class);

    private static final String DEFINITONS_PATH_PREFIX = "Definitions/";
    private static final String WINERY_TEMP_DIR_PREFIX = "winerytmp";

    /**
     * Returns a unique name for the given definitions to be used as filename
     */
    private static String getDefinitionsName(IGenericRepository repository, DefinitionsChildId id) {
        // the prefix is globally unique and the id locally in a namespace
        // therefore a concatenation of both is also unique
        return repository.getNamespaceManager().getPrefix(id.getNamespace()) + "__" + id.getXmlId().getEncoded();
    }

    public static String getDefinitionsFileName(IGenericRepository repository, DefinitionsChildId id) {
        return CsarExporter.getDefinitionsName(repository, id) + Constants.SUFFIX_TOSCA_DEFINITIONS;
    }

    private static String getDefinitionsPathInsideCSAR(IGenericRepository repository, DefinitionsChildId id) {
        return CsarExporter.DEFINITONS_PATH_PREFIX + CsarExporter.getDefinitionsFileName(repository, id);
    }

    /**
     * Writes a complete CSAR containing all necessary things reachable from the given service template
     *
     * @param entryId the id of the service template to export
     * @param out     the output stream to write to
     */
    public void writeCsar(IRepository repository, DefinitionsChildId entryId, OutputStream out) throws ArchiveException, IOException, JAXBException, RepositoryCorruptException {
        CsarExporter.LOGGER.trace("Starting CSAR export with {}", entryId.toString());

        Map<RepositoryFileReference, String> refMap = new HashMap<>();
        Collection<String> definitionNames = new ArrayList<>();

        try (final ArchiveOutputStream zos = new ArchiveStreamFactory().createArchiveOutputStream("zip", out)) {
            ToscaExportUtil exporter = new ToscaExportUtil();
            Map<String, Object> conf = new HashMap<>();

            ExportedState exportedState = new ExportedState();

            DefinitionsChildId currentId = entryId;
            do {
                String defName = CsarExporter.getDefinitionsPathInsideCSAR(repository, currentId);
                definitionNames.add(defName);

                zos.putArchiveEntry(new ZipArchiveEntry(defName));
                Collection<DefinitionsChildId> referencedIds;
                referencedIds = exporter.exportTOSCA(repository, currentId, zos, refMap, conf);
                zos.closeArchiveEntry();

                exportedState.flagAsExported(currentId);
                exportedState.flagAsExportRequired(referencedIds);

                currentId = exportedState.pop();
            } while (currentId != null);

            // if we export a ServiceTemplate, data for the self-service portal might exist
            if (entryId instanceof ServiceTemplateId) {
                ServiceTemplateId serviceTemplateId = (ServiceTemplateId) entryId;
                this.addSelfServiceMetaData(repository, serviceTemplateId, refMap);
                this.addSelfServiceFiles(repository, serviceTemplateId, refMap, zos);
            }

            // now, refMap contains all files to be added to the CSAR

            // write manifest directly after the definitions to have it more at the beginning of the ZIP rather than having it at the very end
            this.addManifest(repository, entryId, definitionNames, refMap, zos);

            // used for generated XSD schemas
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer;
            try {
                transformer = tFactory.newTransformer();
            } catch (TransformerConfigurationException e1) {
                CsarExporter.LOGGER.debug(e1.getMessage(), e1);
                throw new IllegalStateException("Could not instantiate transformer", e1);
            }

            // write all referenced files
            for (RepositoryFileReference ref : refMap.keySet()) {
                String archivePath = refMap.get(ref);
                CsarExporter.LOGGER.trace("Creating {}", archivePath);
                if (ref instanceof DummyRepositoryFileReferenceForGeneratedXSD) {
                    addDummyRepositoryFileReferenceForGeneratedXSD(zos, transformer, (DummyRepositoryFileReferenceForGeneratedXSD) ref, archivePath);
                } else {
                    if (ref.getParent() instanceof DirectoryId) {
                        // special handling for artifact template directories "source" and "files"
                        addArtifactTemplateToZipFile(zos, repository, ref, archivePath);
                    } else {
                        addFileToZipArchive(zos, repository, ref, archivePath);
                        zos.closeArchiveEntry();
                    }
                }
            }

            this.addNamespacePrefixes(zos, repository);
        }
    }

    /**
     * Special handling for artifact template directories source and files
     *
     * @param zos         Output stream for the archive that should contain the file
     * @param ref         Reference to the file that should be added to the archive
     * @param archivePath Path to the file inside the archive
     * @throws IOException thrown when the temporary directory can not be created
     */
    private void addArtifactTemplateToZipFile(ArchiveOutputStream zos, IGenericRepository repository, RepositoryFileReference ref, String archivePath) throws IOException {
        GitInfo gitInfo = BackendUtils.getGitInformation((DirectoryId) ref.getParent());

        if (gitInfo == null) {
            try (InputStream is = repository.newInputStream(ref)) {
                if (is != null) {
                    ArchiveEntry archiveEntry = new ZipArchiveEntry(archivePath);
                    zos.putArchiveEntry(archiveEntry);
                    IOUtils.copy(is, zos);
                    zos.closeArchiveEntry();
                }
            } catch (Exception e) {
                CsarExporter.LOGGER.error("Could not copy file to ZIP outputstream", e);
            }
            return;
        }

        // TODO: This is not quite correct. The files should reside checked out at "source/"
        Path tempDir = Files.createTempDirectory(WINERY_TEMP_DIR_PREFIX);
        try {
            Git git = Git
                .cloneRepository()
                .setURI(gitInfo.URL)
                .setDirectory(tempDir.toFile())
                .call();
            git.checkout().setName(gitInfo.BRANCH).call();
            String path = "artifacttemplates/"
                + Util.URLencode(((ArtifactTemplateId) ref.getParent().getParent()).getQName().getNamespaceURI())
                + "/"
                + ((ArtifactTemplateId) ref.getParent().getParent()).getQName().getLocalPart()
                + "/files/";
            TArtifactTemplate template = BackendUtils.getTArtifactTemplate((DirectoryId) ref.getParent());
            addWorkingTreeToArchive(zos, template, tempDir, path);
        } catch (GitAPIException e) {
            CsarExporter.LOGGER.error(String.format("Error while cloning repo: %s / %s", gitInfo.URL, gitInfo.BRANCH), e);
        } finally {
            deleteDirectory(tempDir);
        }
    }

    /**
     * Adds a file to an archive
     *
     * @param zos         Output stream of the archive
     * @param ref         Reference to the file that should be added to the archive
     * @param archivePath Path inside the archive to the file
     */
    private void addFileToZipArchive(ArchiveOutputStream zos, IGenericRepository repository, RepositoryFileReference ref, String archivePath) {
        try (InputStream is = repository.newInputStream(ref)) {
            ArchiveEntry archiveEntry = new ZipArchiveEntry(archivePath);
            zos.putArchiveEntry(archiveEntry);
            IOUtils.copy(is, zos);
        } catch (Exception e) {
            CsarExporter.LOGGER.error("Could not copy file content to ZIP outputstream", e);
        }
    }

    /**
     * Adds a dummy file to the archive
     *
     * @param zos         Output stream of the archive
     * @param transformer Given transformer to transform the {@link DummyRepositoryFileReferenceForGeneratedXSD} to a
     *                    {@link ArchiveOutputStream}
     * @param ref         The dummy document that should be exported as an archive
     * @param archivePath The output path of the archive
     */
    private void addDummyRepositoryFileReferenceForGeneratedXSD(ArchiveOutputStream zos, Transformer transformer, DummyRepositoryFileReferenceForGeneratedXSD ref, String archivePath) throws IOException {
        ArchiveEntry archiveEntry = new ZipArchiveEntry(archivePath);
        zos.putArchiveEntry(archiveEntry);
        CsarExporter.LOGGER.trace("Special treatment for generated XSDs");
        Document document = ref.getDocument();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(zos);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            CsarExporter.LOGGER.debug("Could not serialize generated xsd", e);
        }
    }

    /**
     * Deletes a directory recursively
     *
     * @param path Path to the directory that should be deleted
     */
    private void deleteDirectory(Path path) {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> s = Files.newDirectoryStream(path)) {
                for (Path p : s) {
                    deleteDirectory(p);
                }
                Files.delete(path);
            } catch (IOException e) {
                CsarExporter.LOGGER.error("Error iterating directory " + path.toAbsolutePath(), e);
            }
        } else {
            try {
                Files.delete(path);
            } catch (IOException e) {
                CsarExporter.LOGGER.error("Error deleting file " + path.toAbsolutePath(), e);
            }
        }
    }

    /**
     * Adds a working tree to an archive
     *
     * @param zos         Output stream of the archive
     * @param template    Template of the artifact
     * @param rootDir     The root of the working tree
     * @param archivePath The path inside the archive to the working tree
     */
    private void addWorkingTreeToArchive(ArchiveOutputStream zos, TArtifactTemplate template, Path rootDir, String archivePath) {
        addWorkingTreeToArchive(rootDir.toFile(), zos, template, rootDir, archivePath);
    }

    /**
     * Adds a working tree to an archive
     *
     * @param file        The current directory to add
     * @param zos         Output stream of the archive
     * @param template    Template of the artifact
     * @param rootDir     The root of the working tree
     * @param archivePath The path inside the archive to the working tree
     */
    private void addWorkingTreeToArchive(File file, ArchiveOutputStream zos, TArtifactTemplate template, Path rootDir, String archivePath) {
        if (file.isDirectory()) {
            if (file.getName().equals(".git")) {
                return;
            }
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    addWorkingTreeToArchive(f, zos, template, rootDir, archivePath);
                }
            }
        } else {
            boolean foundInclude = false;
            boolean included = false;
            boolean excluded = false;
            for (TArtifactReference artifactReference : template.getArtifactReferences().getArtifactReference()) {
                for (Object includeOrExclude : artifactReference.getIncludeOrExclude()) {
                    if (includeOrExclude instanceof TArtifactReference.Include) {
                        foundInclude = true;
                        TArtifactReference.Include include = (TArtifactReference.Include) includeOrExclude;
                        String reference = artifactReference.getReference();
                        if (reference.endsWith("/")) {
                            reference += include.getPattern();
                        } else {
                            reference += "/" + include.getPattern();
                        }
                        reference = reference.substring(1);
                        included |= BackendUtils.isGlobMatch(reference, rootDir.relativize(file.toPath()));
                    } else if (includeOrExclude instanceof TArtifactReference.Exclude) {
                        TArtifactReference.Exclude exclude = (TArtifactReference.Exclude) includeOrExclude;
                        String reference = artifactReference.getReference();
                        if (reference.endsWith("/")) {
                            reference += exclude.getPattern();
                        } else {
                            reference += "/" + exclude.getPattern();
                        }
                        reference = reference.substring(1);
                        excluded |= BackendUtils.isGlobMatch(reference, rootDir.relativize(file.toPath()));
                    }
                }
            }

            if ((!foundInclude || included) && !excluded) {
                try (InputStream is = new FileInputStream(file)) {
                    ArchiveEntry archiveEntry = new ZipArchiveEntry(archivePath + rootDir.relativize(Paths.get(file.getAbsolutePath())));
                    zos.putArchiveEntry(archiveEntry);
                    IOUtils.copy(is, zos);
                    zos.closeArchiveEntry();
                } catch (Exception e) {
                    CsarExporter.LOGGER.error("Could not copy file to ZIP outputstream", e);
                }
            }
        }
    }

    /**
     * Writes the configured mapping namespaceprefix -> namespace to the archive
     * <p>
     * This is kind of a quick hack. TODO: during the import, the prefixes should be extracted using JAXB and stored in
     * the NamespacesResource
     */
    private void addNamespacePrefixes(ArchiveOutputStream zos, IRepository repository) throws IOException {
        Configuration configuration = repository.getConfiguration(new NamespacesId());
        if (configuration instanceof PropertiesConfiguration) {
            // Quick hack: direct serialization only works for PropertiesConfiguration
            PropertiesConfiguration pconf = (PropertiesConfiguration) configuration;
            ArchiveEntry archiveEntry = new ZipArchiveEntry(CsarExporter.PATH_TO_NAMESPACES_PROPERTIES);
            zos.putArchiveEntry(archiveEntry);
            try {
                pconf.save(zos);
            } catch (ConfigurationException e) {
                CsarExporter.LOGGER.debug(e.getMessage(), e);
                zos.write("#Could not export properties".getBytes());
                zos.write(("#" + e.getMessage()).getBytes());
            }
            zos.closeArchiveEntry();
        }
    }

    /**
     * Adds all self service meta data to the targetDir
     *
     * @param repository the repository to work from
     * @param entryId the service template to export for
     * @param targetDir the directory in the CSAR where to put the content to
     * @param refMap    is used later to create the CSAR
     */
    private void addSelfServiceMetaData(IRepository repository, ServiceTemplateId entryId, String targetDir, Map<RepositoryFileReference, String> refMap) throws IOException {
        final SelfServiceMetaDataId selfServiceMetaDataId = new SelfServiceMetaDataId(entryId);

        // This method is also called if the directory SELFSERVICE-Metadata exists without content and even if the directory does not exist at all,
        // but the ServiceTemplate itself exists.
        // The current assumption is that this is enough for an existence.
        // Thus, we have to take care of the case of an empty directory and add a default data.xml
        SelfServiceMetaDataUtils.ensureDataXmlExists(selfServiceMetaDataId);

        refMap.put(SelfServiceMetaDataUtils.getDataXmlRef(selfServiceMetaDataId), targetDir + "data.xml");

        // The schema says that the images have to exist
        // However, at a quick modeling, there might be no images
        // Therefore, we check for existence
        final RepositoryFileReference iconJpgRef = SelfServiceMetaDataUtils.getIconJpgRef(selfServiceMetaDataId);
        if (repository.exists(iconJpgRef)) {
            refMap.put(iconJpgRef, targetDir + "icon.jpg");
        }
        final RepositoryFileReference imageJpgRef = SelfServiceMetaDataUtils.getImageJpgRef(selfServiceMetaDataId);
        if (repository.exists(imageJpgRef)) {
            refMap.put(imageJpgRef, targetDir + "image.jpg");
        }

        Application application = SelfServiceMetaDataUtils.getApplication(selfServiceMetaDataId);

        // clear CSAR name as this may change.
        application.setCsarName(null);

        // hack for the OpenTOSCA container to display something
        application.setVersion("1.0");
        List<String> authors = application.getAuthors();
        if (authors.isEmpty()) {
            authors.add("Winery");
        }

        // make the patches to data.xml permanent
        try {
            BackendUtils.persist(application, SelfServiceMetaDataUtils.getDataXmlRef(selfServiceMetaDataId), MediaTypes.MEDIATYPE_TEXT_XML);
        } catch (IOException e) {
            LOGGER.error("Could not persist patches to data.xml", e);
        }

        Options options = application.getOptions();
        if (options != null) {
            SelfServiceMetaDataId id = new SelfServiceMetaDataId(entryId);
            for (ApplicationOption option : options.getOption()) {
                String url = option.getIconUrl();
                if (Util.isRelativeURI(url)) {
                    putRefIntoRefMap(targetDir, refMap, repository, id, url);
                }
                url = option.getPlanInputMessageUrl();
                if (Util.isRelativeURI(url)) {
                    putRefIntoRefMap(targetDir, refMap, repository, id, url);
                }
            }
        }
    }

    private void putRefIntoRefMap(String targetDir, Map<RepositoryFileReference, String> refMap, IRepository repository, GenericId id, String fileName) {
        RepositoryFileReference ref = new RepositoryFileReference(id, fileName);
        if (repository.exists(ref)) {
            refMap.put(ref, targetDir + fileName);
        } else {
            CsarExporter.LOGGER.error("Data corrupt: pointing to non-existent file " + ref);
        }
    }

    private void addSelfServiceMetaData(IRepository repository, ServiceTemplateId serviceTemplateId, Map<RepositoryFileReference, String> refMap) throws IOException {
        SelfServiceMetaDataId id = new SelfServiceMetaDataId(serviceTemplateId);
        // We add the selfservice information regardless of the existence. - i.e., no "if (repository.exists(id)) {"
        // This ensures that the name of the application is
        // add everything in the root of the CSAR
        String targetDir = Constants.DIRNAME_SELF_SERVICE_METADATA + "/";
        addSelfServiceMetaData(repository, serviceTemplateId, targetDir, refMap);
    }

    private void addSelfServiceFiles(IRepository repository, ServiceTemplateId serviceTemplateId, Map<RepositoryFileReference, String> refMap, ArchiveOutputStream zos) throws IOException {
        ServiceTemplateSelfServiceFilesDirectoryId selfServiceFilesDirectoryId = new ServiceTemplateSelfServiceFilesDirectoryId(serviceTemplateId);
        repository.getContainedFiles(selfServiceFilesDirectoryId)
            .forEach(repositoryFileReference -> {
                String file = IdNames.SELF_SERVICE_PORTAL_FILES + "/" + BackendUtils.getFilenameAndSubDirectory(repositoryFileReference);
                refMap.put(repositoryFileReference, file);
            });
    }

    private void addManifest(IRepository repository, DefinitionsChildId id, Collection<String> definitionNames, Map<RepositoryFileReference, String> refMap, ArchiveOutputStream out) throws IOException {
        String entryDefinitionsReference = CsarExporter.getDefinitionsPathInsideCSAR(repository, id);

        out.putArchiveEntry(new ZipArchiveEntry("TOSCA-Metadata/TOSCA.meta"));
        PrintWriter pw = new PrintWriter(out);
        // Setting Versions
        pw.println("TOSCA-Meta-Version: 1.0");
        pw.println("CSAR-Version: 1.0");
        String versionString = "Created-By: Winery " + Environment.getVersion();
        pw.println(versionString);
        // Winery currently is unaware of tDefinitions, therefore, we use the
        // name of the service template
        pw.println("Entry-Definitions: " + entryDefinitionsReference);
        pw.println();

        assert (definitionNames.contains(entryDefinitionsReference));
        for (String name : definitionNames) {
            pw.println("Name: " + name);
            pw.println("Content-Type: " + org.eclipse.winery.common.constants.MimeTypes.MIMETYPE_TOSCA_DEFINITIONS);
            pw.println();
        }

        // Setting other files, mainly files belonging to artifacts
        for (RepositoryFileReference ref : refMap.keySet()) {
            String archivePath = refMap.get(ref);
            pw.println("Name: " + archivePath);
            String mimeType;
            if (ref instanceof DummyRepositoryFileReferenceForGeneratedXSD) {
                mimeType = MimeTypes.MIMETYPE_XSD;
            } else {
                mimeType = repository.getMimeType(ref);
            }
            pw.println("Content-Type: " + mimeType);
            pw.println();
        }
        pw.flush();
        out.closeArchiveEntry();
    }
}
