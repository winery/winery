/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.backend.consistencycheck;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ToscaDocumentBuilderFactory;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.definitions.EntityTemplateId;
import org.eclipse.winery.common.ids.definitions.EntityTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKVList;
import org.eclipse.winery.model.tosca.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.export.CsarExporter;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.annotations.NonNull;
import org.eclipse.jgit.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ConsistencyChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsistencyChecker.class);
    private static final String ARTEFACT_BE = "artefact";

    public static @NonNull
    ConsistencyErrorLogger checkCorruption(@NonNull ConsistencyCheckerConfiguration configuration) {
        ConsistencyCheckerProgressListener listener = new ConsistencyCheckerProgressListener() {
        };
        return checkCorruption(configuration, listener);
    }

    public static @NonNull
    ConsistencyErrorLogger checkCorruption(@NonNull ConsistencyCheckerConfiguration configuration,
                                           @NonNull ConsistencyCheckerProgressListener progressListener) {
        Set<DefinitionsChildId> allDefinitionsChildIds = configuration.getRepository().getAllDefinitionsChildIds();
        if (configuration.isServiceTemplatesOnly()) {
            allDefinitionsChildIds = allDefinitionsChildIds.stream().filter(id -> id instanceof ServiceTemplateId).collect(Collectors.toSet());
        }
        if (configuration.getVerbosity().contains(ConsistencyCheckerVerbosity.OUTPUT_NUMBER_OF_TOSCA_COMPONENTS)) {
            System.out.format("Number of TOSCA definitions to check: %d\n", allDefinitionsChildIds.size());
        }

        ConsistencyErrorLogger errorLogger = checkAllDefinitions(allDefinitionsChildIds, configuration, progressListener);

        // some console output cleanup
        if (configuration.getVerbosity().contains(ConsistencyCheckerVerbosity.OUTPUT_ERRORS)
            && !configuration.getVerbosity().contains(ConsistencyCheckerVerbosity.OUTPUT_CURRENT_TOSCA_COMPONENT_ID)) {
            System.out.println();
        }

        return errorLogger;
    }

    /**
     * Checks whether a README.md and a LICENSE file exists for the given definitions child id.
     */
    private static void checkDocumentation(ConsistencyErrorLogger errorLogger, ConsistencyCheckerConfiguration configuration, DefinitionsChildId id) {
        checkFileExistence(errorLogger, configuration, id, "README.md");
        checkFileExistence(errorLogger, configuration, id, "LICENSE");
    }

    /**
     * Checks whether the given filename exists within the given defintions child id
     */
    private static void checkFileExistence(ConsistencyErrorLogger errorLogger, ConsistencyCheckerConfiguration configuration, DefinitionsChildId id, String filename) {
        RepositoryFileReference repositoryFileReference = new RepositoryFileReference(id, filename);
        if (!configuration.getRepository().exists(repositoryFileReference)) {
            printAndAddWarning(errorLogger, configuration.getVerbosity(), id, filename + " does not exist.");
        }
    }

    private static void checkPlainConformance(ConsistencyErrorLogger errorLogger, EnumSet<ConsistencyCheckerVerbosity> verbosity, DefinitionsChildId id, Path tempCsar) {
        // TODO implement according to https://winery.github.io/test-repository/plain
        /*if (id.getNamespace().getDecoded().startsWith("http://plain.winery.opentosca.org/")) {
            if (id instanceof EntityTypeId) {
                if (id.getXmlId().getDecoded().endsWith("WithoutProperties")) {
                    // TODO
                }
            }
        }*/
    }

    private static void checkServiceTemplate(ConsistencyErrorLogger errorLogger, ConsistencyCheckerConfiguration configuration, ServiceTemplateId id) {
        TServiceTemplate serviceTemplate;
        try {
            serviceTemplate = configuration.getRepository().getElement(id);
        } catch (IllegalStateException e) {
            LOGGER.debug("Illegal State Exception during reading of id {}", id.toReadableString(), e);
            printAndAddError(errorLogger, configuration.getVerbosity(), id, "Reading error " + e.getMessage());
            return;
        }
        if (serviceTemplate.getTopologyTemplate() == null) {
            return;
        }
        @NonNull final List<TNodeTemplate> nodeTemplates = serviceTemplate.getTopologyTemplate().getNodeTemplates();
        for (TNodeTemplate nodeTemplate : nodeTemplates) {
            NodeTypeId nodeTypeId = new NodeTypeId(nodeTemplate.getType());
            TNodeType nodeType;
            try {
                nodeType = configuration.getRepository().getElement(nodeTypeId);
            } catch (IllegalStateException e) {
                LOGGER.debug("Illegal State Exception during reading of id {}", nodeTypeId.toReadableString(), e);
                printAndAddError(errorLogger, configuration.getVerbosity(), nodeTypeId, "Reading error " + e.getMessage());
                return;
            }
            final WinerysPropertiesDefinition winerysPropertiesDefinition = nodeType.getWinerysPropertiesDefinition();
            if (winerysPropertiesDefinition != null) {
                PropertyDefinitionKVList list = winerysPropertiesDefinition.getPropertyDefinitionKVList();
                if (list != null) {
                    // iterate on all defined properties
                    for (PropertyDefinitionKV propdef : list) {
                        String key = propdef.getKey();
                        if (key == null) {
                            printAndAddError(errorLogger, configuration.getVerbosity(), id, "key is null");
                            continue;
                        }
                        // assign value, but change "null" to "" if no property is defined
                        final Map<String, String> propertiesKV = ModelUtilities.getPropertiesKV(nodeTemplate);
                        if (propertiesKV == null) {
                            printAndAddError(errorLogger, configuration.getVerbosity(), id, "propertiesKV of node template " + nodeTemplate.getId() + " is null");
                        }
                    }
                }
            }
        }
    }

    private static void checkReferencedQNames(ConsistencyErrorLogger errorLogger, ConsistencyCheckerConfiguration configuration, DefinitionsChildId id) {
        if (id instanceof EntityTypeId) {
            TEntityType entityType;
            try {
                entityType = (TEntityType) configuration.getRepository().getDefinitions(id).getElement();
            } catch (IllegalStateException e) {
                LOGGER.debug("Illegal State Exception during reading of id {}", id.toReadableString(), e);
                printAndAddError(errorLogger, configuration.getVerbosity(), id, "Reading error " + e.getMessage());
                return;
            }
            final TEntityType.PropertiesDefinition propertiesDefinition = entityType.getPropertiesDefinition();
            if (propertiesDefinition != null) {
                @Nullable final QName element = propertiesDefinition.getElement();
                if (element != null && StringUtils.isEmpty(element.getNamespaceURI())) {
                    printAndAddError(errorLogger, configuration.getVerbosity(), id, "Referenced element is not a full QName");
                }
            }
        }
    }

    private static void checkXmlSchemaValidation(ConsistencyErrorLogger errorLogger, ConsistencyCheckerConfiguration configuration, DefinitionsChildId id) {
        RepositoryFileReference refOfDefinitions = BackendUtils.getRefOfDefinitions(id);
        if (!configuration.getRepository().exists(refOfDefinitions)) {
            printAndAddError(errorLogger, configuration.getVerbosity(), id, "Id exists, but corresponding XML file does not.");
            return;
        }
        try (InputStream inputStream = configuration.getRepository().newInputStream(refOfDefinitions)) {
            DocumentBuilder documentBuilder = ToscaDocumentBuilderFactory.INSTANCE.getSchemaAwareToscaDocumentBuilder();
            StringBuilder errorStringBuilder = new StringBuilder();
            documentBuilder.setErrorHandler(BackendUtils.getErrorHandler(errorStringBuilder));
            documentBuilder.parse(inputStream);
            String errors = errorStringBuilder.toString();
            if (!errors.isEmpty()) {
                printAndAddError(errorLogger, configuration.getVerbosity(), id, errors);
            }
        } catch (IOException e) {
            LOGGER.debug("I/O error", e);
            printAndAddError(errorLogger, configuration.getVerbosity(), id, "I/O error during XML validation " + e.getMessage());
        } catch (SAXException e) {
            LOGGER.debug("SAX exception", e);
            printAndAddError(errorLogger, configuration.getVerbosity(), id, "SAX error during XML validation: " + e.getMessage());
        }
    }

    private static void validate(ConsistencyErrorLogger errorLogger, RepositoryFileReference xmlSchemaFileReference, @Nullable Object any, ConsistencyCheckerConfiguration configuration, DefinitionsChildId id) {
        if (!(any instanceof Element)) {
            printAndAddError(errorLogger, configuration.getVerbosity(), id, "any is not instance of Document, but " + any.getClass());
            return;
        }
        Element element = (Element) any;
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try (InputStream inputStream = configuration.getRepository().newInputStream(xmlSchemaFileReference)) {
            Source schemaFile = new StreamSource(inputStream);
            Schema schema = factory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            validator.validate(new DOMSource(element));
        } catch (Exception e) {
            printAndAddError(errorLogger, configuration.getVerbosity(), id, "error during validating XML schema " + e.getMessage());
        }
    }

    private static void checkPropertiesValidation(ConsistencyErrorLogger errorLogger, ConsistencyCheckerConfiguration configuration, DefinitionsChildId id) {
        if (id instanceof EntityTemplateId) {
            TEntityTemplate entityTemplate;
            try {
                // TEntityTemplate is abstract. IRepository does not offer getElement for abstract ids
                // Therefore, we have to use the detour through getDefinitions
                entityTemplate = (TEntityTemplate) configuration.getRepository().getDefinitions((EntityTemplateId) id).getElement();
            } catch (IllegalStateException e) {
                LOGGER.debug("Illegal State Exception during reading of id {}", id.toReadableString(), e);
                printAndAddError(errorLogger, configuration.getVerbosity(), id, "Reading error " + e.getMessage());
                return;
            }
            if (Objects.isNull(entityTemplate.getType())) {
                // no printing necessary; type consistency is checked at other places
                return;
            }
            TEntityType entityType;
            try {
                entityType = configuration.getRepository().getTypeForTemplate(entityTemplate);
            } catch (IllegalStateException e) {
                LOGGER.debug("Illegal State Exception during getting type for template {}", entityTemplate.getId(), e);
                printAndAddError(errorLogger, configuration.getVerbosity(), id, "Reading error " + e.getMessage());
                return;
            }
            final WinerysPropertiesDefinition winerysPropertiesDefinition = entityType.getWinerysPropertiesDefinition();
            final TEntityType.PropertiesDefinition propertiesDefinition = entityType.getPropertiesDefinition();
            if ((winerysPropertiesDefinition != null) || (propertiesDefinition != null)) {
                final TEntityTemplate.Properties properties = entityTemplate.getProperties();
                if (properties == null) {
                    printAndAddError(errorLogger, configuration.getVerbosity(), id, "Properties required, but no properties defined");
                    return;
                }
                if (winerysPropertiesDefinition != null) {
                    Map<String, String> kvProperties = entityTemplate.getProperties().getKVProperties();
                    if (kvProperties.isEmpty()) {
                        printAndAddError(errorLogger, configuration.getVerbosity(), id, "Properties required, but no properties set (any case)");
                        return;
                    }
                    for (PropertyDefinitionKV propertyDefinitionKV : winerysPropertiesDefinition.getPropertyDefinitionKVList().getPropertyDefinitionKVs()) {
                        String key = propertyDefinitionKV.getKey();
                        if (kvProperties.get(key) == null) {
                            printAndAddError(errorLogger, configuration.getVerbosity(), id, "Property " + key + " required, but not set.");
                        } else {
                            // removePermanentPrefix the key from the map to enable checking below whether a property is defined which not requried by the property definition 
                            kvProperties.remove(key);
                        }
                    }
                    // All winery-property-definition-keys have been removed from kvProperties.
                    // If any key is left, this is a key not defined at the schema
                    for (Object o : kvProperties.keySet()) {
                        printAndAddError(errorLogger, configuration.getVerbosity(), id, "Property " + o + " set, but not defined at schema.");
                    }
                } else if (propertiesDefinition != null) {
                    @Nullable final Object any = properties.getAny();
                    if (any == null) {
                        printAndAddError(errorLogger, configuration.getVerbosity(), id, "Properties required, but no properties defined (any case)");
                        return;
                    }

                    @Nullable final QName element = propertiesDefinition.getElement();
                    if (element != null) {
                        final Map<String, RepositoryFileReference> mapFromLocalNameToXSD = configuration.getRepository().getXsdImportManager().getMapFromLocalNameToXSD(new Namespace(element.getNamespaceURI(), false), false);
                        final RepositoryFileReference repositoryFileReference = mapFromLocalNameToXSD.get(element.getLocalPart());
                        if (repositoryFileReference == null) {
                            printAndAddError(errorLogger, configuration.getVerbosity(), id, "No Xml Schema definition found for " + element);
                            return;
                        }
                        validate(errorLogger, repositoryFileReference, any, configuration, id);
                    }
                }
            }
        }
    }

    private static void checkId(ConsistencyErrorLogger errorLogger, EnumSet<ConsistencyCheckerVerbosity> verbosity, DefinitionsChildId id) {
        checkNamespaceUri(errorLogger, verbosity, id);
        checkNcname(errorLogger, verbosity, id, id.getXmlId().getDecoded());
    }

    private static void checkNcname(ConsistencyErrorLogger errorLogger, EnumSet<ConsistencyCheckerVerbosity> verbosity, DefinitionsChildId id, String ncname) {
        if (!ncname.trim().equals(ncname)) {
            printAndAddError(errorLogger, verbosity, id, "local name starts or ends with white spaces");
        }
        if (ncname.toLowerCase().contains(ARTEFACT_BE)) {
            printAndAddError(errorLogger, verbosity, id, "artifact is spelled with i in American English, not artefact as in British English");
        }
    }

    public static void checkNamespaceUri(ConsistencyErrorLogger errorLogger, EnumSet<ConsistencyCheckerVerbosity> verbosity, DefinitionsChildId id) {
        Objects.requireNonNull(errorLogger);
        Objects.requireNonNull(verbosity);
        Objects.requireNonNull(id);

        String uriStr = id.getNamespace().getDecoded();
        if (!uriStr.trim().equals(uriStr)) {
            printAndAddError(errorLogger, verbosity, id, "Namespace starts or ends with white spaces");
        }
        URI uri;
        try {
            uri = new URI(uriStr);
        } catch (URISyntaxException e) {
            LOGGER.debug("Invalid URI", e);
            printAndAddError(errorLogger, verbosity, id, "Invalid URI: " + e.getMessage());
            return;
        }
        if (!uri.isAbsolute()) {
            printAndAddError(errorLogger, verbosity, id, "URI is relative");
        }
        if ((uriStr.startsWith("http://www.opentosca.org/") && (!uriStr.toLowerCase().equals(uriStr)))) {
            printAndAddError(errorLogger, verbosity, id, "opentosca URI is not lowercase");
        }
        if (uriStr.endsWith("/")) {
            printAndAddError(errorLogger, verbosity, id, "URI ends with a slash");
        }
        if (uriStr.contains(ARTEFACT_BE)) {
            printAndAddError(errorLogger, verbosity, id, "artifact is spelled with i in American English, not artefact as in British English");
        }
        boolean namespaceUriContainsDifferentType = DefinitionsChildId.ALL_TOSCA_COMPONENT_ID_CLASSES.stream()
            .filter(definitionsChildIdClass -> !definitionsChildIdClass.isAssignableFrom(id.getClass()))
            // we have the issue that nodetypeimplementation also contains nodetype
            // we do the quick hack and check for plural s and /
            .flatMap(definitionsChildIdClass -> {
                final String lowerCaseIdClass = Util.getTypeForComponentId(definitionsChildIdClass).toLowerCase();
                return Stream.of(lowerCaseIdClass + "s", lowerCaseIdClass + "/");
            })
            .anyMatch(definitionsChildName -> uriStr.contains(definitionsChildName));
        if (namespaceUriContainsDifferentType) {
            printAndAddError(errorLogger, verbosity, id, "Namespace URI contains tosca definitions name from other type. E.g., Namespace is ...servicetemplates..., but the type is an artifact template");
        }
    }

    private static void checkCsar(ConsistencyErrorLogger errorLogger, EnumSet<ConsistencyCheckerVerbosity> verbosity, DefinitionsChildId id, Path tempCsar) {
        CsarExporter exporter = new CsarExporter();
        try (OutputStream outputStream = Files.newOutputStream(tempCsar, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            try {
                exporter.writeCsar(RepositoryFactory.getRepository(), id, outputStream);
            } catch (ArchiveException e) {
                LOGGER.debug("Error during checking ZIP", e);
                printAndAddError(errorLogger, verbosity, id, "Invalid zip file: " + e.getMessage());
                return;
            } catch (JAXBException e) {
                LOGGER.debug("Error during checking ZIP", e);
                printAndAddError(errorLogger, verbosity, id, "Some XML could not be parsed: " + e.getMessage() + " " + e.toString());
                return;
            } catch (IOException e) {
                LOGGER.debug("Error during checking ZIP", e);
                printAndAddError(errorLogger, verbosity, id, "I/O error: " + e.getMessage());
                return;
            } catch (RepositoryCorruptException e) {
                LOGGER.debug("Repository is corrupt", e);
                printAndAddError(errorLogger, verbosity, id, "Corrupt: " + e.getMessage());
                return;
            } catch (Exception e) {
                printAndAddError(errorLogger, verbosity, id, e.getMessage());
                return;
            }
        } catch (Exception e) {
            final String error = "Could not write to temp CSAR file";
            LOGGER.debug(error, e);
            printAndAddError(errorLogger, verbosity, id, error);
            return;
        }

        try (InputStream inputStream = Files.newInputStream(tempCsar);
             ZipInputStream zis = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName() == null) {
                    printAndAddError(errorLogger, verbosity, id, "Empty filename in zip file");
                }
            }
        } catch (Exception e) {
            final String error = "Could not read from temp CSAR file";
            LOGGER.debug(error, e);
            printAndAddError(errorLogger, verbosity, id, error);
            return;
        }
    }

    private static ConsistencyErrorLogger checkAllDefinitions(Set<DefinitionsChildId> allDefinitionsChildIds, ConsistencyCheckerConfiguration configuration, ConsistencyCheckerProgressListener progressListener) {
        final ConsistencyErrorLogger errorLogger = new ConsistencyErrorLogger();
        final Path tempCsar;

        try {
            tempCsar = Files.createTempFile("Export", ".csar");
        } catch (IOException e) {
            LOGGER.debug("Could not create temp CSAR file", e);
            errorLogger.error("Could not create temp CSAR file");
            return errorLogger;
        }

        float elementsChecked = 0;
        for (DefinitionsChildId id : allDefinitionsChildIds) {
            float progress = ++elementsChecked / allDefinitionsChildIds.size();
            if (configuration.getVerbosity().contains(ConsistencyCheckerVerbosity.OUTPUT_CURRENT_TOSCA_COMPONENT_ID)) {
                progressListener.updateProgress(progress, id.toReadableString());
            } else {
                progressListener.updateProgress(progress);
            }

            checkId(errorLogger, configuration.getVerbosity(), id);
            checkXmlSchemaValidation(errorLogger, configuration, id);
            checkReferencedQNames(errorLogger, configuration, id);
            checkPropertiesValidation(errorLogger, configuration, id);
            if (id instanceof ServiceTemplateId) {
                checkServiceTemplate(errorLogger, configuration, (ServiceTemplateId) id);
            }
            if (configuration.isCheckDocumentation()) {
                checkDocumentation(errorLogger, configuration, id);
            }
            checkPlainConformance(errorLogger, configuration.getVerbosity(), id, tempCsar);
            checkCsar(errorLogger, configuration.getVerbosity(), id, tempCsar);
        }

        return errorLogger;
    }

    private static void printAndAddError(ConsistencyErrorLogger errorLogger, EnumSet<ConsistencyCheckerVerbosity> verbosity, DefinitionsChildId id, String error) {
        printError(verbosity, error);
        errorLogger.error(id, error);
    }

    private static void printAndAddWarning(ConsistencyErrorLogger errorLogger, EnumSet<ConsistencyCheckerVerbosity> verbosity, DefinitionsChildId id, String error) {
        printError(verbosity, error);
        errorLogger.warning(id, error);
    }

    private static void printError(EnumSet<ConsistencyCheckerVerbosity> verbosity, String error) {
        if (verbosity.contains(ConsistencyCheckerVerbosity.OUTPUT_ERRORS)) {
            if (!verbosity.contains(ConsistencyCheckerVerbosity.OUTPUT_CURRENT_TOSCA_COMPONENT_ID)) {
                System.out.println();
            }
            System.out.println(error);
        }
    }
}
