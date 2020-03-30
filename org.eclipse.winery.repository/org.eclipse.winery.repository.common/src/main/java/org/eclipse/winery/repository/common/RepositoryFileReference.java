/*******************************************************************************
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
package org.eclipse.winery.repository.common;

import org.eclipse.winery.model.ids.GenericId;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Holds a reference to a file "object" stored in the repository
 * <p>
 * One has to create {@link org.eclipse.winery.model.ids.elements.ToscaElementId} (e.g., scc-data)
 */
public class RepositoryFileReference implements Comparable<RepositoryFileReference> {

    private final GenericId parent;
    private final Optional<Path> subDirectory;
    private final String fileName;


    /**
     * @param parent   the id of the toscaElement the file is nested in
     * @param fileName the file name. <em>Must not</em> contain any illegal characters. java.nio.Path cannot be used as
     *                 Path is tied to a FileSystem
     */
    public RepositoryFileReference(GenericId parent, String fileName) {
        Objects.requireNonNull(parent);
        Objects.requireNonNull(fileName);
        this.parent = parent;
        this.subDirectory = Optional.empty();
        this.fileName = fileName;
    }

    /**
     * @param parent       the id of the toscaElement the file is nested in
     * @param fileName     the file name. <em>Must not</em> contain any illegal characters. java.nio.Path cannot be used
     *                     as Path is tied to a FileSystem
     * @param subDirectory the subdirectory
     */
    public RepositoryFileReference(GenericId parent, Path subDirectory, String fileName) {
        Objects.requireNonNull(parent);
        Objects.requireNonNull(subDirectory);
        Objects.requireNonNull(fileName);
        this.parent = parent;
        this.subDirectory = Optional.of(subDirectory);
        this.fileName = fileName;
    }

    public RepositoryFileReference setFileName(String fileName) {
        if (this.subDirectory.isPresent()) {
            return new RepositoryFileReference(this.parent, this.subDirectory.get(), fileName);
        } else {
            return new RepositoryFileReference(this.parent, fileName);
        }
    }

    public GenericId getParent() {
        return this.parent;
    }

    public Optional<Path> getSubDirectory() {
        return this.subDirectory;
    }

    public String getFileName() {
        return this.fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepositoryFileReference)) return false;
        RepositoryFileReference that = (RepositoryFileReference) o;
        return Objects.equals(parent, that.parent) &&
            Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, fileName);
    }

    @Override
    public int compareTo(RepositoryFileReference o) {
        int res;
        res = this.parent.compareTo(o.parent);
        if (res == 0) {
            res = this.fileName.compareTo(o.fileName);
        }
        return res;
    }

    @Override
    public String toString() {
        return "RepositoryFileReference{" +
            "parent=" + parent +
            ", subDirectory=" + subDirectory +
            ", fileName='" + fileName + '\'' +
            '}';
    }
}
