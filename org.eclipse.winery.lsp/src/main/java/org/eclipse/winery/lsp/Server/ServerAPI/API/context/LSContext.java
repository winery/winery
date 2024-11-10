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
/**
 * This class is designed to carry information between method calls.
 * <p>
 * The usage of this class was intended to encapsulate and transport data 
 * needed for various operations within the system, ensuring that 
 * all required information is consistently and reliably available 
 * throughout the execution flow.
 * </p>
 *
 */
package org.eclipse.winery.lsp.Server.ServerAPI.API.context;

import com.google.common.collect.Multimap;
import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.winery.lsp.Server.ServerCore.DataModels.TOSCAFile;
import org.eclipse.winery.lsp.Server.ServerCore.ToscaLSContentImpl;
import org.yaml.snakeyaml.error.Mark;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface LSContext extends Cloneable {
    
    <V> void put(LSContext.Key<V> key, V value);

    <V> V get(LSContext.Key<V> key);

    <V> void put(Class<V> clazz, V value);

    <V> V get(Class<V> clazz);

    // void log(MessageType messageType, String message);

    /** TODO --> move this to implementing class -- and activate above interface **/
    default void log(MessageType messageType, String message) {
        this.getClient().logMessage(new MessageParams(messageType,message));
    }
    
    void setClient(LanguageClient client);
    
    LanguageClient getClient();

    void setClientCapabilities(ClientCapabilities capabilities);

    Optional<ClientCapabilities> getClientCapabilities();

    String getFileContent(String uri);
    
    void setFileContent(String uri, String content);

    TOSCAFile getCurrentToscaFile();
    
    void setCurrentToscaFile(TOSCAFile currentToscaFile);

    void setCotextDependentPositions(Map<String, Mark> contextDependentConstructorPositions);
    
    Map<String, Mark> getContextDependentConstructorPositions() ;

    Set<Path> getDirectoryFilePaths();

    void setDirectoryFilePaths(Set<Path> directoryFilePaths) ;

    Path getCurrentToscaFilePath();
    
    void setCurrentToscaFilePath(Path currentToscaFilePath);

    Multimap<Path, Map<String, TOSCAFile>> getNamespaceDefinitions();

    void setNamespaceDefinitions(Multimap<Path, Map<String, TOSCAFile>> namespaceDefinitions);
    
    Map<String, Path> getProfilePaths();

    void setProfilePaths(Map<String, Path> profilePaths);

    Multimap<Path, Map<String, TOSCAFile>> getImportedToscaFiles();
    
    void setImportedToscaFiles(Multimap<Path, Map<String, TOSCAFile>> importedToscaFiles);

    Map<Path, TOSCAFile> getToscaFilesPath();

    void setToscaFilesPath(Map<Path, TOSCAFile> toscaFilesPath);
    
    boolean isValidatedForImporting();
    
    void setValidatedForImporting(boolean validatedForImporting);
    
    ToscaLSContentImpl clone() ;

        class Key<K> { }
}
