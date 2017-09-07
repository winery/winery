/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation and/or initial documentation
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
import java.util.SortedSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;

import org.eclipse.winery.common.TOSCADocumentBuilderFactory;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.FilebasedRepository;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.export.CSARExporter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.compress.archivers.ArchiveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class WineryCli {

	private static final Logger LOGGER = LoggerFactory.getLogger(WineryCli.class);

	private enum Verbosity {
		OUTPUT_NUMBER_OF_TOSCA_COMPONENTS,
		OUTPUT_CURRENT_TOSCA_COMPONENT_ID,
		OUTPUT_ERROS
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
			verbosity = EnumSet.of(Verbosity.OUTPUT_NUMBER_OF_TOSCA_COMPONENTS, Verbosity.OUTPUT_CURRENT_TOSCA_COMPONENT_ID, Verbosity.OUTPUT_ERROS);
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
		SortedSet<TOSCAComponentId> allToscaComponentIds = repository.getAllToscaComponentIds();
		if (verbosity.contains(Verbosity.OUTPUT_NUMBER_OF_TOSCA_COMPONENTS)) {
			System.out.format("Number of TOSCA definitions to check: %d\n", allToscaComponentIds.size());
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

		for (TOSCAComponentId id : allToscaComponentIds) {
			if (verbosity.contains(Verbosity.OUTPUT_CURRENT_TOSCA_COMPONENT_ID)) {
				System.out.format("Checking %s...\n", id.toReadableString());
			} else {
				System.out.print(".");
			}

			checkId(res, verbosity, id);
			checkXmlSchemaValidation(repository, res, verbosity, id);
			checkCsar(res, verbosity, id, tempCsar);
		}

		// some console output cleanup
		if (verbosity.contains(Verbosity.OUTPUT_ERROS)) {
			if (!verbosity.contains(Verbosity.OUTPUT_CURRENT_TOSCA_COMPONENT_ID)) {
				System.out.println();
			}
		}

		return res;
	}

	private static void checkXmlSchemaValidation(IRepository repository, List<String> res, EnumSet<Verbosity> verbosity, TOSCAComponentId id) {
		try (InputStream inputStream = repository.newInputStream(BackendUtils.getRefOfDefinitions(id))) {
			Definitions definitions = repository.getDefinitions(id);
			DocumentBuilder documentBuilder = TOSCADocumentBuilderFactory.INSTANCE.getSchemaAwareToscaDocumentBuilder();
			StringBuilder errorStringBuilder = new StringBuilder();
			documentBuilder.setErrorHandler(BackendUtils.getErrorHandler(errorStringBuilder));
			documentBuilder.parse(inputStream);
			String errors = errorStringBuilder.toString();
			if (!errors.isEmpty()) {
				printAndAddError(res, verbosity, id, errors);
			}
		} catch (IOException e) {
			printAndAddError(res, verbosity, id, "I/O error during XML validation " + e.getMessage());
		} catch (SAXException e) {
			printAndAddError(res, verbosity, id, "SAX error during XML validation: " + e.getMessage());
		}
	}

	private static void checkId(List<String> res, EnumSet<Verbosity> verbosity, TOSCAComponentId id) {
		checkUri(res, verbosity, id, id.getNamespace().getDecoded());
		checkNcname(res, verbosity, id, id.getXmlId().getDecoded());
	}

	private static void checkNcname(List<String> res, EnumSet<Verbosity> verbosity, TOSCAComponentId id, String ncname) {
		if (!ncname.trim().equals(ncname)) {
			printAndAddError(res, verbosity, id, "local name starts or ends with white spaces");
		}
	}

	private static void checkUri(List<String> res, EnumSet<Verbosity> verbosity, TOSCAComponentId id, String UriStr) {
		if (!UriStr.trim().equals(UriStr)) {
			printAndAddError(res, verbosity, id, "Namespace starts or ends with white spaces");
		}
		URI uri;
		try {
			uri = new URI(UriStr);
		} catch (URISyntaxException e) {
			LOGGER.debug("Invalid URI", e);
			printAndAddError(res, verbosity, id, "Invalid URI: " + e.getMessage());
			return;
		}
		if (!uri.isAbsolute()) {
			printAndAddError(res, verbosity, id, "URI is relative");
		}
	}

	private static void checkCsar(List<String> res, EnumSet<Verbosity> verbosity, TOSCAComponentId id, Path tempCsar) {
		CSARExporter exporter = new CSARExporter();
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
			exporter.writeCSAR(id, outputStream);
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

	public static void printAndAddError(List<String> res, EnumSet<Verbosity> verbosity, TOSCAComponentId id, String error) {
		if (verbosity.contains(Verbosity.OUTPUT_ERROS)) {
			if (!verbosity.contains(Verbosity.OUTPUT_CURRENT_TOSCA_COMPONENT_ID)) {
				System.out.println();
			}
			System.out.println(error);
		}
		res.add(id.toReadableString() + ": " + error);
	}
}
