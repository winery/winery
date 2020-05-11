/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.common.Constants;
import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFile;
import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFileParser;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.YamlRepository;
import org.eclipse.winery.repository.converter.Y2XConverter;
import org.eclipse.winery.repository.converter.support.Utils;
import org.eclipse.winery.repository.converter.support.exception.MultiException;
import org.eclipse.winery.repository.converter.support.reader.XmlReader;
import org.eclipse.winery.repository.converter.support.reader.YamlReader;
import org.eclipse.winery.repository.datatypes.ids.elements.DirectoryId;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.exceptions.WineryRepositoryException;
import org.eclipse.winery.repository.export.entries.CsarEntry;
import org.eclipse.winery.repository.export.entries.DocumentBasedCsarEntry;
import org.eclipse.winery.repository.export.entries.RepositoryRefBasedCsarEntry;
import org.eclipse.winery.repository.export.entries.XMLDefinitionsBasedCsarEntry;
import org.eclipse.winery.repository.export.entries.YAMLDefinitionsBasedCsarEntry;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFileAttributes.CONTENT_TYPE;
import static org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFileAttributes.CREATED_BY;
import static org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFileAttributes.CSAR_VERSION;
import static org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFileAttributes.ENTRY_DEFINITIONS;
import static org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFileAttributes.NAME;
import static org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFileAttributes.TOSCA_META_VERSION;

