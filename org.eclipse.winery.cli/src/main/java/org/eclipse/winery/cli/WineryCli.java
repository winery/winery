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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.SortedSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBException;

import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
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

public class WineryCli {

	private static final Logger LOGGER = LoggerFactory.getLogger(WineryCli.class);

	private enum Verbosity {
		NOTHING,
		OUTPUT_CURRENT_TOSCA_COMPONENT_ID
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

		Verbosity verbosity;
		if (line.hasOption("v")) {
			verbosity = Verbosity.OUTPUT_CURRENT_TOSCA_COMPONENT_ID;
		} else {
			verbosity = Verbosity.NOTHING;
		}

		Optional<String> error = checkCorruptionUsingCsarExport(repository, verbosity);
		if (error.isPresent()) {
			System.out.println("Error in repository found:");
			System.out.println(error.get());
			System.exit(1);
		} else {
			System.out.println("No errors exist.");
		}
	}

	private static Optional<String> checkCorruptionUsingCsarExport(IRepository repository, Verbosity verbosity) {
		CSARExporter exporter = new CSARExporter();
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			SortedSet<TOSCAComponentId> allToscaComponentIds = repository.getAllToscaComponentIds();
			if (verbosity == Verbosity.OUTPUT_CURRENT_TOSCA_COMPONENT_ID) {
				System.out.format("Number of TOSCA definitions to check: %d\n", allToscaComponentIds.size());
			}
			for (TOSCAComponentId id : allToscaComponentIds) {
				if (verbosity == Verbosity.OUTPUT_CURRENT_TOSCA_COMPONENT_ID) {
					System.out.format("Checking %s...\n", id.toReadableString());
				}
				exporter.writeCSAR(id, os);
				try (InputStream is = new ByteArrayInputStream(os.toByteArray());
					 ZipInputStream zis = new ZipInputStream(is)) {
					ZipEntry entry;
					while ((entry = zis.getNextEntry()) != null) {
						if (entry.getName() == null) {
							return Optional.of("Empty filename in zip file");
						}
					}
				}
			}
		} catch (ArchiveException | JAXBException | IOException e) {
			LOGGER.debug("Error during checking ZIP", e);
			return Optional.of(e.getMessage());
		} catch (RepositoryCorruptException e) {
			LOGGER.debug("Repository is corrupt", e);
			return Optional.of(e.getMessage());
		}

		// no error during checking
		return Optional.empty();
	}
}
