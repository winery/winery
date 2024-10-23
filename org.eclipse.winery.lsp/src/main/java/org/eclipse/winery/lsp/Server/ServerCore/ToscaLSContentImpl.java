/*******************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.lsp.Server.ServerCore;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.winery.lsp.Server.ServerAPI.API.context.LSContext;
import org.eclipse.winery.lsp.Server.ServerCore.DataModels.TOSCAFile;
import org.yaml.snakeyaml.error.Mark;

import java.nio.file.Path;
import java.util.*;

public class ToscaLSContentImpl implements LSContext {
    public boolean isValidatedForImporting = false;
    private final Map<String, String> fileContents = new HashMap<>();
    private Map<LSContext.Key<?>, Object> props = new HashMap<>();
    private Map<Class<?>, Object> objects = new HashMap<>();
    private LanguageClient languageClient;
    private ClientCapabilities clientCapabilities;
    private TOSCAFile currentToscaFile;
    private Path currentToscaFilePath;
    private Multimap<Path, Map<String, TOSCAFile>> namespaceDefinitions = LinkedHashMultimap.create();
    private Multimap<Path, Map<String, TOSCAFile>> importedToscaFiles = LinkedHashMultimap.create();
    private Map<String, Path> profilePaths = new HashMap<>();
    private Map<String, Mark> contextDependentConstructorPositions;
    private Set<Path>  directoryFilePaths;
    private Map<Path, TOSCAFile> ToscaFilesPath = new HashMap<>();
    public <V> void put(LSContext.Key<V> key, V value) {
        props.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <V> V get(LSContext.Key<V> key) {
        return (V) props.get(key);
    }

    public <V> void put(Class<V> clazz, V value) {
        objects.put(clazz, value);
    }

    @SuppressWarnings("unchecked")
    public <V> V get(Class<V> clazz) {
        return (V) objects.get(clazz);
    }
    
    @Override
    public void setClient(LanguageClient client) {
        this.languageClient = client;
    }

    @Override
    public LanguageClient getClient() {
        return this.languageClient;
    }

    @Override
    public void setClientCapabilities(ClientCapabilities capabilities) {
        this.clientCapabilities = capabilities;
    }

    @Override
    public Optional<ClientCapabilities> getClientCapabilities() {
        return Optional.ofNullable(this.clientCapabilities);
    }
    
    public void setFileContent(String uri, String content) {
        fileContents.put(uri, content);
    }

    public String getFileContent(String uri) {
        return fileContents.getOrDefault(uri, "");
    }

    @Override
    public TOSCAFile getCurrentToscaFile() {
        return currentToscaFile;
    }

    public void setCurrentToscaFile(TOSCAFile currentToscaFile) {
        this.currentToscaFile = currentToscaFile;
    }

    @Override
    public void setCotextDependentPositions(Map<String, Mark> contextDependentConstructorPositions) {
        this.contextDependentConstructorPositions = contextDependentConstructorPositions;
    }
    
    @Override
    public Map<String, Mark> getContextDependentConstructorPositions() {
        return contextDependentConstructorPositions;
    }
    
    @Override
    public Set<Path> getDirectoryFilePaths() {
        return directoryFilePaths;
    }
    
    @Override
    public void setDirectoryFilePaths(Set<Path>  directoryFilePaths) {
        this.directoryFilePaths = directoryFilePaths;
    }

    @Override
    public Path getCurrentToscaFilePath() {
        return currentToscaFilePath;
    }

    @Override
    public void setCurrentToscaFilePath(Path currentToscaFilePath) {
        this.currentToscaFilePath = currentToscaFilePath;
    }

    @Override
    public Multimap<Path, Map<String, TOSCAFile>> getNamespaceDefinitions() {
        return namespaceDefinitions;
    }

    @Override
    public void setNamespaceDefinitions(Multimap<Path, Map<String, TOSCAFile>> namespaceDefinitions) {
        this.namespaceDefinitions = namespaceDefinitions;
    }

    @Override
    public Map<String, Path> getProfilePaths() {
        return profilePaths;
    }

    @Override
    public void setProfilePaths(Map<String, Path> profilePaths) {
        this.profilePaths = profilePaths;
    }

    @Override
    public Multimap<Path, Map<String, TOSCAFile>> getImportedToscaFiles() {
        return importedToscaFiles;
    }

    @Override
    public void setImportedToscaFiles(Multimap<Path, Map<String, TOSCAFile>> importedToscaFiles) {
        this.importedToscaFiles = importedToscaFiles;
    }
    
    @Override
    public boolean isValidatedForImporting() {
        return isValidatedForImporting;
    }
    
    @Override
    public void setValidatedForImporting(boolean validatedForImporting) {
        isValidatedForImporting = validatedForImporting;
    }
    
    public Map<Path, TOSCAFile> getToscaFilesPath() {
        return ToscaFilesPath;
    }

    public void setToscaFilesPath(Map<Path, TOSCAFile> toscaFilesPath) {
        ToscaFilesPath = toscaFilesPath;
    }

    @Override
    public ToscaLSContentImpl clone() {
        try {
            ToscaLSContentImpl clone = (ToscaLSContentImpl) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
