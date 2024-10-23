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
 * Relationship Template
 * For more details on the TOSCA specification, visit:
 * <a href="https://docs.oasis-open.org/tosca/TOSCA/v2.0/csd06/TOSCA-v2.0-csd06.html#74-relationship-template">Relationship Template</a>
 */
package org.eclipse.winery.lsp.Server.ServerCore.DataModels;

import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaMap;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaString;

import java.util.Map;
import java.util.Optional;

public record RelationshipTemplate(Optional<ToscaString> description,
                                   Optional<ToscaMap<String, Object>> metadata,
                                   Map<String, PropertyDefinition> relationship,
                                   Optional<ToscaString> capability,
                                   ToscaString node,
                                   ToscaMap<String, AttributeDefinition> attributes,
                                   ToscaMap<String, InterfaceAssignment> interfaces,
                                   Optional<ToscaString> copy) { }
