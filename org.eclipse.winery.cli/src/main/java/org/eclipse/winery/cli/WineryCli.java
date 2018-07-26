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
package org.eclipse.winery.cli;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.consistencycheck.ConsistencyChecker;
import org.eclipse.winery.repository.backend.consistencycheck.ConsistencyCheckerConfiguration;
import org.eclipse.winery.repository.backend.consistencycheck.ConsistencyCheckerProgressListener;
import org.eclipse.winery.repository.backend.consistencycheck.ConsistencyCheckerVerbosity;
import org.eclipse.winery.repository.backend.consistencycheck.ConsistencyErrorCollector;
import org.eclipse.winery.repository.backend.consistencycheck.ElementErrorList;
import org.eclipse.winery.repository.backend.filebased.FilebasedRepository;
import org.eclipse.winery.tools.copybaragenerator.CopybaraGenerator;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class WineryCli {

    public static void main(String[] args) throws Exception {
        Option repositoryPathOption = new Option("p", "path", true, "use given path as repository path");
        Option serviceTemplatesOnlyOption = new Option("so", "servicetemplatesonly", false, "checks service templates instead of the whole repository");
        Option checkDocumentationOption = new Option("cd", "checkdocumentation", false, "check existence of README.md and LICENSE. Default: No check");
        Option verboseOption = new Option("v", "verbose", false, "be verbose: Output the checked elements");
        Option generateCopybaraConfigOption = new Option("cb", "generatecopybaraconfig", true, "Generates a configuration for Copybara.");
        generateCopybaraConfigOption.setOptionalArg(true);
        Option helpOption = new Option("h", "help", false, "prints this help");

        Options options = new Options();
        options.addOption(repositoryPathOption);
        options.addOption(serviceTemplatesOnlyOption);
        options.addOption(checkDocumentationOption);
        options.addOption(verboseOption);
        options.addOption(generateCopybaraConfigOption);
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

        if (line.hasOption("cb")) {
            CopybaraGenerator copybaraGenerator = new CopybaraGenerator();
            String outfile = line.getOptionValue("cb");
            if (outfile == null) {
                String copybaraConfigFile = copybaraGenerator.generateCopybaraConfigFile();
                System.out.println(copybaraConfigFile);
            } else {
                Path file = Paths.get(outfile);
                copybaraGenerator.generateCopybaraConfigFile(file);
            }
            System.exit(0);
        }

        if (repository instanceof FilebasedRepository) {
            System.out.println("Using repository path " + ((FilebasedRepository) repository).getRepositoryRoot() + "...");
        } else {
            System.out.println("Using non-filebased repository");
        }

        doConsistencyCheck(line, repository);
    }

    private static void doConsistencyCheck(CommandLine line, IRepository repository) {
        EnumSet<ConsistencyCheckerVerbosity> verbosity;
        if (line.hasOption("v")) {
            verbosity = EnumSet.of(
                ConsistencyCheckerVerbosity.OUTPUT_NUMBER_OF_TOSCA_COMPONENTS,
                ConsistencyCheckerVerbosity.OUTPUT_CURRENT_TOSCA_COMPONENT_ID,
                ConsistencyCheckerVerbosity.OUTPUT_ERRORS
            );
        } else {
            verbosity = EnumSet.of(ConsistencyCheckerVerbosity.OUTPUT_NUMBER_OF_TOSCA_COMPONENTS);
        }

        boolean serviceTemplatesOnly = line.hasOption("so");
        boolean checkDocumentation = line.hasOption("cd");
        ConsistencyCheckerConfiguration configuration = new ConsistencyCheckerConfiguration(serviceTemplatesOnly, checkDocumentation, verbosity, repository);

        final ConsistencyChecker consistencyChecker = new ConsistencyChecker(configuration);
        int numberOfDefinitionsToCheck = consistencyChecker.numberOfDefinitionsToCheck();
        ProgressBar progressBar = new ProgressBar("Check", numberOfDefinitionsToCheck, ProgressBarStyle.ASCII);
        consistencyChecker.setConsistencyCheckerProgressListener(new ConsistencyCheckerProgressListener() {
            @Override
            public void updateProgress(float progress) {
                progressBar.stepTo((long) (progress * numberOfDefinitionsToCheck));
            }

            @Override
            public void updateProgress(float progress, String checkingDefinition) {
                progressBar.setExtraMessage("Now checking " + checkingDefinition);
                progressBar.stepTo((long) (progress * numberOfDefinitionsToCheck));
            }
        });
        progressBar.start();
        consistencyChecker.checkCorruption();
        progressBar.stop();
        ConsistencyErrorCollector errors = consistencyChecker.getErrorCollector();

        System.out.println();
        if (errors.getErrorList().isEmpty()) {
            System.out.println("No errors exist.");
        } else {
            System.out.println("Errors found in the repository:");
            System.out.println();
            for (Map.Entry<QName, ElementErrorList> qName : errors.getErrorList().entrySet()) {
                System.out.println(qName.getKey());

                ElementErrorList elementErrorList = qName.getValue();

                if (!elementErrorList.getErrors().isEmpty()) {
                    System.out.println("\tErrors:");
                    for (String error : elementErrorList.getErrors()) {
                        System.out.println("\t\t" + error);
                    }
                }

                if (!elementErrorList.getWarnings().isEmpty()) {
                    System.out.println("\n\tWarnings:");
                    for (String error : elementErrorList.getWarnings()) {
                        System.out.println("\t\t" + error);
                    }
                }

                System.out.println();
            }
            System.exit(1);
        }
    }
}
