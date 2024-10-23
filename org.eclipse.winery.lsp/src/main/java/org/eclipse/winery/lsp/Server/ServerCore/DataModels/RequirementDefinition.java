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

import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaMap;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaString;
import java.util.Optional;

public record RequirementDefinition(String RequirementDefinitionName,
                                    Optional<ToscaString> description,
                                    Optional<ToscaMap<String, Object>> metadata,
                                    ToscaString relationship,
                                    Optional<ToscaString> node,
                                    ToscaString capability,
                                    Optional<Object> count_range, //TODO must be minimum required and maximum allowed number of relationships created by the requirement. 
                                    Optional<Object> node_filter //TODO look for TOSCA spec 8.8 condition_clause to replace the object type with a stack of functions or something
                                     ) {
}
