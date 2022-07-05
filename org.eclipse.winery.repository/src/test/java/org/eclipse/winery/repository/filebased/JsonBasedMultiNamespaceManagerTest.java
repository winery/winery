/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.filebased;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;

import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.model.ids.admin.NamespacesId;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.filebased.NamespaceProperties;
import org.eclipse.winery.repository.common.RepositoryFileReference;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonBasedMultiNamespaceManagerTest extends RepositoryTest {

    @Test
    void saveNamespaceProperties() throws Exception {
        MultiRepositoryTest.writeDependencyFile();

        Path repositoryRoot = Paths.get(Environments.getInstance().getRepositoryConfig().getRepositoryRoot());
        MultiRepository multiRepository = new MultiRepository(repositoryRoot);

        HashSet<NamespaceProperties> namespaceProperties = new HashSet<>();
        NamespaceProperties firstNamespace = new NamespaceProperties("firstNamespace", "1");
        firstNamespace.setRepositoryId("test");
        namespaceProperties.add(firstNamespace);

        NamespaceProperties secondNamespace = new NamespaceProperties("secondNamespace", "2");
        secondNamespace.setRepositoryId("test-dependency");
        namespaceProperties.add(secondNamespace);

        NamespaceProperties thirdNamespace = new NamespaceProperties("thirdNamespace", "3");
        namespaceProperties.add(thirdNamespace);

        multiRepository.getNamespaceManager().addAllPermanent(namespaceProperties);

        for (IRepository repository : multiRepository.getRepositories()) {
            Path path = repository.ref2AbsolutePath(BackendUtils.getRefOfJsonConfiguration(new NamespacesId()));

            String contents = new Scanner(path).useDelimiter("\\A").next();
            
            if (repository.getId().equals("test")) {
                assertTrue(contents.contains("\"namespace\" : \"firstNamespace\""));
            } else if (repository.getId().equals("test-dependency")) {
                assertTrue(contents.contains("\"namespace\" : \"secondNamespace\""));
            } else {
                assertTrue(contents.contains("\"namespace\" : \"thirdNamespace\""));
            }
        }
    }
}
