/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.SortedSet;
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
import org.eclipse.winery.model.tosca.propertydefinitionkv.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.propertydefinitionkv.PropertyDefinitionKVList;
import org.eclipse.winery.model.tosca.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.FilebasedRepository;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.export.CsarExporter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class WineryCli {

	private static final Logger LOGGER = LoggerFactory.getLogger(WineryCli.class);
	private static final String ARTEFACT_BE = "artefact";

	enum Verbosity {
		OUTPUT_NUMBER_OF_TOSCA_COMPONENTS,
		OUTPUT_CURRENT_TOSCA_COMPONENT_ID,
		OUTPUT_ERRORS,
		NONE
	}

	public static void main(String[] args) throws ParseException {
		Option repositoryPathOption = new Option("p", "path", true, "use given path as repository path");
		Option verboseOption = new Option("v", "verbose", false, "be verbose: Output the checked elements");
		Option helpOption = new Option("h", "help", false, "prints this help");

		Options options = new Options();
		options.addOption(repositoryPathOption);
		options.addOption(verboseOption);
		options.addOption(helpOption);
		CommandLineParser parser = new DefaultParser();
		CommandLine line = parser.parse(options, args);

		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("winery", options);
			System.exit(0);
		}

		IRepository repository;
		if (line.hasOption("p")) {
			repository = RepositoryFactory.getRepository(Paths.get(line.getOptionValue("p")));
		} else {
			repository = RepositoryFactory.getRepository();
		}
		System.out.println("Using repository path " + ((FilebasedRepository) repository).getRepositoryRoot() + "...");

		EnumSet<Verbosity> verbosity;
		if (line.hasOption("v")) {
			verbosity = EnumSet.of(Verbosity.OUTPUT_NUMBER_OF_TOSCA_COMPONENTS, Verbosity.OUTPUT_CURRENT_TOSCA_COMPONENT_ID, Verbosity.OUTPUT_ERRORS);
		} else {
			verbosity = EnumSet.of(Verbosity.OUTPUT_NUMBER_OF_TOSCA_COMPONENTS);
		}

		List<String> errors = checkCorruptionUsingCsarExport(repository, verbosity);

		System.out.println();
		if (errors.isEmpty()) {
			System.out.println("No errors exist.");
		} else {
			System.out.println("Errors in repository found:");
			System.out.println();
			for (String error : errors) {
				System.out.println(error);
			}
			System.exit(1);
		}
	}

	private static List<String> checkCorruptionUsingCsarExport(IRepository repository, EnumSet<Verbosity> verbosity) {
		List<String> res = new ArrayList<>();
		SortedSet<DefinitionsChildId> allDefintionsChildIds = repository.getAllDefinitionsChildIds();
		if (verbosity.contains(Verbosity.OUTPUT_NUMBER_OF_TOSCA_COMPONENTS)) {
			System.out.format("Number of TOSCA definitions to check: %d\n", allDefintionsChildIds.size());
		}
		if (!verbosity.contains(Verbosity.OUTPUT_CURRENT_TOSCA_COMPONENT_ID)) {
			System.out.print("Checking ");
		}

		final Path tempCsar;
		try {
			tempCsar = Files.createTempFile("Export", ".csar");
		} catch (IOException e) {
			LOGGER.debug("Could not create temp CSAR file", e);
			res.add("Could not create temp CSAR file");
			return res;
		}

		for (DefinitionsChildId id : allDefintionsChildIds) {
			if (verbosity.contains(Verbosity.OUTPUT_CURRENT_TOSCA_COMPONENT_ID)) {
				System.out.format("Checking %s...\n", id.toReadableString());
			} else {
				System.out.print(".");
			}

			checkId(res, verbosity, id);
			checkXmlSchemaValidation(repository, res, verbosity, id);
			checkReferencedQNames(repository, res, verbosity, id);
			checkPropertiesValidation(repository, res, verbosity, id);
			if (id instanceof ServiceTemplateId) {
				checkServiceTemplate(repository, res, verbosity, (ServiceTemplateId) id);
			}
			checkPlainConformance(res, verbosity, id, tempCsar);
			checkCsar(res, verbosity, id, tempCsar);
		}

		// some console output cleanup
		if (verbosity.contains(Verbosity.OUTPUT_ERRORS)) {
			if (!verbosity.contains(Verbosity.OUTPUT_CURRENT_TOSCA_COMPONENT_ID)) {
				System.out.println();
			}
		}

		return res;
	}

	private static void checkPlainConformance(List<String> res, EnumSet<Verbosity> verbosity, DefinitionsChildId id, Path tempCsar) {
		// TODO implement according to https://winery.github.io/test-repository/plain
		if (id.getNamespace().getDecoded().startsWith("http://plain.winery.opentosca.org/")) {
			if (id instanceof EntityTypeId) {
				if (id.getXmlId().getDecoded().endsWith("WithoutProperties")) {
					// TODO
				}
			}
		}
	}

	private static void checkServiceTemplate(IRepository repository, List<String> res, EnumSet<Verbosity> verbosity, ServiceTemplateId id) {
		final TServiceTemplate serviceTemplate = repository.getElement(id);
		if (serviceTemplate.getTopologyTemplate() == null) {
			return;
		}
		@NonNull final List<TNodeTemplate> nodeTemplates = serviceTemplate.getTopologyTemplate().getNodeTemplates();
		for (TNodeTemplate nodeTemplate : nodeTemplates) {
			final TNodeType nodeType = repository.getElement(new NodeTypeId(nodeTemplate.getType()));
			final WinerysPropertiesDefinition winerysPropertiesDefinition = nodeType.getWinerysPropertiesDefinition();
			if (winerysPropertiesDefinition != null) {
				PropertyDefinitionKVList list = winerysPropertiesDefinition.getPropertyDefinitionKVList();
				if (list != null) {
					// iterate on all defined properties
					for (PropertyDefinitionKV propdef : list) {
						String key = propdef.getKey();
						if (key == null) {
							printAndAddError(res, verbosity, id, "key is null");
							continue;
						}
						String value;
						// assign value, but change "null" to "" if no property is defined
						final Properties propertiesKV = ModelUtilities.getPropertiesKV(nodeTemplate);
						if (propertiesKV == null) {
							printAndAddError(res, verbosity, id, "propertiesKV of node template " + nodeTemplate.getId() + " is null");
							continue;
						}
					}
				}
			}
		}
	}

	private static void checkReferencedQNames(IRepository repository, List<String> res, EnumSet<Verbosity> verbosity, DefinitionsChildId id) {
		if (id instanceof EntityTypeId) {
			final TEntityType entityType = (TEntityType) repository.getDefinitions(id).getElement();
			final TEntityType.PropertiesDefinition propertiesDefinition = entityType.getPropertiesDefinition();
			if (propertiesDefinition != null) {
				@Nullable final QName element = propertiesDefinition.getElement();
				if (element != null) {
					if (StringUtils.isEmpty(element.getNamespaceURI())) {
						printAndAddError(res, verbosity, id, "Referenced element is not a full QName");
					}
				}
			}
		}
	}

	private static void checkXmlSchemaValidation(IRepository repository, List<String> res, EnumSet<Verbosity> verbosity, DefinitionsChildId id) {
		RepositoryFileReference refOfDefinitions = BackendUtils.getRefOfDefinitions(id);
		if (!repository.exists(refOfDefinitions)) {
			printAndAddError(res, verbosity, id, "Id exists, but corresponding XML file does not.");
			return;
		}
		try (InputStream inputStream = repository.newInputStream(refOfDefinitions)) {
			DocumentBuilder documentBuilder = ToscaDocumentBuilderFactory.INSTANCE.getSchemaAwareToscaDocumentBuilder();
			StringBuilder errorStringBuilder = new StringBuilder();
			documentBuilder.setErrorHandler(BackendUtils.getErrorHandler(errorStringBuilder));
			documentBuilder.parse(inputStream);
			String errors = errorStringBuilder.toString();
			if (!errors.isEmpty()) {
				printAndAddError(res, verbosity, id, errors);
			}
		} catch (IOException e) {
			LOGGER.debug("I/O error", e);
			printAndAddError(res, verbosity, id, "I/O error during XML validation " + e.getMessage());
		} catch (SAXException e) {
			LOGGER.debug("SAX exception", e);
			printAndAddError(res, verbosity, id, "SAX error during XML validation: " + e.getMessage());
		}
	}

	private static void validate(RepositoryFileReference xmlSchemaFileReference, @Nullable Object any, IRepository repository, List<String> res, EnumSet<Verbosity> verbosity, DefinitionsChildId id) {
		if (!(any instanceof Element)) {
			printAndAddError(res, verbosity, id, "any is not instance of Document, but " + any.getClass());
			return;
		}
		Element element = (Element) any;
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		InputStream inputStream = null;
		try {
			inputStream = repository.newInputStream(xmlSchemaFileReference);
			Source schemaFile = new StreamSource(inputStream);
			Schema schema = factory.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			validator.validate(new DOMSource(element));
		} catch (Exception e) {
			printAndAddError(res, verbosity, id, "error during validating XML schema " + e.getMessage());
			try {
				inputStream.close();
			} catch (IOException e1) {
				return;
			}
		}
	}

	public static void checkPropertiesValidation(IRepository repository, List<String> res, EnumSet<Verbosity> verbosity, DefinitionsChildId id) {
		if (id instanceof EntityTemplateId) {
			TEntityTemplate entityTemplate = (TEntityTemplate) repository.getDefinitions(id).getElement();
			if (Objects.isNull(entityTemplate.getType())) {
				// no printing necessary; type consistency is checked at other places
				return;
			}
			final TEntityType entityType = repository.getTypeForTemplate(entityTemplate);
			final WinerysPropertiesDefinition winerysPropertiesDefinition = entityType.getWinerysPropertiesDefinition();
			final TEntityType.PropertiesDefinition propertiesDefinition = entityType.getPropertiesDefinition();
			if ((winerysPropertiesDefinition != null) || (propertiesDefinition != null)) {
				final TEntityTemplate.Properties properties = entityTemplate.getProperties();
				if (properties == null) {
					printAndAddError(res, verbosity, id, "Properties required, but no properties defined");
					return;
				}
				if (winerysPropertiesDefinition != null) {
					Properties kvProperties = entityTemplate.getProperties().getKVProperties();
					if (kvProperties.isEmpty()) {
						printAndAddError(res, verbosity, id, "Properties required, but no properties set (any case)");
						return;
					}
					for (PropertyDefinitionKV propertyDefinitionKV : winerysPropertiesDefinition.getPropertyDefinitionKVList().getPropertyDefinitionKVs()) {
						String key = propertyDefinitionKV.getKey();
						if (kvProperties.get(key) == null) {
							printAndAddError(res, verbosity, id, "Property " + key + " required, but not set.");
						} else {
							// remove the key from the map to enable checking below whether a property is defined which not requried by the property definition 
							kvProperties.remove(key);
						}
					}
					// All winery-property-definition-keys have been removed from kvProperties.
					// If any key is left, this is a key not defined at the schema
					for (Object o : kvProperties.keySet()) {
						printAndAddError(res, verbosity, id, "Property " + o + " set, but not defined at schema.");
					}
				} else if (propertiesDefinition != null) {
					@Nullable final Object any = properties.getAny();
					if (any == null) {
						printAndAddError(res, verbosity, id, "Properties required, but no properties defined (any case)");
						return;
					}

					@Nullable final QName element = propertiesDefinition.getElement();
					if (element != null) {
						final Map<String, RepositoryFileReference> mapFromLocalNameToXSD = repository.getXsdImportManager().getMapFromLocalNameToXSD(new Namespace(element.getNamespaceURI(), false), false);
						final RepositoryFileReference repositoryFileReference = mapFromLocalNameToXSD.get(element.getLocalPart());
						if (repositoryFileReference == null) {
							printAndAddError(res, verbosity, id, "No Xml Schema definition found for " + element);
							return;
						}
						validate(repositoryFileReference, any, repository, res, verbosity, id);
					}
				}
			}
		}
	}

	private static void checkId(List<String> res, EnumSet<Verbosity> verbosity, DefinitionsChildId id) {
		checkNamespaceUri(res, verbosity, id);
		checkNcname(res, verbosity, id, id.getXmlId().getDecoded());
	}

	private static void checkNcname(List<String> res, EnumSet<Verbosity> verbosity, DefinitionsChildId id, String ncname) {
		if (!ncname.trim().equals(ncname)) {
			printAndAddError(res, verbosity, id, "local name starts or ends with white spaces");
		}
		if (ncname.contains(ARTEFACT_BE)) {
			printAndAddError(res, verbosity, id, "artifact is spelled with i in American English, not artefact as in British English");
		}
	}

	static void checkNamespaceUri(List<String> res, EnumSet<Verbosity> verbosity, DefinitionsChildId id) {
		Objects.requireNonNull(res);
		Objects.requireNonNull(verbosity);
		Objects.requireNonNull(id);
		String uriStr = id.getNamespace().getDecoded();
		if (!uriStr.trim().equals(uriStr)) {
			printAndAddError(res, verbosity, id, "Namespace starts or ends with white spaces");
		}
		URI uri;
		try {
			uri = new URI(uriStr);
		} catch (URISyntaxException e) {
			LOGGER.debug("Invalid URI", e);
			printAndAddError(res, verbosity, id, "Invalid URI: " + e.getMessage());
			return;
		}
		if (!uri.isAbsolute()) {
			printAndAddError(res, verbosity, id, "URI is relative");
		}
		if ((uriStr.startsWith("http://www.opentosca.org/") && (!uriStr.toLowerCase().equals(uriStr)))) {
			printAndAddError(res, verbosity, id, "opentosca URI is not lowercase");
		}
		if (uriStr.endsWith("/")) {
			printAndAddError(res, verbosity, id, "URI ends with a slash");
		}
		if (uriStr.contains(ARTEFACT_BE)) {
			printAndAddError(res, verbosity, id, "artifact is spelled with i in American English, not artefact as in British English");
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
			printAndAddError(res, verbosity, id, "Namespace URI contains tosca definitions name from other type. E.g., Namespace is ...servicetemplates..., but the type is an artifact template");
		}
	}

	private static void checkCsar(List<String> res, EnumSet<Verbosity> verbosity, DefinitionsChildId id, Path tempCsar) {
		CsarExporter exporter = new CsarExporter();
		final OutputStream outputStream;
		try {
			outputStream = Files.newOutputStream(tempCsar, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			final String error = "Could not write to temp CSAR file";
			LOGGER.debug(error, e);
			printAndAddError(res, verbosity, id, error);
			return;
		}
		try {
			exporter.writeCsar(RepositoryFactory.getRepository(), id, outputStream);
			try (InputStream inputStream = Files.newInputStream(tempCsar);
				 ZipInputStream zis = new ZipInputStream(inputStream)) {
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null) {
					if (entry.getName() == null) {
						printAndAddError(res, verbosity, id, "Empty filename in zip file");
					}
				}
			}
		} catch (ArchiveException | JAXBException | IOException e) {
			LOGGER.debug("Error during checking ZIP", e);
			printAndAddError(res, verbosity, id, "Invalid zip file");
		} catch (RepositoryCorruptException e) {
			LOGGER.debug("Repository is corrupt", e);
			printAndAddError(res, verbosity, id, "Corrupt: " + e.getMessage());
		}
	}

	public static void printAndAddError(List<String> res, EnumSet<Verbosity> verbosity, DefinitionsChildId id, String error) {
		if (verbosity.contains(Verbosity.OUTPUT_ERRORS)) {
			if (!verbosity.contains(Verbosity.OUTPUT_CURRENT_TOSCA_COMPONENT_ID)) {
				System.out.println();
			}
			System.out.println(error);
		}
		res.add(id.toReadableString() + ": " + error);
	}
}