public class YamlExporter extends CsarExporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(YamlExporter.class);
    private static final String DEFINITIONS_PATH_PREFIX = "_definitions/";

    private final IRepository repository;

    public YamlExporter() {
        this.repository = RepositoryFactory.getRepository();
    }

    public YamlExporter(YamlRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns a unique name for the given definitions to be used as filename
     */
    public static String getDefinitionsName(IRepository repository, DefinitionsChildId id) {
        // the prefix is globally unique and the id locally in a namespace
        // therefore a concatenation of both is also unique
        return repository.getNamespaceManager().getPrefix(id.getNamespace()) + "__" + id.getXmlId().getEncoded();
    }

    /**
     * Writes a complete CSAR containing all necessary things reachable from the given service template
     *
     * @param entryId the id of the service template to export
     * @param out     the output stream to write to
     * @return the TOSCA meta file for the generated Csar
     */
    @Override
    public String writeCsar(IRepository repository, DefinitionsChildId entryId, OutputStream out, Map<String, Object> exportConfiguration)
        throws IOException, RepositoryCorruptException, InterruptedException, AccountabilityException, ExecutionException {
        LOGGER.trace("Starting CSAR export with {}", entryId.toString());

        Map<CsarContentProperties, CsarEntry> refMap = new HashMap<>();
        YamlToscaExportUtil exporter = new YamlToscaExportUtil();
        ExportedState exportedState = new ExportedState();
        DefinitionsChildId currentId = entryId;
        Collection<DefinitionsChildId> referencedIds;

        // Process definitions and referenced files
        do {
            String definitionsPathInsideCSAR = getDefinitionsPathInsideCSAR(repository, currentId);
            CsarContentProperties definitionsFileProperties = new CsarContentProperties(definitionsPathInsideCSAR);
            if (!YamlRepository.ROOT_TYPE_QNAME.equals(currentId.getQName())) {
                referencedIds = exporter.processTOSCA(repository, currentId, definitionsFileProperties, refMap, exportConfiguration);
                // for each entryId add license and readme files (if they exist) to the refMap
                addLicenseAndReadmeFiles(repository, currentId, refMap);

                exportedState.flagAsExported(currentId);
                exportedState.flagAsExportRequired(referencedIds);
            }

            currentId = exportedState.pop();
        } while (currentId != null);

        // Archive creation
        try (final ZipOutputStream zos = new ZipOutputStream(out)) {
            // write all referenced files
            for (Map.Entry<CsarContentProperties, CsarEntry> entry : refMap.entrySet()) {
                CsarContentProperties fileProperties = entry.getKey();
                CsarEntry ref = entry.getValue();
                LOGGER.trace("Creating {}", fileProperties.getPathInsideCsar());

                if (ref instanceof RepositoryRefBasedCsarEntry && ((RepositoryRefBasedCsarEntry) ref).getReference().getParent() instanceof DirectoryId) {
                    addArtifactTemplateToZipFile(zos, (RepositoryRefBasedCsarEntry) ref, fileProperties);
                } else {
                    addCsarEntryToArchive(zos, ref, fileProperties);
                }
            }

            // create manifest file and add it to archive
            return this.addManifest(repository, entryId, refMap, zos, exportConfiguration);
        }
    }

    public static String getDefinitionsPathInsideCSAR(IRepository repository, DefinitionsChildId id) {
        return DEFINITIONS_PATH_PREFIX
            .concat(getDefinitionsName(repository, id))
            .concat(Constants.SUFFIX_TOSCA_DEFINITIONS);
    }

    /**
     * Adds a file to an archive
     *
     * @param zos                   Output stream of the archive
     * @param csarEntry             Reference to the file that should be added to the archive
     * @param csarContentProperties Describing the path inside the archive to the file
     */
    private void addCsarEntryToArchive(ZipOutputStream zos, CsarEntry csarEntry,
                                       CsarContentProperties csarContentProperties) {
        try (InputStream is = csarEntry.getInputStream()) {
            zos.putNextEntry(new ZipEntry(csarContentProperties.getPathInsideCsar()));
            IOUtils.copy(is, zos);
            zos.closeEntry();
        } catch (Exception e) {
            LOGGER.error("Could not copy file content to ZIP outputstream", e);
        }
    }

    public Definitions convertY2X(TServiceTemplate serviceTemplate, String name, String namespace, Path path, Path outPath) {
        return new Y2XConverter().convert(serviceTemplate, name, namespace/* TODO, path, outPath*/);
    }

    public void convertY2X(InputStream zip) throws MultiException {
        Path path = Utils.unzipFile(zip);
        LOGGER.debug("Unzip path: {}", path);

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.{yml,yaml}");
        MultiException exception = Arrays.stream(Optional.ofNullable(path.toFile().listFiles()).orElse(new File[] {}))
            .map(File::toPath)
            .filter(file -> matcher.matches(file.getFileName()))
            .map(file -> {
                YamlReader reader = new YamlReader();
                try {
                    String id = file.getFileName().toString().substring(0, file.getFileName().toString().lastIndexOf("."));
                    Path fileName = file.subpath(path.getNameCount(), file.getNameCount());
                    Path fileOutPath = path.resolve("tmp");
                    String namespace = reader.getNamespace(path, fileName);
                    try (InputStream is = new FileInputStream(new File(path.toFile(), fileName.toString()))) {
                        TServiceTemplate serviceTemplate = reader.parse(is, namespace);
                        LOGGER.debug("Convert filePath = {}, fileName = {}, id = {}, namespace = {}, fileOutPath = {}",
                            path, fileName, id, namespace, fileOutPath);
                        this.convertY2X(serviceTemplate, id, namespace, path, fileOutPath);
                    } catch (Exception e) {
                        return new MultiException().add(e);
                    }
                } catch (MultiException e) {
                    return e;
                }
                return null;
            })
            .filter(Objects::nonNull)
            .reduce(MultiException::add)
            .orElse(new MultiException());
        if (exception.hasException()) throw exception;
    }

    public InputStream convertX2Y(InputStream csar) {
        Path filePath = Utils.unzipFile(csar);
        Path fileOutPath = filePath.resolve("tmp");
        try {
            TOSCAMetaFileParser parser = new TOSCAMetaFileParser();
            TOSCAMetaFile metaFile = parser.parse(filePath.resolve("TOSCA-Metadata").resolve("TOSCA.meta"));

            XmlReader reader = new XmlReader();
            try {
                String fileName = metaFile.getEntryDefinitions();
                Definitions definitions = reader.parse(filePath, Paths.get(fileName));
                this.convertX2Y(definitions, fileOutPath);
            } catch (MultiException e) {
                LOGGER.error("Convert TOSCA XML to TOSCA YAML error", e);
            }
            return Utils.zipPath(fileOutPath);
        } catch (Exception e) {
            LOGGER.error("Error", e);
            throw new AssertionError();
        }
    }

    public InputStream convertX2Y(DefinitionsChildId id) throws MultiException {
        Path path = Utils.getTmpDir(Paths.get(id.getQName().getLocalPart()));
        convertX2Y(repository.getDefinitions(id), path);
        return Utils.zipPath(path);
    }

    public String convertDefinitionsChildToYaml(DefinitionsChildId id) throws MultiException {
        Path path = Utils.getTmpDir(Paths.get(id.getQName().getLocalPart()));
        convertX2Y(repository.getDefinitions(id), path);
        // convention: single file in root contains the YAML support
        // TODO: Links in the YAML should be changed to real links into Winery
        Optional<Path> rootYamlFile;
        try {
            return Files.find(path, 1, (filePath, basicFileAttributes) -> filePath.getFileName().toString().endsWith(".yml"))
                .findAny()
                .map(p -> {
                    try {
                        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        LOGGER.debug("Could not read root file", e);
                        return "Could not read root file";
                    }
                })
                .orElseThrow(() -> {
                    MultiException multiException = new MultiException();
                    multiException.add(new WineryRepositoryException("Root YAML file not found."));
                    return multiException;
                });
        } catch (IOException e) {
            MultiException multiException = new MultiException();
            multiException.add(new WineryRepositoryException("Root YAML file not found.", e));
            throw multiException;
        }
    }

    public void convertX2Y(Definitions definitions, Path outPath) throws MultiException {
        // new X2YConverter(this.repository).convert(definitions/*, outPath*/);
    }

    private String addManifest(IRepository repository, DefinitionsChildId id, Map<CsarContentProperties, CsarEntry> refMap,
                               ZipOutputStream out, Map<String, Object> exportConfiguration) throws IOException {
        String entryDefinitionsReference = getDefinitionsPathInsideCSAR(repository, id);

        out.putNextEntry(new ZipEntry("TOSCA-Metadata/TOSCA.meta"));
        StringBuilder stringBuilder = new StringBuilder();

        // Setting Versions
        stringBuilder.append(TOSCA_META_VERSION).append(": 1.0").append("\n");
        stringBuilder.append(CSAR_VERSION).append(": 1.1").append("\n");
        stringBuilder.append(CREATED_BY).append(": Winery ").append(Environments.getInstance().getVersion()).append("\n");

        // Winery currently is unaware of tDefinitions, therefore, we use the
        // name of the service template
        stringBuilder.append(ENTRY_DEFINITIONS).append(": ").append(entryDefinitionsReference).append("\n");
        stringBuilder.append("\n");

        assert (refMap.keySet()
            .stream()
            .anyMatch(
                fileProperties -> fileProperties
                    .getPathInsideCsar()
                    .equals(entryDefinitionsReference)
            )
        );

        // Setting other files, mainly files belonging to artifacts
        for (Map.Entry<CsarContentProperties, CsarEntry> item : refMap.entrySet()) {
            final CsarEntry csarEntry = item.getValue();
            final CsarContentProperties fileProperties = item.getKey();

            stringBuilder.append(NAME).append(": ").append(fileProperties.getPathInsideCsar()).append("\n");

            String mimeType = "";

            if (csarEntry instanceof DocumentBasedCsarEntry) {
                mimeType = MimeTypes.MIMETYPE_XSD;
            } else if (csarEntry instanceof XMLDefinitionsBasedCsarEntry ||
                csarEntry instanceof YAMLDefinitionsBasedCsarEntry) {
                mimeType = MimeTypes.MIMETYPE_TOSCA_DEFINITIONS;
            } else {
                mimeType = repository.getMimeType(((RepositoryRefBasedCsarEntry) csarEntry).getReference());
            }

            stringBuilder.append(CONTENT_TYPE).append(": ").append(mimeType).append("\n");
            stringBuilder.append("\n");
        }

        String manifestString = stringBuilder.toString();
        out.write(manifestString.getBytes());
        out.closeEntry();

        return manifestString;
    }
}
