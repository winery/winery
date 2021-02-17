/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.tools.copybaragenerator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.eclipse.winery.common.Constants;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopybaraGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CopybaraGenerator.class);

    private final IRepository repository;

    public CopybaraGenerator() {
        this.repository = RepositoryFactory.getRepository();
    }

    public CopybaraGenerator(IRepository repository) {
        this.repository = repository;
    }

    public String generateOriginFilesConfig() {
        SortedSet<DefinitionsChildId> allDefinitionsChildIds = repository.getAllDefinitionsChildIds();
        return allDefinitionsChildIds.stream()
            .filter(id -> {
                RepositoryFileReference repositoryFileReference = new RepositoryFileReference(id, Constants.LICENSE_FILE_NAME);
                if (!repository.exists(repositoryFileReference)) {
                    return false;
                }
                try (InputStream inputStream = repository.newInputStream(repositoryFileReference)) {
                    // we put the whole license file text into a string and check if it starts with "Apache License" after
                    // trimming it
                    final String licenceString = "Apache License";
                    String fileAsString = IOUtils.toString(inputStream);

                    return fileAsString.trim().startsWith(licenceString);
                } catch (IOException e) {
                    LOGGER.error("Could not create input stream for {}", repositoryFileReference.toString(), e);
                    return false;
                }
            })
            .map(id -> BackendUtils.getPathInsideRepo(id))
            .collect(Collectors.joining("**\",\n        \"", "origin_files = glob([\"README.md\", \"LICENSE\", \"", "**\"]),"));
    }

    public String generateCopybaraConfigFile() {
        StringJoiner copybaraConfig = new StringJoiner("\n");
        copybaraConfig.add("urlOrigin = \"https://github.com/OpenTOSCA/tosca-definitions-internal.git\"");
        copybaraConfig.add("urlDestination = \"file:///tmp/copybara/tosca-definitions-public\"");
        copybaraConfig.add("core.workflow(");
        copybaraConfig.add("    name = \"default\",");
        copybaraConfig.add("    origin = git.origin(");
        copybaraConfig.add("        url = urlOrigin,");
        copybaraConfig.add("        ref = \"master\",");
        copybaraConfig.add("    ),");
        copybaraConfig.add("    destination = git.destination(");
        copybaraConfig.add("        url = urlDestination,");
        copybaraConfig.add("        fetch = \"master\",");
        copybaraConfig.add("        push = \"master\",");
        copybaraConfig.add("    ),");
        copybaraConfig.add("    authoring = authoring.pass_thru(\"OpenTOSCA Bot <opentosca@iaas.uni-stuttgart.de>\"),");
        copybaraConfig.add("    " + generateOriginFilesConfig());
        copybaraConfig.add("    destination_files = glob([\"**\"], exclude = [\"README_INTERNAL.md\"]),");
        copybaraConfig.add(")");
        return copybaraConfig.toString();
    }

    public void generateCopybaraConfigFile(Path targetFile) throws IOException {
        String copyBaraConfig = generateCopybaraConfigFile();
        Files.write(targetFile, copyBaraConfig.getBytes(StandardCharsets.UTF_8));
    }
}
