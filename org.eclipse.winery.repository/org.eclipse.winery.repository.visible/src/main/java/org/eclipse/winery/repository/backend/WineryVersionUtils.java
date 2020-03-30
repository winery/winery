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

package org.eclipse.winery.repository.backend;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.filebased.MultiRepository;

public class WineryVersionUtils {
    /**
     * Collects all the definitions which describe the same component in different versions.
     *
     * @param id The {@link DefinitionsChildId} for which all versions should be collected.
     * @return A set of definitions describing the same component in different versions.
     */
    public static SortedSet<? extends DefinitionsChildId> getOtherVersionDefinitionsFromDefinition(DefinitionsChildId id, IRepository repository) {
        SortedSet<? extends DefinitionsChildId> allDefinitionsChildIds = repository.getAllDefinitionsChildIds(id.getClass());

        final String componentName = VersionUtils.getNameWithoutVersion(id.getXmlId().getDecoded());

        allDefinitionsChildIds.removeIf(definition -> {
            if (definition.getNamespace().compareTo(id.getNamespace()) == 0) {
                String name = definition.getNameWithoutVersion();
                return !name.equals(componentName);
            }
            return true;
        });

        return allDefinitionsChildIds;
    }

    /**
     * Collects the versions of the given definition and sets editable flags
     *
     * @param id the {@link DefinitionsChildId} describing the "base" component.
     * @param repo
     * @return A list of available versions of the specified component.
     */
    public static List<WineryVersion> getAllVersionsOfOneDefinition(DefinitionsChildId id, IRepository repo) {
        return getVersionsList(id, new WineryVersion[1], repo);
    }

    public static WineryVersion getCurrentVersionWithAllFlags(DefinitionsChildId id, IRepository repo) {
        WineryVersion[] currentVersionWithFlags = new WineryVersion[1];
        getVersionsList(id, currentVersionWithFlags, repo);
        return currentVersionWithFlags[0];
    }

    /**
     * @param current returns the current version in element [0] of this variable. Has to be non-null.
     * @return a list of available versions
     */
    private static List<WineryVersion> getVersionsList(DefinitionsChildId id, final WineryVersion[] current, IRepository repository) {
        List<WineryVersion> versionList = getOtherVersionDefinitionsFromDefinition(id, repository)
            .stream()
            .map(element -> {
                // FIXME gotta do something about this
                WineryVersion version = VersionUtils.getVersionWithCurrentFlag(((DefinitionsChildId) element).getXmlId().getDecoded(), id.getXmlId().getDecoded());
                if (version.isCurrentVersion()) {
                    current[0] = version;
                }
                return version;
            })
            // sort descending, so that the latest version comes first
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());

        // explicitly set the latest version and releasable flag
        versionList.get(0).setLatestVersion(true);
        versionList.get(0).setReleasable(true);

        boolean changesInFile = false;
        if (current[0].isVersionedInWinery() && repository instanceof MultiRepository) {
            for (IRepository inner : ((MultiRepository) repository).getRepositories()) {
                // notably not instanceof because possible subclasses of GitRepository may need separate handling
                if (inner.getClass().equals(GitBasedRepository.class)) {
                    GitBasedRepository gitRepo = (GitBasedRepository) inner;
                    if (gitRepo.hasChangesInFile(BackendUtils.getRefOfDefinitions(id))) {
                        changesInFile = true;
                    }
                }
            }
        }
        if (current[0].isVersionedInWinery() && repository instanceof GitBasedRepository) {
            GitBasedRepository gitRepo = (GitBasedRepository) repository;
            if (gitRepo.hasChangesInFile(BackendUtils.getRefOfDefinitions(id))) {
                changesInFile = true;
            }
            if (!current[0].isLatestVersion()) {
                // The current version may still be releasable, if it's the latest WIP version of a component version.
                List<WineryVersion> collect = versionList.stream()
                    .filter(version -> version.getComponentVersion().equals(current[0].getComponentVersion()))
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());
                current[0].setReleasable(collect.get(0).isCurrentVersion());
                // And if there are changes, it's also editable.
                current[0].setEditable(changesInFile && current[0].isReleasable());
            } else {
                current[0].setEditable(changesInFile);
            }
        }

        return versionList;
    }

    public static WineryVersion getPredecessor(DefinitionsChildId id, IRepository repository) {
        WineryVersion[] current = new WineryVersion[1];
        List<WineryVersion> versionList = getVersionsList(id, current, repository);
        int index = versionList.indexOf(current[0]);

        if (index < versionList.size()) {
            return versionList.get(index);
        } else {
            return null;
        }
    }
}
