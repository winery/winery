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

package org.eclipse.winery.lsp.Server.ServerCore.Parsing;

import org.eclipse.lsp4j.services.LanguageClient;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface Parser {
    /**
     * Parses a TOSCA file from the specified path.
     *
     * @param path the path to the TOSCA file
     * @return a map where the keys are strings representing different Keywords of the TOSCA file,
     *         and the values are objects corresponding to the parsed values of these components
     * @throws IOException if an I/O error occurs reading from the file or a malformed or unmappable byte sequence is read
     */
    public Map<String, Object> ParseTOSCAFile(Path path, LanguageClient client) throws IOException;
    
    /**
     * Parses a TOSCA file from the given content string.
     *
     * @param content the content of the TOSCA file as a string
     * @return a map where the keys are strings representing different components of the TOSCA file,
     *         and the values are objects corresponding to the parsed values of these components
     */
    public Map<String, Object>  ParseTOSCAFile(String content,LanguageClient client);
}
