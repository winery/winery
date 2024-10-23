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

package org.eclipse.winery.lsp.Server.ServerCore.DataModels;

import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaBoolean;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaInteger;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaList;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaString;

import java.util.Optional;

public record RequirementAssignment(Optional<ToscaString> node,
                                    Optional<ToscaList> nodeList,
                                    Optional<ToscaString> capability,
                                    Optional<RelationshipAssignment> relationship,
                                    Optional<ToscaString> relationshipString,
                                    Optional<Object> allocation, //TODO add allocation block
                                    Optional<ToscaInteger> count, //TODO non-negative integer
                                    Optional<Object> node_filter, //TODO look for TOSCA spec 8.8 condition_clause to replace the object type with a stack of functions or something
                                    Optional<ToscaList> directives, 
                                    Optional<ToscaBoolean> optional) { }
