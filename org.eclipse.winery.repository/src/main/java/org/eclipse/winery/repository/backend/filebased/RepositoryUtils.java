/********************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.backend.filebased;

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

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.admin.AdminId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.elements.ToscaElementId;
import org.eclipse.winery.repository.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RepositoryUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryUtils.class);

    private final MultiRepository multiRepository;

    public RepositoryUtils(MultiRepository repository) {
        this.multiRepository = repository;
    }

    protected void checkGitIgnore() throws IOException {
        File ignore = new File(this.multiRepository.getRepositoryDep().toFile(), Constants.FILE_GIT_IGNORE);

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

    protected static boolean checkRepositoryDuplicate(String url) {
        for (FilebasedRepository frepo : MultiRepository.getRepositoriesMap().keySet()) {
            if ((frepo instanceof GitBasedRepository) && (((GitBasedRepository) frepo).getRepositoryUrl() != null)) {
                if (((GitBasedRepository) frepo).getRepositoryUrl().equals(url)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected String getUrlSeparatorEncoded() throws UnsupportedEncodingException {
        return URLEncoder.encode(Constants.URL_SEPARATOR, StandardCharsets.UTF_8.name());
    }

    protected Optional<Namespace> getNamespaceById(GenericId id) {

        if (id instanceof DefinitionsChildId) {
            return Optional.of(((DefinitionsChildId) id).getNamespace());
        }

        return Optional.empty();
    }

    protected FilebasedRepository getRepositoryByNamespace(Namespace ns) {
        List<FilebasedRepository> repositoryList = new ArrayList<>();

        for (FilebasedRepository repo : this.multiRepository.getRepositoriesMap().keySet()) {
            for (String namespace : this.multiRepository.getRepositoriesMap().get(repo)) {
                if (namespace.equals(ns.getDecoded())) {
                    repositoryList.add(repo);
                }
            }
        }

        if (repositoryList.size() == 1) {
            return repositoryList.get(0);
        }

        for (FilebasedRepository repo : this.multiRepository.getRepositoriesCommonNamespace().keySet()) {
            for (Namespace preNamespace : this.multiRepository.getRepositoriesCommonNamespace().get(repo)) {
                if (ns.getDecoded().contains(preNamespace.getDecoded())) {
                    repositoryList.add(repo);
                }
            }
        }

        if (repositoryList.isEmpty()) {
            return this.multiRepository.getLocalRepository();
        } else {
            return repositoryList.get(0);
        }
    }

    protected FilebasedRepository getRepositoryByNamespace(String ns) {
        boolean containsUrlSeparator = false;

        try {
            containsUrlSeparator = ns.contains(getUrlSeparatorEncoded());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            LOGGER.error("Error while reading the namespace", e);
        }

        if (containsUrlSeparator) {
            return getRepositoryByNamespace(new Namespace(ns, true));
        } else {
            return getRepositoryByNamespace(new Namespace(ns, false));
        }
    }

    protected FilebasedRepository getRepositoryByRef(RepositoryFileReference ref) {
        return getRepositoryById(ref.getParent());
    }

    protected FilebasedRepository getRepositoryById(GenericId id) {

        Optional<List<FilebasedRepository>> optRepositories = getRepositoriesById(id);

        if (optRepositories.isPresent()) {
            List<FilebasedRepository> repositories = optRepositories.get();

            if (repositories.size() == 1) {
                return repositories.get(0);
            } else {
                for (FilebasedRepository repository : repositories) {
                    if (repository.exists(id)) {
                        return repository;
                    }
                }
            }
        }

        return this.multiRepository.getLocalRepository();
    }

    protected Optional<List<FilebasedRepository>> getRepositoriesById(GenericId id) {
        List<FilebasedRepository> repositoryList = new ArrayList<>();

        if (id instanceof AdminId || id instanceof ToscaElementId) {
            return Optional.of(Collections.singletonList(this.multiRepository.getLocalRepository()));
        }

        Optional<Namespace> optNamespace = getNamespaceById(id);

        if (optNamespace.isPresent()) {

            for (FilebasedRepository repo : this.multiRepository.getRepositoriesMap().keySet()) {
                for (String ns : this.multiRepository.getRepositoriesMap().get(repo)) {
                    String idNamespace = optNamespace.get().getDecoded();
                    if (idNamespace.equals(ns)) {
                        repositoryList.add(repo);
                    }
                }
            }

            if (!repositoryList.isEmpty()) {
                return Optional.of(repositoryList);
            }

            for (FilebasedRepository repo : this.multiRepository.getRepositoriesCommonNamespace().keySet()) {
                for (Namespace ns : this.multiRepository.getRepositoriesCommonNamespace().get(repo)) {
                    String idNamespace = optNamespace.get().getDecoded();
                    String repoNamespace = ns.getDecoded();
                    if (idNamespace.contains(repoNamespace)) {
                        repositoryList.add(repo);
                    }
                }
            }
        }

        return repositoryList.isEmpty() ? Optional.of(Collections.singletonList(this.multiRepository.getLocalRepository())) : Optional.of(repositoryList);
    }

    protected Optional<List<FilebasedRepository>> getRepositoriesByRef(RepositoryFileReference ref) {
        return getRepositoriesById(ref.getParent());
    }

    private Optional<List<FilebasedRepository>> searchRepositoriesById(GenericId id) {
        List<FilebasedRepository> repositoryList = new ArrayList<>();
        for (FilebasedRepository repo : this.multiRepository.getRepositoriesMap().keySet()) {
            String relativePath = repo.id2RelativePath(id).toString();
            for (String ns : this.multiRepository.getRepositoriesMap().get(repo)) {
                if (relativePath.equals(ns)) {
                    repositoryList.add(repo);
                }
            }
        }
        return Optional.of(repositoryList);
    }
}
