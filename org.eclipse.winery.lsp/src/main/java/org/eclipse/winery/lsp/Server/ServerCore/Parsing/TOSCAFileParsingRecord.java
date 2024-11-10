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
 * TOSCA File
 * For more details on the TOSCA specification, visit:
 *<a href="https://docs.oasis-open.org/tosca/TOSCA/v2.0/csd06/TOSCA-v2.0-csd06.html#61-keynames">TOSCA file Keynames</a>
 */
package org.eclipse.winery.lsp.Server.ServerCore.Parsing;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RecordBuilder
public record TOSCAFileParsingRecord(
    String toscaDefinitionsVersion,
    Optional<String> description,
    Optional<Map<String, Object>> metadata,
    Optional<Object> dslDefinitions,
    Optional<Map<String, Object>> artifactTypes,
    Optional<Map<String, Object>> dataTypes,
    Optional<Map<String, Object>> capabilityTypes,
    Optional<Map<String, Object>> interfaceTypes,
    Optional<Map<String, Object>> relationshipTypes,
    Optional<Map<String, Object>> nodeTypes,
    Optional<Map<String, Object>> groupTypes,
    Optional<Map<String, Object>> policyTypes,
    Optional<Map<String, Object>> repositories,
    Optional<Map<String, Object>> functions,
    Optional<String> profile,
    Optional<List<Object>> imports,
    Optional<Object> serviceTemplate) { } 
