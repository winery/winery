/********************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.eclipse.winery.common.Constants;
import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.model.ids.GenericId;
import org.eclipse.winery.model.ids.Namespace;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.repository.xml.XmlRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.winery.common.configuration.RepositoryConfigurationObject.RepositoryProvider.YAML;

public class RepositoryUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryUtils.class);

    public static void checkGitIgnore(XmlRepository multiRepository) throws IOException {
        File ignore = new File(multiRepository.getRepositoryRoot().toFile(), Constants.FILE_GIT_IGNORE);

        if (!ignore.exists()) {
            if (ignore.createNewFile()) {
                BufferedWriter out = new BufferedWriter(new FileWriter(ignore));
                out.write("# Will ignore any file except the repositories.json and the local contents of the workspace folder.");
                out.newLine();
                out.write("/**");
                out.newLine();
                out.write("!/" + Constants.DEFAULT_LOCAL_REPO_NAME + "/");
                out.newLine();
                out.write("!/" + Constants.DEFAULT_LOCAL_REPO_NAME + "/**");
                out.close();
            }

            try {
                Files.setAttribute(ignore.toPath(), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
            } catch (UnsupportedOperationException uoe) {
                LOGGER.warn("Error when setting the attributes of the .gitignore", uoe);
            }
        }
    }

    public static boolean checkRepositoryDuplicate(String url, MultiRepository multiRepository) {
        for (IRepository frepo : multiRepository.getRepositoriesMap().keySet()) {
            if ((frepo instanceof GitBasedRepository) && (((GitBasedRepository) frepo).getRepositoryUrl() != null)) {
                if (((GitBasedRepository) frepo).getRepositoryUrl().equals(url)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getUrlSeparatorEncoded() throws UnsupportedEncodingException {
        return URLEncoder.encode(Constants.URL_SEPARATOR, StandardCharsets.UTF_8.name());
    }

    private static Optional<Namespace> getNamespaceById(GenericId id) {
        if (id instanceof DefinitionsChildId) {
            return Optional.of(((DefinitionsChildId) id).getNamespace());
        } else if (id.getParent() != null) {
            return getNamespaceById(id.getParent());
        }

        return Optional.empty();
    }

    private static IRepository getRepositoryByNamespace(Namespace ns, MultiRepository multiRepository) {
        List<IRepository> repositoryList = new ArrayList<>();

        for (IRepository repo : multiRepository.getRepositoriesMap().keySet()) {
            for (String namespace : multiRepository.getRepositoriesMap().get(repo)) {
                if (namespace.equals(ns.getDecoded())) {
                    repositoryList.add(repo);
                }
            }
        }

        if (repositoryList.size() == 1) {
            return repositoryList.get(0);
        }

        for (IRepository repo : multiRepository.getRepositoriesCommonNamespace().keySet()) {
            for (Namespace preNamespace : multiRepository.getRepositoriesCommonNamespace().get(repo)) {
                if (ns.getDecoded().contains(preNamespace.getDecoded())) {
                    repositoryList.add(repo);
                }
            }
        }

        if (repositoryList.isEmpty()) {
            return multiRepository.getLocalRepository();
        } else {
            return repositoryList.get(0);
        }
    }

    public static IRepository getRepositoryByNamespace(String ns, MultiRepository multiRepository) {
        boolean containsUrlSeparator = false;

        try {
            containsUrlSeparator = ns.contains(getUrlSeparatorEncoded());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            LOGGER.error("Error while reading the namespace", e);
        }

        if (containsUrlSeparator) {
            return getRepositoryByNamespace(new Namespace(ns, true), multiRepository);
        } else {
            return getRepositoryByNamespace(new Namespace(ns, false), multiRepository);
        }
    }

    public static IRepository getRepositoryByRef(RepositoryFileReference ref, MultiRepository multiRepository) {
        return getRepositoryById(ref.getParent(), multiRepository);
    }

    public static IRepository getRepositoryById(GenericId id, MultiRepository multiRepository) {
        Optional<List<IRepository>> optRepositories = getRepositoriesById(id, multiRepository);

        if (optRepositories.isPresent()) {
            List<IRepository> repositories = optRepositories.get();

            if (repositories.size() == 1) {
                return repositories.get(0);
            } else {
                for (IRepository repository : repositories) {
                    if (repository.exists(id)) {
                        return repository;
                    }
                }
            }
        }

        return multiRepository.getLocalRepository();
    }

    private static Optional<List<IRepository>> getRepositoriesById(GenericId id, MultiRepository multiRepository) {
        List<IRepository> repositoryList = new ArrayList<>();
        Optional<Namespace> optNamespace = getNamespaceById(id);

        if (optNamespace.isPresent()) {
            for (IRepository repo : multiRepository.getRepositoriesMap().keySet()) {
                for (String ns : multiRepository.getRepositoriesMap().get(repo)) {
                    String idNamespace = optNamespace.get().getDecoded();
                    if (idNamespace.equals(ns)) {
                        repositoryList.add(repo);
                    }
                }
            }

            if (!repositoryList.isEmpty()) {
                return Optional.of(repositoryList);
            }

            for (IRepository repo : multiRepository.getRepositoriesCommonNamespace().keySet()) {
                for (Namespace ns : multiRepository.getRepositoriesCommonNamespace().get(repo)) {
                    String idNamespace = optNamespace.get().getDecoded();
                    String repoNamespace = ns.getDecoded();
                    if (idNamespace.contains(repoNamespace)) {
                        repositoryList.add(repo);
                    }
                }
            }
        }

        return repositoryList.isEmpty()
            ? Optional.of(Collections.singletonList(multiRepository.getLocalRepository()))
            : Optional.of(repositoryList);
    }

    protected static Optional<List<IRepository>> getRepositoriesByRef(RepositoryFileReference ref, MultiRepository multiRepository) {
        return getRepositoriesById(ref.getParent(), multiRepository);
    }

    public static boolean isYamlRepository() {
        RepositoryConfigurationObject config = Environments.getInstance().getRepositoryConfig();
        return YAML == config.getProvider();
    }
}
